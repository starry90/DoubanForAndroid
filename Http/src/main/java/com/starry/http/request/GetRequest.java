package com.starry.http.request;

import com.starry.http.utils.Util;

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
        Map<String, Object> params = commonParams.params();
        StringBuilder sb = new StringBuilder();
        sb.append(url).append("?");
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                String value = Util.convert(params.get(key));
                sb.append(key).append("=").append(value).append("&");
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
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody) {
        return builder.get().build();
    }

}
