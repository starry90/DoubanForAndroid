package com.starry.http.interfaces;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * @author Starry Jerry
 * @since 18-8-7.
 */
public interface HttpConverter {

    /**
     * convert {@code value} to RequestBody
     *
     * @param value Object
     * @return RequestBody
     */
    RequestBody convert(Object value);

    /**
     * convert {@code responseBody} to T
     * <p>
     * 如果不知道怎么解析，请参考 {@code CommonCallback} 最下面注释了的方法
     *
     * @param cbClass      CommonCallback class
     * @param responseBody ResponseBody
     * @param <T>          the type of desired object
     * @return T
     * @throws Exception Exception
     */
    <T> T convert(Class<?> cbClass, ResponseBody responseBody) throws Exception;

}
