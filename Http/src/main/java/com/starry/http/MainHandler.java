package com.starry.http;

import android.os.Handler;
import android.os.Looper;

/**
 * 运行在UI线程的Handler
 *
 * @author Starry Jerry
 * @since 2017/10/22.
 */
public class MainHandler {

    private Handler mHandler;

    private MainHandler() {
        mHandler = new Handler(Looper.getMainLooper());
    }

    private static class Holder {
        private final static MainHandler INSTANCE = new MainHandler();
    }

    public static void post(Runnable runnable) {
        Holder.INSTANCE.mHandler.post(runnable);
    }
}
