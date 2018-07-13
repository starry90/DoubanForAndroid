package com.starry.douban.http;

import com.google.gson.Gson;

import java.io.Closeable;
import java.lang.reflect.Type;

/**
 * @author Starry Jerry
 * @since 2018/7/13.
 */

public class HttpUtil {

    private HttpUtil() {
    }

    /**
     * Ensures that an object reference passed as a parameter to the calling method is not null.
     *
     * @param reference an object reference
     * @return the non-null reference that was validated
     * @throws NullPointerException if {@code reference} is null
     */
    public static <T> T checkNotNull(T reference) {
        if (reference == null) {
            throw new NullPointerException();
        }
        return reference;
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

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }

}
