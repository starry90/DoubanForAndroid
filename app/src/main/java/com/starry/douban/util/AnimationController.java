package com.starry.douban.util;

import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.AnticipateOvershootInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * 动画控制器
 * <p/>
 * Create by Starry Jerry on 2016-8-5 22:06:01
 */
public class AnimationController {
    /**
     * 相对于控件本身
     */
    public static int rela1 = Animation.RELATIVE_TO_SELF;
    /**
     * 相对于父控件
     */
    public static int rela2 = Animation.RELATIVE_TO_PARENT;

    public static int Default = -1;

    // public final int Linear = 0;
    // public final int Accelerate = 1;
    // public final int Decelerate = 2;
    // public final int AccelerateDecelerate = 3;
    // public final int Bounce = 4;
    // public final int Overshoot = 5;
    // public final int Anticipate = 6;
    // public final int AnticipateOvershoot = 7;

    // LinearInterpolator,AccelerateInterpolator,DecelerateInterpolator,AccelerateDecelerateInterpolator,
    // BounceInterpolator,OvershootInterpolator,AnticipateInterpolator,AnticipateOvershootInterpolator

    private AnimationController() {
    }

    public static class MyAnimationListener implements AnimationListener {
        private View view;

        public MyAnimationListener(View view) {
            this.view = view;
        }

        @Override
        public void onAnimationStart(Animation animation) {
            // this.view.setVisibility(View.VISIBLE);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            this.view.setVisibility(View.GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

    }

    private static void setEffect(Animation animation, int interpolatorType, long durationMillis, long delayMillis) {
        switch (interpolatorType) {
            case 0:
                animation.setInterpolator(new LinearInterpolator());
                break;
            case 1:
                animation.setInterpolator(new AccelerateInterpolator());
                break;
            case 2:
                animation.setInterpolator(new DecelerateInterpolator());
                break;
            case 3:
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                break;
            case 4:
                animation.setInterpolator(new BounceInterpolator());
                break;
            case 5:
                animation.setInterpolator(new OvershootInterpolator());
                break;
            case 6:
                animation.setInterpolator(new AnticipateInterpolator());
                break;
            case 7:
                animation.setInterpolator(new AnticipateOvershootInterpolator());
                break;
            default:
                break;
        }
        animation.setDuration(durationMillis);
        animation.setStartOffset(delayMillis);
    }

    private static void baseIn(View view, Animation animation, long durationMillis, long delayMillis) {
        setEffect(animation, Default, durationMillis, delayMillis);
        view.setVisibility(View.VISIBLE);
        view.startAnimation(animation);
    }

    private static void baseOut(View view, Animation animation, long durationMillis, long delayMillis) {
        setEffect(animation, Default, durationMillis, delayMillis);
        animation.setAnimationListener(new MyAnimationListener(view));
        view.startAnimation(animation);
    }

    /**
     * 淡入
     *
     * @param view           要执行动画的视图
     * @param durationMillis 执行动画的时长 单位：毫秒
     * @param delayMillis    延迟多久执行动画 单位毫秒
     */
    public static void fadeIn(View view, long durationMillis, long delayMillis) {
        AlphaAnimation animation = new AlphaAnimation(0, 1);
        baseIn(view, animation, durationMillis, delayMillis);
    }

    /**
     * 淡出
     *
     * @param view           要执行动画的视图
     * @param durationMillis 执行动画的时长 单位：毫秒
     * @param delayMillis    延迟多久执行动画 单位毫秒
     */
    public static void fadeOut(View view, long durationMillis, long delayMillis) {
        AlphaAnimation animation = new AlphaAnimation(1, 0);
        baseOut(view, animation, durationMillis, delayMillis);
    }

    /**
     * 从左滑入相对于父控件
     *
     * @param view           要执行动画的视图
     * @param durationMillis 执行动画的时长 单位：毫秒
     * @param delayMillis    延迟多久执行动画 单位毫秒
     */
    public static void slideLeftIn(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation = new TranslateAnimation(rela2, -1, rela2, 0, rela2, 0, rela2, 0);
        baseIn(view, animation, durationMillis, delayMillis);
    }

