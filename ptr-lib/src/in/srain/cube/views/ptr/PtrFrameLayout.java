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

public class PtrFrameLayout extends ViewGroup {

    public static boolean DEBUG = false;

    private static int ID = 1;
    protected final String LOG_TAG = "ptr-frame-" + ++ID;
    protected View mContent;

    private int mHeaderId = 0;
    private int mContainerId = 0;
    private float mResistance = 1.7f;
    private int mDurationToClose = 200;
    private int mDurationToCloseHeader = 1000;
    private float mRatioOfHeaderHeightToRefresh = 1.2f;
    private boolean mKeepHeaderWhenRefresh = true;
    private boolean mPullToRefresh = false;

    protected int mOffsetToRefresh = 0;
    private View mHeaderView;
    private int mHeaderHeight;

    private MotionEvent mDownEvent;
    private CheckForLongPress mPendingCheckForLongPress = new CheckForLongPress();
    private CheckForLongPress2 mPendingCheckForLongPress2 = new CheckForLongPress2();

    private ScrollChecker mScrollChecker;

    private final static int POS_START = 0;
    private int mCurrentPos = 0;
    private int mLastPos = 0;
    private int mPagingTouchSlop;

    private PtrUIHandler mPtrUIHandler;
    private PtrHandler mPtrHandler;

    private PointF mPtLastMove = new PointF();

    private byte mStatus = PTR_STATUS_INIT;

    protected final static byte PTR_STATUS_INIT = 1;
    protected final static byte PTR_STATUS_PREPARE = 2;
    protected final static byte PTR_STATUS_LOADING = 3;
    protected final static byte PTR_STATUS_COMPLETE = 4;

    private boolean mPreventForHorizontal = false;
    private boolean mIsUnderTouch = false;
    private boolean mDisableWhenHorizontalMove = false;

    private boolean mLongPressing = false;
    private boolean mPendingRemoved = false;
    private boolean mAutoScrollRefresh = false;

    public PtrFrameLayout(Context context) {
        this(context, null);
    }

