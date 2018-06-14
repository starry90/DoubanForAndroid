package com.starry.douban.util;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * Activity Animation
 *
 * @author Starry Jerry
 * @since 2018/6/14.
 */

public class ActivityAnimationUtils {

    public static void transition(Activity activity, Intent intent, View item) {
        Pair squareParticipant = new Pair<>(item, ViewCompat.getTransitionName(item));
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, squareParticipant);
        activity.startActivity(intent, transitionActivityOptions.toBundle());
    }

    public static void transitionForResult(Activity activity, Intent intent, int requestCode, View item) {
        Pair squareParticipant = new Pair<>(item, ViewCompat.getTransitionName(item));
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, squareParticipant);
        activity.startActivityForResult(intent, requestCode, transitionActivityOptions.toBundle());
    }
}
