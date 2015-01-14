package in.srain.cube.views.ptr.demo.ui.viewpager;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.request.JsonData;
import in.srain.cube.views.list.ViewHolderBase;
import in.srain.cube.views.ptr.demo.R;

public class ImageListViewHolder extends ViewHolderBase<JsonData> {

    private ImageLoader mImageLoader;
    private CubeImageView mImageView;

    private ImageListViewHolder(ImageLoader imageLoader) {
        mImageLoader = imageLoader;
    }

    @Override
    public View createView(LayoutInflater inflater) {
        View v = inflater.inflate(R.layout.list_view_item, null);
        mImageView = (CubeImageView) v.findViewById(R.id.list_view_item_image_view);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return v;
    }

    @Override
    public void showData(int position, JsonData itemData) {
        mImageView.loadImage(mImageLoader, itemData.optString("pic"));
    }
}
