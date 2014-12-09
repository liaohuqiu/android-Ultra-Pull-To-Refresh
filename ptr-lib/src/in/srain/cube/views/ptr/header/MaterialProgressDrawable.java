/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package in.srain.cube.views.ptr.header;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.Paint.Style;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.*;
import android.view.animation.Interpolator;
import in.srain.cube.views.ptr.util.PtrLocalDisplay;

import java.util.ArrayList;

/**
 * Fancy progress indicator for Material theme.
 * It's taken from {@link android.support.v4.widget}.
 * I've done some slight changes.
 *
 * @hide
 */
public class MaterialProgressDrawable extends Drawable implements Animatable {

    // Maps to ProgressBar.Large style
    public static final int LARGE = 0;
    // Maps to ProgressBar default style
    public static final int DEFAULT = 1;
    private static final Interpolator LINEAR_INTERPOLATOR = new LinearInterpolator();
    private static final Interpolator END_CURVE_INTERPOLATOR = new EndCurveInterpolator();
    private static final Interpolator START_CURVE_INTERPOLATOR = new StartCurveInterpolator();
    private static final Interpolator EASE_INTERPOLATOR = new AccelerateDecelerateInterpolator();
    // Maps to ProgressBar default style
    private static final int CIRCLE_DIAMETER = 40;
    private static final float CENTER_RADIUS = 8.75f; //should add up to 10 when + stroke_width
    private static final float STROKE_WIDTH = 2.5f;
    // Maps to ProgressBar.Large style
    private static final int CIRCLE_DIAMETER_LARGE = 56;
    private static final float CENTER_RADIUS_LARGE = 12.5f;
    private static final float STROKE_WIDTH_LARGE = 3f;
    /**
     * The duration of a single progress spin in milliseconds.
     */
    private static final int ANIMATION_DURATION = 1000 * 80 / 60;
    /**
     * The number of points in the progress "star".
     */
    private static final float NUM_POINTS = 5f;
    /**
     * Layout info for the arrowhead in dp
     */
    private static final int ARROW_WIDTH = 10;
    private static final int ARROW_HEIGHT = 5;
    private static final float ARROW_OFFSET_ANGLE = 5;
    /**
     * Layout info for the arrowhead for the large spinner in dp
     */
    private static final int ARROW_WIDTH_LARGE = 12;
    private static final int ARROW_HEIGHT_LARGE = 6;
    private static final float MAX_PROGRESS_ARC = .8f;
    private static final int KEY_SHADOW_COLOR = 0x1E000000;
    private static final int FILL_SHADOW_COLOR = 0x3D000000;
    private static final float SHADOW_RADIUS = 3.5f;
    private static final float X_OFFSET = 0f;
    private static final float Y_OFFSET = 1.75f;
    private final int[] COLORS = new int[]{
            0xFFC93437,
            0xFF375BF1,
            0xFFF7D23E,
            0xFF34A350
    };
    /**
     * The list of animators operating on this drawable.
     */
    private final ArrayList<Animation> mAnimators = new ArrayList<Animation>();
    /**
     * The indicator ring, used to manage animation state.
     */
    private final Ring mRing;
    private final Callback mCallback = new Callback() {
        @Override
        public void invalidateDrawable(Drawable d) {
            invalidateSelf();
        }

        @Override
        public void scheduleDrawable(Drawable d, Runnable what, long when) {
            scheduleSelf(what, when);
        }

        @Override
        public void unscheduleDrawable(Drawable d, Runnable what) {
            unscheduleSelf(what);
        }
    };
    /**
     * Canvas rotation in degrees.
     */
    private float mRotation;
    private Resources mResources;
    private View mParent;
    private Animation mAnimation;
    private float mRotationCount;
    private double mWidth;
    private double mHeight;
    private Animation mFinishAnimation;
    private int mBackgroundColor;
    private ShapeDrawable mShadow;

