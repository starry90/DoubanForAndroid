package com.starry.douban.ui;

import com.starry.douban.model.ErrorModel;

/**
 * @author Starry Jerry
 * @since 2017/3/12.
 */
public interface BaseView extends ILoadingView {


    /**
     * 请求失败
     *
     * @param errorModel ErrorModel
     */
    void onFailure(ErrorModel errorModel);


}
