package com.starry.douban.http;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;

import com.starry.douban.http.request.GetRequest;
import com.starry.douban.http.request.PostFormRequest;
import com.starry.douban.http.request.PostStringRequest;
import com.starry.douban.log.Logger;
import com.starry.douban.model.BaseModel;
import com.starry.douban.util.JsonUtil;
import com.starry.douban.util.Preconditions;
import com.starry.douban.widget.LoadingDataLayout;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Response;

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
     * Handler 给主线程发消息
     */
    private Handler mDelivery;

    /**
     * OkHttpClient 只有一个实例
     */
    private OkHttpClient.Builder okHttpBuilder;

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
        mDelivery = new Handler(Looper.getMainLooper());
        okHttpBuilder = new OkHttpClient.Builder()
                .connectTimeout(TIME_OUT, TimeUnit.SECONDS)//连接主机超时
                .readTimeout(TIME_OUT, TimeUnit.SECONDS)//从主机读取数据超时
                .writeTimeout(TIME_OUT, TimeUnit.SECONDS);//从主机写数据超时
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpBuilder.build();
    }

    public Handler getDelivery() {
        return mDelivery;
    }

    /**
     * Get 请求
     */
    public CommonHttpClient.Builder get() {
        return new CommonHttpClient.Builder(new GetRequest());
    }

    /**
     * Post 请求
     */
    public CommonHttpClient.Builder post() {
        return new CommonHttpClient.Builder(new PostFormRequest());
    }

    /**
     * Post JSON
     */
    public CommonHttpClient.Builder postJson() {
        return new CommonHttpClient.Builder(new PostStringRequest());
    }


    /**
     * @param call
     * @param callback 回调对象
     * @param <T>      对象的泛型
     */
    public <T extends BaseModel> void execute(Call call, final CommonCallback<T> callback) {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                e.printStackTrace();
                /**
                 * {@linkplain okhttp3.RealCall#cancel()}
                 * {@linkplain okhttp3.RealCall#isCanceled()}
                 */
                if (call.isCanceled()) {
                    callback.onAfter(LoadingDataLayout.STATUS_ERROR);
                } else {
                    sendFailCallback("连接失败，请检查你的网络设置", -1, callback);
                }
            }

            @Override
            public void onResponse(final Call call, final Response response) {
                int code = -1;

                String json = getJsonString(response);
                Logger.i(json);// log json
                if (response.code() == 200) {
                    try {
                        // T extends BaseModel
                        T t = JsonUtil.toObject(json, callback.getType());
                        Preconditions.checkNotNull(t);
                        if (t.getResult() == 0) { // Success
                            sendSuccessCallback(t, callback);
                            return;
                        } else { // Failure
                            json = t.getMsg();
                            code = t.getResult();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (TextUtils.isEmpty(json)) json = "连接失败，请检查你的网络设置";
                sendFailCallback(json, code, callback);
            }
        });
    }

    private String getJsonString(Response response) {
        try {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void sendFailCallback(final String message, final int code, final CommonCallback callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.setLoadingView(null);
                callback.onAfter(LoadingDataLayout.STATUS_ERROR);
                callback.onFailure(message, code);
            }
        });
    }

    private <T> void sendSuccessCallback(final T object, final CommonCallback<T> callback) {
        mDelivery.post(new Runnable() {
            @Override
            public void run() {
                callback.setLoadingView(null);
                callback.onAfter(LoadingDataLayout.STATUS_SUCCESS);
                callback.onSuccess(object);
            }
        });
    }

    /**
     * 根据tag取消请求
     *
     * @param tag 标签
     */
    public void cancelTag(Object tag) {
        OkHttpClient client = okHttpBuilder.build();
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