    public MaterialProgressDrawable(Context context, View parent) {
        mParent = parent;
        mResources = context.getResources();
        mRing = new Ring(mCallback);
        mRing.setColors(COLORS);
        updateSizes(DEFAULT);
        setupAnimators();
    }

    private void setSizeParameters(double progressCircleWidth, double progressCircleHeight,
                                   double centerRadius, double strokeWidth, float arrowWidth, float arrowHeight) {
        final Ring ring = mRing;
        final DisplayMetrics metrics = mResources.getDisplayMetrics();
        final float screenDensity = metrics.density;
        mWidth = progressCircleWidth * screenDensity;
        mHeight = progressCircleHeight * screenDensity;
        ring.setStrokeWidth((float) strokeWidth * screenDensity);
        ring.setCenterRadius(centerRadius * screenDensity);
        ring.setColorIndex(0);
        ring.setArrowDimensions(arrowWidth * screenDensity, arrowHeight * screenDensity);
        ring.setInsets((int) mWidth, (int) mHeight);
        setUp(mWidth);
    }

    private void setUp(final double diameter) {
        final int shadowYOffset = PtrLocalDisplay.dp2px(Y_OFFSET);
        final int shadowXOffset = PtrLocalDisplay.dp2px(X_OFFSET);
        int mShadowRadius = PtrLocalDisplay.dp2px(SHADOW_RADIUS);
        OvalShape oval = new OvalShadow(mShadowRadius, (int) diameter);
        mShadow = new ShapeDrawable(oval);
        if (Build.VERSION.SDK_INT >= 11) {
            mParent.setLayerType(View.LAYER_TYPE_SOFTWARE, mShadow.getPaint());
        }
        mShadow.getPaint().setShadowLayer(mShadowRadius, shadowXOffset, shadowYOffset, KEY_SHADOW_COLOR);
    }

    /**
     * Set the overall size for the progress spinner. This updates the radius
     * and stroke width of the ring.
     *
     * @param size One of {@link MaterialProgressDrawable#LARGE} or
     *             {@link MaterialProgressDrawable#DEFAULT}
     */
    public void updateSizes(int size) {
        if (size == LARGE) {
            setSizeParameters(CIRCLE_DIAMETER_LARGE, CIRCLE_DIAMETER_LARGE, CENTER_RADIUS_LARGE,
                    STROKE_WIDTH_LARGE, ARROW_WIDTH_LARGE, ARROW_HEIGHT_LARGE);
        } else {
            setSizeParameters(CIRCLE_DIAMETER, CIRCLE_DIAMETER, CENTER_RADIUS, STROKE_WIDTH,
                    ARROW_WIDTH, ARROW_HEIGHT);
        }
    }

    /**
     * @param show Set to true to display the arrowhead on the progress spinner.
     */
    public void showArrow(boolean show) {
        mRing.setShowArrow(show);
    }

    /**
     * @param scale Set the scale of the arrowhead for the spinner.
     */
    public void setArrowScale(float scale) {
        mRing.setArrowScale(scale);
    }

    /**
     * Set the start and end trim for the progress spinner arc.
     *
     * @param startAngle start angle
     * @param endAngle   end angle
     */
    public void setStartEndTrim(float startAngle, float endAngle) {
        mRing.setStartTrim(startAngle);
        mRing.setEndTrim(endAngle);
    }

    /**
     * Set the amount of rotation to apply to the progress spinner.
     *
     * @param rotation Rotation is from [0..1]
     */
    public void setProgressRotation(float rotation) {
        mRing.setRotation(rotation);
    }

    /**
     * Update the background color of the circle image view.
     */
    public void setBackgroundColor(int color) {
        mBackgroundColor = color;
        mRing.setBackgroundColor(color);
    }

    /**
     * Set the colors used in the progress animation from color resources.
     * The first color will also be the color of the bar that grows in response
     * to a user swipe gesture.
     *
     * @param colors
     */
    public void setColorSchemeColors(int... colors) {
        mRing.setColors(colors);
        mRing.setColorIndex(0);
    }

