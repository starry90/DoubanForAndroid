package com.starry.http.request;

import com.starry.http.CommonParams;

import org.json.JSONObject;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author Starry Jerry
 * @since 2016/10/20.
 */
public class PostStringRequest extends OKHttpRequest {

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

    @Override
    protected RequestBody buildRequestBody(CommonParams commonParams) {
        //优先使用content字段，content为空则使用params字段
        String content = commonParams.content();
        if (content == null) {
            Map<String, Object> map = commonParams.params();
            if (map != null) {
                content = new JSONObject(map).toString();
            } else {
                content = "{}";
            }
        }
        return RequestBody.create(MEDIA_TYPE, content);
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody) {
        return builder.post(requestBody).build();
    }

}
