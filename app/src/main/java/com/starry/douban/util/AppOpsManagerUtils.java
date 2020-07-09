package com.starry.douban.util;

import android.Manifest;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.support.v4.content.ContextCompat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author Starry Jerry
 * @since 20-7-9.
 */

public class AppOpsManagerUtils {

    public static boolean checkCallPhonePermission(Context context) {
        //6.0及以上检查方法
        if (Build.VERSION.SDK_INT >= 23) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED;
        } else {
            //6.0以下检查方法
            try {
                AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                Class<?> clazz = AppOpsManager.class;
                Field sysAlert = clazz.getField("OP_CALL_PHONE");
                int op = sysAlert.getInt(manager);
                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                int mode = (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
                return AppOpsManager.MODE_ALLOWED == mode;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }
}