    @Override
    public int getIntrinsicHeight() {
        return (int) mHeight;
    }

    @Override
    public int getIntrinsicWidth() {
        return (int) mWidth;
    }

    @Override
    public void draw(Canvas c) {
        if (mShadow != null) {
            mShadow.getPaint().setColor(mBackgroundColor);
            mShadow.draw(c);
        }

        final Rect bounds = getBounds();
        final int saveCount = c.save();
        c.rotate(mRotation, bounds.exactCenterX(), bounds.exactCenterY());
        mRing.draw(c, bounds);
        c.restoreToCount(saveCount);
    }

    public int getAlpha() {
        return mRing.getAlpha();
    }

    @Override
    public void setAlpha(int alpha) {
        mRing.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mRing.setColorFilter(colorFilter);
    }

    @SuppressWarnings("unused")
    private float getRotation() {
        return mRotation;
    }

    @SuppressWarnings("unused")
    void setRotation(float rotation) {
        mRotation = rotation;
        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public boolean isRunning() {
        final ArrayList<Animation> animators = mAnimators;
        final int N = animators.size();
        for (int i = 0; i < N; i++) {
            final Animation animator = animators.get(i);
            if (animator.hasStarted() && !animator.hasEnded()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void start() {
        mAnimation.reset();
        mRing.storeOriginals();
        // Already showing some part of the ring
        if (mRing.getEndTrim() != mRing.getStartTrim()) {
            mParent.startAnimation(mFinishAnimation);
        } else {
            mRing.setColorIndex(0);
            mRing.resetOriginals();
            mParent.startAnimation(mAnimation);
        }
    }

    @Override
    public void stop() {
        mParent.clearAnimation();
        setRotation(0);
        mRing.setShowArrow(false);
        mRing.setColorIndex(0);
        mRing.resetOriginals();
    }

    private void setupAnimators() {
        final Ring ring = mRing;
        final Animation finishRingAnimation = new Animation() {
            public void applyTransformation(float interpolatedTime, Transformation t) {
                // shrink back down and complete a full rotation before starting other circles
                // Rotation goes between [0..1].
                float targetRotation = (float) (Math.floor(ring.getStartingRotation()
                        / MAX_PROGRESS_ARC) + 1f);
                final float startTrim = ring.getStartingStartTrim()
                        + (ring.getStartingEndTrim() - ring.getStartingStartTrim())
                        * interpolatedTime;
                ring.setStartTrim(startTrim);
                final float rotation = ring.getStartingRotation()
                        + ((targetRotation - ring.getStartingRotation()) * interpolatedTime);
                ring.setRotation(rotation);
                ring.setArrowScale(1 - interpolatedTime);
            }
        };
        finishRingAnimation.setInterpolator(EASE_INTERPOLATOR);
        finishRingAnimation.setDuration(ANIMATION_DURATION / 2);
        finishRingAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ring.goToNextColor();
                ring.storeOriginals();
                ring.setShowArrow(false);
                mParent.startAnimation(mAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        final Animation animation = new Animation() {
            @Override
            public void applyTransformation(float interpolatedTime, Transformation t) {
                // The minProgressArc is calculated from 0 to create an angle that
                // matches the stroke width.
                final float minProgressArc = (float) Math.toRadians(ring.getStrokeWidth()
                        / (2 * Math.PI * ring.getCenterRadius()));
                final float startingEndTrim = ring.getStartingEndTrim();
                final float startingTrim = ring.getStartingStartTrim();
                final float startingRotation = ring.getStartingRotation();
                // Offset the minProgressArc to where the endTrim is located.
                final float minArc = MAX_PROGRESS_ARC - minProgressArc;
                final float endTrim = startingEndTrim
                        + (minArc * START_CURVE_INTERPOLATOR.getInterpolation(interpolatedTime));
                ring.setEndTrim(endTrim);
                final float startTrim = startingTrim
                        + (MAX_PROGRESS_ARC * END_CURVE_INTERPOLATOR
                        .getInterpolation(interpolatedTime));
                ring.setStartTrim(startTrim);
                final float rotation = startingRotation + (0.25f * interpolatedTime);
                ring.setRotation(rotation);
                float groupRotation = ((720.0f / NUM_POINTS) * interpolatedTime)
                        + (720.0f * (mRotationCount / NUM_POINTS));
                setRotation(groupRotation);
            }
        };
        animation.setRepeatCount(Animation.INFINITE);
        animation.setRepeatMode(Animation.RESTART);
        animation.setInterpolator(LINEAR_INTERPOLATOR);
        animation.setDuration(ANIMATION_DURATION);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mRotationCount = 0;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // do nothing
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                ring.storeOriginals();
                ring.goToNextColor();
                ring.setStartTrim(ring.getEndTrim());
                mRotationCount = (mRotationCount + 1) % (NUM_POINTS);
            }
        });
        mFinishAnimation = finishRingAnimation;
        mAnimation = animation;
    }

    private static class Ring {
        private final RectF mTempBounds = new RectF();
        private final Paint mArcPaint = new Paint();
        private final Paint mArrowPaint = new Paint();
        private final Callback mRingCallback;
        private final Paint mCirclePaint = new Paint();
        private float mStartTrim = 0.0f;
        private float mEndTrim = 0.0f;
        private float mRotation = 0.0f;
        private float mStrokeWidth = 5.0f;
        private float mStrokeInset = 2.5f;
        private int[] mColors;
        // mColorIndex represents the offset into the available mColors that the
        // progress circle should currently display. As the progress circle is
        // animating, the mColorIndex moves by one to the next available color.
        private int mColorIndex;
        private float mStartingStartTrim;
        private float mStartingEndTrim;
        private float mStartingRotation;
        private boolean mShowArrow;
        private Path mArrow;
        private float mArrowScale;
        private double mRingCenterRadius;
        private int mArrowWidth;
        private int mArrowHeight;
        private int mAlpha;
        private int mBackgroundColor;

        public Ring(Callback callback) {
            mRingCallback = callback;
            mArcPaint.setStrokeCap(Paint.Cap.SQUARE);
            mArcPaint.setAntiAlias(true);
            mArcPaint.setStyle(Style.STROKE);
            mArrowPaint.setStyle(Paint.Style.FILL);
            mArrowPaint.setAntiAlias(true);

            mCirclePaint.setAntiAlias(true);
        }

        public void setBackgroundColor(int color) {
            mBackgroundColor = color;
        }

        /**
         * Set the dimensions of the arrowhead.
         *
         * @param width  Width of the hypotenuse of the arrow head
         * @param height Height of the arrow point
         */
        public void setArrowDimensions(float width, float height) {
            mArrowWidth = (int) width;
            mArrowHeight = (int) height;
        }

        /**
         * Draw the progress spinner
         */
        public void draw(Canvas c, Rect bounds) {

            mCirclePaint.setColor(mBackgroundColor);
            mCirclePaint.setAlpha(mAlpha);

            c.drawCircle(bounds.exactCenterX(), bounds.exactCenterY(), bounds.width() / 2, mCirclePaint);

            final RectF arcBounds = mTempBounds;
            arcBounds.set(bounds);
            arcBounds.inset(mStrokeInset, mStrokeInset);
            final float startAngle = (mStartTrim + mRotation) * 360;
            final float endAngle = (mEndTrim + mRotation) * 360;
            float sweepAngle = endAngle - startAngle;
            mArcPaint.setColor(mColors[mColorIndex]);
            mArcPaint.setAlpha(mAlpha);
            c.drawArc(arcBounds, startAngle, sweepAngle, false, mArcPaint);
            drawTriangle(c, startAngle, sweepAngle, bounds);
        }

        private void drawTriangle(Canvas c, float startAngle, float sweepAngle, Rect bounds) {
            if (mShowArrow) {
                if (mArrow == null) {
                    mArrow = new android.graphics.Path();
                    mArrow.setFillType(android.graphics.Path.FillType.EVEN_ODD);
                } else {
                    mArrow.reset();
                }
                // Adjust the position of the triangle so that it is inset as
                // much as the arc, but also centered on the arc.
                float inset = (int) mStrokeInset / 2 * mArrowScale;
                float x = (float) (mRingCenterRadius * Math.cos(0) + bounds.exactCenterX());
                float y = (float) (mRingCenterRadius * Math.sin(0) + bounds.exactCenterY());
                // Update the path each time. This works around an issue in SKIA
                // where concatenating a rotation matrix to a scale matrix
                // ignored a starting negative rotation. This appears to have
                // been fixed as of API 21.
                mArrow.moveTo(0, 0);
                mArrow.lineTo(mArrowWidth * mArrowScale, 0);
                mArrow.lineTo((mArrowWidth * mArrowScale / 2), (mArrowHeight
                        * mArrowScale));
                mArrow.offset(x - inset, y);
                mArrow.close();
                // draw a triangle
                mArrowPaint.setColor(mColors[mColorIndex]);
                mArrowPaint.setAlpha(mAlpha);
                c.rotate(startAngle + sweepAngle - ARROW_OFFSET_ANGLE, bounds.exactCenterX(),
                        bounds.exactCenterY());
                c.drawPath(mArrow, mArrowPaint);
            }
        }

        /**
         * Set the colors the progress spinner alternates between.
         *
         * @param colors Array of integers describing the colors. Must be non-<code>null</code>.
         */
        public void setColors(int[] colors) {
            mColors = colors;
            // if colors are reset, make sure to reset the color index as well
            setColorIndex(0);
        }

        /**
         * @param index Index into the color array of the color to display in
         *              the progress spinner.
         */
        public void setColorIndex(int index) {
            mColorIndex = index;
        }

        /**
         * Proceed to the next available ring color. This will automatically
         * wrap back to the beginning of colors.
         */
        public void goToNextColor() {
            mColorIndex = (mColorIndex + 1) % (mColors.length);
        }

        public void setColorFilter(ColorFilter filter) {
            mArcPaint.setColorFilter(filter);
            invalidateSelf();
        }

        /**
         * @return Current alpha of the progress spinner and arrowhead.
         */
        public int getAlpha() {
            return mAlpha;
        }

        /**
         * @param alpha Set the alpha of the progress spinner and associated arrowhead.
         */
        public void setAlpha(int alpha) {
            mAlpha = alpha;
        }

        @SuppressWarnings("unused")
        public float getStrokeWidth() {
            return mStrokeWidth;
        }

        /**
         * @param strokeWidth Set the stroke width of the progress spinner in pixels.
         */
        public void setStrokeWidth(float strokeWidth) {
            mStrokeWidth = strokeWidth;
            mArcPaint.setStrokeWidth(strokeWidth);
            invalidateSelf();
        }

        @SuppressWarnings("unused")
        public float getStartTrim() {
            return mStartTrim;
        }

        @SuppressWarnings("unused")
        public void setStartTrim(float startTrim) {
            mStartTrim = startTrim;
            invalidateSelf();
        }

        public float getStartingStartTrim() {
            return mStartingStartTrim;
        }

        public float getStartingEndTrim() {
            return mStartingEndTrim;
        }

        @SuppressWarnings("unused")
        public float getEndTrim() {
            return mEndTrim;
        }

        @SuppressWarnings("unused")
        public void setEndTrim(float endTrim) {
            mEndTrim = endTrim;
            invalidateSelf();
        }

        @SuppressWarnings("unused")
        public float getRotation() {
            return mRotation;
        }

        @SuppressWarnings("unused")
        public void setRotation(float rotation) {
            mRotation = rotation;
            invalidateSelf();
        }

        public void setInsets(int width, int height) {
            final float minEdge = (float) Math.min(width, height);
            float insets;
            if (mRingCenterRadius <= 0 || minEdge < 0) {
                insets = (float) Math.ceil(mStrokeWidth / 2.0f);
            } else {
                insets = (float) (minEdge / 2.0f - mRingCenterRadius);
            }
            mStrokeInset = insets;
        }

        @SuppressWarnings("unused")
        public float getInsets() {
            return mStrokeInset;
        }

        public double getCenterRadius() {
            return mRingCenterRadius;
        }

        /**
         * @param centerRadius Inner radius in px of the circle the progress
         *                     spinner arc traces.
         */
        public void setCenterRadius(double centerRadius) {
            mRingCenterRadius = centerRadius;
        }

        /**
         * @param show Set to true to show the arrow head on the progress spinner.
         */
        public void setShowArrow(boolean show) {
            if (mShowArrow != show) {
                mShowArrow = show;
                invalidateSelf();
            }
        }

        /**
         * @param scale Set the scale of the arrowhead for the spinner.
         */
        public void setArrowScale(float scale) {
            if (scale != mArrowScale) {
                mArrowScale = scale;
                invalidateSelf();
            }
        }

        /**
         * @return The amount the progress spinner is currently rotated, between [0..1].
         */
        public float getStartingRotation() {
            return mStartingRotation;
        }

        /**
         * If the start / end trim are offset to begin with, store them so that
         * animation starts from that offset.
         */
        public void storeOriginals() {
            mStartingStartTrim = mStartTrim;
            mStartingEndTrim = mEndTrim;
            mStartingRotation = mRotation;
        }

        /**
         * Reset the progress spinner to default rotation, start and end angles.
         */
        public void resetOriginals() {
            mStartingStartTrim = 0;
            mStartingEndTrim = 0;
            mStartingRotation = 0;
            setStartTrim(0);
            setEndTrim(0);
            setRotation(0);
        }

        private void invalidateSelf() {
            mRingCallback.invalidateDrawable(null);
        }
    }

    /**
     * Squishes the interpolation curve into the second half of the animation.
     */
    private static class EndCurveInterpolator extends AccelerateDecelerateInterpolator {
        @Override
        public float getInterpolation(float input) {
            return super.getInterpolation(Math.max(0, (input - 0.5f) * 2.0f));
        }
    }

    /**
     * Squishes the interpolation curve into the first half of the animation.
     */
    private static class StartCurveInterpolator extends AccelerateDecelerateInterpolator {
        @Override
        public float getInterpolation(float input) {
            return super.getInterpolation(Math.min(1, input * 2.0f));
        }
    }

    /**
     * Taken from {@link package android.support.v4.widget}
     */
    private class OvalShadow extends OvalShape {
        private RadialGradient mRadialGradient;
        private int mShadowRadius;
        private Paint mShadowPaint;
        private int mCircleDiameter;

        public OvalShadow(int shadowRadius, int circleDiameter) {
            super();
            mShadowPaint = new Paint();
            mShadowRadius = shadowRadius;
            mCircleDiameter = circleDiameter;
            mRadialGradient = new RadialGradient(mCircleDiameter / 2, mCircleDiameter / 2,
                    mShadowRadius, new int[]{
                    FILL_SHADOW_COLOR, Color.TRANSPARENT
            }, null, Shader.TileMode.CLAMP);
            mShadowPaint.setShader(mRadialGradient);
        }

        @Override
        public void draw(Canvas canvas, Paint paint) {
            final int viewWidth = getBounds().width();
            final int viewHeight = getBounds().height();
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, (mCircleDiameter / 2 + mShadowRadius),
                    mShadowPaint);
            canvas.drawCircle(viewWidth / 2, viewHeight / 2, (mCircleDiameter / 2), paint);
        }
    }
}