    public PtrFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressWarnings("deprecated")
    public PtrFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        PtrLocalDisplay.init(getContext());
        if (DEBUG) {
            CLog.d(LOG_TAG, "PtrBaseFrame(Context context, AttributeSet attrs, int defStyle)");
        }

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
        if (mHeaderId != 0 && mHeaderView == null) {
            mHeaderView = findViewById(mHeaderId);
        }
        if (mContainerId != 0 && mContent == null) {
            mContent = findViewById(mContainerId);
        }
        if (DEBUG) {
            CLog.i(LOG_TAG, "onFinishInflate");
        }
        if (mContent == null) {
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

        if (DEBUG) {
            CLog.d(LOG_TAG, "onMeasure frame: width: %s, height: %s, padding: %s %s %s %s",
                    getMeasuredHeight(), getMeasuredWidth(),
                    getPaddingLeft(), getPaddingRight(), getPaddingTop(), getPaddingBottom());

        }

        if (mHeaderView != null) {
            measureChildWithMargins(mHeaderView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            MarginLayoutParams lp = (MarginLayoutParams) mHeaderView.getLayoutParams();
            mHeaderHeight = mHeaderView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            mOffsetToRefresh = (int) (mHeaderHeight * mRatioOfHeaderHeightToRefresh);
            if (DEBUG) {
                CLog.d(LOG_TAG, "onMeasure header: height: %s, topMargin: %s, bottomMargin: %s, headerHeight: %s",
                        mHeaderView.getMeasuredHeight(), lp.leftMargin, lp.rightMargin, mHeaderHeight);
            }
        }

        if (mContent != null) {
            measureContentView(mContent, widthMeasureSpec, heightMeasureSpec);
            if (DEBUG) {
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
            final int bottom = top + mHeaderView.getMeasuredHeight() + offsetX;
            mHeaderView.layout(left, top, right, bottom);
            if (DEBUG) {
                CLog.d(LOG_TAG, "onLayout header: %s %s %s %s", left, top, right, bottom);
            }
        }
        if (mContent != null) {
            MarginLayoutParams lp = (MarginLayoutParams) mContent.getLayoutParams();
            final int left = paddingLeft + lp.leftMargin;
            final int top = paddingTop + lp.topMargin + offsetX;
            final int right = left + mContent.getMeasuredWidth();
            final int bottom = top + mContent.getMeasuredHeight() + offsetX;
            if (DEBUG) {
                CLog.d(LOG_TAG, "onLayout content: %s %s %s %s", left, top, right, bottom);
            }
            mContent.layout(left, top, right, bottom);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent e) {
        int action = e.getAction();
        if (mLongPressing && action != MotionEvent.ACTION_DOWN) {
            return false;
        }
        switch (action) {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mIsUnderTouch = false;
                if (mCurrentPos > POS_START) {
                    onRelease();
                    return true;
                } else {
                    return super.dispatchTouchEvent(e);
                }

            case MotionEvent.ACTION_DOWN:
                mDownEvent = e;
                mPtLastMove.set(e.getX(), e.getY());

                mIsUnderTouch = true;
                mPreventForHorizontal = false;
                mLongPressing = false;
                postDelayed(mPendingCheckForLongPress, ViewConfiguration.getLongPressTimeout() + 100);
                mPendingRemoved = false;
                return super.dispatchTouchEvent(e);

            case MotionEvent.ACTION_MOVE:
                float offsetX = e.getX() - mPtLastMove.x;
                float offsetY = (int) (e.getY() - mPtLastMove.y);

                if (!mPendingRemoved) {
                    removeCallbacks(mPendingCheckForLongPress);
                    mPendingRemoved = true;
                }

                mPtLastMove.set(e.getX(), e.getY());
                if (mDisableWhenHorizontalMove && !mPreventForHorizontal && (Math.abs(offsetX) > mPagingTouchSlop || Math.abs(offsetX) > 3 * Math.abs(offsetY))) {
                    if (frameIsNotMoved()) {
                        mPreventForHorizontal = true;
                    }
                }
                if (mPreventForHorizontal) {
                    return super.dispatchTouchEvent(e);
                }

                mScrollChecker.abortIfWorking();

                boolean moveDown = offsetY > 0;
                boolean moveUp = !moveDown;
                boolean canMoveUp = mCurrentPos > POS_START;

                if (DEBUG) {
                    CLog.v(LOG_TAG, "ACTION_MOVE: offsetY:%s, mCurrentPos: %s, moveUp: %s, canMoveUp: %s, moveDown: %s", offsetY, mCurrentPos, moveUp, canMoveUp, moveDown);
                }

                // disable move when header not reach top
                if (moveDown && mPtrHandler != null && !mPtrHandler.checkCanDoRefresh(this, mContent, mHeaderView)) {
                    return super.dispatchTouchEvent(e);
                }

                if ((moveUp && canMoveUp) || moveDown) {
                    offsetY = (float) ((double) offsetY / mResistance);
                    movePos(offsetY);
                    return true;
                }
        }
        return super.dispatchTouchEvent(e);
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
        if (mLastPos == POS_START && mCurrentPos != POS_START && mPtrUIHandler != null) {
            if (mStatus == PTR_STATUS_INIT) {
                mStatus = PTR_STATUS_PREPARE;
                mPtrUIHandler.onUIRefreshPrepare(this, mAutoScrollRefresh);
                if (DEBUG) {
                    CLog.i(LOG_TAG, "PtrUIHandler: onUIRefreshPrepare, mAutoScrollRefresh %s", mAutoScrollRefresh);
                }
            }
        }

        // back to initiated position
        if (mLastPos != POS_START && mCurrentPos == POS_START) {
            tryToNotifyReset();
        }

        // Pull to Refresh: reach fresh height when moving from top to bottom
        if (mStatus == PTR_STATUS_PREPARE && !mAutoScrollRefresh && mPullToRefresh
                && mLastPos < mOffsetToRefresh && mCurrentPos >= mOffsetToRefresh) {
            tryToPerformRefresh();
        }

        if (DEBUG) {
            CLog.v(LOG_TAG, "updatePos: change: %s, current: %s last: %s, top: %s, headerHeight: %s",
                    change, mCurrentPos, mLastPos, mContent.getTop(), mHeaderHeight);
        }

        mHeaderView.offsetTopAndBottom(change);
        mContent.offsetTopAndBottom(change);
        invalidate();

        final float oldPercent = mHeaderHeight == 0 ? 0 : mLastPos / mHeaderHeight;
        final float currentPercent = mHeaderHeight == 0 ? 0 : mCurrentPos / mHeaderHeight;
        mPtrUIHandler.onUIPositionChange(this, mIsUnderTouch, mStatus, mLastPos, mCurrentPos, oldPercent, currentPercent);
        onPositionChange(mIsUnderTouch, mStatus, mLastPos, mCurrentPos, oldPercent, currentPercent);
    }

    protected void onPositionChange(boolean isInTouching, byte status, int lastPosition, int currentPosition, float oldPercent, float currentPercent) {
    }

    public int getHeaderHeight() {
        return mHeaderHeight;
    }

    private void onRelease() {

        tryToPerformRefresh();

        // keep header for fresh
        if (mStatus == PTR_STATUS_LOADING) {
            if (mKeepHeaderWhenRefresh) {
                if (mCurrentPos > mHeaderHeight) {
                    mScrollChecker.tryToScrollTo(mHeaderHeight, mDurationToClose);
                } else {
                    // do nothing
                }
            } else {
                mScrollChecker.tryToScrollTo(POS_START, mDurationToCloseHeader);
            }
        } else {
            mScrollChecker.tryToScrollTo(POS_START, mDurationToCloseHeader);
        }
    }

    private boolean tryToPerformRefresh() {
        if (mStatus != PTR_STATUS_PREPARE) {
            return false;
        }
        if ((mCurrentPos == mHeaderHeight && mAutoScrollRefresh) || mCurrentPos >= mOffsetToRefresh) {

            mStatus = PTR_STATUS_LOADING;

            if (mPtrUIHandler != null) {
                mPtrUIHandler.onUIRefreshBegin(this);
                if (DEBUG) {
                    CLog.i(LOG_TAG, "PtrUIHandler: onUIRefreshBegin");
                }
            }
            if (mPtrHandler != null) {
                mPtrHandler.onRefreshBegin(this);
            }
            return true;
        }
        return false;
    }

    private void tryToNotifyReset() {
        if (mStatus == PTR_STATUS_COMPLETE && mCurrentPos == POS_START) {
            if (mPtrUIHandler != null) {
                mPtrUIHandler.onUIReset(this);
                if (DEBUG) {
                    CLog.i(LOG_TAG, "PtrUIHandler: onUIReset");
                }
            }
            mStatus = PTR_STATUS_INIT;
            mAutoScrollRefresh = false;
        }
    }

    protected void onPtrScrollFinish() {
        // by calling autoRefresh
        if (mCurrentPos == mHeaderHeight && mAutoScrollRefresh) {
            if (mStatus == PTR_STATUS_PREPARE) {
                tryToPerformRefresh();
                // it's loading now
                if (mKeepHeaderWhenRefresh && mCurrentPos >= mHeaderHeight) {
                    // keep current position
                } else {
                    // return to initial position
                    mScrollChecker.tryToScrollTo(0, mDurationToCloseHeader);
                }
            }
        }
    }

    private boolean frameIsNotMoved() {
        return mCurrentPos == POS_START;
    }

    final public void refreshComplete() {
        if (DEBUG) {
            CLog.i(LOG_TAG, "refreshComplete");
        }
        mStatus = PTR_STATUS_COMPLETE;
        tryToNotifyReset();
        if (!mIsUnderTouch) {
            mScrollChecker.tryToScrollTo(POS_START, mDurationToCloseHeader);
        }
        if (mPtrUIHandler != null) {
            mPtrUIHandler.onUIRefreshComplete(this);
            if (DEBUG) {
                CLog.i(LOG_TAG, "PtrUIHandler: onUIRefreshComplete");
            }
        }
    }

    public void autoRefresh() {

        if (mStatus != PTR_STATUS_INIT) {
            return;
        }
        mStatus = PTR_STATUS_PREPARE;
        mAutoScrollRefresh = true;
        if (mPtrUIHandler != null) {
            mPtrUIHandler.onUIRefreshPrepare(this, mAutoScrollRefresh);
            if (DEBUG) {
                CLog.i(LOG_TAG, "PtrUIHandler: onUIRefreshPrepare, mAutoScrollRefresh %s", mAutoScrollRefresh);
            }
        }
        mScrollChecker.tryToScrollTo(mHeaderHeight, mDurationToCloseHeader);
    }

    /**
     * It's useful when working with viewpager.
     *
     * @param disable
     */
    public void disableWhenHorizontalMove(boolean disable) {
        mDisableWhenHorizontalMove = disable;
    }

    public View getContentView() {
        return mContent;
    }

    public void setPtrHandler(PtrHandler ptrHandler) {
        mPtrHandler = ptrHandler;
    }

    public void setPtrUIHandler(PtrUIHandler ptrUIHandler) {
        mPtrUIHandler = ptrUIHandler;
    }

    public void setResistance(float resistance) {
        mResistance = resistance;
    }

    public float getResistance() {
        return mResistance;
    }

    public void setDurationToClose(int duration) {
        mDurationToClose = duration;
    }

    public float getDurationToClose() {
        return mDurationToClose;
    }

    public void setDurationToCloseHeader(int duration) {
        mDurationToCloseHeader = duration;
    }

    public float getDurationToCloseHeader() {
        return mDurationToCloseHeader;
    }

    public void setRatioOfHeaderHeightToRefresh(float ratio) {
        mRatioOfHeaderHeightToRefresh = ratio;
        mOffsetToRefresh = (int) (mHeaderHeight * mRatioOfHeaderHeightToRefresh);
    }

    public float getRatioOfHeaderToHeightRefresh() {
        return mRatioOfHeaderHeightToRefresh;
    }

    public void setKeepHeaderWhenRefresh(boolean keepOrNot) {
        mKeepHeaderWhenRefresh = keepOrNot;
    }

    public boolean isKeepHeaderWhenRefresh() {
        return mKeepHeaderWhenRefresh;
    }

    public boolean isPullToRefresh() {
        return mPullToRefresh;
    }

    public void setPullToRefresh(boolean pullToRefresh) {
        mPullToRefresh = pullToRefresh;
    }

    protected View getHeaderView() {
        return mHeaderView;
    }

    public void setPtrHeaderView(View header) {
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

    private boolean contentItemIsLongPressing() {
        return true;
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

    public static class LayoutParams extends MarginLayoutParams {

        /**
         * Creates a new set of layout parameters. The values are extracted from
         * the supplied attributes set and context.
         *
         * @param c     the application environment
         * @param attrs the set of attributes from which to extract the layout
         */
        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        /**
         * {@inheritDoc}
         *
         * @param width
         * @param height
         */
        public LayoutParams(int width, int height) {
            super(width, height);
        }

        /**
         * Copy constructor. Clones the width, height and margin values of the source.
         *
         * @param source The layout params to copy from.
         */
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        /**
         * {@inheritDoc}
         *
         * @param source
         */
        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

    private class CheckForLongPress implements Runnable {
        public void run() {
            if (contentItemIsLongPressing()) {
                postDelayed(mPendingCheckForLongPress2, 100);
            }
        }
    }

    private class CheckForLongPress2 implements Runnable {
        public void run() {
            mLongPressing = true;
            MotionEvent e = MotionEvent.obtain(mDownEvent.getDownTime(), mDownEvent.getEventTime() + ViewConfiguration.getLongPressTimeout(), MotionEvent.ACTION_CANCEL, mDownEvent.getX(), mDownEvent.getY(), mDownEvent.getMetaState());
            PtrFrameLayout.super.dispatchTouchEvent(e);
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