package com.starry.http.request;


import com.starry.http.CommonParams;
import com.starry.http.callback.CommonCallback;
import com.starry.http.utils.MainHandler;
import com.starry.http.utils.Util;

import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * @author Starry Jerry
 * @since 2022/4/11.
 */
public class PostFormUploadRequest extends OKHttpRequest {

    @Override
    protected RequestBody buildRequestBody(CommonParams commonParams) {
        Map<String, Object> params = commonParams.params();
        List<CommonParams.FileInput> files = commonParams.files();
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("上传文件为空，请检查！");
        }

        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                String value = Util.convert(params.get(key));
                builder.addPart(Headers.of("Content-Disposition", "form-data; name=\"" + key + "\""),
                        RequestBody.create(null, value));
            }
        }

        for (int i = 0; i < files.size(); i++) {
            CommonParams.FileInput fileInput = files.get(i);
            RequestBody fileBody = RequestBody.create(getMediaType(fileInput.filename), fileInput.file);
            builder.addFormDataPart(fileInput.key, fileInput.filename, fileBody);
        }
        return builder.build();
    }

    private MediaType getMediaType(String path) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        String contentTypeFor = fileNameMap.getContentTypeFor(path);
        if (contentTypeFor == null) {
            contentTypeFor = "application/octet-stream";
        }
        return MediaType.parse(contentTypeFor);
    }

    protected RequestBody wrapRequestBody(RequestBody requestBody, final CommonCallback callback) {
        return new CountingRequestBody(requestBody, new CountingRequestBody.Listener() {
            @Override
            public void onRequestProgress(final long bytesWritten, final long contentLength) {
                MainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.inProgress(bytesWritten * 1.0f / contentLength, contentLength);
                    }
                });
            }
        });
    }

    @Override
    protected Request buildRequest(Request.Builder builder, RequestBody requestBody) {
        return builder.post(requestBody).build();
    }


    /**
     * Decorates an OkHttp request body to count the number of bytes written when writing it. Can
     * decorate any request body, but is most useful for tracking the upload progress of large
     * multipart requests.
     *
     * @author Leo Nikkilä
     */
    public static class CountingRequestBody extends RequestBody {

        protected RequestBody delegate;
        protected Listener listener;

        protected CountingSink countingSink;

        public CountingRequestBody(RequestBody delegate, Listener listener) {
            this.delegate = delegate;
            this.listener = listener;
        }

        @Override
        public MediaType contentType() {
            return delegate.contentType();
        }

        @Override
        public long contentLength() {
            try {
                return delegate.contentLength();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return -1;
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {

            countingSink = new CountingSink(sink);
            BufferedSink bufferedSink = Okio.buffer(countingSink);

            delegate.writeTo(bufferedSink);

            bufferedSink.flush();
        }

        protected final class CountingSink extends ForwardingSink {

            private long bytesWritten = 0;

            public CountingSink(Sink delegate) {
                super(delegate);
            }

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);

                bytesWritten += byteCount;
                listener.onRequestProgress(bytesWritten, contentLength());
            }

        }

        public interface Listener {
            void onRequestProgress(long bytesWritten, long contentLength);
        }

    }
}
