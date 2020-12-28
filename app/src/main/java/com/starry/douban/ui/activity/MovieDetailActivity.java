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
import com.starry.douban.model.MovieDetail;
import com.starry.douban.util.ToastUtil;
import com.starry.http.HttpManager;
import com.starry.http.callback.StringCallback;
import com.starry.http.error.ErrorModel;

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
        url = Apis.MovieDetail + getIntent().getStringExtra(EXTRA_MOVIE_ID);

        loadData();
    }

    private void initRecyclerView(List<MovieDetail.PerformerBean> performerBeanList) {
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
                .enqueue(new StringCallback<MovieDetail>() {

                    @Override
                    public void onSuccess(MovieDetail response, Object... obj) {
                        showMovieDetail(response);
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

    public void showMovieDetail(MovieDetail response) {
        ImageManager.getBitmap(viewBinding.ivMovieDetail, response.getImages().getLarge(), viewBinding.ivMovieDetailBg);
        viewBinding.tvMovieDetailTitle.setText(response.getTitle());
        viewBinding.tvMovieDetailOriginalTitle.setText(response.getOriginal_title());
        viewBinding.tvMovieDetailRating.setText(String.valueOf(response.getRating().getAverage()));
        viewBinding.tvMovieDetailRatingCount.setText("（" + response.getRatings_count() + "人评）");
        viewBinding.tvMovieDetailGenres.setText(format(response.getGenres().toString()));
        viewBinding.tvMovieDetailCountries.setText(format(response.getCountries().toString()) + " / " + response.getYear());
        viewBinding.tvMovieDetailSummary.setText(response.getSummary());

        List<MovieDetail.PerformerBean> directors = response.getDirectors();
        directors.addAll(response.getCasts());
        initRecyclerView(directors);
    }

}
