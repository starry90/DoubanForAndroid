package com.starry.douban.ui.activity;

import android.graphics.drawable.ColorDrawable;
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
import com.starry.douban.image.ImageManager;
import com.starry.douban.model.ErrorModel;
import com.starry.douban.model.MovieDetail;
import com.starry.douban.presenter.MovieDetailPresenter;
import com.starry.douban.ui.view.MovieDetailView;
import com.starry.douban.widget.LoadingDataLayout;

import java.util.List;

import butterknife.BindView;

/**
 * 电影详情页
 *
 * @author Starry Jerry
 * @since 2016/12/31.
 */
public class MovieDetailActivity extends BaseActivity implements MovieDetailView {

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

    private MovieDetailPresenter mPresenter;

    @Override
    public int getLayoutResID() {
        return R.layout.activity_movie_detail;
    }

    @Override
    public void initData() {
        //Toolbar设置成透明
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(0x00000000));

        url = Apis.MovieDetail + getIntent().getStringExtra("movieId");

        mPresenter = new MovieDetailPresenter(this);

        loadData();
    }

    @Override
    public void setListener() {

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
        mPresenter.getData(url);
    }

    private String format(String str) {
        return str.replace("[", "").replace("]", " ").replace(",", " ");
    }

    @Override
    public void showMovieDetail(MovieDetail response) {
        showLoadingStatus(LoadingDataLayout.STATUS_SUCCESS);
        ImageManager.getBitmap(ivMovieDetail, response.getImages().getLarge(), iv_movie_detail_bg);
        tvMovieDetailTitle.setText(response.getTitle());
        tvMovieDetailOriginalTitle.setText(response.getOriginal_title());
        tvMovieDetailRating.setText(response.getRating().getAverage() + "");
        tv_movie_detail_rating_count.setText("（" + response.getRatings_count() + "人评）");
        tvMovieDetailGenres.setText(format(response.getGenres().toString()));
        tvMovieDetailCountries.setText(format(response.getCountries().toString()) + " / " + response.getYear());
        tv_movie_detail_summary.setText(response.getSummary());

        List<MovieDetail.PerformerBean> directors = response.getDirectors();
        directors.addAll(response.getCasts());
        initRecyclerView(directors);
    }

    @Override
    public void onFailure(ErrorModel errorModel) {

    }

}
