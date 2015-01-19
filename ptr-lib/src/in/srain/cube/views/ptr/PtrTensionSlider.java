package in.srain.cube.views.ptr;

public class PtrTensionSlider extends PtrResistanceSlider {

    private float DRAG_RATE = 0.5f;
    private float mTotalDragDistance;
    private float mCurrentDragPercent;

    public void setTotalDragDistance(float y) {
        mTotalDragDistance = y;
    }

    @Override
    protected void processOnMove(float x, float y) {

        // distance from top
        final float yDiff = getLastY() + y;
        final float scrollTop = yDiff * DRAG_RATE;
        mCurrentDragPercent = scrollTop / mTotalDragDistance;
        if (mCurrentDragPercent < 0) {
            return;
        }

        float boundedDragPercent = Math.min(1f, Math.abs(mCurrentDragPercent));
        float extraOS = Math.abs(scrollTop) - mTotalDragDistance;
        float slingshotDist = mTotalDragDistance;

        // 1 ~ 2
        float tensionSlingshotPercent = Math.max(0,
                Math.min(extraOS, slingshotDist * 2) / slingshotDist);

        float tensionPercent = (float) ((tensionSlingshotPercent / 4) - Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
        float extraMove = (slingshotDist) * tensionPercent / 2;
        int targetY = (int) ((slingshotDist * boundedDragPercent) + extraMove);
        setOffset(x, targetY - getCurrentPos());
    }
}
