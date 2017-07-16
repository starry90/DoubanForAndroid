package com.starry.douban.http.request;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author Starry Jerry
 * @since 2016/10/20.
 */
public class PostStringRequest extends OKHttpRequest {


    @Override
    protected String getMethod() {
        return "POST_JSON";
    }

    @Override
    protected RequestBody buildRequestBody() {
        return RequestBody.create(client.mediaType(), client.content());
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return builder.post(requestBody).build();
    }


}
