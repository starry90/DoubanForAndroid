package com.starry.douban.env;

import com.google.gson.JsonParseException;
import com.starry.http.CommonParams;
import com.starry.http.HttpInterceptor;
import com.starry.http.error.BIZException;
import com.starry.http.error.ErrorModel;
import com.starry.log.Logger;

import java.io.EOFException;
import java.net.SocketTimeoutException;
import java.util.Map;

import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * 拦截器实现类
 *
 * @author Starry Jerry
 * @since 2018/8/2.
 */
public final class InterceptorImpl implements HttpInterceptor {

    private String logFormat = "%s %s %s";

    private String TAG = "InterceptorImpl";

    /**
     * 获取固定格式的log
     *
     * @param url    发生错误的url
     * @param desc   错误描述
     * @param result 错误结果
     * @return 固定格式的log
     */
    private String getFormatLog(String url, String desc, String result) {
        return String.format(logFormat, url, desc, result);
    }

    @Override
    public void logRequest(CommonParams commonParams) {
        Map<String, Object> params = commonParams.params();
        String paramsStr = commonParams.content();
        if (paramsStr == null || paramsStr.length() == 0) {
            paramsStr = params == null ? "" : params.toString();
        }
        Map<String, String> headers = commonParams.headers();
        String headersStr = headers == null ? "" : headers.toString();
        // 日志格式
        // Request
        // --> https://...
        // --> GET
        // --> {...}
        // --> {...}
        String result = String.format("Request\n >>> %s\n >>> %s\n >>> %s\n >>> %s"
                , commonParams.url()
                , commonParams.method()
                , headersStr
                , paramsStr);
        Logger.i(TAG, result);
    }

    @Override
    public ErrorModel handleResponse(Exception why, ErrorModel errorModel) {
        errorModel.setMessage("网络错误");
        if (why instanceof BIZException) { //业务异常
            BIZException cloneWhy = (BIZException) why;
            errorModel.setCode(cloneWhy.getErrorCode())
                    .setMessage(cloneWhy.getErrorMessage())
                    .setResponse(cloneWhy.getResponse());
            errorModel.setResponse(cloneWhy.getResponse());
            Logger.e(getFormatLog(errorModel.getUrl(), errorModel.getResponse(), errorModel.getMessage()));
            return handleBIZ(errorModel);
        } else if (why instanceof EOFException) { //http status code不是200的网络异常
            return errorModel;
        } else if (why instanceof NullPointerException) {
            return errorModel;
        } else if (why instanceof JsonParseException) {
            return errorModel;
        } else {
            return errorModel;
        }
    }

    /**
     * 处理错误的业务
     *
     * @param errorModel ErrorModel
     * @return ErrorModel
     */
    private ErrorModel handleBIZ(ErrorModel errorModel) {
        return errorModel;
    }

    @Override
    public ErrorModel handleFailure(Exception why, ErrorModel errorModel) {
        errorModel.setMessage("网络错误");
        if (why instanceof SocketTimeoutException) {
            return errorModel;
        } else {
            return errorModel;
        }
    }

    @Override
    public Response logResponse(Response response) throws Exception {
        Response clone = response.newBuilder().build();
        ResponseBody responseBody = clone.body();
        String url = clone.request().url().toString();
        String body = responseBody.string();
        // 日志格式
        // Response
        // >>> https://...
        // >>> {"code":10,...
        String result = String.format("Response\n >>> %s\n >>> %s", url, body);
        Logger.i(TAG, result);

        responseBody = ResponseBody.create(responseBody.contentType(), body);
        return response.newBuilder().body(responseBody).build();
    }

}
