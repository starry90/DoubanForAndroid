package com.starry.http.error;

/**
 * HTTP状态码异常<p>
 * 不是2开头code统一以服务器错误处理
 *
 * @author Starry Jerry
 * @since 2018/12/4.
 */
public class HttpStatusException extends Exception {

    public HttpStatusException(int code) {
        super("HTTP status code: " + code);
    }
}