    /**
     * 从右滑入相对于父控件
     *
     * @param view           要执行动画的视图
     * @param durationMillis 执行动画的时长 单位：毫秒
     * @param delayMillis    延迟多久执行动画 单位毫秒
     */
    public static void slideRightIn(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation = new TranslateAnimation(rela2, 1, rela2, 0, rela2, 0, rela2, 0);
        baseIn(view, animation, durationMillis, delayMillis);
    }

    /**
     * 从右滑入相对于控件本身
     *
     * @param view           要执行动画的视图
     * @param durationMillis 执行动画的时长 单位：毫秒
     * @param delayMillis    延迟多久执行动画 单位毫秒
     */
    public static void slideRightInToSelf(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation = new TranslateAnimation(rela1, 1, rela1, 0, rela1, 0, rela1, 0);
        baseIn(view, animation, durationMillis, delayMillis);
    }

    /**
     * 从上滑入相对于父控件
     *
     * @param view           要执行动画的视图
     * @param durationMillis 执行动画的时长 单位：毫秒
     * @param delayMillis    延迟多久执行动画 单位毫秒
     */
    public static void slideTopIn(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation = new TranslateAnimation(rela2, 0, rela2, 0, rela2, -1, rela2, 0);
        baseIn(view, animation, durationMillis, delayMillis);
    }

    /**
     * 从下滑入相对于父控件
     *
     * @param view           要执行动画的视图
     * @param durationMillis 执行动画的时长 单位：毫秒
     * @param delayMillis    延迟多久执行动画 单位毫秒
     */
    public static void slideBottomIn(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation = new TranslateAnimation(rela2, 0, rela2, 0, rela2, 1, rela2, 0);
        baseIn(view, animation, durationMillis, delayMillis);
    }

    /**
     * 从左滑出相对于父控件
     *
     * @param view           要执行动画的视图
     * @param durationMillis 执行动画的时长 单位：毫秒
     * @param delayMillis    延迟多久执行动画 单位毫秒
     */
    public static void slideLeftOut(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation = new TranslateAnimation(rela2, 0, rela2, -1, rela2, 0, rela2, 0);
        baseOut(view, animation, durationMillis, delayMillis);
    }

    /**
     * 从右滑出相对于父控件
     *
     * @param view           要执行动画的视图
     * @param durationMillis 执行动画的时长 单位：毫秒
     * @param delayMillis    延迟多久执行动画 单位毫秒
     */
    public static void slideRightOut(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation = new TranslateAnimation(rela2, 0, rela2, 1, rela2, 0, rela2, 0);
        baseOut(view, animation, durationMillis, delayMillis);
    }

    /**
     * 从右滑出相对于控件本身
     *
     * @param view           要执行动画的视图
     * @param durationMillis 执行动画的时长 单位：毫秒
     * @param delayMillis    延迟多久执行动画 单位毫秒
     */
    public static void slideRightOutToSelf(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation = new TranslateAnimation(rela1, 0, rela1, 1, rela1, 0, rela1, 0);
        baseOut(view, animation, durationMillis, delayMillis);
    }

    /**
     * 从上滑出相对于父控件
     *
     * @param view           要执行动画的视图
     * @param durationMillis 执行动画的时长 单位：毫秒
     * @param delayMillis    延迟多久执行动画 单位毫秒
     */
    public static void slideTopOut(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation = new TranslateAnimation(rela2, 0, rela2, 0, rela2, 0, rela2, -1);
        baseOut(view, animation, durationMillis, delayMillis);
    }

    /**
     * 从下滑出相对于父控件
     *
     * @param view           要执行动画的视图
     * @param durationMillis 执行动画的时长 单位：毫秒
     * @param delayMillis    延迟多久执行动画 单位毫秒
     */
    public static void slideBottomOut(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation = new TranslateAnimation(rela2, 0, rela2, 0, rela2, 0, rela2, 1);
        baseOut(view, animation, durationMillis, delayMillis);
    }

    /**
     * 放大进入相对于父控件
     *
     * @param view           要执行动画的视图
     * @param durationMillis 执行动画的时长 单位：毫秒
     * @param delayMillis    延迟多久执行动画 单位毫秒
     */
    public static void scaleIn(View view, long durationMillis, long delayMillis) {
        ScaleAnimation animation = new ScaleAnimation(0, 1, 0, 1, rela2, 0.5f, rela2, 0.5f);
        baseIn(view, animation, durationMillis, delayMillis);
    }

