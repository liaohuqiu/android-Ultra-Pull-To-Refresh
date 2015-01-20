package in.srain.cube.views.ptr;

import in.srain.cube.util.CLog;

public class PtrTensionSlider extends PtrResistanceSlider {

    private float DRAG_RATE = 0.5f;

    @Override
    protected void processOnMove(float x, float y) {

        final float mTotalDragDistance = getHeaderHeight();

        // distance from top
        final float yDiff = y - getLastY();
        final float scrollTop = yDiff * DRAG_RATE + getCurrentPos();
        final float mCurrentDragPercent = scrollTop / mTotalDragDistance;

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

        CLog.d("slider", "processOnMove:%s scrollTop: %s, targetY: %s", getCurrentPos(), scrollTop, targetY);
        setOffset(x, targetY - getCurrentPos());
    }
}
