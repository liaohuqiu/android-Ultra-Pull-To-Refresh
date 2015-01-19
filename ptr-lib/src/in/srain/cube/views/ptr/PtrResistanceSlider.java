package in.srain.cube.views.ptr;

import android.graphics.PointF;

public class PtrResistanceSlider {

    private float mResistance = 1.7f;
    private PointF mPtLastMove = new PointF();
    private float mOffsetX;
    private float mOffsetY;

    private int mCurrentPos = 0;
    private int mLastPos = 0;

    public float getResistance() {
        return mResistance;
    }

    public void setResistance(float resistance) {
        mResistance = resistance;
    }

    public void onDown(float x, float y) {
        mPtLastMove.set(x, y);
    }

    public final void onMove(float x, float y) {
        float offsetX = x - mPtLastMove.x;
        float offsetY = (y - mPtLastMove.y) / mResistance;
        processOnMove(offsetX, offsetY);
        mPtLastMove.set(x, y);
    }

    public void updatePos(int current, int last) {
        mCurrentPos = current;
        mLastPos = last;
    }

    protected void processOnMove(float x, float y) {
        setOffset(x, y / mResistance);
    }

    protected void setOffset(float x, float y) {
        mOffsetX = x;
        mOffsetY = y;
    }

    public float getOffsetX() {
        return mOffsetX;
    }

    public float getOffsetY() {
        return mOffsetY;
    }

    public int getLastY() {
        return mLastPos;
    }

    public int getCurrentPos() {
        return mCurrentPos;
    }
}
