package com.starry.http.interfaces;


import com.starry.http.CommonParams;
import com.starry.http.error.ErrorModel;

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
     * 打印请求报文
     * 该方法运行于调用网络请求的线程
     *
     * @param commonParams CommonParams
     * @return CommonParams
     */
    CommonParams logRequest(CommonParams commonParams);

    /**
     * 处理 OkHttp onResponse Exception信息
     * 该方法运行在子线程
     *
     * @param ex Exception
     * @return ErrorModel
     * @see Callback#onResponse(okhttp3.Call, okhttp3.Response)
     */
    ErrorModel handleResponse(Exception ex, ErrorModel errorModel);

    /**
     * 处理 OkHttp onFailure Exception信息
     * 该方法运行在子线程
     *
     * @param ex Exception
     * @return ErrorModel
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
        public CommonParams logRequest(CommonParams commonParams) {
            return commonParams;
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
