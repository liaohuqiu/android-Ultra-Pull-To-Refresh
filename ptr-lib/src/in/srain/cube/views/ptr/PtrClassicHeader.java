package in.srain.cube.views.ptr;

import android.view.View;

public interface PtrClassicHeader {

    public View getRotateView();

    public void setLastUpdateTimeKey(String key);

    public void crossRotateLineFromBottomUnderTouch(PtrClassicFrameLayout frame);

    public void crossRotateLineFromTopUnderTouch(PtrClassicFrameLayout frame);
}