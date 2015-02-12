package in.srain.cube.views.ptr;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ScrollView;

public abstract class PtrDefaultHandler implements PtrHandler {

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return checkContentCanBePulledDown(frame, content, header);
    }

    /**
     * Default implement for check can perform pull to refresh
     *
     * @param frame
     * @param content
     * @param header
     * @return
     */
    public static boolean checkContentCanBePulledDown(PtrFrameLayout frame, View content, View header) {
        if (!(content instanceof ViewGroup)) {
            return true;
        }

        ViewGroup viewGroup = (ViewGroup) content;
        if (viewGroup.getChildCount() == 0) {
            return true;
        }

        if (viewGroup instanceof AbsListView) {
            AbsListView listView = (AbsListView) viewGroup;
            if (listView.getFirstVisiblePosition() > 0) {
                return false;
            }
        }

        if (Build.VERSION.SDK_INT >= 14) {
            return !content.canScrollVertically(-1);
        } else {
            if (viewGroup instanceof ScrollView || viewGroup instanceof AbsListView) {
                return viewGroup.getScrollY() == 0;
            }
        }

        View child = viewGroup.getChildAt(0);
        ViewGroup.LayoutParams glp = child.getLayoutParams();
        int top = child.getTop();
        if (glp instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams) glp;
            return top == mlp.topMargin + viewGroup.getPaddingTop();
        } else {
            return top == viewGroup.getPaddingTop();
        }
    }
}