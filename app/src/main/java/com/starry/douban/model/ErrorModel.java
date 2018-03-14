package com.starry.douban.model;

/**
 * 请求失败的Model
 *
 * @author Starry Jerry
 * @since 18-3-14.
 */

public class ErrorModel {

    private int code;

    private String message;

    private int requestCode;

    public ErrorModel(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public ErrorModel(int code, String message, int requestCode) {
        this.code = code;
        this.message = message;
        this.requestCode = requestCode;
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

    public int getRequestCode() {
        return requestCode;
    }

    public ErrorModel setRequestCode(int requestCode) {
        this.requestCode = requestCode;
        return this;
    }
}
