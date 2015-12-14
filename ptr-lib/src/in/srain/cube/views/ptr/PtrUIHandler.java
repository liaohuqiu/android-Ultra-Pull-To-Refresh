package in.srain.cube.views.ptr;

import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * Wrap base UI event. Your header view should implement this class if you want it response to
 * Ptr events.
 */
public interface PtrUIHandler {

    /**
     * Called when the Ptr has back to initial position and refresh has been completed,
     * then view will be reset.
     *
     * @param frame ptr frame layout.
     */
    public void onUIReset(PtrFrameLayout frame);

    /**
     * Called when the Ptr leave initial position or just refresh complete.
     *
     * @param frame ptr frame layout.
     */
    public void onUIRefreshPrepare(PtrFrameLayout frame);

    /**
     * Called when the Ptr begin to perform refresh.
     *
     * @param frame ptr frame layout.
     */
    public void onUIRefreshBegin(PtrFrameLayout frame);

    /**
     * Called when the Ptr refresh finished.
     *
     * @param frame ptr frame layout.
     */
    public void onUIRefreshComplete(PtrFrameLayout frame);

    /**
     * Called when the Ptr position updated.
     *
     * @param frame ptr frame layout.
     * @param isUnderTouch true if is moved under touch.
     * @param status ptr status, it should be one of the following value:
     * <ul>
     *   <li>{@link PtrFrameLayout#PTR_STATUS_INIT}</li>
     *   <li>{@link PtrFrameLayout#PTR_STATUS_PREPARE}</li>
     *   <li>{@link PtrFrameLayout#PTR_STATUS_LOADING}</li>
     *   <li>{@link PtrFrameLayout#PTR_STATUS_COMPLETE}</li>
     * </ul>
     * @param ptrIndicator ptr indicator.
     */
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator);
}
