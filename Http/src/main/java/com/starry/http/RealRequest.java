package com.starry.http;


import com.starry.http.callback.CommonCallback;
import com.starry.http.callback.StringCallback;
import com.starry.http.error.ErrorModel;
import com.starry.http.interfaces.HttpInterceptor;
import com.starry.http.request.OKHttpRequest;
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

    private OKHttpRequest okHttpRequest;

    private CommonParams commonParams;


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
        callback.onBefore();
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
        callback.onBefore();
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

    private static <T> void onFailureResult(Call call, IOException ex, CommonCallback<T> callback) {
        //{@linkplain okhttp3.RealCall#isCanceled()}
        if (call.isCanceled()) {
            Util.sendCanceledCallback(callback);
        } else {
            // handle failure exception
            ErrorModel errorModel = new ErrorModel(0, "");
            errorModel.setUrl(call.request().url().toString());
            HttpManager.getInstance().getInterceptor().handleFailure(ex, errorModel);
            Util.sendFailureCallback(errorModel, callback);
        }
    }

    private static <T> T onResponseResult(Call call, Response response, CommonCallback<T> callback) {
        String url = call.request().url().toString();
        HttpInterceptor httpInterceptor = HttpManager.getInstance().getInterceptor();
        try {
            // 1. check http code
            Util.checkHttpCode(response.code());

            // 2. log response
            HttpResponse httpResponse;
            ResponseBody responseBody = response.body();
            Util.checkNotNull(responseBody);
            if (callback instanceof StringCallback) {
                String bodyString = responseBody.string();
                httpResponse = new HttpResponse(url, bodyString);
            } else {
                httpResponse = new HttpResponse(url, responseBody.byteStream(), responseBody.contentLength());
            }

            // 3. parse response
            T result = callback.parseResponse(httpResponse);

            // 4. call success method
            Util.sendSuccessCallback(result, callback);
            return result;
        } catch (Exception ex) {
            // 1. print stack trace
            ex.printStackTrace();

            // 2. handle response exception
            ErrorModel errorModel = new ErrorModel(0, "");
            errorModel.setUrl(url);
            httpInterceptor.handleFailure(ex, errorModel);

            // 3. call fail method
            Util.sendFailureCallback(errorModel, callback);
        } finally {
            // A connection to https://xxxxx was leaked. Did you forget to close a response body?
            // To avoid leaking resources
            Util.closeQuietly(response);
        }
        return null;
    }

}
