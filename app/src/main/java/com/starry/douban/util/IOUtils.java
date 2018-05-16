package com.starry.douban.util;

import java.io.Closeable;

/**
 * @author Starry Jerry
 * @since 18-5-16.
 */

public class IOUtils {

    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
