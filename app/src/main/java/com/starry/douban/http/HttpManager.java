package com.starry.douban.http;

import com.starry.douban.http.request.GetRequest;
import com.starry.douban.http.request.PostFormRequest;
import com.starry.douban.http.request.PostStringRequest;
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

    public static OkHttpClient getOkHttpClient() {
        return getInstance().mOkHttpClient;
    }

    /**
     * Get 请求
     */
    public CommonParams.Builder get() {
        return new CommonParams.Builder(new GetRequest());
    }

    /**
     * Post 请求
     */
    public CommonParams.Builder post() {
        return new CommonParams.Builder(new PostFormRequest());
    }

    /**
     * Post JSON
     */
    public CommonParams.Builder postJson() {
        return new CommonParams.Builder(new PostStringRequest());
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