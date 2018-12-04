package com.starry.http.cookie;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;

/**
 * @author Starry Jerry
 * @since 2018-12-4.
 */
public abstract class OkHttpCookie implements CookieJar {

    private Map<String, ArrayList<String>> cookiesMap;

    /**
     * 保存cookie到文件
     *
     * @param key        host
     * @param cookieList cookie列表
     */
    public abstract void saveCookie(String key, List<String> cookieList);

    /**
     * 从文件中获取所有Cookie
     *
     * @return 所有Cookie
     */
    public abstract Map<String, ArrayList<String>> getAllCookie();

    public OkHttpCookie() {
        cookiesMap = getAllCookie();
        if (cookiesMap == null) {
            cookiesMap = new HashMap<>();
        }
    }

    @Override
    public void saveFromResponse(HttpUrl url, List<Cookie> cookies) {
        ArrayList<String> cookieList = new ArrayList<>();
        for (Cookie cookie : cookies) {
            cookieList.add(cookie.toString());
        }
        if (!cookieList.isEmpty()) {
            String host = url.host();
            saveCookie(host, cookieList);
            cookiesMap.put(host, cookieList);
        }
    }

    @Override
    public List<Cookie> loadForRequest(HttpUrl url) {
        ArrayList<Cookie> cookies = new ArrayList<>();
        ArrayList<String> cookieList = cookiesMap.get(url.host());
        if (cookieList != null && !cookieList.isEmpty()) {
            for (String cookie : cookieList) {
                cookies.add(Cookie.parse(url, cookie));
            }
        }
        return cookies;
    }
}
