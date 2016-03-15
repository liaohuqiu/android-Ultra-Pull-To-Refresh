package in.srain.cube.views.ptr.demo.ui.header;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import in.srain.cube.views.ptr.demo.R;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

public class RentalsSunDrawable extends Drawable implements Animatable {

    private static final float SCALE_START_PERCENT = 0.3f;
    private static final int ANIMATION_DURATION = 1000;

    private final static float SKY_RATIO = 0.65f;
    private static final float SKY_INITIAL_SCALE = 1.05f;

    private final static float TOWN_RATIO = 0.22f;
    private static final float TOWN_INITIAL_SCALE = 1.20f;
    private static final float TOWN_FINAL_SCALE = 1.30f;

    private static final float SUN_FINAL_SCALE = 0.75f;
    private static final float SUN_INITIAL_ROTATE_GROWTH = 1.2f;
    private static final float SUN_FINAL_ROTATE_GROWTH = 1.5f;

    private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();

    private View mParent;
    private Matrix mMatrix;
    private Animation mAnimation;

    private int mTop;
    private int mScreenWidth;

    private int mSkyHeight;
    private float mSkyTopOffset;
    private float mSkyMoveOffset;

    private int mTownHeight;
    private float mTownInitialTopOffset;
    private float mTownFinalTopOffset;
    private float mTownMoveOffset;

    private int mSunSize = 100;
    private float mSunLeftOffset;
    private float mSunTopOffset;

    private float mPercent = 0.0f;
    private float mRotate = 0.0f;

    private Bitmap mSky;
    private Bitmap mSun;
    private Bitmap mTown;

    private boolean isRefreshing = false;

    private Context mContext;
    private int mTotalDragDistance;

    public RentalsSunDrawable(Context context, View parent) {
        mContext = context;
        mParent = parent;

        mMatrix = new Matrix();

        initiateDimens();
        createBitmaps();
        setupAnimations();
    }

    private Context getContext() {
        return mContext;
    }


    private void initiateDimens() {
        PtrLocalDisplay.init(mContext);
        mTotalDragDistance = PtrLocalDisplay.dp2px(120);

        mScreenWidth = getContext().getResources().getDisplayMetrics().widthPixels;
        mSkyHeight = (int) (SKY_RATIO * mScreenWidth);
        mSkyTopOffset = -(mSkyHeight * 0.28f);
        mSkyMoveOffset = PtrLocalDisplay.designedDP2px(15);

        mTownHeight = (int) (TOWN_RATIO * mScreenWidth);
        mTownInitialTopOffset = (mTotalDragDistance - mTownHeight * TOWN_INITIAL_SCALE) + mTotalDragDistance * .42f;
        mTownFinalTopOffset = (mTotalDragDistance - mTownHeight * TOWN_FINAL_SCALE) + mTotalDragDistance * .42f;
        mTownMoveOffset = PtrLocalDisplay.designedDP2px(10);

        mSunLeftOffset = 0.3f * (float) mScreenWidth;
        mSunTopOffset = (mTotalDragDistance * 0.5f);

        mTop = 0;
    }

