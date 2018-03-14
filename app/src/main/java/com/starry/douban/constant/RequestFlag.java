package com.starry.douban.constant;

/**
 * 当一个页面有多个请求时，在onFailure里用来区分是哪个请求的回调
 *
 * @author Starry Jerry
 * @since 18-3-14.
 */

public class RequestFlag {

    public final static int MAIN_BOOK_LIST = 1;
    public final static int MAIN_MOVIE_LIST = 2;
    public final static int BOOK_DETAIL = 3;
    public final static int MOVIE_DETAIL = 4;

}
