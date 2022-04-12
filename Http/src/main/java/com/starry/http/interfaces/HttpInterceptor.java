package com.starry.http.interfaces;


import com.starry.http.error.ErrorModel;

/**
 * http interceptor
 *
 * @author Starry Jerry
 * @since 2018/8/2.
 */
public interface HttpInterceptor {

    /**
     * 处理失败的信息
     * 该方法运行在子线程
     *
     * @param ex         Exception
     * @param errorModel ErrorModel
     * @return ErrorModel
     */
    ErrorModel handleFailure(Exception ex, ErrorModel errorModel);

}