    private void createBitmaps() {
        mSky = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.sky);
        mSky = Bitmap.createScaledBitmap(mSky, mScreenWidth, mSkyHeight, true);
        mTown = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.buildings);
        mTown = Bitmap.createScaledBitmap(mTown, mScreenWidth, (int) (mScreenWidth * TOWN_RATIO), true);
        mSun = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.sun);
        mSun = Bitmap.createScaledBitmap(mSun, mSunSize, mSunSize, true);
    }

    public void offsetTopAndBottom(int offset) {
        mTop = offset;
        invalidateSelf();
    }

    @Override
    public void draw(Canvas canvas) {
        final int saveCount = canvas.save();
        canvas.translate(0, mTotalDragDistance - mTop);

        drawSky(canvas);
        drawSun(canvas);
        drawTown(canvas);

        canvas.restoreToCount(saveCount);
    }

    private void drawSky(Canvas canvas) {
        Matrix matrix = mMatrix;
        matrix.reset();
        int y = Math.max(0, mTop - mTotalDragDistance);

        //  0  ~ 1
        float dragPercent = Math.min(1f, Math.abs(mPercent));

        /** Change skyScale between {@link #SKY_INITIAL_SCALE} and 1.0f depending on {@link #mPercent} */
        float skyScale;
        float scalePercentDelta = dragPercent - SCALE_START_PERCENT;

        /** less than {@link SCALE_START_PERCENT} will be {@link SKY_INITIAL_SCALE} */
        if (scalePercentDelta > 0) {
            /** will change from 0 ~ 1 **/
            float scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT);
            skyScale = SKY_INITIAL_SCALE - (SKY_INITIAL_SCALE - 1.0f) * scalePercent;
        } else {
            skyScale = SKY_INITIAL_SCALE;
        }

        float offsetX = -(mScreenWidth * skyScale - mScreenWidth) / 2.0f;

        float offsetY = y + 50 + mSkyTopOffset // Offset canvas moving, goes lower when goes down
                - mSkyHeight * (skyScale - 1.0f) / 2 // Offset sky scaling, lower than 0, will go greater when goes down
                + mSkyMoveOffset * dragPercent; // Give it a little move top -> bottom  // will go greater when goes down

        matrix.postScale(skyScale, skyScale);
        matrix.postTranslate(offsetX, offsetY);
        canvas.drawBitmap(mSky, matrix, null);
    }

    private void drawTown(Canvas canvas) {
        Matrix matrix = mMatrix;
        matrix.reset();

        int y = Math.max(0, mTop - mTotalDragDistance);
        float dragPercent = Math.min(1f, Math.abs(mPercent));

        float townScale;
        float townTopOffset;
        float townMoveOffset;
        float scalePercentDelta = dragPercent - SCALE_START_PERCENT;
        if (scalePercentDelta > 0) {
            /**
             * Change townScale between {@link #TOWN_INITIAL_SCALE} and {@link #TOWN_FINAL_SCALE} depending on {@link #mPercent}
             * Change townTopOffset between {@link #mTownInitialTopOffset} and {@link #mTownFinalTopOffset} depending on {@link #mPercent}
             */
            float scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT);
            townScale = TOWN_INITIAL_SCALE + (TOWN_FINAL_SCALE - TOWN_INITIAL_SCALE) * scalePercent;
            townTopOffset = mTownInitialTopOffset - (mTownFinalTopOffset - mTownInitialTopOffset) * scalePercent;
            townMoveOffset = mTownMoveOffset * (1.0f - scalePercent);
        } else {
            float scalePercent = dragPercent / SCALE_START_PERCENT;
            townScale = TOWN_INITIAL_SCALE;
            townTopOffset = mTownInitialTopOffset;
            townMoveOffset = mTownMoveOffset * scalePercent;
        }

        float offsetX = -(mScreenWidth * townScale - mScreenWidth) / 2.0f;
        // float offsetY = (1.0f - dragPercent) * mTotalDragDistance // Offset canvas moving
        float offsetY = y +
                +townTopOffset
                - mTownHeight * (townScale - 1.0f) / 2 // Offset town scaling
                + townMoveOffset; // Give it a little move

        matrix.postScale(townScale, townScale);
        matrix.postTranslate(offsetX, offsetY);

        canvas.drawBitmap(mTown, matrix, null);
    }

    private void drawSun(Canvas canvas) {
        Matrix matrix = mMatrix;
        matrix.reset();

        float dragPercent = mPercent;
        if (dragPercent > 1.0f) { // Slow down if pulling over set height
            dragPercent = (dragPercent + 9.0f) / 10;
        }

        float sunRadius = (float) mSunSize / 2.0f;
        float sunRotateGrowth = SUN_INITIAL_ROTATE_GROWTH;

        float offsetX = mSunLeftOffset;
        float offsetY = mSunTopOffset
                + (mTotalDragDistance / 2) * (1.0f - dragPercent); // Move the sun up

        float scalePercentDelta = dragPercent - SCALE_START_PERCENT;
        if (scalePercentDelta > 0) {
            float scalePercent = scalePercentDelta / (1.0f - SCALE_START_PERCENT);
            float sunScale = 1.0f - (1.0f - SUN_FINAL_SCALE) * scalePercent;
            sunRotateGrowth += (SUN_FINAL_ROTATE_GROWTH - SUN_INITIAL_ROTATE_GROWTH) * scalePercent;

            matrix.preTranslate(offsetX + (sunRadius - sunRadius * sunScale), offsetY * (2.0f - sunScale));
            matrix.preScale(sunScale, sunScale);

            offsetX += sunRadius;
            offsetY = offsetY * (2.0f - sunScale) + sunRadius * sunScale;
        } else {
            matrix.postTranslate(offsetX, offsetY);

            offsetX += sunRadius;
            offsetY += sunRadius;
        }

        float r = (isRefreshing ? -360 : 360) * mRotate * (isRefreshing ? 1 : sunRotateGrowth);
        matrix.postRotate(r, offsetX, offsetY);

        canvas.drawBitmap(mSun, matrix, null);
    }

    public void setPercent(float percent) {
        mPercent = percent;
        setRotate(percent);
    }

    public void setRotate(float rotate) {
        mRotate = rotate;
        mParent.invalidate();
        invalidateSelf();
    }

    public void resetOriginals() {
        setPercent(0);
        setRotate(0);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, mSkyHeight + top);
    }

    @Override
    public void setAlpha(int alpha) {

    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public boolean isRunning() {
        return false;
    }

    @Override
    public void start() {
        mAnimation.reset();
        isRefreshing = true;
        mParent.startAnimation(mAnimation);
    }

    @Override
    public void stop() {
        mParent.clearAnimation();
        isRefreshing = false;
        resetOriginals();
    }

    private void setupAnimations() {
        mAnimation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                setRotate(interpolatedTime);
            }
        };
        mAnimation.setRepeatCount(Animation.INFINITE);
        mAnimation.setRepeatMode(Animation.RESTART);
        mAnimation.setInterpolator(LINEAR_INTERPOLATOR);
        mAnimation.setDuration(ANIMATION_DURATION);
    }

    public int getTotalDragDistance() {
        return mTotalDragDistance;
    }
}
