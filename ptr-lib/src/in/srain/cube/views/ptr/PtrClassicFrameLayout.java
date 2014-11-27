package in.srain.cube.views.ptr;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;

public class PtrClassicFrameLayout extends PtrFrameLayout {

    protected View mRotateView;
    private int mRotateViewId = 0;
    private int mRotateAniTime = 150;
    private RotateAnimation mFlipAnimation;
    private RotateAnimation mReverseFlipAnimation;
    private IPtrClassicHeader mIPtrClassicHeader;

    public PtrClassicFrameLayout(Context context) {
        super(context);
        init(null);
    }

    public PtrClassicFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public PtrClassicFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    protected void init(AttributeSet attrs) {
        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.PtrRotatableHeader, 0, 0);
        if (arr != null) {
            mRotateViewId = arr.getResourceId(R.styleable.PtrRotatableHeader_ptr_rotate_view, mRotateViewId);
            mRotateAniTime = arr.getInt(R.styleable.PtrRotatableHeader_ptr_rotate_ani_time, mRotateAniTime);
        }
        buildAnimation();
    }

    public void setRotateAniTime(int time) {
        if (time == mRotateAniTime || time == 0) {
            return;
        }
        mRotateAniTime = time;
        buildAnimation();
    }

    private void buildAnimation() {
        mFlipAnimation = new RotateAnimation(0, -180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        mFlipAnimation.setDuration(mRotateAniTime);
        mFlipAnimation.setFillAfter(true);

        mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(mRotateAniTime);
        mReverseFlipAnimation.setFillAfter(true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mRotateViewId != 0) {
            mRotateView = findViewById(mRotateViewId);
        }
        ensureHeader();
    }

    protected void ensureHeader() {
        View view = getHeaderView();
        if (view == null) {
            view = new PtrClassicDefaultHeader(getContext());
            setPtrHeaderView(view);
        }
        setRotateHeader(view);
    }

    public void setLastUpdateTimeKey(String key) {
        if (mIPtrClassicHeader != null) {
            mIPtrClassicHeader.setLastUpdateTimeKey(key);
        }
    }

    public void setRotateHeader(View view) {
        if ((view instanceof PtrUIHandler)) {
            setPtrUIHandler((PtrUIHandler) view);
        } else {
            throw new RuntimeException("Header should implement PtrUIHandler");
        }
        if ((view instanceof IPtrClassicHeader)) {
            mIPtrClassicHeader = (IPtrClassicHeader) view;
            mRotateView = mIPtrClassicHeader.getRotateView();
        } else {
            throw new RuntimeException("Header should implement IRotatableHeader");
        }
    }

    @Override
    protected void onPositionChange(boolean inUnderTouch, byte status, int lastPos, int currentPos, float lastPercent, float currentPercent) {
        if (currentPos < mOffsetToRefresh && lastPos >= mOffsetToRefresh) {
            if (inUnderTouch && status == PTR_STATUS_PREPARE) {
                mIPtrClassicHeader.crossRotateLineFromBottomUnderTouch(this);
                if (mRotateView != null) {
                    mRotateView.clearAnimation();
                    mRotateView.startAnimation(mReverseFlipAnimation);
                }
            }
        } else if (currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh) {
            if (inUnderTouch && status == PTR_STATUS_PREPARE) {
                mIPtrClassicHeader.crossRotateLineFromTopUnderTouch(this);
                if (mRotateView != null) {
                    mRotateView.clearAnimation();
                    mRotateView.startAnimation(mFlipAnimation);
                }
            }
        }
    }
}
