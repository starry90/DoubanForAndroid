package com.starry.http.callback;


import com.starry.http.error.ErrorModel;

/**
 * <p>1.获取该类实例时未加方法体（new FinalCallback<String>()），getClass()拿到的Class是FinalCallback<T>
 * <p>2.获取该类实例加了方法体（new FinalCallback<String>(){}），getClass()拿到的Class是FinalCallback<String>
 * <p> 以abstract修饰，获取实例时强制加上{}，避免在解析时无法获取到泛型的Class
 * <p> Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
 * <p> for example
 * <p> new FinalCallback<String>(){}
 *
 * @author Starry Jerry
 * @since 18-11-19.
 */
public abstract class FinalCallback<T> extends StringCallback<T> {

    @Override
    public void onSuccess(T response, Object... obj) {

    }

    @Override
    public void onFailure(ErrorModel errorModel) {

    }
}
