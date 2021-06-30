package com.starry.douban.ui.activity;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;

import com.starry.douban.adapter.MoviePhotoAdapter;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.constant.Apis;
import com.starry.douban.databinding.ActivityMovieDetailBinding;
import com.starry.douban.image.ImageManager;
import com.starry.douban.model.MovieItemDetailBean;
import com.starry.douban.util.JsonUtil;
import com.starry.douban.util.ToastUtil;
import com.starry.http.HttpManager;
import com.starry.http.callback.StringCallback;
import com.starry.http.error.ErrorModel;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 电影详情页
 *
 * @author Starry Jerry
 * @since 2016/12/31.
 */
public class MovieDetailActivity extends BaseActivity<ActivityMovieDetailBinding> {

    public static final String EXTRA_MOVIE_ID = "extra_movie_id";

    private String url;

    @Override
    public ActivityMovieDetailBinding getViewBinding(LayoutInflater layoutInflater) {
        return ActivityMovieDetailBinding.inflate(layoutInflater);
    }

    @Override
    protected Drawable getToolbarBackground() {
        return new ColorDrawable(0x00000000); //透明背景
    }

    @Override
    public void initData() {
        setTitle("电影");
        url = Apis.MOVIE_DETAIL + getIntent().getStringExtra(EXTRA_MOVIE_ID);
        loadData();
    }

    private void initRecyclerView(List<MovieItemDetailBean.PersonBean> performerBeanList) {
        MoviePhotoAdapter mAdapter = new MoviePhotoAdapter(performerBeanList);
        viewBinding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        viewBinding.recyclerView.setAdapter(mAdapter);
        viewBinding.recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                viewBinding.recyclerView.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    @Override
    public void loadData() {
        HttpManager.get(url)
                .tag(this)
                .build()
                .enqueue(new StringCallback<String>() {

                    @Override
                    public void onSuccess(String response, Object... obj) {
                        Document parse = Jsoup.parse(response);
                        Elements script = parse.getElementsByTag("script");
                        for (Element child : script) {
                            if (child.outerHtml().contains("application/ld+json")) {
                                showMovieDetail(JsonUtil.toObject(child.data(), MovieItemDetailBean.class));
                                break;
                            }
                        }
                    }

                    @Override
                    public void onFailure(ErrorModel errorModel) {
                        ToastUtil.showToast(errorModel.getMessage());
                    }

                    @Override
                    public void onAfter(boolean success) {
                        super.onAfter(success);
                        hideLoading(success);
                    }
                });
    }

    private String format(String str) {
        return str.replace("[", "").replace("]", " ").replace(",", " ");
    }

    public void showMovieDetail(MovieItemDetailBean response) {
        ImageManager.getBitmap(viewBinding.ivMovieDetail, response.getImage(), viewBinding.ivMovieDetailBg);
        setMovieTitle(response.getName());
        viewBinding.tvMovieDetailRating.setText(String.valueOf(response.getAggregateRating().getRatingValue()));
        viewBinding.tvMovieDetailRatingCount.setText("（" + response.getAggregateRating().getRatingCount() + "人评）");
        viewBinding.tvMovieDetailGenres.setText(format(response.getGenre().toString()));
        String duration = response.getDuration().replace("PT", "").replace("H", "小时").replace("M", "分钟");
        viewBinding.tvMovieDetailCountries.setText(format(response.getDatePublished()) + " / " + duration);
        viewBinding.tvMovieDetailSummary.setText(response.getDescription());

        List<MovieItemDetailBean.PersonBean> directors = response.getDirector();
        directors.addAll(response.getActor());
        initRecyclerView(directors);
    }

    private void setMovieTitle(String titleName) {
        List<String> nameLis = new ArrayList<>(Arrays.asList(titleName.split(" ")));
        String title = nameLis.get(0);
        String originalTitle = title;
        if (nameLis.size() >= 2) {
            nameLis.remove(0);
            originalTitle = format(nameLis.toString());
        }
        viewBinding.tvMovieDetailTitle.setText(title);
        viewBinding.tvMovieDetailOriginalTitle.setText(originalTitle);
    }

}
