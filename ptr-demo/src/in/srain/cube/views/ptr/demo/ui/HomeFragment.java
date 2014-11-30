package in.srain.cube.views.ptr.demo.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import in.srain.cube.mints.base.BlockMenuFragment;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.demo.R;
import in.srain.cube.views.ptr.demo.ui.classic.*;
import in.srain.cube.views.ptr.demo.ui.storehouse.StoreHouseUsingStringArray;
import in.srain.cube.views.ptr.demo.ui.storehouse.StoreHouseUsingString;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

import java.util.ArrayList;

public class HomeFragment extends BlockMenuFragment {

    @Override
    protected void addItemInfo(ArrayList<BlockMenuFragment.ItemInfo> itemInfos) {

        // GridView
        itemInfos.add(newItemInfo(R.string.ptr_demo_block_grid_view, R.color.cube_mints_4d90fe, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().pushFragmentToBackStack(WithGridView.class, null);
            }
        }));
        itemInfos.add(newItemInfo(R.string.ptr_demo_block_frame_layout, R.color.cube_mints_4d90fe, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().pushFragmentToBackStack(WithTextViewInFrameLayoutFragment.class, null);
            }
        }));
        itemInfos.add(newItemInfo(R.string.ptr_demo_block_only_text_view, R.color.cube_mints_4d90fe, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().pushFragmentToBackStack(EvenOnlyATextView.class, null);
            }
        }));

        itemInfos.add(newItemInfo(R.string.ptr_demo_block_keep_header, R.color.cube_mints_4d90fe, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().pushFragmentToBackStack(KeepHeader.class, null);
            }
        }));
        itemInfos.add(newItemInfo(R.string.ptr_demo_block_hide_header, R.color.cube_mints_4d90fe, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().pushFragmentToBackStack(HideHeader.class, null);
            }
        }));
        itemInfos.add(null);

        itemInfos.add(newItemInfo(R.string.ptr_demo_block_release_to_refresh, R.color.cube_mints_4d90fe, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().pushFragmentToBackStack(ReleaseToRefresh.class, null);
            }
        }));

        itemInfos.add(newItemInfo(R.string.ptr_demo_block_pull_to_refresh, R.color.cube_mints_4d90fe, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().pushFragmentToBackStack(PullToRefresh.class, null);
            }
        }));
        itemInfos.add(newItemInfo(R.string.ptr_demo_block_auto_fresh, R.color.cube_mints_4d90fe, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().pushFragmentToBackStack(AutoRefresh.class, null);
            }
        }));

        /*
        itemInfos.add(newItemInfo(R.string.ptr_demo_title_with_long_press, R.color.cube_mints_4d90fe, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().pushFragmentToBackStack(WithLongPressFragment.class, null);
            }
        }));
        */
        itemInfos.add(newItemInfo(R.string.ptr_demo_block_storehouse_header_using_string_array, R.color.cube_mints_4d90fe, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().pushFragmentToBackStack(StoreHouseUsingStringArray.class, null);
            }
        }));
        itemInfos.add(newItemInfo(R.string.ptr_demo_block_storehouse_header_using_string, R.color.cube_mints_4d90fe, new OnClickListener() {

            @Override
            public void onClick(View v) {
                getContext().pushFragmentToBackStack(StoreHouseUsingString.class, null);
            }
        }));
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.createView(inflater, container, savedInstanceState);
        view.setBackgroundColor(getResources().getColor(R.color.cube_mints_333333));

        final PtrFrameLayout ptrFrameLayout = (PtrFrameLayout) view.findViewById(R.id.fragment_ptr_home_ptr_frame);
        StoreHouseHeader header = new StoreHouseHeader(getContext());
        header.setPadding(0, LocalDisplay.dp2px(20), 0, LocalDisplay.dp2px(20));
        header.initWithString("Ultra PTR");

        ptrFrameLayout.setDurationToCloseHeader(3000);
        ptrFrameLayout.setHeaderView(header);
        ptrFrameLayout.addPtrUIHandler(header);
        ptrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return true;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                ptrFrameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ptrFrameLayout.refreshComplete();
                    }
                }, 1500);
            }
        });
        return view;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragmengt_ptr_home;
    }

    @Override
    protected void setupViews() {
        setHeaderTitle(R.string.ptr_demo_block_for_home);
    }
}
