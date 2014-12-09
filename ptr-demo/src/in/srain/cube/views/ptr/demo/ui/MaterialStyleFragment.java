package in.srain.cube.views.ptr.demo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.mints.base.TitleBaseFragment;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.demo.R;
import in.srain.cube.views.ptr.header.MaterialHeader;

public class MaterialStyleFragment extends TitleBaseFragment {

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_materail_style, null);
        setHeaderTitle(R.string.ptr_demo_material_style);

        CubeImageView imageView = (CubeImageView) view.findViewById(R.id.material_style_image_view);
        ImageLoader imageLoader = ImageLoaderFactory.create(getContext());
        String pic = "http://img5.duitang.com/uploads/item/201406/28/20140628122218_fLQyP.thumb.jpeg";
        imageView.loadImage(imageLoader, pic);

        final PtrFrameLayout frame = (PtrFrameLayout) view.findViewById(R.id.material_style_ptr_frame);
        frame.setWillNotDraw(true);

        // header
        final MaterialHeader header = new MaterialHeader(getContext());
        int[] colors = getResources().getIntArray(R.array.google_colors);
        // header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, LocalDisplay.dp2px(10), 0, LocalDisplay.dp2px(10));

        frame.setWillNotDraw(false);
        frame.setDurationToCloseHeader(1500);
        frame.setHeaderView(header);
        frame.addPtrUIHandler(header);
        frame.postDelayed(new Runnable() {
            @Override
            public void run() {
                frame.autoRefresh(false);
            }
        }, 100);

        frame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return true;
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        frame.refreshComplete();
                    }
                }, (long) (1000 + Math.random() * 3000));
            }
        });
        return view;
    }
}
