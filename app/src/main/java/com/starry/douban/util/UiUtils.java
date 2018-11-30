package com.starry.douban.util;

import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;

import com.starry.douban.base.BaseApp;

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

    public static int getColor(int colorResId) {
        return ContextCompat.getColor(BaseApp.getContext(), colorResId);
    }
}
