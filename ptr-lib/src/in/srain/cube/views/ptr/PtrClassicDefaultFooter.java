package in.srain.cube.views.ptr;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import in.srain.cube.views.ptr.indicator.PtrIndicator;

public class PtrClassicDefaultFooter extends PtrClassicDefaultHeader {

    public PtrClassicDefaultFooter(Context context) {
        super(context);
    }

    public PtrClassicDefaultFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PtrClassicDefaultFooter(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void buildAnimation() {
        super.buildAnimation();
        RotateAnimation tmp = mFlipAnimation;
        mFlipAnimation = mReverseFlipAnimation;
        mReverseFlipAnimation = tmp;
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        super.onUIRefreshPrepare(frame);
        if (frame.isPullToRefresh()) {
            mTitleTextView.setText(getResources().getString(R.string.cube_ptr_pull_up_to_load));
        } else {
            mTitleTextView.setText(getResources().getString(R.string.cube_ptr_pull_up));
        }
    }
}
