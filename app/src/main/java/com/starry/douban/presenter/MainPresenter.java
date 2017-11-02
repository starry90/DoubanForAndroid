package com.starry.douban.presenter;

import com.starry.douban.constant.Apis;
import com.starry.douban.http.CommonCallback;
import com.starry.douban.http.HttpManager;
import com.starry.douban.model.Books;
import com.starry.douban.ui.view.MainView;

import java.util.LinkedHashMap;

/**
 * @author Starry Jerry
 * @since 2017/3/12.
 */
public class MainPresenter {

    private MainView mView;

    public MainPresenter(MainView mView) {
        this.mView = mView;
    }

    /**
     * 获取图书列表
     */
    public void getBookList(LinkedHashMap<String, String> params) {
        HttpManager.get()
                .tag(mView)
                .url(Apis.BookSearch)
                .params(params)
                .build()
                .execute(new CommonCallback<Books>(mView) {


                    @Override
                    public void onSuccess(Books response, Object... obj) {
                        mView.onRefreshList(response);
                    }

                    @Override
                    public void onFailure(String message, int code, Object... obj) {
                        mView.onFailure(message, code, obj);
                    }

                });
    }
}
