package com.starry.http;


import com.starry.http.callback.CommonCallback;
import com.starry.http.callback.StringCallback;
import com.starry.http.error.ErrorModel;
import com.starry.http.error.HttpStatusException;
import com.starry.http.interfaces.HttpInterceptor;
import com.starry.http.request.OKHttpRequest;
import com.starry.http.utils.MainHandler;
import com.starry.http.utils.Util;

import java.io.IOException;

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

    private HttpInterceptor httpInterceptor;

    public RealRequest(OKHttpRequest okHttpRequest, CommonParams commonParams) {
        this.okHttpRequest = okHttpRequest;
        this.commonParams = commonParams;
        this.httpInterceptor = HttpManager.getInstance().getInterceptor();
    }

    /**
     * 同步执行请求
     * Invokes the request immediately, and blocks until the response can be processed or is in  error.
     *
     * @param callback 回调对象
     * @return 返回结果
     */
    public <T> T execute(CommonCallback<T> callback) {
        commonParams = httpInterceptor.logRequest(commonParams);
        Request request = okHttpRequest.build(commonParams, callback);
        Call call = HttpManager.getInstance().getOkHttpClient().newCall(request);
        return execute(call, callback);
    }

    /**
     * 异步执行请求
     * Schedules the request to be executed at some point in the future.
     *
     * @param callback 回调对象
     */
    public <T> void enqueue(CommonCallback<T> callback) {
        commonParams = httpInterceptor.logRequest(commonParams);
        Request request = okHttpRequest.build(commonParams, callback);
        Call call = HttpManager.getInstance().getOkHttpClient().newCall(request);
        enqueue(call, callback);
    }

    /**
     * @param call     Call
     * @param callback 回调对象
     * @param <T>      对象的泛型
     */
    private <T> T execute(Call call, final CommonCallback<T> callback) {
        callback.onBefore();
        try {
            Response response = call.execute();
            return onResponseResult(response, callback);
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
    private <T> void enqueue(Call call, final CommonCallback<T> callback) {
        callback.onBefore();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException ex) {
                ex.printStackTrace();
                onFailureResult(call, ex, callback);
            }

            @Override
            public void onResponse(Call call, Response response) {
                onResponseResult(response, callback);
            }
        });
    }

    private <T> void onFailureResult(Call call, IOException ex, CommonCallback<T> callback) {
        //{@linkplain okhttp3.RealCall#isCanceled()}
        if (call.isCanceled()) {
            sendCanceledCallback(callback);
        } else {
            // handle failure exception
            ErrorModel errorModel = new ErrorModel(0, "");
            errorModel.setUrl(commonParams.url());
            httpInterceptor.handleFailure(ex, errorModel);
            sendFailureCallback(errorModel, callback);
        }
    }

    private <T> T onResponseResult(Response response, CommonCallback<T> callback) {
        Response cloneResponse = null;
        try {
            // 1. check http code
            checkHttpCode(response.code());

            // 2. log response
            if (callback instanceof StringCallback) {
                cloneResponse = httpInterceptor.logResponse(response);
            } else {
                cloneResponse = response;
            }
            // 3. parse response
            T result = callback.parseResponse(cloneResponse);

            // 4. call success method
            sendSuccessCallback(result, callback);
            return result;
        } catch (Exception ex) {
            // 1. print stack trace
            ex.printStackTrace();

            // 2. handle response exception
            ErrorModel errorModel = new ErrorModel(0, "");
            errorModel.setUrl(commonParams.url());
            httpInterceptor.handleResponse(ex, errorModel);

            // 3. call fail method
            sendFailureCallback(errorModel, callback);
        } finally {
            Util.closeQuietly(response);
            Util.closeQuietly(cloneResponse);
        }
        return null;
    }

    /**
     * 检查http code
     *
     * @param code 错误码
     * @throws Exception 自定义网络异常
     */
    private void checkHttpCode(int code) throws Exception {
        if (code < 200 || code >= 300) {// 不是2开头code统一以服务器错误处理
            throw new HttpStatusException(code);
        }
    }

    private void sendFailureCallback(final ErrorModel errorModel, final CommonCallback callback) {
        if (errorModel.isProcessed()) { //处理过错误信息，不再回调
            return;
        }

        MainHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onAfter(false);
                callback.onFailure(errorModel);
            }
        });
    }

    private <T> void sendSuccessCallback(final T object, final CommonCallback<T> callback) {
        MainHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onAfter(true);
                callback.onSuccess(object);
            }
        });
    }

    private <T> void sendCanceledCallback(final CommonCallback<T> callback) {
        MainHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onAfter(false);
            }
        });
    }

}
