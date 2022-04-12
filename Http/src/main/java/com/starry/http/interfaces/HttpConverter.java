package com.starry.http.interfaces;

/**
 * @author Starry Jerry
 * @since 18-8-7.
 */
public interface HttpConverter {

    /**
     * convert {@code responseBody} to T
     * <p>
     * 如果不知道怎么解析，请参考 {@code CommonCallback} 最下面注释了的方法
     *
     * @param cbClass    CommonCallback class
     * @param bodyString 返回报文
     * @param <T>        the type of desired object
     * @return T
     * @throws Exception Exception
     */
    <T> T convert(Class<?> cbClass, String bodyString) throws Exception;

}
