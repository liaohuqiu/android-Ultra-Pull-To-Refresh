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
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

import java.util.ArrayList;

public class StoreHouseHeader extends View implements PtrUIHandler {

    public ArrayList<StoreHouseBarItem> mItemList = new ArrayList<StoreHouseBarItem>();

    private int lineWidth = PtrLocalDisplay.dp2px(1);
    private float scale = 1;
    private int mDropHeight = PtrLocalDisplay.dp2px(40);
    private float internalAnimationFactor = 0.7f;
    private int horizontalRandomness = 850;

    private float mProgress = 0;

    private int mDrawZoneWidth = 0;
    private int mDrawZoneHeight = 0;
    private int mBoundHeight;
    private int mBoundsWidth;
    private int mOffsetX = 0;
    private int mOffsetY = 0;
    private float mBarDarkAlpha = 0.4f;

    private Transformation mTransformation = new Transformation();
    private boolean mIsInLoading = false;

    public StoreHouseHeader(Context context) {
        super(context);
    }

    public StoreHouseHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StoreHouseHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setProgress(float progress) {
        mProgress = progress;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = (int) (getPaddingTop() + getPaddingBottom() + mDrawZoneHeight * 3);
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setBounds(getMeasuredWidth(), getMeasuredHeight());
    }

    private void setBounds(int width, int height) {
        mBoundsWidth = width;
        mBoundHeight = height;
        mOffsetX = (mBoundsWidth - mDrawZoneWidth) / 2;
        mOffsetY = (int) (mDrawZoneHeight * 1.5);
    }

    public void initWithString(String str) {
        // ArrayList<float[]> pointList = StoreHousePath.getPath("ABCDEFGHIJKLMNOPQRSTUVWXYZ", 0.2f, 14);
        // ArrayList<float[]> pointList = StoreHousePath.getPath("ALIBABA", 0.2f, 14);
        // ArrayList<float[]> pointList = StoreHousePath.getPath("SRAIN", 0.2f, 14);
        ArrayList<float[]> pointList = StoreHousePath.getPath("Alibaba", 0.25f, 14);
        //ArrayList<float[]> pointList = StoreHousePath.getPath("cube", 0.5f, 14);
        initWithPointArray(pointList);
    }

    public void initWithStringArray(int id) {
        String[] points = getResources().getStringArray(id);
        ArrayList<float[]> pointList = new ArrayList<float[]>();
        for (int i = 0; i < points.length; i++) {
            String[] x = points[i].split(",");
            float[] f = new float[4];
            for (int j = 0; j < 4; j++) {
                f[j] = Float.parseFloat(x[j]);
            }
            pointList.add(f);
        }
        initWithPointArray(pointList);
    }

    public void initWithPointArray(ArrayList<float[]> pointList) {

        float drawWidth = 0;
        float drawHeight = 0;
        mItemList.clear();
        for (int i = 0; i < pointList.size(); i++) {
            float[] line = pointList.get(i);
            PointF startPoint = new PointF(PtrLocalDisplay.dp2px(line[0]), PtrLocalDisplay.dp2px(line[1]));
            PointF endPoint = new PointF(PtrLocalDisplay.dp2px(line[2]), PtrLocalDisplay.dp2px(line[3]));

            drawWidth = Math.max(drawWidth, startPoint.x);
            drawWidth = Math.max(drawWidth, endPoint.x);

            drawHeight = Math.max(drawHeight, startPoint.y);
            drawHeight = Math.max(drawHeight, endPoint.y);

            StoreHouseBarItem item = new StoreHouseBarItem(i, startPoint, endPoint, Color.WHITE, lineWidth);
            item.reset(horizontalRandomness);
            mItemList.add(item);
        }
        mDrawZoneWidth = (int) Math.ceil(drawWidth);
        mDrawZoneHeight = (int) Math.ceil(drawHeight);
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
        /*
        postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mIsInLoading) {
                    beginLoading();
                }
            }
        }, mItemList.size() * 100);
        */
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

    /**
     * When the content view has reached top and refresh has been completed, view will be reset.
     *
     * @param frame
     */
    @Override
    public void onUIReset(PtrFrameLayout frame) {
        loadFinish();
    }

    /**
     * prepare for loading
     *
     * @param frame
     * @param isAutoRefresh
     */
    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame, boolean isAutoRefresh) {

    }

    /**
     * perform refreshing UI
     *
     * @param frame
     */
    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        beginLoading();
    }

    /**
     * perform UI after refresh
     *
     * @param frame
     */
    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        loadFinish();
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, int oldPosition, int currentPosition, float oldPercent, float currentPercent) {
        float f = currentPosition * 1f / getMeasuredHeight();
        if (f > 1) f = 1;
        CLog.d("ptr-test", "onPositionChange: %s %s", currentPosition, getMeasuredHeight(), getHeight());
        setProgress(f);
        invalidate();
    }
}