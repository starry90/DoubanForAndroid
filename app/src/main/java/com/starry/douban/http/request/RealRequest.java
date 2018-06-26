package com.starry.douban.http.request;


import android.support.annotation.NonNull;

import com.starry.douban.http.CommonCallback;
import com.starry.douban.http.CommonParams;
import com.starry.douban.http.Errors;
import com.starry.douban.http.HandlerMain;
import com.starry.douban.http.HttpManager;
import com.starry.douban.http.NetworkException;
import com.starry.douban.log.Logger;
import com.starry.douban.model.BaseModel;
import com.starry.douban.model.ErrorModel;
import com.starry.douban.util.JsonUtil;
import com.starry.douban.util.Preconditions;

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

    private final String TAG = getClass().getSimpleName();

    private Request request;

    private CommonParams commonParams;

    RealRequest(Request request, CommonParams commonParams) {
        this.request = request;
        this.commonParams = commonParams;
    }

    /**
     * 同步执行请求
     * Invokes the request immediately, and blocks until the response can be processed or is in  error.
     *
     * @param callback 回调对象
     * @return 返回结果
     */
    public <T extends BaseModel> T execute(CommonCallback<T> callback) {
        logRequest(request.url().toString(), request.method(), commonParams.params());
        Call call = HttpManager.getInstance().getOkHttpClient().newCall(request);
        return execute(call, callback);
    }

    /**
     * 异步执行请求
     * Schedules the request to be executed at some point in the future.
     *
     * @param callback 回调对象
     */
    public <T extends BaseModel> void enqueue(CommonCallback<T> callback) {
        logRequest(request.url().toString(), request.method(), commonParams.params());
        Call call = HttpManager.getInstance().getOkHttpClient().newCall(request);
        enqueue(call, callback);
    }

    /**
     * 打印报文
     *
     * @param url    URL
     * @param method 方法
     * @param params 参数
     */
    private void logRequest(String url, String method, Map<String, String> params) {
        String paramsStr = params == null ? "" : params.toString();
        // 日志格式
        // Request
        // --> https://api.douban.com/v2/book/search?tag=热门&start=0&count=20
        // --> GET
        // --> {tag=热门, start=0, count=20}
        String result = String.format("Request\n >>> %s\n >>> %s\n >>> %s", url, method, paramsStr);
        Logger.i(TAG, result);
    }

    /**
     * 打印返回报文
     *
     * @param url  URL
     * @param json 返回报文
     */
    private void logResponse(String url, String json) {
        // 日志格式
        // Response
        // --> https://api.douban.com/v2/book/search?tag=热门&start=0&count=20
        // --> {"count":20,"start":0,"total":122,"books":[{"rating":{"max":10,"numRaters":487,……
        String result = String.format("Response\n >>> %s\n >>> %s", url, json);
        Logger.i(TAG, result);
    }

    /**
     * @param call     Call
     * @param callback 回调对象
     * @param <T>      对象的泛型
     */
    private <T extends BaseModel> T execute(Call call, final CommonCallback<T> callback) {
        callback.onBefore();
        try {
            Response response = call.execute();
            return onResponseResult(response, callback);
        } catch (IOException ex) {
            ex.printStackTrace();
            onFailureResult(call, callback);
        }
        return null;
    }

    /**
     * @param call     Call
     * @param callback 回调对象
     * @param <T>      对象的泛型
     */
    private <T extends BaseModel> void enqueue(Call call, final CommonCallback<T> callback) {
        callback.onBefore();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException ex) {
                ex.printStackTrace();
                onFailureResult(call, callback);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                onResponseResult(response, callback);
            }
        });
    }

    private void onFailureResult(Call call, CommonCallback callback) {
        //{@linkplain okhttp3.RealCall#isCanceled()}
        if (call.isCanceled()) {
            sendCanceledCallback(callback);
        } else {
            sendFailCallback(Errors.Code.NETWORK_UNAVAILABLE, Errors.Message.NETWORK_UNAVAILABLE, callback);
        }
    }

    private <T extends BaseModel> T onResponseResult(Response response, CommonCallback<T> callback) {
        try {
            // 1. check http code
            checkHttpCode(response.code());

            // 2. print json log
            String json = response.body().string();
            logResponse(response.request().url().toString(), json);
            response.close(); //To avoid leaking resources

            // 3. parse json to object
            // T extends BaseModel
            T result = JsonUtil.toObject(json, callback.getType());
            Preconditions.checkNotNull(result);

            // 4. check result code
            checkResultCode(result.getCode(), result.getMsg());

            // 5. call success method
            sendSuccessCallback(result, callback);
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            NetworkException netEx = NetworkException.newException(ex);
            sendFailCallback(netEx.getErrorCode(), netEx.getErrorMessage(), callback);
        }
        return null;
    }

    /**
     * 检查http code
     *
     * @param code 错误码
     * @throws NetworkException 自定义网络异常
     */
    private void checkHttpCode(int code) throws NetworkException {
        if (code < 200 || code >= 300) {// 不是2开头code统一以服务器错误处理
            throw NetworkException.newException(code, Errors.Message.SERVER_ERROR);
        }
    }

    /**
     * 检查返回结果code
     *
     * @param code    错误码
     * @param message 错误信息
     * @throws NetworkException 自定义网络异常
     */
    private void checkResultCode(int code, String message) throws NetworkException {
        if (code != 0) {// 服务返回结果code不等于0，请求得到的数据有问题
            throw NetworkException.newException(code, message);
        }
    }

    private void sendFailCallback(final int code, final String message, final CommonCallback callback) {
        HandlerMain.getHandler().post(new Runnable() {
            @Override
            public void run() {
                callback.onAfter(false);
                callback.onFailure(new ErrorModel(code, message));
            }
        });
    }

    private <T> void sendSuccessCallback(final T object, final CommonCallback<T> callback) {
        HandlerMain.getHandler().post(new Runnable() {
            @Override
            public void run() {
                callback.onAfter(true);
                callback.onSuccess(object);
            }
        });
    }

    private <T> void sendCanceledCallback(final CommonCallback<T> callback) {
        HandlerMain.getHandler().post(new Runnable() {
            @Override
            public void run() {
                callback.onAfter(false);
            }
        });
    }

}
