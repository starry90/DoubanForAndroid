package com.starry.http.utils;

import com.starry.http.callback.CommonCallback;
import com.starry.http.error.ErrorModel;
import com.starry.http.error.HttpStatusException;

import java.io.Closeable;

/**
 * @author Starry Jerry
 * @since 2018/7/13.
 */

public class Util {

    private Util() {
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
            throw new NullPointerException("reference == null");
        }
        return reference;
    }

    public static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static String convert(Object value) {
        if (value == null) {
            return "";
        }
        return value.toString();
    }

    /**
     * 检查http code
     *
     * @param code 错误码
     * @throws HttpStatusException http状态码异常
     */
    public static void checkHttpCode(int code) throws HttpStatusException {
        if (code < 200 || code >= 300) {// 不是2开头code统一以服务器错误处理
            throw new HttpStatusException(code);
        }
    }

    public static void sendFailureCallback(final ErrorModel errorModel, final CommonCallback callback) {
        if (errorModel.isProcessed()) { //处理过错误信息，不再回调
            return;
        }

        MainHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(errorModel);
                callback.onAfter(false);
            }
        });
    }

    public static <T> void sendSuccessCallback(final T object, final CommonCallback<T> callback) {
        MainHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(object);
                callback.onAfter(true);
            }
        });
    }

    public static <T> void sendCanceledCallback(final CommonCallback<T> callback) {
        MainHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onAfter(false);
            }
        });
    }

}
