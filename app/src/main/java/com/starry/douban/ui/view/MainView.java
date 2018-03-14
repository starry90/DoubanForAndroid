package com.starry.douban.ui.view;

import com.starry.douban.model.Books;
import com.starry.douban.ui.BaseView;

/**
 * @author Starry Jerry
 * @since 2017/3/12.
 */
public interface MainView extends BaseView {


    /**
     * 刷新图书列表
     *
     * @param response Books
     */
    void refreshBookList(Books response);

}
