package in.srain.cube.views.ptr.indicator;

public class PtrTensionIndicator extends PtrIndicator {

    private float DRAG_RATE = 1f;
    private float mDownY;
    private float mDownPos;

    @Override
    public void onPressDown(float x, float y) {
        super.onPressDown(x, y);
        mDownY = y;
        mDownPos = getCurrentPosY();
    }

    @Override
    protected void processOnMove(float currentX, float currentY, float offsetX, float offsetY) {
        final float oneHeight = getHeaderHeight() / 2;

        // distance from top
        final float scrollTop = (currentY - mDownY) * DRAG_RATE + mDownPos;
        final float mCurrentDragPercent = scrollTop / oneHeight;

        if (mCurrentDragPercent < 0) {
            setOffset(currentX, 0);
            return;
        }

        float boundedDragPercent = Math.min(1f, Math.abs(mCurrentDragPercent));
        float extraOS = scrollTop - oneHeight;

        // 0 ~ 2
        float tensionSlingshotPercent = Math.max(0,
                Math.min(extraOS, oneHeight * 2) / oneHeight);

        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 4f;
        float extraMove = (oneHeight) * tensionPercent;
        int targetY = (int) ((oneHeight * boundedDragPercent) + extraMove);
        int change = targetY - getCurrentPosY();

        setOffset(currentX, change);
    }
}
