package com.starry.douban.ui.view;

import com.starry.douban.model.BookDetail;
import com.starry.douban.ui.BaseView;

/**
 * @author Starry Jerry
 * @since 2017/3/12.
 */
public interface BookDetailView extends BaseView {


    /**
     * 显示图书详情
     *
     * @param response BookDetail
     */
    void showBookDetail(BookDetail response);

}
