package in.srain.cube.views.ptr.demo.ui.classic;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.demo.R;

public class AutoRefresh extends WithGridView {

    @Override
    protected void setupViews(final PtrClassicFrameLayout ptrFrame) {
        ptrFrame.setLoadingMinTime(3000);
        setHeaderTitle(R.string.ptr_demo_block_auto_fresh);
        ptrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrFrame.autoRefresh(true);
            }
        }, 150);
    }
}