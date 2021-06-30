package com.starry.douban.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.starry.douban.R;
import com.starry.douban.base.BaseFragment;
import com.starry.douban.base.BaseFragmentPagerAdapter;
import com.starry.douban.databinding.FragmentMovieParentBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Starry Jerry
 * @since 2016/12/15.
 */
public class MovieParentFragment extends BaseFragment<FragmentMovieParentBinding> {

    /**
     * {"tags":["热门","最新","经典","可播放","豆瓣高分","冷门佳片","华语","欧美","韩国","日本","动作","喜剧","爱情","科幻","悬疑","恐怖","成长"]}
     */
    private String[] tabTitles = new String[]{"热门", "最新", "经典", "可播放", "豆瓣高分", "冷门佳片", "华语", "欧美", "韩国", "日本", "动作", "喜剧", "爱情", "科幻", "悬疑", "恐怖", "成长"};

    @Override
    public FragmentMovieParentBinding getViewBinding(LayoutInflater layoutInflater) {
        return FragmentMovieParentBinding.inflate(layoutInflater);
    }

    @Override
    public void initData() {
        viewBinding.tab.addView(LayoutInflater.from(getActivity()).inflate(R.layout.tab_smart_indicator, viewBinding.tab, false));
        SmartTabLayout viewPagerTab = (SmartTabLayout) viewBinding.tab.findViewById(R.id.viewpager_tab);

        List<Fragment> pages = new ArrayList<>();
        for (int i = 0; i < tabTitles.length; i++) {
            Bundle bundle = new Bundle();
            bundle.putString("tag", tabTitles[i]);
            MovieFragment movieFragment = new MovieFragment();
            movieFragment.setArguments(bundle);
            pages.add(movieFragment);
        }
        //https://stackoverflow.com/questions/38722325/fragmentmanager-is-already-executing-transactions-when-is-it-safe-to-initialise/40829361#40829361
        //java.lang.IllegalStateException: FragmentManager is already executing transactions
        //第一次getFragmentManager()获取到的FragmentManager，只提供给activity那一层使用。
        //在viewPager那一层只能使用getChildFragmentManager()获取FragmentManager来处理子fragment
        //https://www.jianshu.com/p/6d102b9332be
        BaseFragmentPagerAdapter adapter = new BaseFragmentPagerAdapter(getChildFragmentManager(), pages);
        adapter.setPageTitles(Arrays.asList(tabTitles));
        viewBinding.viewpager.setAdapter(adapter);
        viewBinding.viewpager.setOffscreenPageLimit(tabTitles.length);
        viewPagerTab.setViewPager(viewBinding.viewpager);
    }
}
