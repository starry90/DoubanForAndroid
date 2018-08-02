package com.starry.douban.http.error;

/**
 * 请求失败的Model
 *
 * @author Starry Jerry
 * @since 18-3-14.
 */

public class ErrorModel {

    private int code;

    private String message;

    private String url;

    /**
     * 返回的错误的报文
     */
    private String response;

    /**
     * 错误已处理
     */
    private boolean processed;

    public ErrorModel(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public ErrorModel setCode(int code) {
        this.code = code;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ErrorModel setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public ErrorModel setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getResponse() {
        return response;
    }

    public ErrorModel setResponse(String response) {
        this.response = response;
        return this;
    }

    public boolean isProcessed() {
        return processed;
    }

    public ErrorModel setProcessed(boolean processed) {
        this.processed = processed;
        return this;
    }

    @Override
    public String toString() {
        return "ErrorModel{" +
                "code=" + code +
                ", message='" + message + '\'' +
                ", url='" + url + '\'' +
                ", response='" + response + '\'' +
                ", processed=" + processed +
                '}';
    }
}
