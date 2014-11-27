package in.srain.cube.views.ptr.demo.ui.classic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import in.srain.cube.mints.base.TitleBaseFragment;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.demo.R;

public class WithTextViewInFrameLayoutFragment extends TitleBaseFragment {

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        setHeaderTitle(R.string.ptr_demo_title_frame_layout);

        final View contentView = inflater.inflate(R.layout.fragment_classic_header_with_viewgroup, container, false);

        final PtrClassicFrameLayout ptrFrame = (PtrClassicFrameLayout) contentView.findViewById(R.id.fragment_rotate_header_with_view_group_frame);
        ptrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptrFrame.refreshComplete();
                    }
                }, 1000);
            }
        });
        return contentView;
    }
}