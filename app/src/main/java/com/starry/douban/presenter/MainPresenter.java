package com.starry.douban.presenter;

import com.starry.douban.constant.Apis;
import com.starry.douban.http.CommonCallback;
import com.starry.douban.http.HttpManager;
import com.starry.douban.model.Books;
import com.starry.douban.ui.ILoadingView;
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
     * 1
     * 获取图书列表
     *
     * @param tag
     * @param start
     * @param count
     */
    public void getBookList(String tag, int start, int count) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("tag", tag);
        params.put("start", start + "");
        params.put("count", count + "");
        HttpManager.getInstance()
                .get()
                .tag(mView)
                .url(Apis.BookSearch)
                .params(params)
                .build()
                .execute(new CommonCallback<Books>() {


                    @Override
                    public void onSuccess(Books response, Object... obj) {
                        mView.onRefreshList(response);
                    }

                    @Override
                    public void onFailure(String message, int code, Object... obj) {
                        mView.onFailure(message, code, obj);
                    }

                    @Override
                    public void setLoadingView(ILoadingView view) {
                        super.setLoadingView(mView);
                    }
                });
    }
}
