package com.starry.douban.presenter;

import com.starry.douban.constant.RequestFlag;
import com.starry.douban.http.CommonCallback;
import com.starry.douban.http.HttpManager;
import com.starry.douban.model.ErrorModel;
import com.starry.douban.model.MovieDetail;
import com.starry.douban.ui.view.MovieDetailView;

/**
 * @author Starry Jerry
 * @since 2017/3/12.
 */
public class MovieDetailPresenter {

    private MovieDetailView mView;

    public MovieDetailPresenter(MovieDetailView mView) {
        this.mView = mView;
    }

    /**
     * 获取图书列表
     */
    public void getData(String url) {
        HttpManager.get()
                .tag(mView)
                .url(url)
                .build()
                .execute(new CommonCallback<MovieDetail>(mView) {

                    @Override
                    public void onSuccess(MovieDetail response, Object... obj) {
                        mView.showMovieDetail(response);
                    }

                    @Override
                    public void onFailure(ErrorModel errorModel) {
                        errorModel.setRequestCode(RequestFlag.MOVIE_DETAIL);
                        mView.onFailure(errorModel);
                    }
                });
    }
}
