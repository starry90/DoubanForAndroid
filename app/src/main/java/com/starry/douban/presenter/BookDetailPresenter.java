package com.starry.douban.presenter;

import com.starry.douban.constant.RequestFlag;
import com.starry.douban.http.CommonCallback;
import com.starry.douban.http.HttpManager;
import com.starry.douban.model.BookDetail;
import com.starry.douban.model.ErrorModel;
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
     */
    public void getData(String url) {
        HttpManager.get()
                .tag(mView)
                .url(url)
                .build()
                .execute(new CommonCallback<BookDetail>(mView) {

                    @Override
                    public void onSuccess(BookDetail response, Object... obj) {
                        mView.showBookDetail(response);
                    }

                    @Override
                    public void onFailure(ErrorModel errorModel) {
                        errorModel.setRequestCode(RequestFlag.BOOK_DETAIL);
                        mView.onFailure(errorModel);
                    }
                });
    }
}
