package in.srain.cube.views.ptr;

import android.view.View;

public interface PtrHandler {

    /**
     * if content is empty or the first child is in view, should do refresh
     * after release
     */
    public boolean checkCanDoRefresh(final PtrFrameLayout frame, final View content, final View header);

    public void onRefreshBegin(final PtrFrameLayout frame);
}