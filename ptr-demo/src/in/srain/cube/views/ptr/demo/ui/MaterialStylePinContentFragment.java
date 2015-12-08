package in.srain.cube.views.ptr.demo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MaterialStylePinContentFragment extends MaterialStyleFragment {

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.createView(inflater, container, savedInstanceState);
        // close at once
        mPtrFrameLayout.setDurationToClose(100);
        mPtrFrameLayout.setPinContent(true);
        return view;
    }
}