    /**
     * 缩小退出相对于父控件
     *
     * @param view           要执行动画的视图
     * @param durationMillis 执行动画的时长 单位：毫秒
     * @param delayMillis    延迟多久执行动画 单位毫秒
     */
    public static void scaleOut(View view, long durationMillis, long delayMillis) {
        ScaleAnimation animation = new ScaleAnimation(1, 0, 1, 0, rela2, 0.5f, rela2, 0.5f);
        baseOut(view, animation, durationMillis, delayMillis);
    }

    /**
     * 旋转进入
     *
     * @param view           要执行动画的视图
     * @param durationMillis 执行动画的时长 单位：毫秒
     * @param delayMillis    延迟多久执行动画 单位毫秒
     */
    public static void rotateIn(View view, long durationMillis, long delayMillis) {
        RotateAnimation animation = new RotateAnimation(-90, 0, rela1, 0, rela1, 1);
        baseIn(view, animation, durationMillis, delayMillis);
    }

    /**
     * 旋转退出
     *
     * @param view           要执行动画的视图
     * @param durationMillis 执行动画的时长 单位：毫秒
     * @param delayMillis    延迟多久执行动画 单位毫秒
     */
    public static void rotateOut(View view, long durationMillis, long delayMillis) {
        RotateAnimation animation = new RotateAnimation(0, 90, rela1, 0, rela1, 1);
        baseOut(view, animation, durationMillis, delayMillis);
    }

    /**
     * 逐渐放大+旋转进入
     *
     * @param view           要执行动画的视图
     * @param durationMillis 执行动画的时长 单位：毫秒
     * @param delayMillis    延迟多久执行动画 单位毫秒
     */
    public static void scaleRotateIn(View view, long durationMillis, long delayMillis) {
        ScaleAnimation animation1 = new ScaleAnimation(0, 1, 0, 1, rela1, 0.5f, rela1, 0.5f);
        RotateAnimation animation2 = new RotateAnimation(0, 360, rela1, 0.5f, rela1, 0.5f);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(animation1);
        animation.addAnimation(animation2);
        baseIn(view, animation, durationMillis, delayMillis);
    }

    /**
     * 逐渐缩小+旋转退 出
     *
     * @param view           要执行动画的视图
     * @param durationMillis 执行动画的时长 单位：毫秒
     * @param delayMillis    延迟多久执行动画 单位毫秒
     */
    public static void scaleRotateOut(View view, long durationMillis, long delayMillis) {
        ScaleAnimation animation1 = new ScaleAnimation(1, 0, 1, 0, rela1, 0.5f, rela1, 0.5f);
        RotateAnimation animation2 = new RotateAnimation(0, 360, rela1, 0.5f, rela1, 0.5f);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(animation1);
        animation.addAnimation(animation2);
        baseOut(view, animation, durationMillis, delayMillis);
    }

    /**
     * 滑动+淡入
     *
     * @param view           要执行动画的视图
     * @param durationMillis 执行动画的时长 单位：毫秒
     * @param delayMillis    延迟多久执行动画 单位毫秒
     */
    public static void slideFadeIn(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation1 = new TranslateAnimation(rela2, 1, rela2, 0, rela2, 0, rela2, 0);
        AlphaAnimation animation2 = new AlphaAnimation(0, 1);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(animation1);
        animation.addAnimation(animation2);
        baseIn(view, animation, durationMillis, delayMillis);
    }

    /**
     * 滑动+淡出
     *
     * @param view           要执行动画的视图
     * @param durationMillis 执行动画的时长 单位：毫秒
     * @param delayMillis    延迟多久执行动画 单位毫秒
     */
    public static void slideFadeOut(View view, long durationMillis, long delayMillis) {
        TranslateAnimation animation1 = new TranslateAnimation(rela2, 0, rela2, -1, rela2, 0, rela2, 0);
        AlphaAnimation animation2 = new AlphaAnimation(1, 0);
        AnimationSet animation = new AnimationSet(false);
        animation.addAnimation(animation1);
        animation.addAnimation(animation2);
        baseOut(view, animation, durationMillis, delayMillis);
    }
}
