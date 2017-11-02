package com.starry.douban.http.request;


import com.starry.douban.http.CommonCallback;
import com.starry.douban.http.CommonParams;
import com.starry.douban.http.Errors;
import com.starry.douban.http.HandlerMain;
import com.starry.douban.http.HttpManager;
import com.starry.douban.http.NetworkException;
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

    private Request request;

    private CommonParams commonParams;

    public RealRequest(Request request, CommonParams commonParams) {
        this.request = request;
        this.commonParams = commonParams;
    }

    /**
     * 执行请求
     *
     * @param callback 回调对象
     */
    public void execute(CommonCallback callback) {
        if (callback == null) {
            callback = CommonCallback.NO_CALLBACK;
        }
        logRequest(request.url().toString(), request.method(), commonParams.params());
        Call call = HttpManager.getOkHttpClient().newCall(request);
        execute(call, callback);
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
        String result = String.format("Request\n --> %s\n --> %s\n --> %s", url, method, paramsStr);
        Logger.i(result);
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
        String result = String.format("Response\n --> %s\n --> %s", url, json);
        Logger.i(result);
    }

    /**
     * @param call     Call
     * @param callback 回调对象
     * @param <T>      对象的泛型
     */
    private <T extends BaseModel> void execute(Call call, final CommonCallback<T> callback) {
        callback.onBefore();
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                e.printStackTrace();
                //{@linkplain okhttp3.RealCall#isCanceled()}
                if (call.isCanceled()) {
                    callback.onAfter(LoadingDataLayout.STATUS_ERROR);
                } else {
                    sendFailCallback(Errors.Code.NETWORK_UNAVAILABLE, Errors.Message.NETWORK_UNAVAILABLE, callback);
                }
            }

            @Override
            public void onResponse(final Call call, final Response response) {
                try {
                    // 1. check http code
                    checkHttpCode(response.code());

                    // 2. print json log
                    String json = response.body().string();
                    logResponse(response.request().url().toString(), json);

                    // 3. parse json to object
                    // T extends BaseModel
                    T result = JsonUtil.toObject(json, callback.getType());
                    Preconditions.checkNotNull(result);

                    // 4. check result code
                    checkResultCode(result.getCode(), result.getMsg());

                    // 5. call success method
                    sendSuccessCallback(result, callback);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    NetworkException netE = NetworkException.newException(ex);
                    sendFailCallback(netE.getErrorCode(), netE.getErrorMessage(), callback);
                }
            }
        });
    }

    /**
     * 检查http code
     *
     * @param code 错误码
     * @throws NetworkException 自定义网络异常
     */
    private void checkHttpCode(int code) throws NetworkException {
        if (code < 200 || code >= 300) {// 不是2开头code统一以服务错误处理
            throw NetworkException.newException(Errors.Code.SERVER_ERROR, Errors.Message.SERVER_ERROR);
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
