package com.starry.douban.util;

import android.widget.Toast;

import com.starry.douban.base.BaseApplication;

/**
 * 吐司
 *
 * @author liuzx
 * @since 2016-6-17 10:05:46
 */
public class Toaster {

    private static Toast toast;

    private static String networkError = "连接失败，请检查你的网络设置";

    /**
     * 链接错误，超时...
     */
    public static void showToastError() {
        if (toast == null) {
            toast = Toast.makeText(BaseApplication.getInstance(), networkError, Toast.LENGTH_SHORT);
        } else {
            toast.setText(networkError);
        }
        toast.show();
    }

    /**
     * 自定义吐司内容
     *
     * @param message 吐司内容
     */
    public static void showToast(String message) {
        if (toast == null) {
            toast = Toast.makeText(BaseApplication.getInstance(), message, Toast.LENGTH_SHORT);
        } else {
            toast.setText(message);
        }
        toast.show();
    }

    /**
     * 自定义吐司内容
     *
     * @param resId The resource id of the string resource to use. Can be
     *              formatted text.
     */
    public static void showToast(int resId) {
        if (toast == null) {
            toast = Toast.makeText(BaseApplication.getInstance(), resId, Toast.LENGTH_SHORT);
        } else {
            toast.setText(resId);
        }
        toast.show();
    }
}
