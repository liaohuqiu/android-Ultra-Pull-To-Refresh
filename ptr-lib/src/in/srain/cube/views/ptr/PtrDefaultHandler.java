package in.srain.cube.views.ptr;

import android.view.View;
import android.view.ViewGroup;
import in.srain.cube.util.CLog;

public abstract class PtrDefaultHandler implements PtrHandler {

    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return checkContentCanPullDown(frame, content, header);
    }

    public static boolean checkContentCanPullDown(PtrFrameLayout frame, View content, View header) {
        if (!(content instanceof ViewGroup)) {
            return true;
        }

        ViewGroup viewGroup = (ViewGroup) content;
        if (viewGroup.getChildCount() == 0) {
            return true;
        }

        View child = viewGroup.getChildAt(0);
        return viewGroup.getChildAt(0).getTop() == viewGroup.getPaddingTop();
    }
}