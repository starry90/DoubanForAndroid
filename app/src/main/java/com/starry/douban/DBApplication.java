package com.starry.douban;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.starry.douban.base.ActivityCallback;
import com.starry.douban.log.Logger;

import java.io.File;

/**
 * @author Starry Jerry
 */
public class DBApplication extends Application {

    public static String host = "";

    private static DBApplication instance;

    private ActivityCallback activityLifeController;

    /**
     * @return BaseApplication instance
     */
    public static DBApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        activityLifeController = new ActivityCallback();
        registerActivityLifecycleCallbacks(activityLifeController);
    }

    /**
     * 得到分配的缓存大小，这里按可用内存的八分之一（推荐）的大小来做
     *
     * @return 可分配的缓存大小
     */
    public int getMemoryCacheSize() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        int memoryClass = am.getMemoryClass();
        Logger.i("当前可用内存大小为：" + memoryClass + "M");

        return memoryClass / 8;
    }

    /**
     * 获得磁盘缓存的路径
     * 当SD卡存在或SD卡不可被移除的时，就用getExternalCacheDir()方法来获取缓存路径，否则就调用getCacheDir()方法来获取缓存路径。
     * getExternalCacheDir()-> /sdcard/Android/data/<application
     * package>/cache getCacheDir() -> /data/data/<application package>/cache
     *
     * @param context
     * @param uniqueName 缓存文件夹名称，可以自定义。这里我们缓存的是bitmap，所以缓存文件夹名定义为：bitmap
     * @return
     */
    private File getDiskCacheDir(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * 获取网络是否已连接
     *
     * @return
     */
    public static boolean isNetworkReady() {
        ConnectivityManager manager = (ConnectivityManager) instance.getApplicationContext().getSystemService(
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


    public void removeAll() {
        activityLifeController.removeAll();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
        unregisterActivityLifecycleCallbacks(activityLifeController);
    }
}
