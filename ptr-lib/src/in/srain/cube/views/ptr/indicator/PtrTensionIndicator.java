package in.srain.cube.views.ptr.indicator;

public class PtrTensionIndicator extends PtrIndicator {

    private float DRAG_RATE = 0.5f;
    private float mDownY;
    private float mDownPos;
    private float mOneHeight = 0;

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
    public void setHeaderHeight(int height) {
        super.setHeaderHeight(height);
        mOneHeight = height * 4f / 5;
    }

    @Override
    protected void processOnMove(float currentX, float currentY, float offsetX, float offsetY) {

        if (currentY < mDownY) {
            super.processOnMove(currentX, currentY, offsetX, offsetY);
            return;
        }

        // distance from top
        final float scrollTop = (currentY - mDownY) * DRAG_RATE + mDownPos;
        final float currentDragPercent = scrollTop / mOneHeight;

        if (currentDragPercent < 0) {
            setOffset(offsetX, 0);
            return;
        }

        mCurrentDragPercent = currentDragPercent;

        // 0 ~ 1
        float boundedDragPercent = Math.min(1f, Math.abs(currentDragPercent));
        float extraOS = scrollTop - mOneHeight;

        // 0 ~ 2
        // if extraOS lower than 0, which means scrollTop lower than onHeight, tensionSlingshotPercent will be 0.
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, mOneHeight * 2) / mOneHeight);

        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
        float extraMove = (mOneHeight) * tensionPercent / 2;
        int targetY = (int) ((mOneHeight * boundedDragPercent) + extraMove);
        int change = targetY - getCurrentPosY();

        setOffset(currentX, change);
    }

    private float offsetToTarget(float scrollTop) {

        // distance from top
        final float currentDragPercent = scrollTop / mOneHeight;

        mCurrentDragPercent = currentDragPercent;

        // 0 ~ 1
        float boundedDragPercent = Math.min(1f, Math.abs(currentDragPercent));
        float extraOS = scrollTop - mOneHeight;

        // 0 ~ 2
        // if extraOS lower than 0, which means scrollTop lower than mOneHeight, tensionSlingshotPercent will be 0.
        float tensionSlingshotPercent = Math.max(0, Math.min(extraOS, mOneHeight * 2) / mOneHeight);

        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
        float extraMove = (mOneHeight) * tensionPercent / 2;
        int targetY = (int) ((mOneHeight * boundedDragPercent) + extraMove);

        return 0;
    }

    @Override
    public int getOffsetToKeepHeaderWhileLoading() {
        return getOffsetToRefresh();
    }

    @Override
    public int getOffsetToRefresh() {
        return (int) mOneHeight;
    }

    public float getOverDragPercent() {
        if (isUnderTouch()) {
            return mCurrentDragPercent;
        } else {
            if (mReleasePercent <= 0) {
                return 1.0f * getCurrentPosY() / getOffsetToKeepHeaderWhileLoading();
            }
            // after release
            return mReleasePercent * getCurrentPosY() / mReleasePos;
        }
    }
}
