package com.starry.douban.ui;

/**
 * @author Starry Jerry
 * @since 2017/3/12.
 */
public interface BaseView extends ILoadingView {


    /**
     * 请求失败
     *
     * @param message 失败提示消息
     * @param code    确认是哪一个请求
     * @param obj     扩展参数
     */
    void onFailure(String message, int code, Object... obj);


}
