package com.starry.douban.base;

import android.app.Application;
import android.content.Context;

import com.github.moduth.blockcanary.BlockCanary;
import com.squareup.leakcanary.LeakCanary;
import com.starry.douban.constant.Common;
import com.starry.douban.util.FileUtils;
import com.starry.douban.util.TimeUtils;

import java.io.File;
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

    public static File getCrashDir() {
        return FileUtils.buildPath(Common.DIR_ROOT, Common.DIR_CRASH);
    }

    public static File getDownloadDir() {
        return FileUtils.buildPath(getContext().getExternalFilesDir(""), Common.DIR_DOWNLOAD);
    }

    public void onCreate(Application application) {
        if (LeakCanary.isInAnalyzerProcess(application)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(application);
        // Normal app init code...
        BlockCanary.install(application, new AppBlockCanaryContext(context)).start();

        lifeCallback = new ActivityCallback();
        application.registerActivityLifecycleCallbacks(lifeCallback);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                ex.printStackTrace();
                String crashFile = String.format("%s.txt", TimeUtils.date2String(new Date()));
                FileUtils.saveCrashInfo(ex, getCrashDir(), crashFile);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }

}
