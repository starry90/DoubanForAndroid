package com.starry.douban.http;


import com.google.gson.reflect.TypeToken;
import com.starry.douban.log.Logger;
import com.starry.douban.model.BaseModel;
import com.starry.douban.model.ErrorModel;
import com.starry.douban.ui.ILoadingView;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 网络请求回调类
 *
 * @param <T> 解析的对象
 * @author Starry Jerry
 * @since 2016/6/19.
 */
public abstract class CommonCallback<T> {

    private ILoadingView mLoadingView;

    public final static CommonCallback<BaseModel> NO_CALLBACK = new CommonCallback<BaseModel>() {
        @Override
        public void onSuccess(BaseModel response, Object... obj) {

        }

        @Override
        public void onFailure(ErrorModel errorModel) {

        }
    };

    /**
     * 无Loading View的网络请求
     */
    public CommonCallback() {
    }

    /**
     * 有Loading View的网络请求
     *
     * @param mLoadingView ILoadingView
     */
    public CommonCallback(ILoadingView mLoadingView) {
        this.mLoadingView = mLoadingView;
    }

    /**
     * @param response 返回的对象
     * @param obj      可扩展参数
     */
    public abstract void onSuccess(T response, Object... obj);

    /**
     * @param errorModel ErrorModel
     */
    public abstract void onFailure(ErrorModel errorModel);

    /**
     * 开始执行网络请求
     */
    public void onBefore() {
    }

    /**
     * 网络请求结束
     *
     * @param status 1正常 2加载失败 3数据为空
     */
    public void onAfter(final int status) {
        //保证是在UI线程操作UI
        HandlerMain.getHandler().post(new Runnable() {
            @Override
            public void run() {
                if (mLoadingView != null) {
                    mLoadingView.hideLoading(status);
                    mLoadingView.onLoadingComplete();
                    mLoadingView = null;
                }
            }
        });
    }

    /**
     * @param progress 进度
     */
    public void inProgress(float progress) {
        Logger.d("progress=" + progress);
    }

    /**
     * 获取Json对象的类型，因为数据可能是Json数组也可能是Json对象
     */
    public Type getType() {
        Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        if (type instanceof Class) {//如果是Object直接返回
            return type;
        } else {//如果是集合，获取集合的类型map或list
            return new TypeToken<T>() {
            }.getType();
        }
    }

}