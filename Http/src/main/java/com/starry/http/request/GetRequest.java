package com.starry.http.request;

import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author Starry Jerry
 * @since 2016/6/19.
 */
public class GetRequest extends OKHttpRequest {

    @Override
    protected String buildUrl() {
        String url = commonParams.url();
        Map<String, String> params = commonParams.params();
        StringBuilder sb = new StringBuilder();
        sb.append(url).append("?");
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
        return requestBuilder.get().build();
    }

}
