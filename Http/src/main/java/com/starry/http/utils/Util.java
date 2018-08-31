package com.starry.http.utils;

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

}
