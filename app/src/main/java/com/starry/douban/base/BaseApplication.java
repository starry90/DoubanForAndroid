package com.starry.douban.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;

import com.starry.douban.log.Logger;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Stack;

/**
 * @author liuzx
 */
public class BaseApplication extends Application {

    public static String host = "";

    private static BaseApplication instance;

    /***
     * 寄存整个应用Activity
     **/
    private final Stack<WeakReference<Activity>> activitys = new Stack<WeakReference<Activity>>();

    /**
     * @return BaseApplication instance
     */
    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;


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


    /******************************************* Application中存放的Activity操作（压栈/出栈）API（开始） *****************************************/

    /**
     * 将Activity压入Application栈
     *
     * @param task 将要压入栈的Activity对象
     */
    public void pushTask(WeakReference<Activity> task) {
        activitys.push(task);
    }

    /**
     * 将传入的Activity对象从栈中移除
     *
     * @param task
     */
    public void removeTask(WeakReference<Activity> task) {
        activitys.remove(task);
    }

    /**
     * 根据指定位置从栈中移除Activity
     *
     * @param taskIndex Activity栈索引
     */
    public void removeTask(int taskIndex) {
        if (activitys.size() > taskIndex)
            activitys.remove(taskIndex);
    }

    /**
     * 将栈中Activity移除至栈顶
     */
    public void removeToTop() {
        int end = activitys.size();
        int start = 1;
        for (int i = end - 1; i >= start; i--) {
            if (!activitys.get(i).get().isFinishing()) {
                activitys.get(i).get().finish();
            }
        }
    }

    /**
     * 移除全部（用于整个应用退出）
     */
    public void removeAll() {
        // finish所有的Activity
        for (WeakReference<Activity> task : activitys) {
            if (!task.get().isFinishing()) {
                task.get().finish();
            }
        }
    }

    /******************************************* Application中存放的Activity操作（压栈/出栈）API（结束） *****************************************/

}
