package com.starry.douban.ui;

/**
 * @author Starry Jerry
 * @since 2017/3/12.
 */
public interface ILoadingView {

    /**
     * 显示 Loading View
     */
    void showLoading();

    /**
     * 隐藏 Loading View
     *
     * @param status 1正常 2加载失败 3数据为空 {@link com.starry.douban.widget.LoadingDataLayout }
     */
    void hideLoading(int status);

    /**
     * 加载完成
     */
    void onLoadingComplete();

}
