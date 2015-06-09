package in.srain.cube.views.ptr.demo.ui.viewpager;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;
import in.srain.cube.mints.base.TitleBaseActivity;
import in.srain.cube.request.JsonData;
import in.srain.cube.request.RequestFinishHandler;
import in.srain.cube.util.LocalDisplay;
import in.srain.cube.views.pager.TabPageIndicator;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.demo.R;
import in.srain.cube.views.ptr.demo.data.DemoRequestData;

import java.util.ArrayList;

public class ViewPagerActivity extends TitleBaseActivity {

    private TabPageIndicator mCatTabPageIndicator;
    private ViewPager mFragmentViewPager;
    private FragmentViewPagerAdapter mPagerAdapter;
    private PtrFrameLayout mPtrFrame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHeaderTitle(R.string.ptr_demo_block_with_view_pager);
        setContentView(R.layout.activity_view_pager);
        initCateViews();
    }

    private void initCateViews() {

        int startIndex = 0;

        mCatTabPageIndicator = (TabPageIndicator) findViewById(R.id.view_pager_tab_indicator);
        mFragmentViewPager = (ViewPager) this.findViewById(R.id.view_pager_view_pager);
        ArrayList<ViewPagerFragment> list = new ArrayList<ViewPagerFragment>();

        for (int i = 1; i <= 8; i++) {
            list.add(ViewPagerFragment.create(i));
        }
        mPagerAdapter = new FragmentViewPagerAdapter(getSupportFragmentManager(), list);
        mFragmentViewPager.setAdapter(mPagerAdapter);

        mCatTabPageIndicator.setViewHolderCreator(new TabPageIndicator.ViewHolderCreator() {
            @Override
            public TabPageIndicator.ViewHolderBase createViewHolder() {
                return new HomeCatItemViewHolder();
            }
        });
        mCatTabPageIndicator.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {

            @Override
            public void onPageSelected(int i) {
                switchTo(i);
            }
        });
        mCatTabPageIndicator.setViewPager(mFragmentViewPager);

        mPtrFrame = (PtrClassicFrameLayout) findViewById(R.id.view_pager_ptr_frame);
        mPtrFrame.disableWhenHorizontalMove(true);
        mPtrFrame.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return mPagerAdapter.checkCanDoRefresh();
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                mPagerAdapter.updateData();
            }
        });
        mFragmentViewPager.setCurrentItem(startIndex);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mCatTabPageIndicator.moveToItem(mFragmentViewPager.getCurrentItem());
    }

    private void switchTo(int position) {
        mPagerAdapter.switchTO(position);
    }

    private class FragmentViewPagerAdapter extends FragmentStatePagerAdapter {

        private final ArrayList<ViewPagerFragment> mViewPagerFragments;
        private ViewPagerFragment mCurrentFragment;

        public FragmentViewPagerAdapter(FragmentManager fm, ArrayList<ViewPagerFragment> list) {
            super(fm);
            mViewPagerFragments = list;
        }

        @Override
        public Fragment getItem(int position) {
            return mViewPagerFragments.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
        }

        protected void updateData() {
            final ViewPagerFragment fragment = mCurrentFragment;
            DemoRequestData.getImageList(new RequestFinishHandler<JsonData>() {
                @Override
                public void onRequestFinish(final JsonData data) {
                    if (fragment == mCurrentFragment) {
                        fragment.update(data);
                        mPtrFrame.refreshComplete();
                    }
                }
            });
        }

        @Override
        public int getCount() {
            return mViewPagerFragments.size();
        }

        public void switchTO(final int position) {
            int len = mViewPagerFragments.size();
            for (int i = 0; i < len; i++) {
                ViewPagerFragment fragment = mViewPagerFragments.get(i);
                if (i == position) {
                    mCurrentFragment = fragment;
                    fragment.show();
                } else {
                    fragment.hide();
                }
            }
        }

        public boolean checkCanDoRefresh() {
            if (mCurrentFragment == null) {
                return true;
            }
            return mCurrentFragment.checkCanDoRefresh();
        }
    }

    private class HomeCatItemViewHolder extends TabPageIndicator.ViewHolderBase {

        private TextView mNameView;
        private View mTagView;

        @Override
        public View createView(LayoutInflater layoutInflater, int position) {
            View view = layoutInflater.inflate(R.layout.view_pager_indicator_item, null);
            view.setLayoutParams(new AbsListView.LayoutParams(LocalDisplay.dp2px(40), -1));
            mNameView = (TextView) view.findViewById(R.id.view_pager_indicator_name);
            mTagView = view.findViewById(R.id.view_pager_indicator_tab_current);
            return view;
        }

        @Override
        public void updateView(int position, boolean isCurrent) {
            mNameView.setText(position + "");
            if (isCurrent) {
                mTagView.setVisibility(View.VISIBLE);
            } else {
                mTagView.setVisibility(View.INVISIBLE);
            }
        }
    }
}