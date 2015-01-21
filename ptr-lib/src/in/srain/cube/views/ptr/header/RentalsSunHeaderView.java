package in.srain.cube.views.ptr.header;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.indicator.PtrTensionIndicator;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

public class RentalsSunHeaderView extends View implements PtrUIHandler {

    private RentalsDrawable mDrawable;
    private PtrFrameLayout mPtrFrameLayout;

    public RentalsSunHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public RentalsSunHeaderView(Context context) {
        super(context);
        init();
    }

    public RentalsSunHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setUp(PtrFrameLayout ptrFrameLayout) {
        mPtrFrameLayout = ptrFrameLayout;
        PtrTensionIndicator slider = new PtrTensionIndicator();
        mPtrFrameLayout.setPtrIndicator(slider);
    }

    private void init() {
        mDrawable = new RentalsDrawable(getContext(), this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = mDrawable.getIntrinsicHeight() + getPaddingTop() + getPaddingBottom();
        height = PtrLocalDisplay.dp2px(120);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        // mPtrFrameLayout.setMaxPullDistance((int) (mPtrFrameLayout.getHeaderHeight()));
        mDrawable.setTotalDragDistance(bottom - top);
        mDrawable.setBounds(0, 0, right - left, bottom - top);
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        mDrawable.resetOriginals();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mDrawable.draw(canvas);
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {

    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {

    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

        float percent = Math.min(1f, ptrIndicator.getCurrentPercent());

        if (status == PtrFrameLayout.PTR_STATUS_PREPARE) {
            mDrawable.offsetTopAndBottom(ptrIndicator.getCurrentPosY());
            mDrawable.setPercent(percent);
            invalidate();
        }
    }

    @Override
    public void invalidateDrawable(Drawable dr) {
        if (dr == mDrawable) {
            invalidate();
        } else {
            super.invalidateDrawable(dr);
        }
    }
}
