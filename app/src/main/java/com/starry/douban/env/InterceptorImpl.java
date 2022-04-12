package com.starry.douban.env;

import com.google.gson.JsonParseException;
import com.starry.http.error.BIZException;
import com.starry.http.error.ErrorModel;
import com.starry.http.error.HttpStatusException;
import com.starry.http.interfaces.HttpInterceptor;
import com.starry.log.Logger;

import org.json.JSONException;

import java.net.SocketTimeoutException;

/**
 * 拦截器实现类
 *
 * @author Starry Jerry
 * @since 2018/8/2.
 */
public final class InterceptorImpl implements HttpInterceptor {

    private String logFormat = "%s %s %s";

    private static final String TAG = "InterceptorImpl";

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
    public ErrorModel handleFailure(Exception why, ErrorModel errorModel) {
        if (why instanceof BIZException) { //业务异常
            BIZException cloneWhy = (BIZException) why;
            errorModel.setCode(cloneWhy.getErrorCode())
                    .setMessage(cloneWhy.getErrorMessage())
                    .setResponse(cloneWhy.getResponse());
            errorModel.setResponse(cloneWhy.getResponse());
            Logger.e(TAG, getFormatLog(errorModel.getUrl(), errorModel.getResponse(), errorModel.getMessage()));
            return handleBIZ(errorModel);
        } else if (why instanceof HttpStatusException) { //网络状态码错误
            return errorModel.setCode(10001).setMessage("网络状态码错误");
        } else if (why instanceof JsonParseException || why instanceof JSONException) { //JSON报文错误
            return errorModel.setCode(10002).setMessage("JSON报文错误");
        } else if (why instanceof SocketTimeoutException) { //超时提示
            return errorModel.setCode(10003).setMessage("超时提示");
        } else if (!AppWrapper.getInstance().networkAvailable()) { //无网提示
            return errorModel.setCode(10004).setMessage("无网提示");
        } else { //其它错误提示
            return errorModel.setCode(10005).setMessage("未知错误");
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


}
