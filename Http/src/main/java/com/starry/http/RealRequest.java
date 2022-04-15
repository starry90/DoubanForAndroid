package com.starry.http;


import com.starry.http.callback.CommonCallback;
import com.starry.http.error.ErrorModel;
import com.starry.http.error.HttpStatusException;
import com.starry.http.request.OKHttpRequest;
import com.starry.http.utils.MainHandler;
import com.starry.http.utils.Util;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * @author Starry Jerry
 * @since 2016/6/19.
 */
public class RealRequest {

    private final OKHttpRequest okHttpRequest;

    private final CommonParams commonParams;


    public RealRequest(OKHttpRequest okHttpRequest, CommonParams commonParams) {
        this.okHttpRequest = okHttpRequest;
        this.commonParams = commonParams;
    }

    /**
     * 同步执行请求
     * Invokes the request immediately, and blocks until the response can be processed or is in  error.
     *
     * @param callback 回调对象
     * @return 返回结果
     */
    public <T> T execute(CommonCallback<T> callback) {
        return execute(getRequest(callback), callback);
    }

    /**
     * 异步执行请求
     * Schedules the request to be executed at some point in the future.
     *
     * @param callback 回调对象
     */
    public <T> void enqueue(CommonCallback<T> callback) {
        enqueue(getRequest(callback), callback);
    }

    private <T> Request getRequest(CommonCallback<T> callback) {
        return okHttpRequest.build(commonParams, callback);
    }

    private static Call getCall(Request request) {
        return HttpManager.getInstance().getOkHttpClient().newCall(request);
    }

    static <T> T execute(Request request, final CommonCallback<T> callback) {
        return execute(getCall(request), callback);
    }

    static <T> void enqueue(Request request, final CommonCallback<T> callback) {
        enqueue(getCall(request), callback);
    }

    /**
     * @param call     Call
     * @param callback 回调对象
     * @param <T>      对象的泛型
     */
    private static <T> T execute(Call call, final CommonCallback<T> callback) {
        try {
            Response response = call.execute();
            return onResponseResult(call, response, callback);
        } catch (IOException ex) {
            ex.printStackTrace();
            onFailureResult(call, ex, callback);
        }
        return null;
    }

    /**
     * @param call     Call
     * @param callback 回调对象
     * @param <T>      对象的泛型
     */
    private static <T> void enqueue(Call call, final CommonCallback<T> callback) {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException ex) {
                ex.printStackTrace();
                onFailureResult(call, ex, callback);
            }

            @Override
            public void onResponse(Call call, Response response) {
                onResponseResult(call, response, callback);
            }
        });
    }

    private static <T> void onFailureResult(Call call, IOException ex, final CommonCallback<T> callback) {
        //{@linkplain okhttp3.RealCall#isCanceled()}
        if (call.isCanceled()) {
            MainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onAfter(false);
                }
            });
        } else {
            // handle failure exception
            ErrorModel errorModel = HttpManager.getInstance()
                    .getHttpConverter()
                    .responseErrorConverter(ex, call.request().url().toString());
            sendFailureCallback(errorModel, callback);
        }
    }

    private static <T> T onResponseResult(Call call, Response response, final CommonCallback<T> callback) {
        String url = call.request().url().toString();
        try {
            // 1. check http code
            int code = response.code();
            if (code < 200 || code >= 300) {// 不是2开头code统一以服务器错误处理
                throw new HttpStatusException(code);
            }

            // 2. check responseBody
            ResponseBody responseBody = response.body();
            Util.checkNotNull(responseBody, "responseBody == null");

            // 3. parse responseBody
            final T result = callback.parseResponse(responseBody);

            // 4. call success method
            MainHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onSuccess(result);
                    callback.onAfter(true);
                }
            });
            return result;
        } catch (Exception ex) {
            // 1. print stack trace
            ex.printStackTrace();

            // 2. handle response exception
            ErrorModel errorModel = HttpManager.getInstance()
                    .getHttpConverter()
                    .responseErrorConverter(ex, url);

            // 3. call fail method
            sendFailureCallback(errorModel, callback);
        } finally {
            // A connection to https://xxxxx was leaked. Did you forget to close a response body?
            // To avoid leaking resources
            Util.closeQuietly(response);
        }
        return null;
    }

    static <T> void sendFailureCallback(final ErrorModel errorModel, final CommonCallback<T> callback) {
        MainHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(errorModel);
                callback.onAfter(false);
            }
        });
    }

}
