package com.starry.http.callback;

import com.starry.http.HttpManager;
import com.starry.http.interfaces.HttpConverter;
import com.starry.http.utils.Util;

import okhttp3.ResponseBody;

/**
 * @author Starry Jerry
 * @since 2018/7/1.
 */

public abstract class StringCallback<T> extends CommonCallback<T> {

    @Override
    public T parseResponse(ResponseBody responseBody) throws Exception {
        HttpConverter httpConverter = HttpManager.getInstance().getHttpConverter();
        T result = httpConverter.responseBodyConverter(getClass(), responseBody.string());
        Util.checkNotNull(result);
        return result;
    }

}
