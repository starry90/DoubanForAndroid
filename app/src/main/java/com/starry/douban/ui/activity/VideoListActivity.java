package com.starry.douban.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;

import com.ogaclejapan.smarttablayout.SmartTabLayout;
import com.starry.douban.R;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.base.BaseFragmentPagerAdapter;
import com.starry.douban.constant.Apis;
import com.starry.douban.databinding.ActivityVideoListBinding;
import com.starry.douban.ui.fragment.VideoFragment;
import com.starry.douban.util.JsonUtil;
import com.starry.douban.util.RegexHelper;
import com.starry.douban.util.ToastUtil;
import com.starry.http.HttpManager;
import com.starry.http.callback.StringCallback;
import com.starry.http.error.ErrorModel;
import com.starry.log.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author Starry Jerry
 * @since 2021-07-18
 */

public class VideoListActivity extends BaseActivity<ActivityVideoListBinding> {

    public static void showActivity(Activity context) {
        Intent intent = new Intent(context, VideoListActivity.class);
        context.startActivity(intent);
    }

    @Override
    public ActivityVideoListBinding getViewBinding(LayoutInflater layoutInflater) {
        return ActivityVideoListBinding.inflate(layoutInflater);
    }

    @Override
    public void initData() {
        setTitle("视频");
        viewBinding.tab.addView(LayoutInflater.from(getActivity()).inflate(R.layout.tab_smart_indicator, viewBinding.tab, false));
        loadData();
    }

    @Override
    public void loadData() {
        HttpManager.get(Apis.VIDEO_HOST)
                .tag(this)
                .build()
                .enqueue(new StringCallback<String>() {

                    @Override
                    public void onSuccess(String response, Object... obj) {
                        //    <div class="hend">
                        //            <ul>
                        //                <li><a href="/">首页</a></li>
                        //                <li><a href="/***shipin/***">***</a></li>
                        //                <li><a href="/***shipin/***">***</a></li>
                        //                <li><a href="/***shipin/***">***</a></li>
                        //                <li><a href="/***shipin/***">***</a></li>
                        //                <li><a href="/***shipin/***">***</a></li>
                        //                <li><a href="/***shipin/***">***</a></li>
                        //                <li><a href="/***shipin/***">***</a></li>
                        //            </ul>
                        //        </div>
                        Document parse = Jsoup.parse(response);
                        //组合选择器
                        //parent > child: (p > span: 选择p元素的直接子元素中的span元素)
                        Elements videoElements = parse.select("div.hend > ul > li");
                        ArrayList<String> tabTitles = new ArrayList<>();
                        ArrayList<String> tabUrls = new ArrayList<>();

                        for (Element videoItem : videoElements) {
                            String video = videoItem.toString();

                            Matcher tagMatcher = RegexHelper.matcher(video, "\">", "<");
                            if (tagMatcher.find()) {
                                String tag = tagMatcher.group();
                                if (tag.contains("首页")) {
                                    //跳过首页
                                    continue;
                                }
                                tabTitles.add(tagMatcher.group());
                            }

                            Matcher tagUrlMatcher = RegexHelper.matcher(video, "<a href=\"", "\"");
                            if (tagUrlMatcher.find()) {
                                tabUrls.add(Apis.VIDEO_HOST + tagUrlMatcher.group());
                            }
                        }
                        Logger.json(JsonUtil.toJson(tabTitles));
                        Logger.json(JsonUtil.toJson(tabUrls));

                        initTab(tabTitles, tabUrls);
                    }

                    @Override
                    public void onFailure(ErrorModel errorModel) {
                        ToastUtil.showToast(errorModel.getMessage());
                    }

                    @Override
                    public void onAfter(boolean success) {
                        hideLoading(success);
                    }
                });
    }

    private void initTab(List<String> tabTitles, List<String> tabUrls) {
        SmartTabLayout viewPagerTab = viewBinding.tab.findViewById(R.id.viewpager_tab);
        List<Fragment> pages = new ArrayList<>();
        for (String tabUrl : tabUrls) {
            pages.add(VideoFragment.getInstance(tabUrl));
        }
        BaseFragmentPagerAdapter adapter = new BaseFragmentPagerAdapter(getSupportFragmentManager(), pages);
        adapter.setPageTitles(tabTitles);
        viewBinding.viewpager.setAdapter(adapter);
        viewBinding.viewpager.setOffscreenPageLimit(tabTitles.size());
        viewPagerTab.setViewPager(viewBinding.viewpager);
    }

}
