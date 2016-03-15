package in.srain.cube.views.ptr.demo.ui;

import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.demo.R;
import in.srain.cube.views.ptr.demo.ui.classic.WithTextViewInFrameLayoutFragment;

public class EnableNextPTRAtOnce extends WithTextViewInFrameLayoutFragment {

    @Override
    protected void setupViews(PtrClassicFrameLayout ptrFrame) {
        setHeaderTitle(R.string.ptr_demo_enable_next_ptr_at_once);
        ptrFrame.setEnabledNextPtrAtOnce(true);
        ptrFrame.setPullToRefresh(false);
    }
}