package com.starry.douban.http;


import com.starry.douban.http.error.ErrorModel;

import java.lang.reflect.Type;

import okhttp3.Callback;
import okhttp3.Response;

/**
 * http interceptor
 *
 * @author Starry Jerry
 * @since 2018/8/2.
 */
public interface HttpInterceptor {

    /**
     * 对象转换成Json
     *
     * @param src Object
     * @return String
     */
    String toJson(Object src);

    /**
     * 转换Json成对象
     *
     * @param json json串
     * @param type Type
     * @param <T>  T
     * @return T
     */
    <T> T convert(String json, Type type);

    /**
     * 打印请求报文
     * 该方法运行在UI线程
     *
     * @param commonParams CommonParams
     */
    void logRequest(CommonParams commonParams);

    /**
     * 处理 OkHttp onResponse Exception信息
     * 该方法运行在子线程
     *
     * @param ex Exception
     * @return NetworkException
     * @see Callback#onResponse(okhttp3.Call, okhttp3.Response)
     */
    ErrorModel handleResponse(Exception ex, ErrorModel errorModel);

    /**
     * 处理 OkHttp onFailure Exception信息
     * 该方法运行在子线程
     *
     * @param ex Exception
     * @return NetworkException
     * @see Callback#onFailure(okhttp3.Call, java.io.IOException)
     */
    ErrorModel handleFailure(Exception ex, ErrorModel errorModel);

    /**
     * 打印返回报文
     * 该方法运行在子线程
     *
     * @param response Response
     * @return Response
     */
    Response logResponse(Response response) throws Exception;

    HttpInterceptor NO_INTERCEPTOR = new HttpInterceptor() {
        @Override
        public String toJson(Object src) {
            return src.toString();
        }

        @Override
        public <T> T convert(String json, Type type) {
            return null;
        }

        @Override
        public void logRequest(CommonParams commonParams) {

        }

        @Override
        public ErrorModel handleResponse(Exception ex, ErrorModel errorModel) {
            return new ErrorModel(Integer.MIN_VALUE, "");
        }

        @Override
        public ErrorModel handleFailure(Exception ex, ErrorModel errorModel) {
            return new ErrorModel(Integer.MAX_VALUE, "");
        }

        @Override
        public Response logResponse(Response response) throws Exception {
            return response;
        }
    };
}
