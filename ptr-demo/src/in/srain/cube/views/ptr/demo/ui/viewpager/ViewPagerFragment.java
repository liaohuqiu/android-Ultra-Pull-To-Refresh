package in.srain.cube.views.ptr.demo.ui.viewpager;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import in.srain.cube.app.CubeFragment;
import in.srain.cube.image.ImageLoader;
import in.srain.cube.image.ImageLoaderFactory;
import in.srain.cube.request.JsonData;
import in.srain.cube.util.CLog;
import in.srain.cube.views.list.ListViewDataAdapter;
import in.srain.cube.views.ptr.demo.R;
import in.srain.cube.views.ptr.demo.ui.MaterialStyleFragment;

public class ViewPagerFragment extends CubeFragment {

    private int mPage;
    private ListView mListView;
    private ImageLoader mImageLoader;
    private ListViewDataAdapter<JsonData> mAdapter;

    public static ViewPagerFragment create(int page) {
        ViewPagerFragment fragment = new ViewPagerFragment();
        fragment.mPage = page;
        return fragment;
    }

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mImageLoader = ImageLoaderFactory.create(getContext()).tryToAttachToContainer(getContext());
        View view = inflater.inflate(R.layout.fragment_view_pager, null);
        mListView = (ListView) view.findViewById(R.id.view_pager_list_view);

        View headerView = inflater.inflate(R.layout.view_pager_fragment_list_view_header, null);
        TextView mHeaderTextView = (TextView) headerView.findViewById(R.id.view_pager_fragment_list_view_header_title);
        mHeaderTextView.setBackgroundColor(0xff4d90fe * mPage / 30);
        mHeaderTextView.setText("Page: " + mPage);

        mListView.addHeaderView(headerView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0) {
					JsonData js = mAdapter.getItem(position);
                    final String url = js!=null?js.optString("pic"):null;
                    if (!TextUtils.isEmpty(url)) {
                        getContext().pushFragmentToBackStack(MaterialStyleFragment.class, url);
                    }
                }
            }
        });

        mAdapter = new ListViewDataAdapter<JsonData>();
        mAdapter.setViewHolderClass(this, ImageListViewHolder.class, mImageLoader);
        mListView.setAdapter(mAdapter);
        return view;
    }

    public void update(JsonData data) {
        mAdapter.getDataList().clear();
        mAdapter.getDataList().addAll(data.optJson("data").optJson("list").toArrayList());
        mAdapter.notifyDataSetChanged();
    }

    public void show() {

    }

    public void hide() {

    }

    public boolean checkCanDoRefresh() {
        if (mAdapter.getCount() == 0 || mListView == null) {
            return true;
        }
        CLog.d("test", "checkCanDoRefresh: %s %s", mListView.getFirstVisiblePosition(), mListView.getChildAt(0).getTop());
        return mListView.getFirstVisiblePosition() == 0 && mListView.getChildAt(0).getTop() == 0;
    }

}
