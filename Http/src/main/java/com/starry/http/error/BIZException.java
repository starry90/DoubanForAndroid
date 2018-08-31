package com.starry.http.error;

/**
 * @author Starry Jerry
 * @since 2017/10/29.
 */

public class BIZException extends Exception {

    private int errorCode;
    private String errorMessage;
    private String response;
    private static String logFormat = "%d --> %s";

    public BIZException(int errorCode, String errorMessage) {
        super(String.format(logFormat, errorCode, errorMessage));
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public BIZException setResponse(String response) {
        this.response = response;
        return this;
    }

    public String getResponse() {
        return response;
    }

}
