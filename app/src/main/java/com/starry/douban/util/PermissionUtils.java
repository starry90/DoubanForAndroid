package com.starry.douban.util;

import android.Manifest;
import android.annotation.SuppressLint;
import android.support.v4.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.functions.Consumer;


/**
 * @author Starry Jerry
 * @since 18-5-10.
 */

public class PermissionUtils {

    public static abstract class PermissionCallback implements Consumer<Boolean> {

        @Override
        public void accept(Boolean success) {
            if (success) {
                success();
            } else {
                failure();
            }
        }

        public abstract void success();

        public abstract void failure();

    }

    @SuppressLint("CheckResult")
    public static void requestStorage(FragmentActivity activity, final PermissionCallback callback) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        if (rxPermissions.isGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            callback.success();
            return;
        }

        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(callback);
    }

    @SuppressLint("CheckResult")
    public static void requestCamera(FragmentActivity activity, final PermissionCallback callback) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        if (rxPermissions.isGranted(Manifest.permission.CAMERA)) {
            callback.success();
            return;
        }

        rxPermissions.request(Manifest.permission.CAMERA)
                .subscribe(callback);
    }
}
