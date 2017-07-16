package com.starry.douban.http.request;


import com.starry.douban.http.CommonCallback;
import com.starry.douban.http.HttpManager;
import com.starry.douban.http.CommonHttpClient;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;

/**
 * @author Starry Jerry
 * @since 2016/6/19.
 */
public class PostFormRequest extends OKHttpRequest {

    @Override
    protected String getMethod() {
        return "POST";
    }

    @Override
    protected RequestBody buildRequestBody() {
        List<CommonHttpClient.FileInput> files = client.files();
        Map<String, String> params = client.params();
        if (files == null || files.isEmpty()) {
            FormBody.Builder builder = new FormBody.Builder();
            addParams(builder, params);
            return builder.build();
        } else {
            MultipartBody.Builder builder = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM);
            addParams(builder, params);

            for (int i = 0; i < files.size(); i++) {
                CommonHttpClient.FileInput fileInput = files.get(i);
                RequestBody fileBody = RequestBody.create(MediaType.parse(guessMimeType(fileInput.filename)), fileInput.file);
                builder.addFormDataPart(fileInput.key, fileInput.filename, fileBody);
            }
            return builder.build();
        }
    }

    private void addParams(FormBody.Builder builder, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            builder.add("1", "1");
            return;
        }

        for (String key : params.keySet()) {
            builder.add(key, params.get(key));
        }
    }


    private void addParams(MultipartBody.Builder builder, Map<String, String> params) {
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, params.get(key)));
            }
        }
    }

    private String guessMimeType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return contentTypeFor;
    }


    protected RequestBody wrapRequestBody(RequestBody requestBody, final CommonCallback callback) {
        if (callback == null) return requestBody;
        CountingRequestBody countingRequestBody = new CountingRequestBody(requestBody, new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(final long bytesWritten, final long contentLength) {

                HttpManager.getInstance().getDelivery().post(new Runnable() {
                    @Override
                    public void run() {
                        callback.inProgress(bytesWritten * 1.0f / contentLength);
                    }
                });

            }
        });
        return countingRequestBody;
    }

    @Override
    protected Request buildRequest(RequestBody requestBody) {
        return builder.post(requestBody).build();
    }


}
