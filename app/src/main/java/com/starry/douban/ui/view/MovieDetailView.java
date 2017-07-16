package com.starry.douban.ui.view;

import com.starry.douban.model.MovieDetail;
import com.starry.douban.ui.BaseView;

/**
 * @author Starry Jerry
 * @since 2017/3/12.
 */
public interface MovieDetailView extends BaseView {


    /**
     * 刷新电影列表
     *
     * @param response
     */
    void onRefresh(MovieDetail response);

}
