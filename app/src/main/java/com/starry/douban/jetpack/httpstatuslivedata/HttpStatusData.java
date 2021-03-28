package com.starry.douban.jetpack.httpstatuslivedata;

import android.support.annotation.NonNull;

import com.starry.http.error.ErrorModel;

/**
 * 带有状态的数据
 * onChange 官方只是处理成功数据的回调，我们需要封装处理网络错误,服务器自定义的一些返回码等这种状况
 *
 * @author Starry Jerry
 * @since 2021/3/25.
 */
public class HttpStatusData<T> {

    private DataStatus status;

    private T data;

    /**
     * http 请求的错误信息
     */
    private ErrorModel errorModel;

    HttpStatusData<T> success(@NonNull T data) {
        this.status = DataStatus.DATA_SUCCESS;
        this.data = data;
        this.errorModel = null;
        return this;
    }

    HttpStatusData<T> failure(@NonNull ErrorModel errorModel) {
        this.status = DataStatus.DATA_ERROR;
        this.data = null;
        this.errorModel = errorModel;
        return this;
    }

    public DataStatus getStatus() {
        return status;
    }

    public T getData() {
        return data;
    }

    public ErrorModel getErrorModel() {
        return errorModel;
    }

    /**
     * 基本只需要维护SUCCESS，ERROR。LOADING 也可以省去
     */
    public enum DataStatus {
        DATA_SUCCESS,
        DATA_ERROR
    }

}
