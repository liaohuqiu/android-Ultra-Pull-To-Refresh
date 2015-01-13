package in.srain.cube.views.ptr;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.*;
import android.widget.Scroller;
import android.widget.TextView;
import in.srain.cube.util.CLog;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

/**
 * This layout view for "Pull to Refresh(Ptr)" support all of the view, you can contain everything you want.
 * support: pull to refresh / release to refresh / auto refresh / keep header view while refreshing / hide header view while refreshing
 * It defines {@link in.srain.cube.views.ptr.PtrUIHandler}, which allows you customize the UI easily.
 */
public class PtrFrameLayout extends ViewGroup {

    // status enum
    public final static byte PTR_STATUS_INIT = 1;
    public final static byte PTR_STATUS_PREPARE = 2;
    public final static byte PTR_STATUS_LOADING = 3;
    public final static byte PTR_STATUS_COMPLETE = 4;

    private final static int POS_START = 0;
    private static final boolean DEBUG_LAYOUT = false;
    public static boolean DEBUG = false;
    private static int ID = 1;
    // auto refresh status
    private static byte STATUS_AUTO_SCROLL_AT_ONCE = 0x01;
    private static byte STATUS_AUTO_SCROLL_LATER = 0x02;
    protected final String LOG_TAG = "ptr-frame-" + ++ID;
    protected View mContent;
    protected int mOffsetToRefresh = 0;
    // optional config for define header and content in xml file
    private int mHeaderId = 0;
    private int mContainerId = 0;
    // config
    private float mResistance = 1.7f;
    private int mDurationToClose = 200;
    private int mDurationToCloseHeader = 1000;
    private float mRatioOfHeaderHeightToRefresh = 1.2f;
    private boolean mKeepHeaderWhenRefresh = true;
    private boolean mPullToRefresh = false;
    private View mHeaderView;
    private PtrUIHandlerHolder mPtrUIHandlerHolder = PtrUIHandlerHolder.create();
    private PtrHandler mPtrHandler;
    // working parameters
    private ScrollChecker mScrollChecker;
    private PointF mPtLastMove = new PointF();
    private int mCurrentPos = 0;
    private int mLastPos = 0;
    private int mPagingTouchSlop;
    private int mHeaderHeight;

    private byte mStatus = PTR_STATUS_INIT;
    private boolean mIsUnderTouch = false;
    private boolean mDisableWhenHorizontalMove = false;
    private int mAutoScrollRefreshTag = 0x00;
    private int mPressedPos = 0;

    // disable when detect moving horizontally
    private boolean mPreventForHorizontal = false;

    // intercept child event while working
    private boolean mInterceptEventWhileWorking = false;
    private MotionEvent mDownEvent;
    private MotionEvent mLastMoveEvent;

    private PtrUIHandlerHook mRefreshCompleteHook;

    private int mLoadingMinTime = 500;
    private long mLoadingStartTime = 0;

    public PtrFrameLayout(Context context) {
        this(context, null);
    }

