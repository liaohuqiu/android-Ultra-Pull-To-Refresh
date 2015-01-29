package in.srain.cube.views.ptr.indicator;

public class PtrTensionIndicator extends PtrIndicator {

    private float DRAG_RATE = 0.5f;
    private float mDownY;
    private float mDownPos;

    private float mCurrentDragPercent;

    private int mReleasePos;
    private float mReleasePercent = -1;

    @Override
    public void onPressDown(float x, float y) {
        super.onPressDown(x, y);
        mDownY = y;
        mDownPos = getCurrentPosY();
    }

    @Override
    public void onRelease() {
        super.onRelease();
        mReleasePos = getCurrentPosY();
        mReleasePercent = mCurrentDragPercent;
    }

    @Override
    public void onUIRefreshComplete() {
        mReleasePos = getCurrentPosY();
        mReleasePercent = getOverDragPercent();
    }

    @Override
    protected void processOnMove(float currentX, float currentY, float offsetX, float offsetY) {
        final float oneHeight = getHeaderHeight() * 4 / 5;

        // distance from top
        final float scrollTop = (currentY - mDownY) * DRAG_RATE + mDownPos;
        final float currentDragPercent = scrollTop / oneHeight;

        if (currentDragPercent < 0) {
            setOffset(offsetX, 0);
            return;
        }

        mCurrentDragPercent = currentDragPercent;
        float boundedDragPercent = Math.min(1f, Math.abs(currentDragPercent));
        float extraOS = scrollTop - oneHeight;

        // 0 ~ 2
        // if extraOS lower than 0, which means scrollTop lower than onHeight, tensionSlingshotPercent will be 0.
        float tensionSlingshotPercent = Math.max(0,
                Math.min(extraOS, oneHeight * 2) / oneHeight);

        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
        float extraMove = (oneHeight) * tensionPercent / 2;
        int targetY = (int) ((oneHeight * boundedDragPercent) + extraMove);
        int change = targetY - getCurrentPosY();

        setOffset(currentX, change);
    }

    @Override
    public int getHeightOfHeaderWhileLoading() {
        return getOffsetToRefresh();
    }

    @Override
    public int getOffsetToRefresh() {
        final float oneHeight = getHeaderHeight() * 4 / 5;
        return (int) oneHeight;
    }

    public float getOverDragPercent() {
        if (isUnderTouch()) {
            return mCurrentDragPercent;
        } else {
            if (mReleasePercent <= 0) {
                return 1.0f * getCurrentPosY() / getHeightOfHeaderWhileLoading();
            }
            // after release
            return mReleasePercent * getCurrentPosY() / mReleasePos;
        }
    }
}
