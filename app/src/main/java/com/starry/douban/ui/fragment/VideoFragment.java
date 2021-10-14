package com.starry.douban.ui.fragment;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.starry.douban.adapter.VideoAdapter;
import com.starry.douban.base.BaseFragment;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.constant.Apis;
import com.starry.douban.databinding.FragmentVideoBinding;
import com.starry.douban.model.VideoItemBean;
import com.starry.douban.ui.activity.VideoPlayerActivity;
import com.starry.douban.util.JsonUtil;
import com.starry.douban.util.RegexHelper;
import com.starry.douban.util.StringUtils;
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
 * @since 21-7-24.
 */
public class VideoFragment extends BaseFragment<FragmentVideoBinding> {

    private static final String EXTRA_VIDEO_TAG_URL = "extra_video_tag_url";

    private VideoAdapter mAdapter;

    private String videoTagUrl;

    private int start = 1;

    private static final String PAGE_NUMBER_FORMAT = "/index_%d.html";

    private String pageNumber = "";

    public static VideoFragment getInstance(String videoTagUrl) {
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_VIDEO_TAG_URL, videoTagUrl);
        VideoFragment movieFragment = new VideoFragment();
        movieFragment.setArguments(bundle);
        return movieFragment;
    }

    @Override
    public FragmentVideoBinding getViewBinding(LayoutInflater layoutInflater) {
        return FragmentVideoBinding.inflate(layoutInflater);
    }

    @Override
    public void initData() {
        videoTagUrl = getArguments().getString(EXTRA_VIDEO_TAG_URL);
        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new VideoAdapter();
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                getMp4Url(mAdapter.getItem(position));
            }
        });

        viewBinding.rvVideoList.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        viewBinding.rvVideoList.setAdapter(mAdapter);
        viewBinding.rvVideoList.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                start = 1;
                pageNumber = "";
                loadData();
            }

            @Override
            public void onLoadMore() {
                loadData();
            }
        });
    }

    @Override
    public void onLazyLoadingData() {
        super.onLazyLoadingData();
        loadData();
    }

    @Override
    public void loadData() {
        HttpManager.get(videoTagUrl + pageNumber)
                .headers("referer", videoTagUrl)
                .tag(this)
                .build()
                .enqueue(new StringCallback<String>() {

                    @Override
                    public void onSuccess(String response, Object... obj) {
                        //  <figure id="zhibo">
                        //            <a href="/***.html" title="***">
                        //               <div class="dimback"><img src="https://***.jpg"  class="lazy" alt="***"></div>
                        //            </a>
                        //  </figure>

                        // ================================================================ //

                        //   <figure>
                        //<div class="pos">
                        //            <a href="/***.html" title="推荐-***">
                        //                <img src="https://***.jpg"  class="lazy" alt="推荐-***">
                        //<p><span>2021-07-20</span></p>
                        //            </a>
                        //</div>
                        //            <a href="***.html" title="推荐-***">
                        //                <figcaption>
                        //                   推荐-***
                        //                </figcaption>
                        //            </a>
                        //</figure>
                        //
                        //
                        //<figure>
                        //<div class="pos">
                        //        <a href="https://***=1" target="_blank">
                        //            <img src="https://***.gif"  class="lazy" target="_blank">
                        //         </a>
                        //    </div>
                        //<a href="https://***=1" target="_blank">
                        //            <figcaption>
                        //              广告 - 官方
                        //            </figcaption>
                        //        </a>
                        //</figure>
                        Document parse = Jsoup.parse(response);
                        Elements videoElements;
                        if (videoTagUrl.contains("/zhi")) {
                            videoElements = parse.getElementsByTag("figure");
                        } else {
                            videoElements = parse.getElementsByClass("pos");
                        }

                        ArrayList<VideoItemBean> videoList = new ArrayList<>();
                        for (Element bookElement : videoElements) {
                            String video = bookElement.toString();

                            if (video.contains("target=\"_blank\">")) {
                                //跳过广告
                                continue;
                            }

                            VideoItemBean videoItemBean = new VideoItemBean();
                            Matcher imageMatcher = RegexHelper.matcher(video, "<img src=\"", "\"");
                            if (imageMatcher.find()) {
                                videoItemBean.setVideoImage(imageMatcher.group());
                            }

                            Matcher subjectMatcher = RegexHelper.matcher(video, "<a href=\"", "\"");
                            if (subjectMatcher.find()) {
                                videoItemBean.setSubjectUrl(Apis.VIDEO_HOST + subjectMatcher.group());
                            }

                            Matcher titleMatcher = RegexHelper.matcher(video, "title=\"", "\"");
                            if (titleMatcher.find()) {
                                videoItemBean.setVideoTitle(titleMatcher.group());
                            }

                            videoList.add(videoItemBean);
                        }
                        Logger.json(JsonUtil.toJson(videoList));
                        refreshList(videoList);
                    }

                    @Override
                    public void onFailure(ErrorModel errorModel) {
                        ToastUtil.showToast(errorModel.getMessage());
                    }

                    @Override
                    public void onAfter(boolean success) {
                        viewBinding.rvVideoList.refreshComplete();
                        viewBinding.rvVideoList.loadMoreComplete();
                        hideLoading(success);
                    }
                });
    }

    private void refreshList(List<VideoItemBean> results) {
        //如果是第一页先清空数据
        viewBinding.rvVideoList.setVisibility(View.VISIBLE);
        if (start++ == 1) {
            mAdapter.setAll(results);
        } else {
            mAdapter.addAll(results);
        }
        pageNumber = StringUtils.format(PAGE_NUMBER_FORMAT, start);
        //如果没有数据了，禁用加载更多功能
//        viewBinding.rvBookList.setLoadingMoreEnabled(hasData);

        if (mAdapter.getItemCount() == 0) {
            showEmpty();
        }
    }

    private void getMp4Url(final VideoItemBean videoItemBean) {
        HttpManager.get(videoItemBean.getSubjectUrl())
                .headers("referer", videoTagUrl)
                .tag(this)
                .build()
                .enqueue(new StringCallback<String>() {
                    @Override
                    public void onSuccess(String response, Object... obj) {
                        //  <tr>
                        //    <td width="70%"><input type="text" id="toolsid1" data-clipboard-text="https://***.mp4" value="https://***.mp4"></td>
                        //    <td width="29%">
                        //      <a id="fdmclick" href="javascript:void(0);"   data-clipboard-text="https://***.mp4">
                        //          <img src="/***.png">
                        //      </a>
                        //        <a href="https://***" target="_blank"><img src="/***.png"></a>
                        //    </td>
                        //</tr>
                        Matcher mp4Matcher = RegexHelper.matcher(response, "mp4\" value=\"", "\"");
                        if (mp4Matcher.find()) {
                            videoItemBean.setVideoPlayerUrl(mp4Matcher.group());
                            VideoPlayerActivity.showActivity(mActivity, videoItemBean);
                        } else {
                            ToastUtil.showToast("未找到视频地址");
                        }
                    }

                    @Override
                    public void onFailure(ErrorModel errorModel) {
                        ToastUtil.showToast(errorModel.getMessage());
                    }
                });
    }

}
