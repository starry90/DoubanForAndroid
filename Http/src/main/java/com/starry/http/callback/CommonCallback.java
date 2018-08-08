package com.starry.http.callback;


import com.starry.http.error.ErrorModel;

import okhttp3.Response;

/**
 * 网络请求回调类
 *
 * @param <T> 解析的对象
 * @author Starry Jerry
 * @since 2016/6/19.
 */
public abstract class CommonCallback<T> {

    /**
     * 开始执行网络请求
     */
    public void onBefore() {
    }

    /**
     * parse {@link Response}
     *
     * @param response {@link Response}
     * @throws Exception
     */
    public abstract T parseResponse(Response response) throws Exception;

    /**
     * @param response 返回的对象
     * @param obj      可扩展参数
     */
    public abstract void onSuccess(T response, Object... obj);

    /**
     * @param errorModel ErrorModel
     */
    public abstract void onFailure(ErrorModel errorModel);

    /**
     * 网络请求结束
     *
     * @param success 请求状态标记，true表示请求成功结果正确
     */
    public void onAfter(boolean success) {
    }

    /**
     * @param progress 进度
     * @param total    总长度
     */
    public void inProgress(float progress, long total) {
    }

    /**/
    //获取Json对象的类型，因为数据可能是Json数组也可能是Json对象
    /*
    public Type getType() {
        Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if (type instanceof Class) {//如果是Object直接返回
            return type;
        } else {//如果是集合，获取集合的类型map或list
            return new TypeToken<T>() {
            }.getType();
        }
    }*/

}