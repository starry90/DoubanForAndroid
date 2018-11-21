package com.starry.douban.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.starry.douban.BuildConfig;
import com.starry.douban.base.BaseApp;
import com.starry.douban.constant.Apis;
import com.starry.douban.constant.PreferencesName;
import com.starry.douban.event.AppUpdateEvent;
import com.starry.douban.model.AppUpdate;
import com.starry.douban.ui.activity.UpdateDialogActivity;
import com.starry.douban.util.SPUtil;
import com.starry.http.HttpManager;
import com.starry.http.callback.FileCallback;
import com.starry.http.callback.StringCallback;
import com.starry.http.error.ErrorModel;
import com.starry.rx.RxBus;

import java.io.File;

/**
 * work service
 *
 * @author Starry Jerry
 * @since 18-9-7.
 */
public class WorkService extends Service {

    /**
     * https://developer.android.google.cn/guide/components/intents-filters#Types
     * <p>为了确保应用的安全性，启动 Service 时，请始终使用显式 Intent，且不要为服务声明 Intent 过滤器。
     * <p>使用隐式 Intent 启动服务存在安全隐患，因为您无法确定哪些服务将响应 Intent，且用户无法看到哪些服务已启动。
     * <p>从 Android 5.0（API 级别 21）开始，如果使用隐式 Intent 调用 bindService()，系统会引发异常。
     * <p>为了避免无意中运行不同应用的 Service，请始终使用显式 Intent 启动您自己的服务，且不必为该服务声明 Intent 过滤器。
     */
    private static final String EXTRA_ACTION = "action";

    private static final String ACTION_CHECK_APP_VERSION = "com.starry.douban.CHECK_APP_VERSION";
    private static final String ACTION_DOWNLOAD_APP = "com.starry.douban.DOWNLOAD_APP";

    private static final String EXTRA_APP_DOWNLOAD_DIR = "appDownloadDir";
    private static final String EXTRA_APP_DOWNLOAD_NAME = "appDownloadName";


    private static final String TAG = "WorkService";
    private volatile Looper mServiceLooper;
    private volatile WorkHandler mWorkHandler;

    private class WorkHandler extends Handler {
        WorkHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            onHandleIntent((Intent) msg.obj);
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread(TAG);
        thread.start();

        mServiceLooper = thread.getLooper();
        mWorkHandler = new WorkHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message msg = mWorkHandler.obtainMessage();
        msg.obj = intent;
        mWorkHandler.sendMessage(msg);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mServiceLooper.quit();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void stopWorkService(Context context) {
        context.stopService(new Intent(context, WorkService.class));
    }

    public static void startCheckAppVersion() {
        Intent intent = new Intent(BaseApp.getContext(), WorkService.class);
        intent.putExtra(EXTRA_ACTION, ACTION_CHECK_APP_VERSION);
        BaseApp.getContext().startService(intent);
    }

    public static void startDownloadApp(String dirPath, String fileName) {
        Intent intent = new Intent(BaseApp.getContext(), WorkService.class);
        intent.putExtra(EXTRA_ACTION, ACTION_DOWNLOAD_APP);
        intent.putExtra(EXTRA_APP_DOWNLOAD_DIR, dirPath);
        intent.putExtra(EXTRA_APP_DOWNLOAD_NAME, fileName);
        BaseApp.getContext().startService(intent);
    }

    private void onHandleIntent(Intent intent) {
        String action = intent.getStringExtra(EXTRA_ACTION);
        if (TextUtils.isEmpty(action)) {
            return;
        }

        switch (action) {
            case ACTION_CHECK_APP_VERSION:
                getAppVersion();
                break;
            case ACTION_DOWNLOAD_APP:
                downloadApp(intent.getStringExtra(EXTRA_APP_DOWNLOAD_DIR),
                        intent.getStringExtra(EXTRA_APP_DOWNLOAD_NAME));
                break;
        }
    }

    private void getAppVersion() {
        HttpManager.get(Apis.APP_UPDATE)
                .build()
                .enqueue(new StringCallback<AppUpdate>() {
                    @Override
                    public void onSuccess(AppUpdate response, Object... obj) {
                        response.savePreferences();
                        if (response.getVersionCode() > BuildConfig.VERSION_CODE) {
                            UpdateDialogActivity.startPage(response.getMsg() + "\n" + response.getOther());
                        }
                    }

                    @Override
                    public void onFailure(ErrorModel errorModel) {
                    }
                });
    }

    private void downloadApp(String dirPath, String fileName) {
        String url = SPUtil.getString(PreferencesName.APP_UPDATE_APK_URL);
        if (TextUtils.isEmpty(url)) {
            return;
        }
        File file = new File(dirPath, fileName);
        if (file.exists()) { //已下载，直接安装
            BaseApp.installApp(file);
            return;
        }

        HttpManager.get(url)
                .tag(this)
                .build()
                .enqueue(new FileCallback(dirPath, fileName) {

                    @Override
                    public void onBefore() {
                        super.onBefore();
                        RxBus.getDefault().post(new AppUpdateEvent(1));
                    }

                    @Override
                    public void onSuccess(File response, Object... obj) {
                        BaseApp.installApp(response);
                    }

                    @Override
                    public void onFailure(ErrorModel errorModel) {
                    }

                    @Override
                    public void inProgress(float progress, long total) {
                        super.inProgress(progress, total);
                        RxBus.getDefault().post(new AppUpdateEvent(2, progress));
                    }
                });
    }

}
