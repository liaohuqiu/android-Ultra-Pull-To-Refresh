package in.srain.cube.views.ptr;

import android.view.View;
import android.widget.AbsListView;

/**
 * Basic handler wrap the logic of {@link PtrHandler#checkCanDoRefresh(PtrFrameLayout, View, View)}.
 * This default handler make ptr only response to move down when the content in initial position.
 */
public abstract class PtrDefaultHandler implements PtrHandler {

    /**
     * Judge if contents can move up.
     *
     * @param view
     * @return
     */
    public static boolean canChildScrollUp(View view) {
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (view instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) view;
                return absListView.getChildCount() > 0
                        && (absListView.getFirstVisiblePosition() > 0 || absListView.getChildAt(0)
                        .getTop() < absListView.getPaddingTop());
            } else {
                return view.getScrollY() > 0;
            }
        } else {
            return view.canScrollVertically(-1);
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
    }

    @Override
    public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
        return checkContentCanBePulledDown(frame, content, header);
    }
}