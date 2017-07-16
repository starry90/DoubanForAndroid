package com.starry.douban.http;


import com.google.gson.reflect.TypeToken;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 网络请求回调类
 *
 * @param <T> 解析的对象
 * @author Starry Jerry
 * @since 2016/6/19.
 */
public abstract class CommonCallback<T> {

    /**
     * @param response 返回的对象
     * @param obj      可扩展参数
     */
    public abstract void onSuccess(T response, Object... obj);

    /**
     * @param message 失败提示
     * @param code    失败code
     * @param obj     可扩展参数
     */
    public abstract void onFailure(String message, int code, Object... obj);

    /**
     */
    public void onBefore() {
    }

    /**
     * @param
     */
    public void onAfter() {
    }

    /**
     * @param progress
     */
    public void inProgress(float progress) {

    }

    /**
     * 获取Json对象的类型，因为数据可能是Json数组也可能是Json对象
     */
    public Type getType() {
        Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if (type instanceof Class) {//如果是Object直接返回
            return type;
        } else {//如果是集合，获取集合的类型map或list
            return new TypeToken<T>() {}.getType();
        }
    }

}