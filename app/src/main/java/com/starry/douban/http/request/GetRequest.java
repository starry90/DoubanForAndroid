package com.starry.douban.http.request;

import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author Starry Jerry
 * @since 2016/6/19.
 */
public class GetRequest extends OKHttpRequest {

    @Override
    protected String getMethod() {
        return "GET";
    }

    @Override
    protected String buildUrl() {
        String url = client.url();
        Map<String, String> params = client.params();
        StringBuilder sb = new StringBuilder();
        sb.append(url + "?");
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                sb.append(key).append("=").append(params.get(key)).append("&");
            }
        }

        sb = sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    protected RequestBody buildRequestBody() {
        return null;
    }


    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return builder.get().build();
    }


}
