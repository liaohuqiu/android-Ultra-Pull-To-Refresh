package in.srain.cube.views.ptr.demo.ui.classic;

import android.widget.TextView;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.demo.R;

public class AutoRefresh extends WithTextViewBaseFragment {

    @Override
    protected void setupViews(final PtrClassicFrameLayout ptrFrame, TextView textView) {
        setHeaderTitle(R.string.ptr_demo_title_auto_fresh);
        ptrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrFrame.autoRefresh();
            }
        }, 150);
    }
}
