package com.starry.http;

import com.starry.http.interfaces.HttpConverter;
import com.starry.http.interfaces.HttpInterceptor;
import com.starry.http.utils.HttpsUtils;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * HTTP请求管理者
 * <p>
 * For example,
 * <pre><code>
 * HttpManager.getInstance()
 * .setTimeOut(30)
 * .setHttpConverter(GsonConverter.create())
 * .setInterceptor(new InterceptorImpl())
 * .setCertificates()
 * .build();
 *
 * @author Starry Jerry
 * @since 2016/6/19.
 */
public class HttpManager {

    /**
     * 超时时间
     */
    private static final int TIME_OUT = 30;

    /**
     * OkHttpClient
     */
    private OkHttpClient okHttpClient;
    /**
     * OkHttpClient.Builder
     */
    private OkHttpClient.Builder okHttpClientBuilder;

    /**
     * http converter
     */
    private HttpConverter httpConverter;
    /**
     * http interceptor
     */
    private HttpInterceptor interceptor;

    /**
     * @return HttpManager
     */
    public static HttpManager getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        /**
         * HttpManager 只有一个实例
         */
        private final static HttpManager INSTANCE = new HttpManager();
    }

    private HttpManager() {
        okHttpClientBuilder = new OkHttpClient.Builder();
        interceptor = HttpInterceptor.NO_INTERCEPTOR;
        timeOut(TIME_OUT);
    }

    private void timeOut(long timeOut) {
        okHttpClientBuilder.connectTimeout(timeOut, TimeUnit.SECONDS)//连接主机超时
                .readTimeout(timeOut, TimeUnit.SECONDS)//从主机读取数据超时
                .writeTimeout(timeOut, TimeUnit.SECONDS);//从主机写数据超时
    }

    public void build() {
        if (httpConverter == null) throw new NullPointerException("httpConverter == null");
        okHttpClient = okHttpClientBuilder.build();
    }

    public OkHttpClient getOkHttpClient() {
        return okHttpClient;
    }

    public void setOkHttpClient(OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
        this.okHttpClientBuilder = okHttpClient.newBuilder();
    }

    public HttpInterceptor getInterceptor() {
        return interceptor;
    }

    public HttpManager setInterceptor(HttpInterceptor interceptor) {
        this.interceptor = interceptor;
        return this;
    }

    public HttpConverter getHttpConverter() {
        return httpConverter;
    }

    public HttpManager setHttpConverter(HttpConverter httpConverter) {
        this.httpConverter = httpConverter;
        return this;
    }

    /**
     * 设置超时时间 单位秒
     *
     * @param timeOut 超时时间 默认30秒
     * @return HttpManager
     */
    public HttpManager setTimeOut(long timeOut) {
        timeOut(timeOut);
        return this;
    }

    /**
     * https单向认证
     * 用含有服务端公钥的证书校验服务端证书
     */
    public HttpManager setCertificates(InputStream... certificates) {
        setCertificates(null, null, certificates);
        return this;
    }

    /**
     * https双向认证
     *
     * @param bksFile      bks证书
     * @param password     the password used to check the integrity of the keystore, the password used to unlock the keystore or {@code null}
     * @param certificates 用含有服务端公钥的证书校验服务端证书
     */
    public HttpManager setCertificates(InputStream bksFile, String password, InputStream... certificates) {
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, bksFile, password, certificates);
        if (sslParams != null) {
            okHttpClientBuilder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        }
        return this;
    }

    /**
     * Get 请求
     */
    public static CommonParams.Builder get(String url) {
        return newBuilder(CommonParams.GET, url);
    }

    /**
     * Post 请求
     */
    public static CommonParams.Builder post(String url) {
        return newBuilder(CommonParams.POST_FORM, url);
    }

    /**
     * Post String
     */
    public static CommonParams.Builder postString(String url) {
        return newBuilder(CommonParams.POST_STRING, url);
    }

    private static CommonParams.Builder newBuilder(String method, String url) {
        return new CommonParams.Builder(method).url(url);
    }

    /**
     * 根据tag取消请求
     *
     * @param tag 标签
     */
    public static void cancelTag(Object tag) {
        OkHttpClient client = getInstance().getOkHttpClient();
        for (Call call : client.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
        for (Call call : client.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }

}