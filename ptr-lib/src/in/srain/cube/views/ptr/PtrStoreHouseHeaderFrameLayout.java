package in.srain.cube.views.ptr;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import in.srain.cube.util.CLog;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

import java.lang.reflect.Field;

public class PtrStoreHouseHeaderFrameLayout extends PtrFrameLayout implements PtrUIHandler {

    private StoreHouseHeader mStoreHouseHeader;

    public PtrStoreHouseHeaderFrameLayout(Context context) {
        super(context);
        initViews(null);
    }

    public PtrStoreHouseHeaderFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(attrs);
    }

    public PtrStoreHouseHeaderFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews(attrs);
    }

    public static int getResId(String variableName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(variableName);
            return idField.getInt(idField);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    protected void initViews(AttributeSet attrs) {

        mStoreHouseHeader = new StoreHouseHeader(getContext());
        setHeaderView(mStoreHouseHeader);
        TypedArray arr = getContext().obtainStyledAttributes(attrs, R.styleable.PtrStoreHouseHeader, 0, 0);
        if (arr != null) {
            if (arr.hasValue(R.styleable.PtrStoreHouseHeader_ptr_storehouse_text)) {
                String str = arr.getString(R.styleable.PtrStoreHouseHeader_ptr_storehouse_text);
                if (!TextUtils.isEmpty(str)) {
                    mStoreHouseHeader.initWithString(str);
                }
            }
        }

        arr.recycle();
        setPtrUIHandler(this);
    }

    public StoreHouseHeader getHeader() {
        return mStoreHouseHeader;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    /**
     * When the content view has reached top and refresh has been completed, view will be reset.
     *
     * @param frame
     */
    @Override
    public void onUIReset(PtrFrameLayout frame) {
        mStoreHouseHeader.loadFinish();
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
        mStoreHouseHeader.beginLoading();
    }

    /**
     * perform UI after refresh
     *
     * @param frame
     */
    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        mStoreHouseHeader.loadFinish();
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, int oldPosition, int currentPosition, float oldPercent, float currentPercent) {
        float f = currentPosition * 1f / mStoreHouseHeader.getMeasuredHeight();
        if (f > 1) f = 1;
        CLog.d("ptr-test", "onPositionChange: %s %s", currentPosition, mStoreHouseHeader.getMeasuredHeight(), mStoreHouseHeader.getHeight());
        mStoreHouseHeader.setProgress(f);
        mStoreHouseHeader.invalidate();
    }
}
