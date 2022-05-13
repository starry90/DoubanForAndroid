package com.starry.http.request;


import com.starry.http.CommonParams;
import com.starry.http.utils.Util;

import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author Starry Jerry
 * @since 2016/6/19.
 */
public class PostFormRequest extends OKHttpRequest {

    @Override
    protected RequestBody buildRequestBody(CommonParams commonParams) {
        Map<String, Object> params = commonParams.params();
        FormBody.Builder builder = new FormBody.Builder();
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                String value = Util.convert(params.get(key));
                builder.add(key, value);
            }
        }
        return builder.build();
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody) {
        return builder.post(requestBody).build();
    }

}
