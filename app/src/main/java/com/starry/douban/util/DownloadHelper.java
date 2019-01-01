package com.starry.douban.util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import java.io.File;

/**
 * @author Starry Jerry
 * @since 18-12-21.
 */
public class DownloadHelper {

    private static final String TAG = "DownloadHelper";

    private boolean debug = false;

    private Context context;
    /**
     * 系统下载管理器
     */
    private DownloadManager mDownloadManager;
    /**
     * 下载ID
     */
    private long mDownloadId;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件下载地址
     */
    private String downloadUrl;

    private boolean downloading;

    /**
     * 通知栏点击事件，点击进入下载详情
     */
    private BroadcastReceiver mDownloadDetailsReceiver;

    private final Runnable mQueryProgressRunnable = new Runnable() {

        @Override
        public void run() {
            queryProgress();
            if (downloading) {
                mHandler.postDelayed(mQueryProgressRunnable, 1000);
            }
        }
    };

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private H5DownloadListener downloadListener;

    public interface H5DownloadListener {

        void onStart();

        void onProgress(long soFarSize, long totalSize);

        void onFinish(String fileFullPath, long totalSize);

        void onFailed();
    }

    public void setDownloadListener(H5DownloadListener listener) {
        this.downloadListener = listener;
    }

    private DownloadHelper(Context context, String fileName, String downloadUrl) {
        this.context = context;
        this.fileName = fileName;
        this.downloadUrl = downloadUrl;
    }

    public static DownloadHelper getInstance(Context context, String fileName, String downloadUrl) {
        DownloadHelper helper = new DownloadHelper(context, fileName, downloadUrl);
        helper.registerReceiver();
        return helper;
    }


    /**
     * 注册广播
     */
    private void registerReceiver() {
        mDownloadDetailsReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_NOTIFICATION_CLICKED.equals(action)) {
                    showDownloadList();
                }
            }
        };
        context.registerReceiver(mDownloadDetailsReceiver, new IntentFilter(DownloadManager.ACTION_NOTIFICATION_CLICKED));
    }

    /**
     * 显示下载列表
     */
    public void showDownloadList() {
        Intent downloadIntent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
        if (downloadIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(downloadIntent);
        }
    }

    /**
     * 开始下载
     */
    public void startDownload() {
        if (downloadListener != null) downloadListener.onStart();

        mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setTitle(fileName) // 通知标题信息
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        try {
            mDownloadId = mDownloadManager.enqueue(request); // 加入下载队列
            if (mDownloadId != 0) {
                startQueryProgress();
            }
        } catch (IllegalArgumentException e) {
            if (downloadListener != null) downloadListener.onFailed();
            // "更新失败", "请在设置中开启下载管理"
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.setData(Uri.parse("package:" + "com.android.providers.downloads"));
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
            }
        }
    }

    /**
     * 查询下载进度
     */
    private void queryProgress() {
        // 通过ID向下载管理查询下载情况，返回一个cursor
        Cursor c = mDownloadManager.query(new DownloadManager.Query().setFilterById(mDownloadId));
        if (c != null && c.moveToFirst()) {
            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            logDebug("下载状态：" + status);
            switch (status) {
                case DownloadManager.STATUS_PAUSED: //下载暂停， 由系统触发
                case DownloadManager.STATUS_PENDING: //下载延迟， 由系统触发
                    break;
                case DownloadManager.STATUS_RUNNING: //正在下载， 由系统触发
                    long soFarSize = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                    long totalSize = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    if (totalSize > 0) {
                        logDebug(String.format("total:%s soFar:%s ", totalSize, soFarSize) + soFarSize * 1.0f / totalSize);
                        if (downloadListener != null) {
                            downloadListener.onProgress(soFarSize, totalSize);
                        }
                    }
                    break;
                case DownloadManager.STATUS_SUCCESSFUL: //下载完成， 由系统触发
                    stopQueryProgress();
                    File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                    totalSize = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                    if (downloadListener != null) {
                        String fullName = downloadDir.getPath() + File.separator + fileName;
                        logDebug(fullName);
                        downloadListener.onFinish(fullName, totalSize);
                    }
                    break;
                case DownloadManager.STATUS_FAILED: //下载失败， 由系统触发
                    if (downloadListener != null) downloadListener.onFailed();
                    break;
            }
        } else {
            stopQueryProgress();
            if (downloadListener != null) downloadListener.onFailed();
        }
        closeCursor(c);
    }

    private void closeCursor(Cursor cursor) {
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
    }

    private void logDebug(String msg) {
        if (debug) {
            Log.e(TAG, msg);
        }
    }

    /**
     * 移除下载并删除下载文件
     */
    public void removeDownload() {
        mDownloadManager.remove(mDownloadId);
        stopQueryProgress();
    }

    /**
     * 开始查询下载进度
     */
    private void startQueryProgress() {
        downloading = true;
        mHandler.post(mQueryProgressRunnable);
    }

    /**
     * 停止查询下载进度
     */
    private void stopQueryProgress() {
        downloading = false;
        mHandler.removeCallbacks(mQueryProgressRunnable);
    }

    public boolean isDownloading() {
        return downloading;
    }

    public void onDestroy() {
        try {
            stopQueryProgress(); //停止查询下载进度
            context.unregisterReceiver(mDownloadDetailsReceiver);
        } catch (Exception ex) {
            //java.lang.IllegalArgumentException: Receiver not registered:
        }
    }
}
