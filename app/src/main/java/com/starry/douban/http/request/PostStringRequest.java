package com.starry.douban.http.request;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author Starry Jerry
 * @since 2016/10/20.
 */
public class PostStringRequest extends OKHttpRequest {

    @Override
    protected RequestBody buildRequestBody() {
        return RequestBody.create(commonParams.mediaType(), commonParams.content());
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return requestBuilder.post(requestBody).build();
    }

}
