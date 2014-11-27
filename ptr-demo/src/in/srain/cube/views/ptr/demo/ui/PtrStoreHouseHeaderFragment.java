package in.srain.cube.views.ptr.demo.ui;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import in.srain.cube.mints.base.TitleBaseFragment;
import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.util.CLog;
import in.srain.cube.util.Debug;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.demo.R;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

public class PtrStoreHouseHeaderFragment extends TitleBaseFragment {

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_store_house_ptr_header, null);

        setHeaderTitle("Storehouse");

        Resources res = getResources();

        CubeImageView imageView = (CubeImageView) view.findViewById(R.id.store_house_ptr_image);
        ImageLoader imageLoader = ImageLoaderFactory.create(getContext());
        String pic = "http://img5.duitang.com/uploads/item/201406/28/20140628122218_fLQyP.thumb.jpeg";
        imageView.loadImage(imageLoader, pic);

        Debug.DEBUG_PTR_FRAME = true;
        final PtrFrameLayout frame = (PtrFrameLayout) view.findViewById(R.id.store_house_ptr_frame);
        final StoreHouseHeader houseHeader = (StoreHouseHeader) view.findViewById(R.id.store_house_ptr_header);
        frame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return true;
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                CLog.d("ptr-test", "onRefreshBegin");
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.refreshComplete();
                    }
                }, 4000);
            }
        });
        frame.setPtrUIHandler(new PtrUIHandler() {
            @Override
            public void onUIRefreshBegin(PtrFrameLayout frame) {
                houseHeader.beginLoading();
            }

            /**
             * perform UI after refresh
             *
             * @param frame
             */
            @Override
            public void onUIRefreshComplete(PtrFrameLayout frame) {

            }

            /**
             * When the content view has reached top and refresh has been completed, view will be reset.
             *
             * @param frame
             */
            @Override
            public void onUIReset(PtrFrameLayout frame) {

            }

            @Override
            public void onUIRefreshPrepare(PtrFrameLayout frame, boolean isAutoRefresh) {
                CLog.d("ptr-test", "onRefreshComplete");
                houseHeader.loadFinish();
            }

            @Override
            public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, int oldPosition, int currentPosition, float oldPercent, float currentPercent) {
                float f = currentPosition * 1f / houseHeader.getMeasuredHeight();
                if (f > 1) f = 1;
                // CLog.d("ptr-test", "onPositionChange: %s %s", currentPosition, houseHeader.getMeasuredHeight(), houseHeader.getHeight());
                houseHeader.setProgress(f);
                houseHeader.invalidate();
            }

        });
        return view;
    }
}
