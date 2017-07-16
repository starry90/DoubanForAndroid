package com.starry.douban.ui.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.starry.douban.util.viewpager.v4.FragmentPagerItem;
import com.starry.douban.util.viewpager.v4.FragmentPagerItemAdapter;
import com.starry.douban.util.viewpager.v4.FragmentPagerItems;
import com.starry.douban.R;
import com.starry.douban.base.BaseLazyFragment;

import butterknife.BindView;

/**
 * @author Starry Jerry
 * @since 2016/12/15.
 */
public class MovieParentFragment extends BaseLazyFragment {

    @BindView(R.id.tab)
    FrameLayout tab;
    @BindView(R.id.header)
    LinearLayout header;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    FragmentPagerItemAdapter mAdapter;

    private String[] tabTitles = new String[]{"正在热映", "即将上映", "Top250", "科幻", "喜剧"};

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_movie_parent;
    }

    @Override
    public void initData() {
        tab.addView(LayoutInflater.from(getActivity()).inflate(R.layout.tab_smart_indicator, tab, false));
        SmartTabLayout viewPagerTab = (SmartTabLayout) tab.findViewById(R.id.viewpagertab);

        FragmentPagerItems pages = new FragmentPagerItems(getActivity());
        for (int i = 0; i < tabTitles.length; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt("type", i);
            pages.add(FragmentPagerItem.of(tabTitles[i], MovieFragment.class, bundle));
        }

        mAdapter = new FragmentPagerItemAdapter(getActivity().getSupportFragmentManager(), pages);

        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(mAdapter);
        viewPagerTab.setViewPager(viewPager);
    }

    @Override
    public void setListener() {

    }

    @Override
    public void onLazyLoadingData() {
        super.onLazyLoadingData();
        ((MovieFragment) mAdapter.getPage(0)).onLazyLoadingData();
    }

    @Override
    public void loadData() {

    }
}
