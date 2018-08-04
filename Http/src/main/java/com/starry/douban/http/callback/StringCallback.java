package com.starry.douban.http.callback;

import com.google.gson.reflect.TypeToken;
import com.starry.douban.http.HttpManager;
import com.starry.douban.http.Util;
import com.starry.douban.http.error.BIZException;

import org.json.JSONObject;

import java.lang.reflect.Type;

import okhttp3.Response;

/**
 * @author Starry Jerry
 * @since 2018/7/1.
 */

public abstract class StringCallback<T> extends CommonCallback<T> {

    private String code = "code";

    private String msg = "msg";

    @Override
    public T parseResponse(Response response) throws Exception {
        // 1. get json
        String json = response.body().string();
        response.close(); //To avoid leaking resources

        // 2. check result code
        JSONObject jsonObject = new JSONObject(json);
        int responseCode = jsonObject.optInt(code);
        String responseMsg = jsonObject.optString(msg);
        checkResultCode(responseCode, responseMsg, json);

        // 3. parse json to object
        T result;
        Type type = getType();
        if (type == new TypeToken<String>() {
        }.getType()) {
            result = (T) json;
        } else {
            result = HttpManager.getInstance().getInterceptor().convert(json, type);
        }
        Util.checkNotNull(result);
        return result;
    }

    /**
     * 检查返回结果code
     *
     * @param code    错误码
     * @param message 错误信息
     * @throws BIZException 业务异常
     */
    private void checkResultCode(int code, String message, String response) throws BIZException {
        if (code != 0) {// 服务返回结果code不等于0，请求得到的数据有问题
            throw new BIZException(code, message).setResponse(response);
        }
    }

}
