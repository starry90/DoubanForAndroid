package com.starry.http.interfaces;


import com.starry.http.CommonParams;
import com.starry.http.HttpResponse;
import com.starry.http.error.ErrorModel;

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
     */
    void logRequest(CommonParams commonParams);

    /**
     * 处理失败的信息
     * 该方法运行在子线程
     *
     * @param ex         Exception
     * @param errorModel ErrorModel
     * @return ErrorModel
     */
    ErrorModel handleFailure(Exception ex, ErrorModel errorModel);

    /**
     * 打印返回报文
     * 该方法运行在子线程
     *
     * @param httpResponse 返回报文
     */
    void logResponse(HttpResponse httpResponse);

}
