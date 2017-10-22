package com.starry.douban.http;


import com.starry.douban.http.request.OKHttpRequest;
import com.starry.douban.http.request.RealRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;

/**
 * Builder模式
 *
 * @author Starry Jerry
 * @since 2016/6/19.
 */
public class CommonParams {

    // JSON --> application/json;charset=utf-8
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;charset=utf-8");

    private String url;
    private Object tag;

    private Map<String, String> params;
    private Map<String, String> headers;

    private List<FileInput> files = new ArrayList<>();

    private String content;
    private MediaType mediaType;

    private CommonParams(Builder builder) {
        this.url = builder.url;
        this.tag = builder.tag;
        this.params = builder.params;
        this.headers = builder.headers;

        this.files = builder.files;
        this.content = builder.content;
        this.mediaType = builder.mediaType;
    }

    public String url() {
        return url;
    }

    public Object tag() {
        return tag;
    }

    public Map<String, String> params() {
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

    public MediaType mediaType() {
        return mediaType;
    }

    public static final class Builder {
        private String url;
        private Object tag;
        private Map<String, String> headers;
        private Map<String, String> params;
        private List<FileInput> files = new ArrayList<>();
        private String content;
        private MediaType mediaType;
        private OKHttpRequest builder;

        public Builder(OKHttpRequest builder) {
            this.builder = builder;
            mediaType = MEDIA_TYPE_JSON;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder tag(Object tag) {
            this.tag = tag;
            return this;
        }

        public Builder params(Map<String, String> params) {
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
            files.add(new FileInput(name, filename, file));
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder mediaType(MediaType mediaType) {
            this.mediaType = mediaType;
            return this;
        }

        public RealRequest build() {
            if (this.params == null) {
                params = new LinkedHashMap<>();
            }
            return builder.build(new CommonParams(this));
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
