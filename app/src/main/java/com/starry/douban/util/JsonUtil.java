package com.starry.douban.util;

import com.google.gson.Gson;

import java.lang.reflect.Type;

/**
 * JSON工具类
 *
 * @author Starry Jerry
 * @since 2017/7/16.
 */
public class JsonUtil {

    /**
     * object to json string
     *
     * @param src the object for which Json representation is to be created setting for Gson
     * @return json string
     */
    public static String toJson(Object src) {
        try {
            return new Gson().toJson(src);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    /**
     * json string to object
     *
     * @param json     the string from which the object is to be deserialized
     * @param classOfT the class of T
     * @param <T>      the type of the desired object
     * @return an object of type T from the string. Returns {@code null} if {@code json} is {@code null}.
     */
    public static <T> T toObject(String json, Class<T> classOfT) {
        try {
            return new Gson().fromJson(json, classOfT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * json string to object
     *
     * @param json    the string from which the object is to be deserialized
     * @param typeOfT The specific genericized type of src. You can obtain this type by using the
     * @param <T>     the type of the desired object
     * @return an object of type T from the string. Returns {@code null} if {@code json} is {@code null}.
     */
    public static <T> T toObject(String json, Type typeOfT) {
        try {
            return new Gson().fromJson(json, typeOfT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
