package com.starry.douban.ui.activity;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.starry.douban.R;
import com.starry.douban.adapter.MoviePhotoAdapter;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.constant.Apis;
import com.starry.douban.http.HttpManager;
import com.starry.douban.http.callback.StringCallback;
import com.starry.douban.http.error.ErrorModel;
import com.starry.douban.image.ImageManager;
import com.starry.douban.model.MovieDetail;
import com.starry.douban.util.ToastUtil;
import com.starry.douban.widget.LoadingDataLayout;

import java.util.List;

import butterknife.BindView;

/**
 * 电影详情页
 *
 * @author Starry Jerry
 * @since 2016/12/31.
 */
public class MovieDetailActivity extends BaseActivity {

    @BindView(R.id.iv_movie_detail_bg)
    ImageView iv_movie_detail_bg;
    @BindView(R.id.iv_movie_detail)
    ImageView ivMovieDetail;
    @BindView(R.id.tv_movie_detail_title)
    TextView tvMovieDetailTitle;
    @BindView(R.id.tv_movie_detail_original_title)
    TextView tvMovieDetailOriginalTitle;
    @BindView(R.id.tv_movie_detail_rating)
    TextView tvMovieDetailRating;
    /**
     * 评分人数
     */
    @BindView(R.id.tv_movie_detail_rating_count)
    TextView tv_movie_detail_rating_count;
    @BindView(R.id.tv_movie_detail_genres)
    TextView tvMovieDetailGenres;
    @BindView(R.id.tv_movie_detail_countries)
    TextView tvMovieDetailCountries;
    @BindView(R.id.tv_movie_detail_summary)
    TextView tv_movie_detail_summary;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private String url;

    @Override
    public int getLayoutResID() {
        return R.layout.activity_movie_detail;
    }

    @Override
    protected Drawable getToolbarBackground() {
        return new ColorDrawable(0x00000000); //透明背景
    }

    @Override
    public void initData() {
        url = Apis.MovieDetail + getIntent().getStringExtra("movieId");

        loadData();
    }

    private void initRecyclerView(List<MovieDetail.PerformerBean> performerBeanList) {
        MoviePhotoAdapter mAdapter = new MoviePhotoAdapter(getActivity(), performerBeanList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(mAdapter);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                recyclerView.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });
    }

    @Override
    public void loadData() {
        HttpManager.get()
                .tag(this)
                .url(url)
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
                });
    }

    private String format(String str) {
        return str.replace("[", "").replace("]", " ").replace(",", " ");
    }

    public void showMovieDetail(MovieDetail response) {
        showLoadingStatus(LoadingDataLayout.STATUS_SUCCESS);
        ImageManager.getBitmap(ivMovieDetail, response.getImages().getLarge(), iv_movie_detail_bg);
        tvMovieDetailTitle.setText(response.getTitle());
        tvMovieDetailOriginalTitle.setText(response.getOriginal_title());
        tvMovieDetailRating.setText(String.valueOf(response.getRating().getAverage()));
        tv_movie_detail_rating_count.setText("（" + response.getRatings_count() + "人评）");
        tvMovieDetailGenres.setText(format(response.getGenres().toString()));
        tvMovieDetailCountries.setText(format(response.getCountries().toString()) + " / " + response.getYear());
        tv_movie_detail_summary.setText(response.getSummary());

        List<MovieDetail.PerformerBean> directors = response.getDirectors();
        directors.addAll(response.getCasts());
        initRecyclerView(directors);
    }

}
