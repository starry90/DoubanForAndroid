package com.starry.douban.util;

import android.content.DialogInterface;

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
}
