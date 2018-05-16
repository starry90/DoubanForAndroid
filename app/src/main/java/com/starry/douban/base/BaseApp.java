package com.starry.douban.base;

import android.app.Application;
import android.content.Context;
import android.os.Build;

import com.starry.douban.util.FileUtils;
import com.starry.douban.util.IOUtils;
import com.starry.douban.util.TimeUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;
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
                saveCrashInfo(ex, FileUtils.getCrashDir(), crashFile);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }

    /**
     * 保存崩溃信息
     */
    private void saveCrashInfo(Throwable ex, File crashDir, String crashName) {
        StringBuilder sb = new StringBuilder();
        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        Field[] declaredFields = Build.class.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            try {
                String name = declaredField.getName();
                sb.append(name).append(":").append(declaredField.get(name)).append("\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sb.append("====================================================").append("\n");

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        sb.append(info.toString());
        printWriter.close();
        File crashFile = new File(crashDir, crashName);
        FileWriter writer = null;
        try {
            writer = new FileWriter(crashFile, false);
            writer.write(sb.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(writer);
        }
    }

}
