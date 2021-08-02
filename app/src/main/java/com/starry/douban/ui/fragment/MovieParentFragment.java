package com.starry.douban.ui.fragment;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.starry.douban.R;
import com.starry.douban.base.BaseFragment;
import com.starry.douban.base.BaseFragmentPagerAdapter;
import com.starry.douban.databinding.FragmentMovieParentBinding;
import com.starry.douban.util.UiUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Starry Jerry
 * @since 2016/12/15.
 */
public class MovieParentFragment extends BaseFragment<FragmentMovieParentBinding> {

    /**
     * https://movie.douban.com/j/search_tags
     * {"tags":["热门","最新","经典","可播放","豆瓣高分","冷门佳片","华语","欧美","韩国","日本","动作","喜剧","爱情","科幻","悬疑","恐怖","成长"]}
     */
    private final String[] tabTitles = new String[]{"热门", "最新", "经典", "可播放", "豆瓣高分", "冷门佳片", "华语", "欧美", "韩国", "日本", "动作", "喜剧", "爱情", "科幻", "悬疑", "恐怖", "成长"};

    @Override
    protected Drawable getToolbarBackground() {
        //黑色文字
        setTitleColor(R.color.text_color_level_1);
        return new ColorDrawable(0x0FFFFFFFF); //白色背景
    }

    @Override
    public FragmentMovieParentBinding getViewBinding(LayoutInflater layoutInflater) {
        return FragmentMovieParentBinding.inflate(layoutInflater);
    }

    @Override
    public void initData() {
        setTitle("电影");
        //Activity加载多Fragment时，toolbar不设置fitsSystemWindows，
        // 否则会导致自定义标题往距上边距有个状态栏高度，导致标题未垂直居中
        toolbar.setFitsSystemWindows(false);
        viewBinding.viewStatusBarFix.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, UiUtils.getStatusBarHeight(mActivity)));

        viewBinding.tab.addView(LayoutInflater.from(getActivity()).inflate(R.layout.tab_smart_indicator, viewBinding.tab, false));
        SmartTabLayout viewPagerTab = (SmartTabLayout) viewBinding.tab.findViewById(R.id.viewpager_tab);

        List<Fragment> pages = new ArrayList<>();
        for (String tabTitle : tabTitles) {
            Bundle bundle = new Bundle();
            bundle.putString("tag", tabTitle);
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
