package in.srain.cube.views.ptr;

import android.view.View;
import android.widget.AbsListView;

public abstract class PtrDefaultHandler implements PtrHandler {

    public static boolean canChildScrollUp(View mTarget) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mTarget instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mTarget;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return mTarget.getScrollY() > 0;
            }
        } else {
            return mTarget.canScrollVertically(-1);
        }
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
        return !canChildScrollUp(content);
        /*
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (content instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) content;
                if (absListView.getChildCount() == 0) {
                    return true;
                }
                if (absListView.getFirstVisiblePosition() == 0 && absListView.getChildAt(0).getTop() >= absListView.getPaddingTop()) {
                    return true;
                }
                return false;
            } else {
                return content.getScrollY() <= 0;
            }
        } else {
            return content.canScrollVertically(1);
        }
        */
        /*
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
        */
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return checkContentCanBePulledDown(frame, content, header);
    }
}