package in.srain.cube.views.ptr;

import android.view.View;

/**
 * Wrap base refresh event, you can use {@link PtrFrameLayout#setPtrHandler(PtrHandler)} to
 * set your Handler and do refresh in onRefreshBegin().
 *
 * <p>Here is a simple example:</p>
 *
 * <pre>
 * ptrFrame.setPtrHandler(new PtrDefaultHandler() {
 *     @Override
 *     public void onRefreshBegin(PtrFrameLayout frame) {
 *         // do refresh.
 *     }
 *
 *     @Override
 *     public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
 *         return true;
 *     }
 * });
 * </pre>
 */
public interface PtrHandler {

    /**
     * Check can do refresh or not. For example the content is empty or the first child is in view.
     * <p/>
     * {@link in.srain.cube.views.ptr.PtrDefaultHandler#checkContentCanBePulledDown}
     */
    public boolean checkCanDoRefresh(final PtrFrameLayout frame, final View content, final View header);

    /**
     * Called when refresh begin.
     *
     * @param frame
     */
    public void onRefreshBegin(final PtrFrameLayout frame);
}