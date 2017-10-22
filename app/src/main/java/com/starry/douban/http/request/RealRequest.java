package com.starry.douban.http.request;


import android.text.TextUtils;

import com.starry.douban.http.CommonCallback;
import com.starry.douban.http.CommonParams;
import com.starry.douban.http.HandlerMain;
import com.starry.douban.http.HttpManager;
import com.starry.douban.log.Logger;
import com.starry.douban.model.BaseModel;
import com.starry.douban.util.JsonUtil;
import com.starry.douban.util.Preconditions;
import com.starry.douban.widget.LoadingDataLayout;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @author Starry Jerry
 * @since 2016/6/19.
 */
public class RealRequest {

    private OKHttpRequest okHttpRequest;

    private CommonParams commonParams;

    public RealRequest(OKHttpRequest request, CommonParams commonParams) {
        this.okHttpRequest = request;
        this.commonParams = commonParams;
    }

    /**
     * 执行请求
     *
     * @param callback 回调对象
     * @param <T>      对象的泛型
     */
    public <T extends BaseModel> void execute(CommonCallback<T> callback) {
        if (callback != null) {
            callback.onBefore();
        }
        Request request = okHttpRequest.generateRequest(callback);
        logRequest(request.method(), request.url().toString(), commonParams.params());
        Call call = HttpManager.getOkHttpClient().newCall(request);
        execute(call, callback);
    }

    /**
     * 打印请求方法及参数
     *
     * @param method 方法
     * @param url    URL
     * @param params 参数
     */
    private void logRequest(String method, String url, Map<String, String> params) {
        String paramsStr = params == null ? "" : params.toString();
        // 日志格式
        // Method：GET
        // Url：https://api.douban.com/v2/movie/in_theaters
        // Params：{start=0, count=20}
        String result = String.format("Method：%s\nUrl：%s\nParams：%s", method, url, paramsStr);
        Logger.i(result);
    }

    /**
     * @param call     Call
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
        HandlerMain.getHandler().post(new Runnable() {
            @Override
            public void run() {
                callback.onAfter(LoadingDataLayout.STATUS_ERROR);
                callback.onFailure(message, code);
            }
        });
    }

    private <T> void sendSuccessCallback(final T object, final CommonCallback<T> callback) {
        HandlerMain.getHandler().post(new Runnable() {
            @Override
            public void run() {
                callback.onAfter(LoadingDataLayout.STATUS_SUCCESS);
                callback.onSuccess(object);
            }
        });
    }

}
