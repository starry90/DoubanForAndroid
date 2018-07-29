package com.starry.douban.http;

import com.starry.douban.log.Logger;

import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * HTTP请求管理者
 *
 * @author Starry Jerry
 * @since 2016/6/19.
 */
public class HttpManager {

    public static final String TAG = "HttpManager";

    /**
     * 超时时间
     */
    private static final int TIME_OUT = 30;

    /**
     * OkHttpClient 只有一个实例
     */
    private OkHttpClient mOkHttpClient;

    /**
     * @return HttpManager
     */
    public static HttpManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        /**
         * HttpManager 只有一个实例
         */
        private final static HttpManager INSTANCE = new HttpManager();
    }

    private HttpManager() {
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)//连接主机超时
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)//从主机读取数据超时
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS)//从主机写数据超时
                .build();
    }

    public OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    /**
     * Get 请求
     */
    public static CommonParams.Builder get(String url) {
        return newBuilder(CommonParams.GET, url);
    }

    /**
     * Post 请求
     */
    public static CommonParams.Builder post(String url) {
        return newBuilder(CommonParams.POST_FORM, url);
    }

    /**
     * Post String
     */
    public static CommonParams.Builder postString(String url) {
        return newBuilder(CommonParams.POST_STRING, url);
    }

    private static CommonParams.Builder newBuilder(String method, String url) {
        return new CommonParams.Builder(method).url(url);
    }

    /**
     * 根据tag取消请求
     *
     * @param tag 标签
     */
    public void cancelTag(Object tag) {
        OkHttpClient client = mOkHttpClient;
        for (Call call : client.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
                Logger.i("queuedCalls cancel");
            }
        }
        for (Call call : client.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
                Logger.i("runningCalls cancel");
            }
        }
    }

}