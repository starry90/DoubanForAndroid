package com.starry.douban.util;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;

import com.starry.log.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AppUtil {

    /**
     * FileProvider 目录
     */
    private static final String WEB_VIEW_CACHE = "webViewCache";

    /**
     * 调用系统camera拍照时，图片存放的路径
     */
    public static File getFileChooserTmpPicFile(Context mContext) {
        File dir = new File(mContext.getExternalCacheDir(), WEB_VIEW_CACHE);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File picFile = null;
        try {
            picFile = File.createTempFile("pic_", ".jpeg", dir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return picFile;
    }

    /**
     * feature_bug21913:调用系统camera录像时，视频存放的位置
     */
    public static File getFileChooserTmpVideoFile(Context mContext) {
        File dir = new File(mContext.getExternalCacheDir(), WEB_VIEW_CACHE);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File videoFile = null;
        try {
            videoFile = File.createTempFile("video_", ".3gp", dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return videoFile;
    }

    public static Uri getUriFromFile(Context context, File file) {
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = getUriFromFileForN(context, file);
        } else {
            uri = Uri.fromFile(file);
        }
        return uri;
    }

    static Uri getUriFromFileForN(Context context, File file) {
        return FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
    }

    private static void startActivityForResult(Fragment fragment, Activity activity, Intent intent, int requestCode) {
        if (fragment != null) {
            fragment.startActivityForResult(intent, requestCode);
        } else if (activity != null) {
            activity.startActivityForResult(intent, requestCode);
        }
    }

    /**
     * 打开系统照相机
     * Fragment  onActivityResult接收返回值
     *
     * @param savedFile 图片保存的位置
     */
    public static Uri openSystemCamera(Fragment fragment, int requestCode, File savedFile) {
        return openSystemCamera(fragment, null, requestCode, savedFile);
    }

    /**
     * 打开系统照相机
     * Activity onActivityResult接收返回值
     *
     * @param savedFile 图片保存的位置
     */
    public static Uri openSystemCamera(Activity activity, int requestCode, File savedFile) {
        return openSystemCamera(null, activity, requestCode, savedFile);
    }

    private static Uri openSystemCamera(Fragment fragment, Activity activity, int requestCode, File savedFile) {
        Uri uri = null;
        try {
            //调用照相机
            Intent intent = new Intent();
            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            uri = getUriFromFile(activity, savedFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);//// 设置系统相机拍摄照片完成后图片文件的存放地址
            startActivityForResult(fragment, activity, intent, requestCode);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return uri;
    }

    /**
     * 打开系统录音机
     * Fragment  onActivityResult接收返回值
     */
    public static void openSystemMicrophone(Fragment fragment, int requestCode) {
        openSystemMicrophone(fragment, null, requestCode);
    }

    /**
     * 打开系统录音机
     * Activity onActivityResult接收返回值
     */
    public static void openSystemMicrophone(Activity activity, int requestCode) {
        openSystemMicrophone(null, activity, requestCode);
    }

    private static void openSystemMicrophone(Fragment fragment, Activity activity, int requestCode) {
        try {
            Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
            startActivityForResult(fragment, activity, intent, requestCode);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * 打开系统摄像机
     * Fragment  onActivityResult接收返回值
     */
    public static Uri openSystemCamcorder(Fragment fragment, int requestCode, File savedFile) {
        return openSystemCamcorder(fragment, null, requestCode, savedFile);
    }

    /**
     * 打开系统摄像机
     * Activity onActivityResult接收返回值
     */
    public static Uri openSystemCamcorder(Activity activity, int requestCode, File savedFile) {
        return openSystemCamcorder(null, activity, requestCode, savedFile);
    }

    private static Uri openSystemCamcorder(Fragment fragment, Activity activity, int requestCode, File savedFile) {
        Uri uri = null;
        try {
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1); //0:低品质，1:高品质
            uri = Uri.fromFile(savedFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(fragment, activity, intent, requestCode);
        } catch (Throwable t) {
            t.printStackTrace();

        }
        return uri;
    }

    /**
     * 打开系统相册
     * Fragment  onActivityResult接收返回值
     */
    public static void openSystemGallery(Fragment fragment, int requestCode) {
        openSystemGallery(fragment, null, requestCode);
    }

    /**
     * 打开系统相册
     * Activity onActivityResult接收返回值
     */
    public static void openSystemGallery(Activity activity, int requestCode) {
        openSystemGallery(null, activity, requestCode);
    }

    private static void openSystemGallery(Fragment fragment, Activity activity, int requestCode) {
        try {
            Intent intent = new Intent();
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            intent.setAction("android.intent.action.PICK");
            startActivityForResult(fragment, activity, intent, requestCode);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    /**
     * 请求一般类型的文件内容
     * Fragment  onActivityResult接收返回值
     */
    public static void requestGetFileIntent(Fragment fragment, String acceptType, int requestCode) {
        requestGetFileIntent(fragment, null, acceptType, requestCode);
    }

    /**
     * 请求一般类型的文件内容
     * Activity onActivityResult接收返回值
     */
    public static void requestGetFileIntent(Activity mActivity, String acceptType, int requestCode) {
        requestGetFileIntent(null, mActivity, acceptType, requestCode);
    }

    private static void requestGetFileIntent(Fragment fragment, Activity activity, String acceptType, int requestCode) {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType(acceptType.toLowerCase(Locale.ENGLISH));
            startActivityForResult(fragment, activity, intent, requestCode);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    // 获取当前应用应用名
    public static String getAppName(Context context) {
        return context.getApplicationInfo().loadLabel(context.getPackageManager()).toString();
    }

    // 获取当前应用版本名
    public static String getVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS).versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    // 获取当前应用版本号
    public static int getVersionCode(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(),
                    PackageManager.GET_CONFIGURATIONS).versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // 获取当前应用包名
    public static String getPackageName(Context context) {
        return context.getPackageName();
    }

    // 获取当前应用图标
    public static Drawable getAppIcon(Context context) {
        return context.getApplicationInfo().loadIcon(context.getPackageManager());
    }

    // 通过进程名获取进程的进程id
    public static int getPid(Context context, String processName) {
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcessList) {
            if (processName.equals(appProcess.processName)) {
                return appProcess.pid;
            }
        }
        return 0;
    }

    // 安装apk
    public static void installApk(Context context, String authority, String apkPath) {
        File file = new File(apkPath);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        Uri uri = FileProviderUtil.getUriForFile(context, authority, file, intent);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    // 启动apk
    public static void launchApk(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        context.startActivity(intent);
    }

    // 通过文件名获取包名
    public static String getPackageName(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            return appInfo.packageName;
        } else {
            return null;
        }
    }

    // 判断本程序 最顶部activity 是否是 此activity
    public static boolean isActivityTopStartThisProgram(Context context, String activityName) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> list = activityManager.getRunningTasks(100);
        if (list != null && list.size() > 0) {
            for (RunningTaskInfo runningTaskInfo : list) {
                Logger.e("tag",
                        "runningTaskInfo.topActivity.getClassName() = " + runningTaskInfo.topActivity.getClassName());
                Logger.e("tag", "activityName = " + activityName);
                if (context.getPackageName().equals(runningTaskInfo.baseActivity.getPackageName())) {
                    if (runningTaskInfo.topActivity.getClassName().equals(activityName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean isAnimatorVision() {
        return android.os.Build.VERSION.SDK_INT >= 11;
    }
}
