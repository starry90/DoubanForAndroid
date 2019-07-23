package com.starry.douban.base;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.github.moduth.blockcanary.BlockCanary;
import com.squareup.leakcanary.LeakCanary;
import com.starry.douban.constant.Common;
import com.starry.douban.env.ActivityCallback;
import com.starry.douban.env.AppBlockCanaryContext;
import com.starry.douban.env.CookieImpl;
import com.starry.douban.env.GsonConverter;
import com.starry.douban.env.InterceptorImpl;
import com.starry.douban.receiver.CommonReceiver;
import com.starry.douban.service.WorkService;
import com.starry.douban.util.AppUtil;
import com.starry.douban.util.CommonUtils;
import com.starry.douban.util.TimeUtils;
import com.starry.http.HttpManager;

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
        WorkService.stopWorkService(context);
    }

    public static File getCrashDir() {
        return CommonUtils.buildPath(Common.DIR_ROOT, Common.DIR_CRASH);
    }

    public static File getDownloadDir() {
        return CommonUtils.buildPath(getContext().getExternalFilesDir(""), Common.DIR_DOWNLOAD);
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

        lifeCallback = new ActivityCallback();
        application.registerActivityLifecycleCallbacks(lifeCallback);
        initOkHttp();
        registerCommonReceiver(application);

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {
                ex.printStackTrace();
                String crashFile = String.format("%s.txt", TimeUtils.date2String(new Date()));
                CommonUtils.saveCrashInfo(ex, getCrashDir(), crashFile);
                android.os.Process.killProcess(android.os.Process.myPid());
            }
        });
    }

    private void initOkHttp() {
        HttpManager.getInstance()
                .setTimeOut(30)
                .setCookie(new CookieImpl())
                .setHttpConverter(GsonConverter.create())
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
        if (null == networkInfo || !networkInfo.isAvailable() || !networkInfo.isConnected()) {
            return false;
        }

        return true;
    }

}
