package com.starry.douban.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.ViewGroup;

/**
 * 属性动画
 *
 * @author Starry Jerry
 * @since 18-7-4.
 */
public class CommonAnimator {

    public interface Listener {
        void onAnimationEnd();
    }

    private static AnimatorSet baseAnimatorSet(Builder builder) {
        AnimatorSet animatorSet = new AnimatorSet();
        int start = builder.startValue;
        int end = builder.endValue;
        float[] alphaValues = builder.alphaValues;
        final View target = builder.target;

        if (start != end) {
            ValueAnimator animator = createIntAnimator(target, start, end);
            animatorSet.playTogether(animator);
        }

        if (alphaValues != null) {
            ObjectAnimator alphaAnimator = createAlphaAnimator(target, alphaValues);
            animatorSet.playTogether(alphaAnimator);
        }

        setDuration(animatorSet, builder.duration);
        return animatorSet;
    }

    private static void hideWithAnimatorSet(final Builder builder) {
        AnimatorSet animatorSet = baseAnimatorSet(builder);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (builder.defaultAnimator) { //默认动画结束后隐藏View
                    builder.target.setVisibility(View.GONE);
                }
                if (builder.listener != null)
                    builder.listener.onAnimationEnd();
            }
        });
        animatorSet.start();
    }

    private static void showWithAnimatorSet(final Builder builder) {
        AnimatorSet animatorSet = baseAnimatorSet(builder);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (builder.listener != null)
                    builder.listener.onAnimationEnd();
            }
        });
        builder.target.setVisibility(View.VISIBLE);
        animatorSet.start();
    }

    /**
     * 获取View在没有任何限制下的测量高度
     *
     * @param target View
     * @return View的高度
     */
    public static int getMeasureUnSpecHeight(View target) {
        View parent = (View) target.getParent();
        target.measure(View.MeasureSpec.makeMeasureSpec(parent.getMeasuredWidth(), View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        return target.getMeasuredHeight();
    }

    private static void setDuration(AnimatorSet animatorSet, long duration) {
        if (duration == 0) {
            duration = 300;
        }
        animatorSet.setDuration(duration);
    }

    private static ObjectAnimator createAlphaAnimator(final View v, float... values) {
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(v, View.ALPHA, values);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

            }
        });
        return alphaAnimator;
    }

    private static ValueAnimator createIntAnimator(final View v, int start, int end) {
        ValueAnimator animator = ValueAnimator.ofInt(start, end);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator arg0) {
                int value = (int) arg0.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = v.getLayoutParams();
                layoutParams.height = value;
                v.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    public static class Builder {

        private View target;

        private int duration;

        private int startValue;

        private int endValue;

        private float[] alphaValues;

        private Listener listener;

        private boolean defaultAnimator;

        public Builder(View target) {
            this.target = target;
        }

        public Builder target(View target) {
            this.target = target;
            return this;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public Builder startValue(int startValue) {
            this.startValue = startValue;
            return this;
        }

        public Builder endValue(int endValue) {
            this.endValue = endValue;
            return this;
        }

        public Builder alphaValues(float... alphaValues) {
            this.alphaValues = alphaValues;
            return this;
        }

        public Builder listener(Listener listener) {
            this.listener = listener;
            return this;
        }

        /**
         * 默认隐藏动画: 从View的实际高度变成0
         */
        public Builder defaultHideAnimator() {
            defaultAnimator = true;
            ViewGroup.LayoutParams layoutParams = target.getLayoutParams();
            this.startValue = layoutParams.height;
            this.endValue = 0;
            return this;
        }

        /**
         * 默认显示动画: 从0变成View的实际高度
         */
        public Builder defaultShowAnimator() {
            defaultAnimator = true;
            //测量扩展动画的起始高度和结束高度
            this.startValue = target.getMeasuredHeight();
            this.endValue = getMeasureUnSpecHeight(target);
            return this;
        }

        public void hide() {
            hideWithAnimatorSet(this);
        }

        public void show() {
            showWithAnimatorSet(this);
        }
    }

}
