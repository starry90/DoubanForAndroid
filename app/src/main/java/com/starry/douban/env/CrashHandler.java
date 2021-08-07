package com.starry.douban.env;

import android.os.Process;
import android.support.annotation.NonNull;

import com.starry.douban.util.CommonUtils;
import com.starry.douban.util.TimeUtils;

import java.util.Date;

/**
 * 崩溃捕获
 * <p>
 * 采用静态代理，代理了系统捕获异常类，增强处理异常的方法：如打印日志到文件
 *
 * @author Starry Jerry
 * @since 21-8-7.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    /**
     * 代理对象
     * com.android.internal.os.RuntimeInit$UncaughtHandler@1de23357
     */
    private final Thread.UncaughtExceptionHandler defaultUncaughtExceptionHandler;

    public CrashHandler() {
        //frameworks\base\core\java\com\android\internal\os\RuntimeInit.java
        //    protected static final void commonInit() {
        //        // ......
        //
        //        /*
        //         * set handlers; these apply to all threads in the VM. Apps can replace
        //         * the default handler, but not the pre handler.
        //         */
        //        Thread.setUncaughtExceptionPreHandler(new LoggingHandler());
        //        // 1
        //        Thread.setDefaultUncaughtExceptionHandler(new KillApplicationHandler());
        //
        //        // ......
        //    }
        // commonInit()方法在 RuntimeInit 的 main()中会调用。设置了 DefaultUncaughtExceptionHandler，并且这个 Handler会应用到虚拟机的所有线程中。
        // killApplicationHandler 这个类实现了 UncaughtExceptionHandler 接口，并且在 uncaughtException()方法中，将崩溃告知了 AMS,然后将进程杀死。
        // 所以说 Android 其实默认给所有线程设置了 KillApplicationHandler 这个 ExceptionHandler。
        defaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable ex) {
        ex.printStackTrace();
        String crashFile = String.format("%s.txt", TimeUtils.date2String(new Date()));
        CommonUtils.saveCrashInfo(ex, AppWrapper.getCrashDir(), crashFile);

        if (defaultUncaughtExceptionHandler != null) {
            // 调用代理对象异常处理方法
            defaultUncaughtExceptionHandler.uncaughtException(t, ex);
        } else {
            Process.killProcess(Process.myPid());
        }
    }
}
