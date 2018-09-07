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
import com.starry.douban.model.AppUpdate;
import com.starry.http.HttpManager;
import com.starry.http.callback.StringCallback;
import com.starry.http.error.ErrorModel;

/**
 * work service
 *
 * @author Starry Jerry
 * @since 18-9-7.
 */
public class WorkService extends Service {

    private static final String ACTION_CHECK_APP_VERSION = "com.starry.douban.CHECK_APP_VERSION";

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

    public static void checkAppVersion() {
        Intent intent = new Intent(BaseApp.getContext(), WorkService.class);
        intent.setAction(ACTION_CHECK_APP_VERSION);
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


}
