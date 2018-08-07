package com.starry.http.callback;

import com.starry.http.HttpConverter;
import com.starry.http.HttpManager;
import com.starry.http.Util;

import okhttp3.Response;

/**
 * @author Starry Jerry
 * @since 2018/7/1.
 */

public abstract class StringCallback<T> extends CommonCallback<T> {

    @Override
    public T parseResponse(Response response) throws Exception {
        HttpConverter httpConverter = HttpManager.getInstance().getHttpConverter();
        T result = httpConverter.convert(getClass(), response.body());
        Util.checkNotNull(result);
        return result;
    }

}
