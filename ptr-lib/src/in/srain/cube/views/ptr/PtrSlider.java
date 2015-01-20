package in.srain.cube.views.ptr;

import android.graphics.PointF;

public abstract class PtrSlider {

    private float mResistance = 1.7f;
    private PointF mPtLastMove = new PointF();
    private float mOffsetX;
    private float mOffsetY;

    private int mCurrentPos = 0;
    private int mLastPos = 0;
    private int mHeaderHeight;

    public void setHeaderHeight(int height) {
        mHeaderHeight = height;
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

    protected abstract void processOnMove(float x, float y);

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

    public int getHeaderHeight() {
        return mHeaderHeight;
    }

    public void convertFrom(PtrSlider ptrSlider) {
        mCurrentPos = ptrSlider.mCurrentPos;
        mLastPos = ptrSlider.mLastPos;
        mHeaderHeight = ptrSlider.mHeaderHeight;
    }
}