    public PtrFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PtrFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        PtrLocalDisplay.init(getContext());

        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.PtrFrameLayout, 0, 0);
        if (arr != null) {

            mHeaderId = arr.getResourceId(R.styleable.PtrFrameLayout_ptr_header, mHeaderId);
            mContainerId = arr.getResourceId(R.styleable.PtrFrameLayout_ptr_content, mContainerId);

            mResistance = arr.getFloat(R.styleable.PtrFrameLayout_ptr_resistance, mResistance);

            mDurationToClose = arr.getInt(R.styleable.PtrFrameLayout_ptr_duration_to_close, mDurationToClose);
            mDurationToCloseHeader = arr.getInt(R.styleable.PtrFrameLayout_ptr_duration_to_close_header, mDurationToCloseHeader);

            mRatioOfHeaderHeightToRefresh = arr.getFloat(R.styleable.PtrFrameLayout_ptr_ratio_of_header_height_to_refresh, mRatioOfHeaderHeightToRefresh);
            mKeepHeaderWhenRefresh = arr.getBoolean(R.styleable.PtrFrameLayout_ptr_keep_header_when_refresh, mKeepHeaderWhenRefresh);

            mPullToRefresh = arr.getBoolean(R.styleable.PtrFrameLayout_ptr_pull_to_fresh, mPullToRefresh);
            arr.recycle();
        }

        mScrollChecker = new ScrollChecker();

        final ViewConfiguration conf = ViewConfiguration.get(getContext());
        mPagingTouchSlop = conf.getScaledTouchSlop() * 2;
    }

    @Override
    protected void onFinishInflate() {
        final int childCount = getChildCount();
        if (childCount > 2) {
            throw new IllegalStateException("PtrFrameLayout only can host 2 elements");
        } else if (childCount == 2) {
            if (mHeaderId != 0 && mHeaderView == null) {
                mHeaderView = findViewById(mHeaderId);
            }
            if (mContainerId != 0 && mContent == null) {
                mContent = findViewById(mContainerId);
            }

            // not specify header or content
            if (mContent == null || mHeaderView == null) {

                View child1 = getChildAt(0);
                View child2 = getChildAt(1);
                if (child1 instanceof PtrUIHandler) {
                    mHeaderView = child1;
                    mContent = child2;
                } else if (child2 instanceof PtrUIHandler) {
                    mHeaderView = child2;
                    mContent = child1;
                } else {
                    // both are not specified
                    if (mContent == null && mHeaderView == null) {
                        mHeaderView = child1;
                        mContent = child2;
                    }
                    // only one is specified
                    else {
                        if (mHeaderView == null) {
                            mHeaderView = mContent == child1 ? child2 : child1;
                        } else {
                            mContent = mHeaderView == child1 ? child2 : child1;
                        }
                    }
                }
            }
        } else if (childCount == 1) {
            mContent = getChildAt(0);
        } else {
            TextView errorView = new TextView(getContext());
            errorView.setClickable(true);
            errorView.setTextColor(0xffff6600);
            errorView.setGravity(Gravity.CENTER);
            errorView.setTextSize(20);
            errorView.setText("The content view in PtrFrameLayout is empty. Do you forget to specify its id in xml layout file?");
            mContent = errorView;
            addView(mContent);
        }
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (DEBUG && DEBUG_LAYOUT) {
            CLog.d(LOG_TAG, "onMeasure frame: width: %s, height: %s, padding: %s %s %s %s",
                    getMeasuredHeight(), getMeasuredWidth(),
                    getPaddingLeft(), getPaddingRight(), getPaddingTop(), getPaddingBottom());

        }

        if (mHeaderView != null) {
            measureChildWithMargins(mHeaderView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            mHeaderHeight = mHeaderView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            mOffsetToRefresh = (int) (mHeaderHeight * mRatioOfHeaderHeightToRefresh);
            if (DEBUG && DEBUG_LAYOUT) {
                CLog.d(LOG_TAG, "onMeasure header: height: %s, topMargin: %s, bottomMargin: %s, headerHeight: %s",
                        mHeaderView.getMeasuredHeight(), lp.leftMargin, lp.rightMargin, mHeaderHeight);
            }
        }

        if (mContent != null) {
            measureContentView(mContent, widthMeasureSpec, heightMeasureSpec);
            if (DEBUG && DEBUG_LAYOUT) {
                ViewGroup.MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
                CLog.d(LOG_TAG, "onMeasure content, width: %s, height: %s, margin: %s %s %s %s",
                        getMeasuredWidth(), getMeasuredHeight(),
                        lp.leftMargin, lp.topMargin, lp.rightMargin, lp.bottomMargin);
                CLog.d(LOG_TAG, "onMeasure, mCurrentPos: %s, mLastPos: %s, top: %s",
                        mCurrentPos, mLastPos, mContent.getTop());
            }
        }
    }

    private void measureContentView(View child,
                                    int parentWidthMeasureSpec,
                                    int parentHeightMeasureSpec) {
        final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

        final int childWidthMeasureSpec = getChildMeasureSpec(parentWidthMeasureSpec,
                getPaddingLeft() + getPaddingRight() + lp.leftMargin + lp.rightMargin, lp.width);
        final int childHeightMeasureSpec = getChildMeasureSpec(parentHeightMeasureSpec,
                getPaddingTop() + getPaddingBottom() + lp.topMargin, lp.height);

        child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean flag, int i, int j, int k, int l) {
        layoutChildren();
    }

    private void layoutChildren() {
        int offsetX = mCurrentPos;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();

        if (mHeaderView != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + offsetX - mHeaderHeight;
            final int right = left + mHeaderView.getMeasuredWidth();
            final int bottom = top + mHeaderView.getMeasuredHeight();
            mHeaderView.layout(left, top, right, bottom);
            if (DEBUG && DEBUG_LAYOUT) {
                CLog.d(LOG_TAG, "onLayout header: %s %s %s %s", left, top, right, bottom);
            }
        }
        if (mContent != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + offsetX;
            final int right = left + mContent.getMeasuredWidth();
            final int bottom = top + mContent.getMeasuredHeight();
            if (DEBUG && DEBUG_LAYOUT) {
                CLog.d(LOG_TAG, "onLayout content: %s %s %s %s", left, top, right, bottom);
            }
            mContent.layout(left, top, right, bottom);
        }
    }

    public boolean dispatchTouchEventSupper(MotionEvent e) {
        return super.dispatchTouchEvent(e);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        if (!isEnabled() || mContent == null || mHeaderView == null) {
            return dispatchTouchEventSupper(e);
        }
        int action = e.getAction();
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsUnderTouch = false;
                if (mCurrentPos > POS_START) {
                    if (DEBUG) {
                        CLog.d(LOG_TAG, "call onRelease when user release");
                    }
                    onRelease(false);
                    if (mCurrentPos != mPressedPos) {
                        sendCancelEvent();
                        return true;
                    }
                    return dispatchTouchEventSupper(e);
                } else {
                    return dispatchTouchEventSupper(e);
                }

            case MotionEvent.ACTION_DOWN:
                mDownEvent = e;
                mPtLastMove.set(e.getX(), e.getY());

                mScrollChecker.abortIfWorking();

                mIsUnderTouch = true;
                mPreventForHorizontal = false;
                if (mInterceptEventWhileWorking && mCurrentPos > POS_START) {
                    // do nothing, intercept child event
                } else {
                    dispatchTouchEventSupper(e);
                }
                mPressedPos = mCurrentPos;
                return true;

            case MotionEvent.ACTION_MOVE:
                mLastMoveEvent = e;
                float offsetX = e.getX() - mPtLastMove.x;
                float offsetY = (int) (e.getY() - mPtLastMove.y);

                mPtLastMove.set(e.getX(), e.getY());
                if (mDisableWhenHorizontalMove && !mPreventForHorizontal && (Math.abs(offsetX) > mPagingTouchSlop || Math.abs(offsetX) > 3 * Math.abs(offsetY))) {
                    if (frameIsNotMoved()) {
                        mPreventForHorizontal = true;
                    }
                }
                if (mPreventForHorizontal) {
                    return dispatchTouchEventSupper(e);
                }

                boolean moveDown = offsetY > 0;
                boolean moveUp = !moveDown;
                boolean canMoveUp = mCurrentPos > POS_START;

                if (DEBUG) {
                    boolean canMoveDown = mPtrHandler != null && mPtrHandler.checkCanDoRefresh(this, mContent, mHeaderView);
                    CLog.v(LOG_TAG, "ACTION_MOVE: offsetY:%s, mCurrentPos: %s, moveUp: %s, canMoveUp: %s, moveDown: %s: canMoveDown: %s", offsetY, mCurrentPos, moveUp, canMoveUp, moveDown, canMoveDown);
                }

                // disable move when header not reach top
                if (moveDown && mPtrHandler != null && !mPtrHandler.checkCanDoRefresh(this, mContent, mHeaderView)) {
                    return dispatchTouchEventSupper(e);
                }

                if ((moveUp && canMoveUp) || moveDown) {
                    offsetY = (float) ((double) offsetY / mResistance);
                    movePos(offsetY);
                    return true;
                }
        }
        return dispatchTouchEventSupper(e);
    }

    /**
     * if deltaY > 0, move the content down
     *
     * @param deltaY
     */
    private void movePos(float deltaY) {
        // has reached the top
        if ((deltaY < 0 && mCurrentPos == POS_START)) {
            if (DEBUG) {
                CLog.e(LOG_TAG, String.format("has reached the top"));
            }
            return;
        }

        int to = mCurrentPos + (int) deltaY;

        // over top
        if (to < POS_START) {
            if (DEBUG) {
                CLog.e(LOG_TAG, String.format("over top"));
            }
            to = POS_START;
        }

        mCurrentPos = to;
        updatePos();
        mLastPos = mCurrentPos;
    }

    private void updatePos() {
        int change = mCurrentPos - mLastPos;
        if (change == 0) {
            return;
        }

        // leave initiated position
        if (mLastPos == POS_START && mCurrentPos != POS_START && mPtrUIHandlerHolder.hasHandler()) {
            if (mStatus == PTR_STATUS_INIT) {
                mStatus = PTR_STATUS_PREPARE;
                mPtrUIHandlerHolder.onUIRefreshPrepare(this);
                if (DEBUG) {
                    CLog.i(LOG_TAG, "PtrUIHandler: onUIRefreshPrepare, mAutoScrollRefreshTag %s", mAutoScrollRefreshTag);
                }
            }

            // send cancel event to children
            if (mIsUnderTouch && mInterceptEventWhileWorking) {
                sendCancelEvent();
            }
        }

        // back to initiated position
        if (mLastPos != POS_START && mCurrentPos == POS_START) {
            tryToNotifyReset();

            // recover event to children
            if (mIsUnderTouch && mInterceptEventWhileWorking) {
                sendDownEvent();
            }
        }

        // Pull to Refresh
        if (mStatus == PTR_STATUS_PREPARE) {
            // reach fresh height while moving from top to bottom
            if (mIsUnderTouch && mAutoScrollRefreshTag == 0 && mPullToRefresh
                    && mLastPos < mOffsetToRefresh && mCurrentPos >= mOffsetToRefresh) {
                tryToPerformRefresh();
            }
            // reach header height while auto refresh
            if (mAutoScrollRefreshTag == STATUS_AUTO_SCROLL_LATER && mLastPos < mHeaderHeight && mCurrentPos >= mHeaderHeight) {
                tryToPerformRefresh();
            }
        }

        if (DEBUG) {
            CLog.v(LOG_TAG, "updatePos: change: %s, current: %s last: %s, top: %s, headerHeight: %s",
                    change, mCurrentPos, mLastPos, mContent.getTop(), mHeaderHeight);
        }

        mHeaderView.offsetTopAndBottom(change);
        mContent.offsetTopAndBottom(change);
        invalidate();

        final float oldPercent = mHeaderHeight == 0 ? 0 : mLastPos * 1f / mHeaderHeight;
        final float currentPercent = mHeaderHeight == 0 ? 0 : mCurrentPos * 1f / mHeaderHeight;
        if (mPtrUIHandlerHolder.hasHandler()) {
            mPtrUIHandlerHolder.onUIPositionChange(this, mIsUnderTouch, mStatus, mLastPos, mCurrentPos, oldPercent, currentPercent);
        }
        onPositionChange(mIsUnderTouch, mStatus, mLastPos, mCurrentPos, oldPercent, currentPercent);
    }

    protected void onPositionChange(boolean isInTouching, byte status, int lastPosition, int currentPosition, float oldPercent, float currentPercent) {
    }

    @SuppressWarnings("unused")
    public int getHeaderHeight() {
        return mHeaderHeight;
    }

    private void onRelease(boolean stayForLoading) {

        tryToPerformRefresh();

        if (mStatus == PTR_STATUS_LOADING) {
            // keep header for fresh
            if (mKeepHeaderWhenRefresh) {
                // scroll header back
                if (mCurrentPos > mHeaderHeight && !stayForLoading) {
                    mScrollChecker.tryToScrollTo(mHeaderHeight, mDurationToClose);
                } else {
                    // do nothing
                }
            } else {
                tryScrollBackToTopWhileLoading();
            }
        } else {
            if (mStatus == PTR_STATUS_COMPLETE) {
                notifyUIRefreshComplete(false);
            } else {
                tryScrollBackToTopAbortRefresh();
            }
        }
    }

    /**
     * please DO REMEMBER resume the hook
     *
     * @param hook
     */
    public void setRefreshCompleteHook(PtrUIHandlerHook hook) {
        mRefreshCompleteHook = hook;
        hook.setResumeAction(new Runnable() {
            @Override
            public void run() {
                if (DEBUG) {
                    CLog.d(LOG_TAG, "mRefreshCompleteHook resume.");
                }
                notifyUIRefreshComplete(true);
            }
        });
    }

    /**
     * Scroll back to to if is not under touch
     */
    private void tryScrollBackToTop() {
        if (!mIsUnderTouch) {
            mScrollChecker.tryToScrollTo(POS_START, mDurationToCloseHeader);
        }
    }

    /**
     * just make easier to understand
     */
    private void tryScrollBackToTopWhileLoading() {
        tryScrollBackToTop();
    }

    /**
     * just make easier to understand
     */
    private void tryScrollBackToTopAfterComplete() {
        tryScrollBackToTop();
    }

    /**
     * just make easier to understand
     */
    private void tryScrollBackToTopAbortRefresh() {
        tryScrollBackToTop();
    }

    private boolean tryToPerformRefresh() {
        if (mStatus != PTR_STATUS_PREPARE) {
            return false;
        }
        if ((mCurrentPos >= mHeaderHeight && mAutoScrollRefreshTag > 0) || mCurrentPos >= mOffsetToRefresh) {
            mStatus = PTR_STATUS_LOADING;
            performRefresh();
        }
        return false;
    }

    private void performRefresh() {
        mLoadingStartTime = System.currentTimeMillis();
        if (mPtrUIHandlerHolder.hasHandler()) {
            mPtrUIHandlerHolder.onUIRefreshBegin(this);
            if (DEBUG) {
                CLog.i(LOG_TAG, "PtrUIHandler: onUIRefreshBegin");
            }
        }
        if (mPtrHandler != null) {
            mPtrHandler.onRefreshBegin(this);
        }
    }

    /**
     * If at the top and not in loading, reset
     */
    private boolean tryToNotifyReset() {
        if ((mStatus == PTR_STATUS_COMPLETE || mStatus == PTR_STATUS_PREPARE) && mCurrentPos == POS_START) {
            if (mPtrUIHandlerHolder.hasHandler()) {
                mPtrUIHandlerHolder.onUIReset(this);
                if (DEBUG) {
                    CLog.i(LOG_TAG, "PtrUIHandler: onUIReset");
                }
            }
            mStatus = PTR_STATUS_INIT;
            mAutoScrollRefreshTag = 0;
            return true;
        }
        return false;
    }

    protected void onPtrScrollAbort() {
        if (mCurrentPos > 0 && mAutoScrollRefreshTag > 0) {
            if (DEBUG) {
                CLog.d(LOG_TAG, "call onRelease after scroll abort");
            }
            onRelease(true);
        }
    }

    protected void onPtrScrollFinish() {
        if (mCurrentPos > 0 && mAutoScrollRefreshTag > 0) {
            if (DEBUG) {
                CLog.d(LOG_TAG, "call onRelease after scroll finish");
            }
            onRelease(true);
        }
    }

    private boolean frameIsNotMoved() {
        return mCurrentPos == POS_START;
    }

    /**
     * Call this when data is loaded
     */
    final public void refreshComplete() {
        if (DEBUG) {
            CLog.i(LOG_TAG, "refreshComplete");
        }

        if (mRefreshCompleteHook != null) {
            mRefreshCompleteHook.reset();
        }

        int delay = (int) (mLoadingMinTime - (System.currentTimeMillis() - mLoadingStartTime));
        if (delay <= 0) {
            if (DEBUG) {
                CLog.d(LOG_TAG, "performRefreshComplete at once");
            }
            performRefreshComplete();
        } else {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    performRefreshComplete();
                }
            }, delay);
            if (DEBUG) {
                CLog.d(LOG_TAG, "performRefreshComplete after delay: %s", delay);
            }
        }
    }

    private void performRefreshComplete() {
        mStatus = PTR_STATUS_COMPLETE;

        // if is auto refresh do nothing, wait scroller stop
        if (mScrollChecker.mIsRunning && mAutoScrollRefreshTag > 0) {
            // do nothing
            if (DEBUG) {
                CLog.d(LOG_TAG, "performRefreshComplete do nothing, scrolling: %s, auto refresh: %s",
                        mScrollChecker.mIsRunning, mAutoScrollRefreshTag);
            }
            return;
        }

        notifyUIRefreshComplete(false);
    }

    private void notifyUIRefreshComplete(boolean ignoreHook) {
        /**
         * after hook operation is done, will call {@link #notifyUIRefreshComplete} and ignore hook
         */
        if (mCurrentPos != POS_START && !ignoreHook && mRefreshCompleteHook != null) {
            if (DEBUG) {
                CLog.d(LOG_TAG, "notifyUIRefreshComplete mRefreshCompleteHook run.");
            }

            mRefreshCompleteHook.takeOver();
            return;
        }
        if (mPtrUIHandlerHolder.hasHandler()) {
            if (DEBUG) {
                CLog.i(LOG_TAG, "PtrUIHandler: onUIRefreshComplete");
            }
            mPtrUIHandlerHolder.onUIRefreshComplete(this);
        }
        tryScrollBackToTopAfterComplete();
        tryToNotifyReset();
    }

    public void autoRefresh() {
        autoRefresh(true, mDurationToCloseHeader);
    }

    public void autoRefresh(boolean atOnce) {
        autoRefresh(atOnce, mDurationToCloseHeader);
    }

    public void autoRefresh(boolean atOnce, int duration) {

        if (mStatus != PTR_STATUS_INIT) {
            return;
        }

        mAutoScrollRefreshTag = atOnce ? STATUS_AUTO_SCROLL_AT_ONCE : STATUS_AUTO_SCROLL_LATER;

        mStatus = PTR_STATUS_PREPARE;
        if (mPtrUIHandlerHolder.hasHandler()) {
            mPtrUIHandlerHolder.onUIRefreshPrepare(this);
            if (DEBUG) {
                CLog.i(LOG_TAG, "PtrUIHandler: onUIRefreshPrepare, mAutoScrollRefreshTag %s", mAutoScrollRefreshTag);
            }
        }
        mScrollChecker.tryToScrollTo(mOffsetToRefresh, duration);
        if (atOnce) {
            mStatus = PTR_STATUS_LOADING;
            performRefresh();
        }
    }

    /**
     * It's useful when working with viewpager.
     *
     * @param disable
     */
    public void disableWhenHorizontalMove(boolean disable) {
        mDisableWhenHorizontalMove = disable;
    }

    /**
     * loading will last at least for so long
     *
     * @param time
     */
    public void setLoadingMinTime(int time) {
        mLoadingMinTime = time;
    }

    /**
     * It's useful when you want to intercept event while moving the frame
     *
     * @param yes
     */
    public void setInterceptEventWhileWorking(boolean yes) {
        mInterceptEventWhileWorking = yes;
    }

    public View getContentView() {
        return mContent;
    }

    public void setPtrHandler(PtrHandler ptrHandler) {
        mPtrHandler = ptrHandler;
    }

    public void addPtrUIHandler(PtrUIHandler ptrUIHandler) {
        PtrUIHandlerHolder.addHandler(mPtrUIHandlerHolder, ptrUIHandler);
    }

    public void removePtrUIHandler(PtrUIHandler ptrUIHandler) {
        mPtrUIHandlerHolder = PtrUIHandlerHolder.removeHandler(mPtrUIHandlerHolder, ptrUIHandler);

    }

    public float getResistance() {
        return mResistance;
    }

    public void setResistance(float resistance) {
        mResistance = resistance;
    }

    public float getDurationToClose() {
        return mDurationToClose;
    }

    public void setDurationToClose(int duration) {
        mDurationToClose = duration;
    }

    public long getDurationToCloseHeader() {
        return mDurationToCloseHeader;
    }

    public void setDurationToCloseHeader(int duration) {
        mDurationToCloseHeader = duration;
    }

    public void setRatioOfHeaderHeightToRefresh(float ratio) {
        mRatioOfHeaderHeightToRefresh = ratio;
        mOffsetToRefresh = (int) (mHeaderHeight * mRatioOfHeaderHeightToRefresh);
    }

    public int getOffsetToRefresh() {
        return mOffsetToRefresh;
    }

    public void setOffsetToRefresh(int offset) {
        mOffsetToRefresh = offset;
    }

    public float getRatioOfHeaderToHeightRefresh() {
        return mRatioOfHeaderHeightToRefresh;
    }

    public boolean isKeepHeaderWhenRefresh() {
        return mKeepHeaderWhenRefresh;
    }

    public void setKeepHeaderWhenRefresh(boolean keepOrNot) {
        mKeepHeaderWhenRefresh = keepOrNot;
    }

    public boolean isPullToRefresh() {
        return mPullToRefresh;
    }

    public void setPullToRefresh(boolean pullToRefresh) {
        mPullToRefresh = pullToRefresh;
    }

    public View getHeaderView() {
        return mHeaderView;
    }

    public void setHeaderView(View header) {
        if (mHeaderView != null && header != null && mHeaderView != header) {
            removeView(mHeaderView);
        }
        ViewGroup.LayoutParams lp = header.getLayoutParams();
        if (lp == null) {
            lp = new LayoutParams(-1, -2);
            header.setLayoutParams(lp);
        }
        mHeaderView = header;
        addView(header);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    private void sendCancelEvent() {
        if (DEBUG) {
            CLog.d(LOG_TAG, "send cancel event");
        }
        MotionEvent e = MotionEvent.obtain(mDownEvent.getDownTime(), mDownEvent.getEventTime() + ViewConfiguration.getLongPressTimeout(), MotionEvent.ACTION_CANCEL, mDownEvent.getX(), mDownEvent.getY(), mDownEvent.getMetaState());
        dispatchTouchEventSupper(e);
    }

    private void sendDownEvent() {
        if (DEBUG) {
            CLog.d(LOG_TAG, "send down event");
        }
        final MotionEvent last = mLastMoveEvent;
        MotionEvent e = MotionEvent.obtain(last.getDownTime(), last.getEventTime(), MotionEvent.ACTION_DOWN, last.getX(), last.getY(), last.getMetaState());
        dispatchTouchEventSupper(e);
    }

    public static class LayoutParams extends MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    class ScrollChecker implements Runnable {

        private int mLastFlingY;
        private Scroller mScroller;
        private boolean mIsRunning = false;
        private int mStart;
        private int mTo;

        public ScrollChecker() {
            mScroller = new Scroller(getContext());
        }

        public void run() {
            boolean finish = !mScroller.computeScrollOffset() || mScroller.isFinished();
            int curY = mScroller.getCurrY();
            int deltaY = curY - mLastFlingY;
            if (DEBUG) {
                if (deltaY != 0) {
                    CLog.v(LOG_TAG,
                            "scroll: %s, start: %s, to: %s, mCurrentPos: %s, current :%s, last: %s, delta: %s",
                            finish, mStart, mTo, mCurrentPos, curY, mLastFlingY, deltaY);
                }
            }
            if (!finish) {
                mLastFlingY = curY;
                movePos(deltaY);
                post(this);
            } else {
                finish();
            }
        }

        private void finish() {
            if (DEBUG) {
                CLog.v(LOG_TAG, "finish, mCurrentPos:%s", mCurrentPos);
            }
            reset();
            onPtrScrollFinish();
        }

        private void reset() {
            mIsRunning = false;
            mLastFlingY = 0;
            removeCallbacks(this);
        }

        public void abortIfWorking() {
            if (mIsRunning) {
                if (!mScroller.isFinished()) {
                    mScroller.forceFinished(true);
                }
                onPtrScrollAbort();
                reset();
            }
        }

        public void tryToScrollTo(int to, int duration) {
            if (mCurrentPos == to) {
                return;
            }
            mStart = mCurrentPos;
            mTo = to;
            int distance = to - mStart;
            if (DEBUG) {
                CLog.d(LOG_TAG, "tryToScrollTo: start: %s, distance:%s, to:%s", mStart, distance, to);
            }
            removeCallbacks(this);

            mLastFlingY = 0;
            mScroller = new Scroller(getContext());
            mScroller.startScroll(0, 0, 0, distance, duration);
            post(this);
            mIsRunning = true;
        }
    }
}