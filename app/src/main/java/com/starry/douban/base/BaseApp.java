package com.starry.douban.base;

import android.app.Application;
import android.content.Context;

import com.starry.douban.util.FileUtils;
import com.starry.douban.util.TimeUtils;

import java.util.Date;

/**
 * @author Starry Jerry
 * @since 18-3-6.
 */

public class BaseApp {

    private Context context;

    private ActivityCallback lifeCallback;

    private BaseApp() {
    }

    public static BaseApp getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private final static BaseApp INSTANCE = new BaseApp();
    }

    public static Context getContext() {
        return getInstance().context;
    }

    public void attachBaseContext(Context base) {
        context = base;
    }

    /**
     * 退出应用
     */
    public void exitApp() {
        lifeCallback.finishAll();
    }

    public void onCreate(Application application) {
        lifeCallback = new ActivityCallback();
        application.registerActivityLifecycleCallbacks(lifeCallback);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                ex.printStackTrace();
                String crashFile = String.format("Crash-%s.txt", TimeUtils.date2String(new Date()));
                FileUtils.saveCrashInfo(ex, FileUtils.getCrashDir(), crashFile);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }

}
