package com.starry.douban.http.request;


import com.starry.douban.http.CommonCallback;
import com.starry.douban.http.HttpManager;
import com.starry.douban.model.BaseModel;

import okhttp3.Call;
import okhttp3.Request;

/**
 * @author Starry Jerry
 * @since 2016/6/19.
 */
public class RequestCall {
    private OKHttpRequest okHttpRequest;
    private Request request;
    private Call call;


    public RequestCall(OKHttpRequest request) {
        this.okHttpRequest = request;
    }


    /**
     * 执行请求
     *
     * @param callback 回调对象
     * @param <T>      对象的泛型
     */
    public <T extends BaseModel> void execute(CommonCallback<T> callback) {
        if (callback != null) {
            callback.onBefore();
        }
        HttpManager.getInstance().execute(generateCall(callback), callback);
    }

    private Call generateCall(CommonCallback callback) {
        request = okHttpRequest.generateRequest(callback);
        call = HttpManager.getInstance().getOkHttpClient().newCall(request);
        return call;
    }

    public Request getRequest() {
        return request;
    }

    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

}
