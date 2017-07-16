package com.starry.douban.model;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2016/12/10.
 */
public class Books extends BaseModel {

    private List<BookBean> books;

    public List<BookBean> getBooks() {
        return books;
    }

    public Books setBooks(List<BookBean> books) {
        this.books = books;
        return this;
    }
}
