package com.starry.douban.env;

import com.starry.log.Logger;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSource;


/**
 * 网络请求响应日志拦截器
 */
public class HttpLogInterceptor implements Interceptor {

    private static final String TAG = "HttpLogInterceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        // 请求日志格式
        // Request
        // --> https://...
        // --> GET
        // --> {...}
        String url = request.url().toString();
        String requestLog = String.format("Request%n --> %s%n --> %s%n --> %s%n --> %s"
                , url
                , request.method()
                , request.headers()
                , request.body() != null ? parseParams(request.newBuilder().build().body()) : "");
        Logger.i(TAG, requestLog);

        Response originalResponse;
        String responseLog;
        try {
            originalResponse = chain.proceed(request);
        } catch (Exception exception) {
            responseLog = String.format("Response%n >>> %s%n >>> %s", url, exception.toString());
            Logger.e(TAG, responseLog);
            throw exception;
        }

        String bodyString = parseResponse(originalResponse.newBuilder().build());
        // 响应日志格式
        // Response
        // >>> https://...
        // >>> ......
        // >>> {"code":0,...}
        responseLog = String.format("Response%n >>> %s%n >>> %s%n >>> %s"
                , url
                , originalResponse.headers()
                , bodyString);
        Logger.i(TAG, responseLog);

        return originalResponse;
    }

    /**
     * 解析响应结果
     */
    private String parseResponse(Response response) {
        String bodyString = "无法解析";
        try {
            ResponseBody responseBody = response.body();
            MediaType contentType;
            if (responseBody != null && isParsed(contentType = responseBody.contentType())) {
                //okhttp3.ResponseBody.string()多次调用会报如下错
                //java.lang.IllegalStateException: closed
                BufferedSource source = responseBody.source();
                source.request(Long.MAX_VALUE);
                Buffer buffer = source.buffer();
                Charset charset = Util.bomAwareCharset(source, contentType.charset(StandardCharsets.UTF_8));
                bodyString = buffer.clone().readString(charset);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bodyString;
    }


    /**
     * 解析请求参数
     *
     * @param body RequestBody
     * @return 请求参数
     */
    public static String parseParams(RequestBody body) {
        MediaType contentType = body.contentType();
        if (isParsed(contentType)) {
            try {
                Buffer requestBuffer = new Buffer();
                body.writeTo(requestBuffer);
                Charset charset = contentType.charset(StandardCharsets.UTF_8);
                return URLDecoder.decode(requestBuffer.readString(charset), convertCharset(charset));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "请求参数无法解析";
    }

    /**
     * 是否可以解析
     */
    public static boolean isParsed(MediaType mediaType) {
        if (mediaType == null) return false;
        return mediaType.toString().toLowerCase().contains("text")
                || isJson(mediaType) || isForm(mediaType)
                || isHtml(mediaType) || isXml(mediaType);
    }

    public static boolean isJson(MediaType mediaType) {
        return mediaType.toString().toLowerCase().contains("json");
    }

    public static boolean isXml(MediaType mediaType) {
        return mediaType.toString().toLowerCase().contains("xml");
    }

    public static boolean isHtml(MediaType mediaType) {
        return mediaType.toString().toLowerCase().contains("html");
    }

    public static boolean isForm(MediaType mediaType) {
        return mediaType.toString().toLowerCase().contains("x-www-form-urlencoded");
    }

    public static String convertCharset(Charset charset) {
        String s = charset.toString();
        int i = s.indexOf("[");
        if (i == -1)
            return s;
        return s.substring(i + 1, s.length() - 1);
    }
}
