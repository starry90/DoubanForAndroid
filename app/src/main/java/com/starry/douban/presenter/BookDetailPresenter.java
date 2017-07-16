package com.starry.douban.presenter;

import com.starry.douban.http.CommonCallback;
import com.starry.douban.http.HttpManager;
import com.starry.douban.model.BookDetail;
import com.starry.douban.ui.view.BookDetailView;

/**
 * @author Starry Jerry
 * @since 2017/3/12.
 */
public class BookDetailPresenter {

    private BookDetailView mView;

    public BookDetailPresenter(BookDetailView mView) {
        this.mView = mView;
    }

    /**
     * 获取图书详情
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
                .execute(new CommonCallback<BookDetail>() {

                    @Override
                    public void onSuccess(BookDetail response, Object... obj) {
                        mView.onRefresh(response);
                    }

                    @Override
                    public void onFailure(String message, int code, Object... obj) {
                        mView.onFailure(message, code, obj);
                    }

                });
    }
}
