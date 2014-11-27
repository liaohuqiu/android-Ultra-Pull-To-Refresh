package in.srain.cube.views.ptr.header;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Transformation;
import in.srain.cube.util.CLog;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

import java.util.ArrayList;

public class StoreHouseHeader extends View {

    public ArrayList<StoreHouseBarItem> mItemList = new ArrayList<StoreHouseBarItem>();

    private int lineWidth = PtrLocalDisplay.dp2px(1);
    private float scale = 1;
    private int mDropHeight = PtrLocalDisplay.dp2px(40);
    private float internalAnimationFactor = 0.7f;
    private int horizontalRandomness = 850;

    // private int[] startX = new int[]{0, 30, 52, 26, 60, 60, 85, 108, 108, 147, 177, 147, 176, 206, 228, 202};
    // private int[] startY = new int[]{30, 0, 30, 30, 15, 30, 15, 0, 30, 0, 0, 32, 30, 0, 30, 30};
    // private int[] endX = new int[]{22, 22, 30, 0, 60, 60, 60, 85, 85, 117, 147, 147, 198, 198, 206, 176};
    // private int[] endY = new int[]{0, 0, 0, 30, 0, 15, 15, 15, 15, 0, 0, 0, 0, 0, 0, 30};

    private int[] startX = new int[]{50, 0, 0, 50, 50, 62, 62, 110, 110, 62, 62, 147, 147, 123, 171, 123, 181, 208, 181, 246, 246, 292};
    private int[] startY = new int[]{0, 0, 36, 36, 72, 0, 0, 0, 36, 36, 36, 1, 1, 23, 23, 49, 1, 1, 72, 0, 0, 72};
    private int[] endX = new int[]{0, 0, 50, 50, 0, 62, 110, 110, 62, 62, 110, 123, 171, 123, 171, 171, 232, 208, 232, 246, 292, 292};
    private int[] endY = new int[]{0, 36, 36, 72, 72, 36, 0, 36, 36, 72, 72, 23, 23, 72, 72, 49, 1, 72, 72, 72, 72, 0};


    private float mProgress = 0;

    private int mDrawWidth = 0;
    private int mDrawHeight = 0;
    private int mBoundHeight;
    private int mBoundsWidth;
    private int mOffsetX = 0;
    private int mOffsetY = 0;
    private float mBarDarkAlpha = 0.4f;

    private Transformation mTransformation = new Transformation();
    private boolean mIsInLoading = false;

    public StoreHouseHeader(Context context) {
        super(context);
        init();
    }

    public StoreHouseHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void setProgress(float progress) {
        mProgress = progress;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setBounds(getMeasuredWidth(), getMeasuredHeight());
    }

    public void setBounds(int width, int height) {
        mBoundsWidth = width;
        mBoundHeight = height;

        mOffsetX = (mBoundsWidth - mDrawWidth) / 2;
        mOffsetY = (mBoundHeight - mDrawHeight) / 2;
    }

    public void initWithString(String str) {

    }

    public void initWithStringArray(int id) {

    }

    public void initWithPointArray(ArrayList<float[]> pointList) {

    }

