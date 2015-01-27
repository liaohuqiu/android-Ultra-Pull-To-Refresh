package in.srain.cube.views.ptr.indicator;

import in.srain.cube.util.CLog;

public class PtrTensionIndicator extends PtrIndicator {

    private float DRAG_RATE = 1f;
    private float mDownY;
    private float mDownPos;

    private float mCurrentDragPercent;

    @Override
    public void onPressDown(float x, float y) {
        super.onPressDown(x, y);
        mDownY = y;
        mDownPos = getCurrentPosY();
    }

    @Override
    protected void processOnMove(float currentX, float currentY, float offsetX, float offsetY) {
        final float oneHeight = getHeaderHeight() * 4 / 5;

        // distance from top
        final float scrollTop = (currentY - mDownY) * DRAG_RATE + mDownPos;
        final float currentDragPercent = scrollTop / oneHeight;

        if (currentDragPercent < 0) {
            setOffset(currentX, 0);
            return;
        }

        mCurrentDragPercent = currentDragPercent;
        float boundedDragPercent = Math.min(1f, Math.abs(currentDragPercent));
        float extraOS = scrollTop - oneHeight;

        // 0 ~ 2
        float tensionSlingshotPercent = Math.max(0,
                Math.min(extraOS, oneHeight * 2) / oneHeight);

        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
        float extraMove = (oneHeight) * tensionPercent / 2;
        int targetY = (int) ((oneHeight * boundedDragPercent) + extraMove);
        int change = targetY - getCurrentPosY();

        CLog.d("test-ten", "%s %s %s %s %s %s", mCurrentDragPercent, extraOS, tensionSlingshotPercent, extraMove, targetY, oneHeight);
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

    @Override
    public float getCurrentPercent() {
        return mCurrentDragPercent;
    }
}
