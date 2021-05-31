package com.starry.douban.env;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Starry Jerry
 * @since 2017/7/18.
 */
public class ActivityCallback implements Application.ActivityLifecycleCallbacks {

    /***
     * 寄存整个应用Activity
     **/
    private final Set<Activity> activitySet = new HashSet<>();

    /**
     * finish所有的Activity（用于整个应用退出）
     */
    public void finishAll() {
        synchronized (activitySet) {
            for (Activity activity : activitySet) {
                activity.finish();
            }
            activitySet.clear();
        }
    }

    /**
     * 关闭除了某一类型class之外的所有的Activity
     *
     * @param cls 例外的不关闭的Activity类
     */
    public void finishAllExcept(Class<Activity> cls) {
        synchronized (activitySet) {
            for (Activity activity : activitySet) {
                if (!cls.isInstance(activity)) {
                    activity.finish();
                }
            }
            activitySet.clear();
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activitySet.add(activity);
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
        activitySet.remove(activity);
    }
}
