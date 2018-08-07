package com.starry.http;


import com.starry.http.request.GetRequest;
import com.starry.http.request.OKHttpRequest;
import com.starry.http.request.PostFormRequest;
import com.starry.http.request.PostStringRequest;
import com.starry.http.request.RealRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Builder模式
 *
 * @author Starry Jerry
 * @since 2016/6/19.
 */
public class CommonParams {

    public static final String GET = "GET";
    public static final String POST_FORM = "POST_FORM";
    public static final String POST_STRING = "POST_STRING";

    private String url;
    private Object tag;
    private String method;

    private Map<String, String> headers;
    private Map<String, Object> params;
    private List<FileInput> files;
    private String content;

    private CommonParams(Builder builder) {
        this.url = builder.url;
        this.tag = builder.tag;
        this.method = builder.method;

        this.params = builder.params;
        this.headers = builder.headers;
        this.files = builder.files;
        this.content = builder.content;
    }

    public String url() {
        return url;
    }

    public Object tag() {
        return tag;
    }

    public String method() {
        return method;
    }

    public Map<String, Object> params() {
        return params;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public List<FileInput> files() {
        return files;
    }

    public String content() {
        return content;
    }

    public static final class Builder {
        private String url;
        private Object tag;
        private String method;

        private Map<String, String> headers;
        private Map<String, Object> params;
        private List<FileInput> files;
        private String content;
        private OKHttpRequest okHttpRequest;

        public Builder(String method) {
            this.method = method;
            if (POST_FORM.equals(method)) {
                this.okHttpRequest = new PostFormRequest();
            } else if (POST_STRING.equals(method)) {
                this.okHttpRequest = new PostStringRequest();
            } else {
                this.okHttpRequest = new GetRequest();
            }
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Builder params(Map<String, Object> params) {
            this.params = params;
            return this;
        }

        public Builder params(String key, String val) {
            if (this.params == null) {
                params = new LinkedHashMap<>();
            }
            params.put(key, val);
            return this;
        }

        public Builder headers(Map<String, String> headers) {
            this.headers = headers;
            return this;
        }

        public Builder headers(String key, String val) {
            if (this.headers == null) {
                headers = new LinkedHashMap<>();
            }
            headers.put(key, val);
            return this;
        }

        public Builder files(String name, String filename, File file) {
            if (files == null) {
                files = new ArrayList<>();
            }
            files.add(new FileInput(name, filename, file));
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public RealRequest build() {
            if (this.params == null) {
                params = new LinkedHashMap<>();
            }
            return okHttpRequest.build(new CommonParams(this));
        }

    }

    public static final class FileInput {
        public String key;
        public String filename;
        public File file;

        public FileInput(String name, String filename, File file) {
            this.key = name;
            this.filename = filename;
            this.file = file;
        }

        @Override
        public String toString() {
            return "FileInput{" +
                    "key='" + key + '\'' +
                    ", filename='" + filename + '\'' +
                    ", file=" + file +
                    '}';
        }
    }
}
