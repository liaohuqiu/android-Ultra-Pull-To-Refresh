package in.srain.cube.views.ptr.demo.ui.classic;


import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import in.srain.cube.image.CubeImageView;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.mints.base.TitleBaseFragment;
import in.srain.cube.request.JsonData;
import in.srain.cube.request.RequestFinishHandler;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.demo.R;
import in.srain.cube.views.ptr.demo.data.DemoRequestData;
import in.srain.cube.views.ptr.demo.ui.MaterialStyleFragment;
import in.srain.cube.views.ptr.header.MaterialHeader;

/**
 * Created by yeungeek on 2015/6/20.
 *
 * @see <p>
 * <a href="https://github.com/liaohuqiu/android-Ultra-Pull-To-Refresh/issues/105">issue#105</a>
 * </p>
 */
public class WithRecyclerView extends TitleBaseFragment {
    private ImageLoader mImageLoader;
    private PtrClassicFrameLayout mPtrFrame;
    private RecyclerAdapter mAdapter;

    @Override
    protected View createView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
        setHeaderTitle(R.string.ptr_demo_block_recycler_view);
        mImageLoader = ImageLoaderFactory.create(getContext());

        final View contentView = layoutInflater.inflate(R.layout.fragment_classic_header_with_recycler_view, null);
        final RecyclerView recyclerView = (RecyclerView) contentView.findViewById(R.id.rotate_header_recycler_view);
        //itemClick
        mAdapter = new RecyclerAdapter();
        mAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position >= 0) {
                    final String url = mAdapter.getItem(position).optString("pic");
                    if (!TextUtils.isEmpty(url)) {
                        getContext().pushFragmentToBackStack(MaterialStyleFragment.class, url);
                    }
                }
            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
//        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);

        mPtrFrame = (PtrClassicFrameLayout) contentView.findViewById(R.id.rotate_header_recycler_view_frame);
        mPtrFrame.setLastUpdateTimeRelateObject(this);

        //materialHeader();//change header

        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                updateData();
            }

            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
//                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header); //same work
                return canDoRefresh(recyclerView);
            }
        });

        // the following are default settings
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        // default is false
        mPtrFrame.setPullToRefresh(false);
        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
        mPtrFrame.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPtrFrame.autoRefresh();
            }
        }, 100);

        return contentView;
    }

    private void materialHeader() {
        // header
        final MaterialHeader header = new MaterialHeader(getContext());
        int[] colors = getResources().getIntArray(R.array.google_colors);
        header.setColorSchemeColors(colors);
        header.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        header.setPadding(0, LocalDisplay.dp2px(15), 0, LocalDisplay.dp2px(10));
        header.setPtrFrameLayout(mPtrFrame);

        mPtrFrame.setLoadingMinTime(1000);
        mPtrFrame.setDurationToCloseHeader(1500);
        mPtrFrame.setHeaderView(header);
        mPtrFrame.addPtrUIHandler(header);
    }

    protected void updateData() {
        DemoRequestData.getImageList(new RequestFinishHandler<JsonData>() {
            @Override
            public void onRequestFinish(final JsonData data) {
                mPtrFrame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.getDataList().clear();
                        mAdapter.getDataList().addAll(data.optJson("data").optJson("list").toArrayList());
                        mPtrFrame.refreshComplete();
                        mAdapter.notifyDataSetChanged();
                    }
                }, 0);
            }
        });
    }

    public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        protected ArrayList<JsonData> mItemDataList = new ArrayList();
        protected OnItemClickListener onItemClickListener;

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(view, onItemClickListener);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (position < getItemCount() && position < mItemDataList.size()) {
                final ViewHolder viewHolder = (ViewHolder) holder;
                viewHolder.mImageView.loadImage(mImageLoader, mItemDataList.get(position).optString("pic"));
            }
        }

        @Override
        public int getItemCount() {
            return mItemDataList.size();
        }

        public JsonData getItem(final int position) {
            if (position < mItemDataList.size()) {
                return mItemDataList.get(position);
            } else {
                return null;
            }
        }

        public ArrayList<JsonData> getDataList() {
            return mItemDataList;
        }

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.onItemClickListener = onItemClickListener;
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private OnItemClickListener mOnItemClickListener;
        CubeImageView mImageView;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        public ViewHolder(View itemView, OnItemClickListener onItemClickListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            this.mOnItemClickListener = onItemClickListener;
            mImageView = (CubeImageView) itemView.findViewById(R.id.list_view_item_image_view);
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        @Override
        public void onClick(View v) {
            if (null != mOnItemClickListener) {
                mOnItemClickListener.onItemClick(v, getLayoutPosition());
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(final View view, final int position);
    }

    //check
    public boolean canDoRefresh(final RecyclerView recyclerView) {
        if (recyclerView.getChildCount() == 0) {
            return true;
        }
        int top = recyclerView.getChildAt(0).getTop();
        if (top != 0) {
            return false;
        }
//        final RecyclerView recyclerView = (RecyclerView) mRecyclerView;
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            int position = ((LinearLayoutManager) layoutManager).findFirstCompletelyVisibleItemPosition();
            if (position == 0) {
                return true;
            } else if (position == -1) {
                position = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
                return position == 0;
            }
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            boolean allViewAreOverScreen = true;
            int[] positions = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
            for (int i = 0; i < positions.length; i++) {
                if (positions[i] == 0) {
                    return true;
                }
                if (positions[i] != -1) {
                    allViewAreOverScreen = false;
                }
            }
            if (allViewAreOverScreen) {
                positions = ((StaggeredGridLayoutManager) layoutManager).findFirstVisibleItemPositions(null);
                for (int i = 0; i < positions.length; i++) {
                    if (positions[i] == 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
