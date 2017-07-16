package com.starry.douban.presenter;

import com.starry.douban.http.CommonCallback;
import com.starry.douban.http.HttpManager;
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
     *
     * @param url
     */
    public void getData(String url) {
        HttpManager
                .getInstance(mView)
                .get()
                .tag(mView)
                .url(url)
                .build()
                .execute(new CommonCallback<MovieDetail>() {

                    @Override
                    public void onSuccess(MovieDetail response, Object... obj) {
                        mView.onRefresh(response);
                    }

                    @Override
                    public void onFailure(String message, int code, Object... obj) {
                        mView.onFailure(message, code, obj);
                    }

                });
    }
}
