package com.starry.douban.http.request;

import com.starry.douban.http.CommonCallback;
import com.starry.douban.http.CommonHttpClient;
import com.starry.douban.log.Logger;

import java.util.Map;

import okhttp3.Headers;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author Starry Jerry
 * @since 2017/3/23.
 */
public abstract class OKHttpRequest {

    protected CommonHttpClient client;

    protected Request.Builder builder = new Request.Builder();

    protected abstract String getMethod();

    protected String buildUrl() {
        return client.url();
    }

    protected abstract RequestBody buildRequestBody();

    protected abstract Request buildRequest(RequestBody requestBody);

    protected RequestBody wrapRequestBody(RequestBody requestBody, final CommonCallback callback) {
        return requestBody;
    }


    public Request generateRequest(CommonCallback callback) {
        prepareBuilder();
        appendHeaders();
        RequestBody requestBody = wrapRequestBody(buildRequestBody(), callback);
        logHttpMethod(getMethod(), client.params());
        return buildRequest(requestBody);
    }


    private void prepareBuilder() {
        builder.url(buildUrl()).tag(client.tag());
    }


    protected void appendHeaders() {
        Map<String, String> headers = client.headers();
        if (headers == null || headers.isEmpty()) return;

        Headers.Builder headerBuilder = new Headers.Builder();
        for (String key : headers.keySet()) {
            headerBuilder.add(key, headers.get(key));
        }
        builder.headers(headerBuilder.build());
    }

    /**
     * 打印请求方法及参数
     *
     * @param method 方法
     * @param params 参数
     */
    private void logHttpMethod(String method, Map<String, String> params) {
        String paramsStr = params == null ? "" : params.toString();
        Logger.i(client.url());
        Logger.i("HttpMethod == [" + method + "]  Params == " + paramsStr);
    }


    public RequestCall build(CommonHttpClient okHttp) {
        this.client = okHttp;
        return new RequestCall(this);
    }
}
