package com.starry.douban.presenter;

import com.starry.douban.http.CommonCallback;
import com.starry.douban.http.HttpManager;
import com.starry.douban.model.Movies;
import com.starry.douban.ui.view.MovieView;

import java.util.LinkedHashMap;

/**
 * @author Starry Jerry
 * @since 2017/3/12.
 */
public class MoviePresenter {

    private MovieView mView;

    public MoviePresenter(MovieView mView) {
        this.mView = mView;
    }

    /**
     * 获取图书列表
     *
     * @param url
     * @param start
     * @param count
     */
    public void getMovieList(String url, int start, int count) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("start", start + "");
        params.put("count", count + "");
        HttpManager.getInstance()
                .get()
                .tag(mView)
                .url(url)
                .params(params)
                .build()
                .execute(new CommonCallback<Movies>(mView) {

                    @Override
                    public void onSuccess(Movies response, Object... obj) {
                        mView.onRefreshList(response);
                    }

                    @Override
                    public void onFailure(String message, int code, Object... obj) {
                        mView.onFailure(message, code, obj);
                    }
                });
    }
}
