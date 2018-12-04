package com.starry.douban.env;

import com.google.gson.reflect.TypeToken;
import com.starry.douban.constant.Common;
import com.starry.douban.util.JsonUtil;
import com.starry.douban.util.SPUtil;
import com.starry.http.cookie.OkHttpCookie;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Starry Jerry
 * @since 2018-12-4.
 */
public class CookieImpl extends OkHttpCookie {
    @Override
    public void saveCookie(String key, List<String> cookieList) {
        SPUtil.putString(key, JsonUtil.toJson(cookieList), Common.SP_FILE_COOKIE);
    }

    @Override
    public Map<String, ArrayList<String>> getAllCookie() {
        Map<String, ArrayList<String>> cookieMap = new HashMap<>();
        Map<String, ?> all = SPUtil.getAll(Common.SP_FILE_COOKIE);
        for (Map.Entry<String, ?> entry : all.entrySet()) {
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            ArrayList<String> cookieList = JsonUtil.toObject(entry.getValue().toString(), type);
            cookieMap.put(entry.getKey(), cookieList);
        }
        return cookieMap;
    }
}