    private void init() {
        // ArrayList<float[]> pointList = StoreHousePath.getPath("ABCDEFGHIJKLMNOPQRSTUVWXYZ", 0.2f, 14);
        // ArrayList<float[]> pointList = StoreHousePath.getPath("ALIBABA", 0.2f, 14);
        // ArrayList<float[]> pointList = StoreHousePath.getPath("SRAIN", 0.2f, 14);
        ArrayList<float[]> pointList = StoreHousePath.getPath("ETAO", 0.2f, 14);
        //ArrayList<float[]> pointList = StoreHousePath.getPath("cube", 0.5f, 14);

        float drawWidth = 0;
        float drawHeight = 0;
        for (int i = 0; i < pointList.size(); i++) {
            float[] line = pointList.get(i);
            if (line[0] > drawWidth) {
                drawWidth = line[0];
            }
            if (line[1] > drawWidth) {
                drawWidth = line[1];
            }
            if (line[2] > drawHeight) {
                drawHeight = line[2];
            }
            if (line[3] > drawHeight) {
                drawHeight = line[3];
            }
        }
        mDrawWidth = PtrLocalDisplay.dp2px(drawWidth);
        mDrawHeight = PtrLocalDisplay.dp2px(mDrawHeight);

        /*
        for (int i = 0; i < startX.length; i++) {
            startX[i] = LocalDisplay.dp2px(startX[i] / 2);
            startY[i] = LocalDisplay.dp2px(startY[i] / 2);
            endX[i] = LocalDisplay.dp2px(endX[i] / 2);
            endY[i] = LocalDisplay.dp2px(endY[i] / 2);

            if (startX[i] > mDrawWidth) {
                mDrawWidth = startX[i];
            }
            if (endX[i] > mDrawWidth) {
                mDrawWidth = endX[i];
            }
            if (startY[i] > mDrawHeight) {
                mDrawHeight = startY[i];
            }
            if (endY[i] > mDrawHeight) {
                mDrawHeight = endY[i];
            }
        }
        */

        for (int i = 0; i < pointList.size(); i++) {
            float[] line = pointList.get(i);
            PointF startPoint = new PointF(PtrLocalDisplay.dp2px(line[0]), PtrLocalDisplay.dp2px(line[1]));
            PointF endPoint = new PointF(PtrLocalDisplay.dp2px(line[2]), PtrLocalDisplay.dp2px(line[3]));

            StoreHouseBarItem item = new StoreHouseBarItem(i, startPoint, endPoint, Color.WHITE, lineWidth);
            item.reset(horizontalRandomness);
            mItemList.add(item);
        }
    }

    public void beginLoading() {
        mIsInLoading = true;
        for (int i = 0; i < mItemList.size(); i++) {
            StoreHouseBarItem item = mItemList.get(i);
            item.setFillAfter(false);
            item.setFillEnabled(true);
            item.setFillBefore(false);
            item.setStartOffset(100 * i);
            item.setDuration(400);
            item.start();
        }
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mIsInLoading) {
                    beginLoading();
                }
            }
        }, mItemList.size() * 100);
        CLog.d("ptr-test", "beginLoading");
        invalidate();
    }

    public void loadFinish() {
        mIsInLoading = false;
        CLog.d("ptr-test", "loadFinish");
    }

    @Override
    public void onDraw(Canvas canvas) {
        float progress = mProgress;
        int c1 = canvas.save();
        int len = mItemList.size();

        for (int i = 0; i < mItemList.size(); i++) {

            canvas.save();
            StoreHouseBarItem storeHouseBarItem = mItemList.get(i);
            float offsetX = mOffsetX + storeHouseBarItem.midPoint.x;
            float offsetY = mOffsetY + storeHouseBarItem.midPoint.y;

            if (mIsInLoading) {
                storeHouseBarItem.getTransformation(getDrawingTime(), mTransformation);
                canvas.translate(offsetX, offsetY);
            } else {

                if (progress == 0) {
                    storeHouseBarItem.reset(horizontalRandomness);
                    continue;
                }

                float startPadding = (1 - internalAnimationFactor) * i / len;
                float endPadding = 1 - internalAnimationFactor - startPadding;

                // done
                if (progress == 1 || progress >= 1 - endPadding) {
                    canvas.translate(offsetX, offsetY);
                    storeHouseBarItem.setAlpha(mBarDarkAlpha);
                } else {
                    float realProgress;
                    if (progress <= startPadding) {
                        realProgress = 0;
                    } else {
                        realProgress = Math.min(1, (progress - startPadding) / internalAnimationFactor);
                    }
                    offsetX += storeHouseBarItem.translationX * (1 - realProgress);
                    offsetY += -mDropHeight * (1 - realProgress);
                    Matrix matrix = new Matrix();
                    matrix.postRotate((float) (360 * realProgress));
                    matrix.postScale(realProgress, realProgress);
                    matrix.postTranslate(offsetX, offsetY);
                    storeHouseBarItem.setAlpha(mBarDarkAlpha * realProgress);
                    canvas.concat(matrix);
                }
            }
            storeHouseBarItem.draw(canvas);
            canvas.restore();
        }
        if (mIsInLoading) {
            invalidate();
        }
        canvas.restoreToCount(c1);
    }
}