package in.srain.cube.views.ptr.demo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import in.srain.cube.mints.base.BlockMenuFragment;
import in.srain.cube.views.ptr.demo.R;
import in.srain.cube.views.ptr.demo.ui.classic.*;

import java.util.ArrayList;

public class HomeFragment extends BlockMenuFragment {

    @Override
    protected void addItemInfo(ArrayList<BlockMenuFragment.ItemInfo> itemInfos) {

        // GridView
        itemInfos.add(newItemInfo(R.string.ptr_demo_title_grid_view, R.color.cube_holo_blue_bright, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().pushFragmentToBackStack(WithGridView.class, null);
            }
        }));
        itemInfos.add(newItemInfo(R.string.ptr_demo_title_frame_layout, R.color.cube_holo_blue_bright, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().pushFragmentToBackStack(WithTextViewInFrameLayoutFragment.class, null);
            }
        }));
        itemInfos.add(newItemInfo(R.string.ptr_demo_title_only_text_view, R.color.cube_holo_blue_bright, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().pushFragmentToBackStack(EvenOnlyATextView.class, null);
            }
        }));

        itemInfos.add(newItemInfo(R.string.ptr_demo_title_keep_header, R.color.cube_holo_blue_bright, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().pushFragmentToBackStack(KeepHeader.class, null);
            }
        }));
        itemInfos.add(newItemInfo(R.string.ptr_demo_title_hide_header, R.color.cube_holo_blue_bright, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().pushFragmentToBackStack(HideHeader.class, null);
            }
        }));
        itemInfos.add(null);

        itemInfos.add(newItemInfo(R.string.cube_ptr_release_to_refresh, R.color.cube_holo_blue_bright, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().pushFragmentToBackStack(ReleaseToRefresh.class, null);
            }
        }));

        itemInfos.add(newItemInfo(R.string.ptr_demo_title_pull_to_refresh, R.color.cube_holo_blue_bright, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().pushFragmentToBackStack(PullToRefresh.class, null);
            }
        }));
        itemInfos.add(newItemInfo(R.string.ptr_demo_title_auto_fresh, R.color.cube_holo_blue_bright, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().pushFragmentToBackStack(AutoRefresh.class, null);
            }
        }));


        itemInfos.add(newItemInfo("StoreHouse", R.color.cube_holo_blue_bright, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().pushFragmentToBackStack(PtrStoreHouseHeaderFragment.class, null);
            }
        }));
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.createView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(R.color.c333333));
        return view;
    }

    @Override
    protected void setupViews() {
        setHeaderTitle(R.string.ptr_demo_title_for_home);
    }
}
