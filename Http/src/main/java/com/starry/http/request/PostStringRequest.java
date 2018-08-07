package com.starry.http.request;

import com.starry.http.HttpConverter;
import com.starry.http.HttpManager;

import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author Starry Jerry
 * @since 2016/10/20.
 */
public class PostStringRequest extends OKHttpRequest {

    @Override
    protected RequestBody buildRequestBody() {
        //优先使用content字段，content为空则使用params字段
        String content = commonParams.content();
        Object temp = content;
        if (content == null) {
            temp = commonParams.params();
        }
        HttpConverter httpConverter = HttpManager.getInstance().getHttpConverter();
        return httpConverter.convert(temp);
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return requestBuilder.post(requestBody).build();
    }

}
