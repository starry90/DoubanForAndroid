package com.starry.douban.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import com.starry.log.Logger;

/**
 * @author Starry Jerry
 * @since 19-7-23.
 */
public class CommonReceiver extends BroadcastReceiver {

    private static final String TAG = "CommonReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
            logAction(action, "开屏");
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
            logAction(action, "锁屏");
        } else if (Intent.ACTION_USER_PRESENT.equals(action)) { //解锁
            logAction(action, "解锁");
        } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) { //网络变化
            logAction(action, "网络变化");
        }
    }

    private void logAction(String action, String description) {
        Logger.e(TAG, "收到广播：" + description + ", action=" + action);
    }
}
