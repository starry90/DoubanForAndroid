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

import com.starry.douban.base.BaseApp;
import com.starry.douban.constant.Apis;
import com.starry.douban.constant.PreferencesName;
import com.starry.douban.event.AppUpdateEvent;
import com.starry.douban.model.AppUpdate;
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
        intent.setAction(ACTION_CHECK_APP_VERSION);
        BaseApp.getContext().startService(intent);
    }

    public static void startDownloadApp(String dirPath, String fileName) {
        Intent intent = new Intent(BaseApp.getContext(), WorkService.class);
        intent.setAction(ACTION_DOWNLOAD_APP);
        intent.putExtra(EXTRA_APP_DOWNLOAD_DIR, dirPath);
        intent.putExtra(EXTRA_APP_DOWNLOAD_NAME, fileName);
        BaseApp.getContext().startService(intent);
    }

    private void onHandleIntent(Intent intent) {
        String action = intent.getAction();
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
