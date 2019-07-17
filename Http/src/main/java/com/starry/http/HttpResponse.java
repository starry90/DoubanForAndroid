package com.starry.http;

import java.io.InputStream;

/**
 * @author Starry Jerry
 * @since 19-5-31.
 */
public class HttpResponse {

    private String url;

    private String bodyString;

    private InputStream bodyInputStream;

    private long bodyContentLength;

    public HttpResponse(String url, String bodyString) {
        this.url = url;
        this.bodyString = bodyString;
    }

    public HttpResponse(String url, InputStream bodyInputStream, long bodyContentLength) {
        this.url = url;
        this.bodyInputStream = bodyInputStream;
        this.bodyContentLength = bodyContentLength;
    }

    public String getUrl() {
        return url;
    }

    public String getBodyString() {
        return bodyString;
    }

    public InputStream getBodyInputStream() {
        return bodyInputStream;
    }

    public long getBodyContentLength() {
        return bodyContentLength;
    }
}
