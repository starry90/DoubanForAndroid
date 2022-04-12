package com.starry.douban.env;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

import com.github.moduth.blockcanary.BlockCanary;
import com.squareup.leakcanary.LeakCanary;
import com.starry.douban.constant.Common;
import com.starry.douban.receiver.CommonReceiver;
import com.starry.douban.service.WorkService;
import com.starry.douban.util.AppUtil;
import com.starry.douban.util.BuglyHelper;
import com.starry.douban.util.FileUtils;
import com.starry.http.HttpManager;

import java.io.File;
import java.lang.reflect.Method;

/**
 * @author Starry Jerry
 * @since 18-3-6.
 */

public class AppWrapper {

    private Context context;

    private ActivityCallback lifeCallback;

    private AppWrapper() {
    }

    public static AppWrapper getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private final static AppWrapper INSTANCE = new AppWrapper();
    }

    public static Context getContext() {
        return getInstance().context;
    }

    public void attachBaseContext(Context base) {
        context = base;
        setHiddenApiExemptions();
    }

    /**
     * 参考 https://blog.csdn.net/qq_27512671/article/details/105580036
     * <p>
     * 设置豁免所有hide api
     * http://androidxref.com/9.0.0_r3/xref/art/test/674-hiddenapi/src-art/Main.java#100
     * VMRuntime.getRuntime().setHiddenApiExemptions(new String[]{"L"});
     * <p>
     * 元反射基础上，本进程将所有灰黑api加入白名单（能避免弹窗，logcat打印，后续即使不使用元反射也能达到效果）
     * <p>
     * 优点：
     * <p>
     * 1能避免弹窗
     * 2能避免代码扫描，logcat打印
     * 3某些用getMethod无法发现的方法，可以被发现了了，也可以反射了
     * 4对于正常反射的代码，仍然不会弹窗，打印logcat
     */
    private void setHiddenApiExemptions() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            return;
        }

        try {
            Method forName = Class.class.getDeclaredMethod("forName", String.class);
            Method getDeclaredMethod = Class.class.getDeclaredMethod("getDeclaredMethod", String.class, Class[].class);

            Class<?> vmRuntimeClass = (Class<?>) forName.invoke(null, "dalvik.system.VMRuntime");
            Method getRuntime = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "getRuntime", null);
            Method setHiddenApiExemptions = (Method) getDeclaredMethod.invoke(vmRuntimeClass, "setHiddenApiExemptions", new Class[]{String[].class});
            Object sVmRuntime = getRuntime.invoke(null);
            setHiddenApiExemptions.invoke(sVmRuntime, new Object[]{new String[]{"L"}});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出应用
     */
    public void exitApp() {
        lifeCallback.finishAll();
        WorkService.stopWorkService(context);
    }

    public static File getPictureDir() {
        return FileUtils.buildPath(getContext().getExternalFilesDir(""), Common.DIR_PICTURE);
    }

    public static File getCrashDir() {
        return FileUtils.buildPath(getContext().getExternalFilesDir(""), Common.DIR_CRASH);
    }

    public static File getDownloadDir() {
        return FileUtils.buildPath(getContext().getExternalFilesDir(""), Common.DIR_DOWNLOAD);
    }

    public static void installApp(File file) {
        AppUtil.installApk(getContext(), Common.FILE_PROVIDER_AUTHORITY, file.getAbsolutePath());
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
        // bugly init code
        BuglyHelper.install(application);

        lifeCallback = new ActivityCallback();
        application.registerActivityLifecycleCallbacks(lifeCallback);
        initOkHttp();
        registerCommonReceiver(application);

        Thread.setDefaultUncaughtExceptionHandler(new CrashHandler());
    }

    private void initOkHttp() {
        HttpManager.getInstance()
                .setTimeOut(30)
                .setCookie(new CookieImpl())
                .setHttpConverter(GsonConverter.create())
                .addInterceptor(new HttpLogInterceptor())
                .setInterceptor(new InterceptorImpl())
                .setCertificates()
                .build();
    }

    private void registerCommonReceiver(Application application) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_ON); //开屏
        filter.addAction(Intent.ACTION_SCREEN_OFF); //锁屏
        filter.addAction(Intent.ACTION_USER_PRESENT); //解屏
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION); //网络变化
        application.registerReceiver(new CommonReceiver(), filter);
    }

    /**
     * 获取网络是否已连接
     *
     * @return {@code true} if the network is available, {@code false} otherwise
     */
    public boolean networkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(
                Context.CONNECTIVITY_SERVICE);
        if (null == manager) {
            return false;
        }

        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return null != networkInfo && networkInfo.isAvailable() && networkInfo.isConnected();
    }

}
