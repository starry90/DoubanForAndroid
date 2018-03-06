package com.starry.douban;

import android.app.Application;
import android.content.Context;

import com.starry.douban.base.BaseApp;

/**
 * @author Starry Jerry
 */
public class DBApplication extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        BaseApp.getInstance().attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        BaseApp.getInstance().onCreate(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        BaseApp.getInstance().onTerminate(this);
    }
}
