package com.starry.douban.jetpack.httpstatuslivedata;

import android.arch.lifecycle.MutableLiveData;

import com.starry.http.HttpManager;
import com.starry.http.error.ErrorModel;

/**
 * 含有数据加载状态的LiveData 封装处理
 * onChange 官方只是处理成功数据的回调，我们需要封装处理网络错误,服务器自定义的一些返回码等这种状况
 *
 * @author Starry Jerry
 * @since 2021/3/25.
 */
public class HttpStatusLiveData<T> extends MutableLiveData<HttpStatusData<T>> {

    /**
     * 表示数据请求异常
     */
    public void postFailure(ErrorModel errorModel) {
        postValue(new HttpStatusData<T>().failure(errorModel));
    }

    /**
     * 表示数据请求成功
     *
     * @param data T
     */
    public void postSuccess(T data) {
        postValue(new HttpStatusData<T>().success(data));
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        //取消网络请求
        HttpManager.cancelTag(this);
    }
}