package com.starry.douban.constant;

/**
 * api来源：豆瓣
 * <p/>
 * https://developers.douban.com/
 */
public class Apis {

    public static final String GITHUB_AUTHOR_HOME = "https://github.com/starry90";

    public static final String APP_UPDATE = "https://github.com/starry90/DoubanForAndroid/raw/master/data/update.txt";

    public static final String HOST_DOUBAN = "https://api.douban.com/";

    /**
     * 图书搜索
     */
    public static String BookSearch = HOST_DOUBAN + "/v2/book/search";

    /**
     * 图书详情
     */
    public static String BookDetail = HOST_DOUBAN + "/v2/book/";

    /**
     * 正在热映的电影
     */
    public static String MovieInTheaters = HOST_DOUBAN + "/v2/movie/in_theaters";
    /**
     * 即将上映
     */
    public static String MovieComingSoon = HOST_DOUBAN + "/v2/movie/coming_soon";
    /**
     * Top250
     */
    public static String MovieTop250 = HOST_DOUBAN + "/v2/movie/top250";
    /**
     * 北美票房榜
     */
    public static String MovieUSBox = HOST_DOUBAN + "/v2/movie/us_box";
    /**
     * 口碑榜
     */
    public static String MovieWeekly = HOST_DOUBAN + "/v2/movie/weekly";
    /**
     * 新片榜
     */
    public static String MovieNew = HOST_DOUBAN + "/v2/movie/new_movies";

    /**
     * 电影详情
     */
    public static String MovieDetail = HOST_DOUBAN + "/v2/movie/subject/";

    /**
     * 电影搜索
     */
    public static String MovieSearch = HOST_DOUBAN + "/v2/movie/search";

}
