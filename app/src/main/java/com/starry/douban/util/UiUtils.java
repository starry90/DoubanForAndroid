package com.starry.douban.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import com.starry.douban.env.AppWrapper;

/**
 * @author Starry Jerry
 * @since 2018/10/27.
 */

public class UiUtils {

    public static void dismissDialog(DialogInterface dialog) {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * 设置对话框显示位置 <br>
     * {@linkplain android.view.Gravity#BOTTOM} 显示在底部 <br>
     * {@linkplain android.view.Gravity#TOP} 显示在顶部 <br>
     *
     * @param dialog  对话框
     * @param gravity 对话框显示位置
     */
    public static void setGravity(Dialog dialog, int gravity) {
        Window window = dialog.getWindow();
        if (window != null) {
            window.setGravity(gravity);
        }
    }

    /**
     * 设置对话框宽度占满全屏<br>
     * https://blog.csdn.net/sydMobile/article/details/83588708
     *
     * @param dialog 对话框
     */
    public static void setMatchWidth(Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            // 把 DecorView 的默认 padding 取消，同时 DecorView 的默认大小也会取消
            window.getDecorView().setPadding(0, 0, 0, 0);
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            window.setAttributes(lp);
        }
    }

    public static int getColor(int colorResId) {
        return ContextCompat.getColor(AppWrapper.getContext(), colorResId);
    }


    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int height = 0;
        // 获得状态栏高度
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            height = context.getResources().getDimensionPixelSize(resourceId);

        }
        return height;
    }
}
