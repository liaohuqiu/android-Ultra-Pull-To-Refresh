package in.srain.cube.views.ptr.demo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.mints.base.TitleBaseFragment;
import in.srain.cube.views.list.ListViewDataAdapter;
import in.srain.cube.views.list.ViewHolderBase;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.demo.R;
import in.srain.cube.views.ptr.demo.image.Images;

import java.util.Arrays;

public class WithLongPressFragment extends TitleBaseFragment {

    private ImageLoader mImageLoader;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mImageLoader = ImageLoaderFactory.create(getActivity());

        View view = inflater.inflate(R.layout.fragment_with_long_press, null);

        setHeaderTitle(R.string.ptr_demo_block_with_long_press);

        final PtrFrameLayout ptrFrameLayout = (PtrFrameLayout) view.findViewById(R.id.with_long_press_list_view_frame);

        ListView listView = (ListView) view.findViewById(R.id.with_long_press_list_view);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getContext(), "Long Pressed: " + id, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        final ListViewDataAdapter<String> listViewDataAdapter = new ListViewDataAdapter<String>();
        listViewDataAdapter.setViewHolderClass(this, ViewHolder.class);

        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                ptrFrameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        listViewDataAdapter.getDataList().clear();
                        listViewDataAdapter.getDataList().addAll(Arrays.asList(Images.imageUrls));
                        listViewDataAdapter.notifyDataSetChanged();
                        ptrFrameLayout.refreshComplete();
                    }
                }, 2000);
            }
        });
        ptrFrameLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                ptrFrameLayout.autoRefresh();
            }
        }, 100);
        listView.setAdapter(listViewDataAdapter);
        return view;
    }

    private class ViewHolder extends ViewHolderBase<String> {

        private CubeImageView mImageView;

        /**
         * create a view from resource Xml file, and hold the view that may be used in displaying data.
         *
         * @param layoutInflater
         */
        @Override
        public View createView(LayoutInflater layoutInflater) {
            View view = layoutInflater.inflate(R.layout.with_long_press_list_view_item, null);
            mImageView = (CubeImageView) view.findViewById(R.id.with_long_press_list_image);
            return view;
        }

        /**
         * using the held views to display data
         *
         * @param position
         * @param itemData
         */
        @Override
        public void showData(int position, String itemData) {
            mImageView.loadImage(mImageLoader, itemData);
        }
    }
}
