package in.srain.cube.views.ptr.demo.image;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageTask;
import in.srain.cube.image.iface.ImageLoadHandler;

public class PtrImageLoadHandler implements ImageLoadHandler {

    @Override
    public void onLoading(ImageTask imageTask, CubeImageView cubeImageView) {
        if (cubeImageView != null) {
            cubeImageView.setImageDrawable(new ColorDrawable(Color.GRAY));
        }
    }

    @Override
    public void onLoadFinish(ImageTask imageTask, CubeImageView cubeImageView, BitmapDrawable bitmapDrawable) {
        if (bitmapDrawable != null && cubeImageView != null) {
            cubeImageView.setImageDrawable(bitmapDrawable);
        }
    }

    @Override
    public void onLoadError(ImageTask imageTask, CubeImageView cubeImageView, int i) {
        if (cubeImageView != null) {
            cubeImageView.setImageDrawable(new ColorDrawable(Color.RED));
        }
    }
}
