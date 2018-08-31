package com.starry.douban.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.starry.douban.R;
import com.starry.douban.base.BaseFragment;
import com.starry.douban.base.BaseFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * @author Starry Jerry
 * @since 2016/12/15.
 */
public class MovieParentFragment extends BaseFragment {

    @BindView(R.id.tab)
    FrameLayout tab;
    @BindView(R.id.header)
    LinearLayout header;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private String[] tabTitles = new String[]{"正在热映", "即将上映", "Top250", "科幻", "喜剧"};

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_movie_parent;
    }

    @Override
    public void initData() {
        tab.addView(LayoutInflater.from(getActivity()).inflate(R.layout.tab_smart_indicator, tab, false));
        SmartTabLayout viewPagerTab = (SmartTabLayout) tab.findViewById(R.id.viewpager_tab);

        List<Fragment> pages = new ArrayList<>();
        for (int i = 0; i < tabTitles.length; i++) {
            Bundle bundle = new Bundle();
            bundle.putInt("type", i);
            MovieFragment movieFragment = new MovieFragment();
            movieFragment.setArguments(bundle);
            pages.add(movieFragment);
        }
        BaseFragmentPagerAdapter adapter = new BaseFragmentPagerAdapter(getActivity().getSupportFragmentManager(), pages);
        adapter.setPageTitles(Arrays.asList(tabTitles));
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(5);
        viewPagerTab.setViewPager(viewPager);
    }
}
