package in.srain.cube.views.ptr;

public class PtrResistanceSlider extends PtrSlider {

    private float mResistance = 1.7f;

    public float getResistance() {
        return mResistance;
    }

    public void setResistance(float resistance) {
        mResistance = resistance;
    }

    @Override
    protected void processOnMove(float x, float y) {
        setOffset(x, y / mResistance);
    }
}
