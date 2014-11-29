package in.srain.cube.views.ptr.demo.ui.classic;

import android.widget.TextView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.demo.R;

public class KeepHeader extends WithTextViewInFrameLayoutFragment {

    @Override
    protected void setupViews(PtrClassicFrameLayout ptrFrame) {
        setHeaderTitle(R.string.ptr_demo_title_keep_header);
        ptrFrame.setKeepHeaderWhenRefresh(true);
    }
}
