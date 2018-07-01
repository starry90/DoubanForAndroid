package com.starry.douban.http.callback;

import com.starry.douban.http.HttpManager;
import com.starry.douban.http.error.NetworkException;
import com.starry.douban.log.Logger;
import com.starry.douban.util.JsonUtil;
import com.starry.douban.util.Preconditions;

import org.json.JSONObject;

import okhttp3.Response;

/**
 * @author Starry Jerry
 * @since 2018/7/1.
 */

public abstract class StringCallback<T> extends CommonCallback<T> {

    private final String TAG = HttpManager.TAG;

    private String code = "code";

    private String msg = "msg";

    @Override
    public T parseResponse(Response response) throws Exception {
        // 1. print json log
        String json = response.body().string();
        logResponse(response.request().url().toString(), json);
        response.close(); //To avoid leaking resources

        // 2. check result code
        JSONObject jsonObject = new JSONObject(json);
        int responseCode = jsonObject.optInt(code);
        String responseMsg = jsonObject.optString(msg);
        checkResultCode(responseCode, responseMsg);

        // 3. parse json to object
        T result = JsonUtil.toObject(json, getType());
        Preconditions.checkNotNull(result);
        return result;
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

}
