package com.starry.douban;

import android.app.Application;
import android.content.Context;

import com.starry.douban.env.AppWrapper;

/**
 * @author Starry Jerry
 */
public class DBApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        AppWrapper.getInstance().attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppWrapper.getInstance().onCreate(this);
    }

    /**
     * This method is for use in emulated process environments.  It will
     * never be called on a production Android device, where processes are
     * removed by simply killing them; no user code (including this callback)
     * is executed when doing so.
     * <p>
     * 该方法不会被回调
     * </p>
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
    }
}
