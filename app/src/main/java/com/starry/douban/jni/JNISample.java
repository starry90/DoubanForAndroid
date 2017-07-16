package com.starry.douban.jni;

/**
 * @author Starry Jerry
 * @since 2017/3/28.
 */
public class JNISample {

    static {
        System.loadLibrary("Sample");
    }

    public native String test();
}
