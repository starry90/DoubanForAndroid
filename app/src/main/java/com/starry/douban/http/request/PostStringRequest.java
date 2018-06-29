package com.starry.douban.http.request;

import com.starry.douban.util.JsonUtil;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author Starry Jerry
 * @since 2016/10/20.
 */
public class PostStringRequest extends OKHttpRequest {

    @Override
    protected RequestBody buildRequestBody() {
        return RequestBody.create(commonParams.mediaType(), getContent());
    }

    private String getContent() {
        //优先使用content字段，content为空则使用params字段
        String content = commonParams.content();
        if (content == null || content.length() == 0) {
            content = JsonUtil.toJson(commonParams.params());
        }
        return content;
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return requestBuilder.post(requestBody).build();
    }

}
