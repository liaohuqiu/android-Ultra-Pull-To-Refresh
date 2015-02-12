package in.srain.cube.views.ptr.indicator;

import android.graphics.PointF;

public class PtrIndicator {

    public final static int POS_START = 0;
    protected int mOffsetToRefresh = 0;
    private PointF mPtLastMove = new PointF();
    private float mOffsetX;
    private float mOffsetY;
    private int mCurrentPos = 0;
    private int mLastPos = 0;
    private int mHeaderHeight;
    private int mPressedPos = 0;

    private float mRatioOfHeaderHeightToRefresh = 1.2f;
    private float mResistance = 1.7f;
    private boolean mIsUnderTouch = false;
    private int mOffsetToKeepHeaderWhileLoading = -1;

    public boolean isUnderTouch() {
        return mIsUnderTouch;
    }

    public float getResistance() {
        return mResistance;
    }

    public void setResistance(float resistance) {
        mResistance = resistance;
    }

    public void onRelease() {
        mIsUnderTouch = false;
    }

    public void onUIRefreshComplete() {

    }

    protected void processOnMove(float currentX, float currentY, float offsetX, float offsetY) {
        setOffset(offsetX, offsetY / mResistance);
    }

    public void setRatioOfHeaderHeightToRefresh(float ratio) {
        mRatioOfHeaderHeightToRefresh = ratio;
        mOffsetToRefresh = (int) (mHeaderHeight * ratio);
    }

    public float getRatioOfHeaderToHeightRefresh() {
        return mRatioOfHeaderHeightToRefresh;
    }

    public int getOffsetToRefresh() {
        return mOffsetToRefresh;
    }

    public void setOffsetToRefresh(int offset) {
        mRatioOfHeaderHeightToRefresh = mHeaderHeight / offset;
        mOffsetToRefresh = offset;
    }

    public void onPressDown(float x, float y) {
        mIsUnderTouch = true;
        mPressedPos = mCurrentPos;
        mPtLastMove.set(x, y);
    }

    public final void onMove(float x, float y) {
        float offsetX = x - mPtLastMove.x;
        float offsetY = (y - mPtLastMove.y);
        processOnMove(x, y, offsetX, offsetY);
        mPtLastMove.set(x, y);
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

    public int getLastPosY() {
        return mLastPos;
    }

    public int getCurrentPosY() {
        return mCurrentPos;
    }

    /**
     * Update current position before update the UI
     */
    public final void setCurrentPos(int current) {
        mLastPos = mCurrentPos;
        mCurrentPos = current;
        onUpdatePos(current, mLastPos);
    }

    protected void onUpdatePos(int current, int last) {

    }

    public int getHeaderHeight() {
        return mHeaderHeight;
    }

    public void setHeaderHeight(int height) {
        mHeaderHeight = height;
        updateHeight();
    }

    protected void updateHeight() {
        mOffsetToRefresh = (int) (mRatioOfHeaderHeightToRefresh * mHeaderHeight);
    }

    public void convertFrom(PtrIndicator ptrSlider) {
        mCurrentPos = ptrSlider.mCurrentPos;
        mLastPos = ptrSlider.mLastPos;
        mHeaderHeight = ptrSlider.mHeaderHeight;
    }

    public boolean hasLeftStartPosition() {
        return mCurrentPos > POS_START;
    }

    public boolean hasJustLeftStartPosition() {
        return mLastPos == POS_START && hasLeftStartPosition();
    }

    public boolean hasJustBackToStartPosition() {
        return mLastPos != POS_START && isInStartPosition();
    }

    public boolean isOverOffsetToRefresh() {
        return mCurrentPos >= getOffsetToRefresh();
    }

    public boolean hasMovedAfterPressedDown() {
        return mCurrentPos != mPressedPos;
    }

    public boolean isInStartPosition() {
        return mCurrentPos == POS_START;
    }

    public boolean crossRefreshLineFromTopToBottom() {
        return mLastPos < getOffsetToRefresh() && mCurrentPos >= getOffsetToRefresh();
    }

    public boolean hasJustReachedHeaderHeightFromTopToBottom() {
        return mLastPos < mHeaderHeight && mCurrentPos >= mHeaderHeight;
    }

    public boolean isOverOffsetToKeepHeaderWhileLoading() {
        return mCurrentPos > getOffsetToKeepHeaderWhileLoading();
    }

    public void setOffsetToKeepHeaderWhileLoading(int offset) {
        mOffsetToKeepHeaderWhileLoading = offset;
    }

    public int getOffsetToKeepHeaderWhileLoading() {
        return mOffsetToKeepHeaderWhileLoading >= 0 ? mOffsetToKeepHeaderWhileLoading : mHeaderHeight;
    }

    public boolean isAlreadyHere(int to) {
        return mCurrentPos == to;
    }

    public float getLastPercent() {
        final float oldPercent = mHeaderHeight == 0 ? 0 : mLastPos * 1f / mHeaderHeight;
        return oldPercent;
    }

    public float getCurrentPercent() {
        final float currentPercent = mHeaderHeight == 0 ? 0 : mCurrentPos * 1f / mHeaderHeight;
        return currentPercent;
    }

    public boolean willOverTop(int to) {
        return to < POS_START;
    }
}
