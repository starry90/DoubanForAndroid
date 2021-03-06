package com.starry.douban.env;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Starry Jerry
 * @since 2017/7/18.
 */
public class ActivityCallback implements Application.ActivityLifecycleCallbacks {

    /***
     * 寄存整个应用Activity
     **/
    private final List<Activity> activityList = new LinkedList<>();

    /**
     * finish所有的Activity（用于整个应用退出）
     */
    public void finishAll() {
        synchronized (activityList) {
            for (Activity activity : activityList) {
                activity.finish();
            }
            activityList.clear();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activityList.add(activity);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        activityList.remove(activity);
    }
}
