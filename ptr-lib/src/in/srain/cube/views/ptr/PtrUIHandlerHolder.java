package in.srain.cube.views.ptr;

import in.srain.cube.views.ptr.indicator.PtrIndicator;

/**
 * A single linked list to wrap PtrUIHandler
 */
class PtrUIHandlerHolder implements PtrUIHandler {

    private PtrUIHandler mHandler;
    private PtrUIHandlerHolder mNext;

    private boolean contains(PtrUIHandler handler) {
        return mHandler != null && mHandler == handler;
    }

    private PtrUIHandlerHolder() {

    }

    public boolean hasHandler() {
        return mHandler != null;
    }

    private PtrUIHandler getHandler() {
        return mHandler;
    }

    public static void addHandler(PtrUIHandlerHolder head, PtrUIHandler handler) {

        if (null == handler) {
            return;
        }
        if (head == null) {
            return;
        }
        if (null == head.mHandler) {
            head.mHandler = handler;
            return;
        }

        PtrUIHandlerHolder current = head;
        for (; ; current = current.mNext) {

            // duplicated
            if (current.contains(handler)) {
                return;
            }
            if (current.mNext == null) {
                break;
            }
        }

        PtrUIHandlerHolder newHolder = new PtrUIHandlerHolder();
        newHolder.mHandler = handler;
        current.mNext = newHolder;
    }

    public static PtrUIHandlerHolder create() {
        return new PtrUIHandlerHolder();
    }

    public static PtrUIHandlerHolder removeHandler(PtrUIHandlerHolder head, PtrUIHandler handler) {
        if (head == null || handler == null || null == head.mHandler) {
            return head;
        }

        PtrUIHandlerHolder current = head;
        PtrUIHandlerHolder pre = null;
        do {

            // delete current: link pre to next, unlink next from current;
            // pre will no change, current move to next element;
            if (current.contains(handler)) {

                // current is head
                if (pre == null) {

                    head = current.mNext;
                    current.mNext = null;

                    current = head;
                } else {

                    pre.mNext = current.mNext;
                    current.mNext = null;
                    current = pre.mNext;
                }
            } else {
                pre = current;
                current = current.mNext;
            }

        } while (current != null);

        if (head == null) {
            head = new PtrUIHandlerHolder();
        }
        return head;
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIReset(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIRefreshPrepare(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIRefreshBegin(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIRefreshComplete(frame);
            }
        } while ((current = current.mNext) != null);
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        PtrUIHandlerHolder current = this;
        do {
            final PtrUIHandler handler = current.getHandler();
            if (null != handler) {
                handler.onUIPositionChange(frame, isUnderTouch, status, ptrIndicator);
            }
        } while ((current = current.mNext) != null);
    }
}
