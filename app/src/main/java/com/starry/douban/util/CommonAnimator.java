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

    private Builder builder;

    public Builder getBuilder() {
        return builder;
    }

    private CommonAnimator(Builder builder) {
        this.builder = builder;
    }

    public interface Listener {
        void onAnimationEnd();
    }

    /**
     * 默认收起效果: 从View的实际高度变成0，同时渐变效果从100%变成0%
     */
    public void foldWithDefault() {
        foldWithAnimatorSet(true);
    }

    /**
     * 其它收起效果，设置了相关参数就有，未设置就没有
     */
    public void foldWithAnimatorSet() {
        foldWithAnimatorSet(false);
    }

    private void foldWithAnimatorSet(final boolean defaultAnimator) {
        AnimatorSet animatorSet = new AnimatorSet();

        int start = builder.startValue;
        int end = builder.endValue;
        final View target = builder.target;
        if (defaultAnimator && start == 0 && end == 0) { //默认动画从View的实际高度变成0
            start = target.getHeight();
            end = 0;
        }
        if (start != end) {
            ValueAnimator animator = createIntAnimator(target, start, end);
            animatorSet.playTogether(animator);
        }

        float[] alphaValues = builder.alphaValues;
        if (defaultAnimator && alphaValues == null) { //默认渐变效果从100%变成0%
            alphaValues = new float[]{1f, 0f};
        }
        if (alphaValues != null) {
            ObjectAnimator alphaAnimator = createAlphaAnimator(target, alphaValues);
            animatorSet.playTogether(alphaAnimator);
        }

        setDuration(animatorSet, builder.duration);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (defaultAnimator) { //默认动画结束后隐藏View
                    target.setVisibility(View.GONE);
                }
                if (builder.listener != null)
                    builder.listener.onAnimationEnd();
            }
        });
        animatorSet.start();
    }

    /**
     * 默认展开效果: 从0变成View的实际高度，同时渐变效果从0%变成100%
     */
    public void unfoldWithDefault() {
        unfoldWithAnimatorSet(true);
    }

    /**
     * 其它展开效果，设置了相关参数就有，未设置就没有
     */
    public void unfoldWithAnimatorSet() {
        unfoldWithAnimatorSet(false);
    }

    private void unfoldWithAnimatorSet(boolean defaultAnimator) {
        AnimatorSet animatorSet = new AnimatorSet();

        int start = builder.startValue;
        int end = builder.endValue;
        View target = builder.target;
        if (defaultAnimator && start == 0 && end == 0) { //默认动画从0变成View的实际高度
            //测量扩展动画的起始高度和结束高度
            start = target.getMeasuredHeight();
            end = getMeasureUnSpecHeight(target);
        }
        if (start != end) {
            Animator animator = createIntAnimator(target, start, end);
            animatorSet.playTogether(animator);
        }

        float[] alphaValues = builder.alphaValues;
        if (defaultAnimator && alphaValues == null) { //默认渐变效果从0%变成100%
            alphaValues = new float[]{0f, 1f};
        }
        if (alphaValues != null) {
            ObjectAnimator alphaAnimator = createAlphaAnimator(target, alphaValues);
            animatorSet.playTogether(alphaAnimator);
        }

        setDuration(animatorSet, builder.duration);
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (builder.listener != null)
                    builder.listener.onAnimationEnd();
            }
        });
        target.setVisibility(View.VISIBLE);
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

    private void setDuration(AnimatorSet animatorSet, long duration) {
        if (duration == 0) {
            duration = 300;
        }
        animatorSet.setDuration(duration);
    }

    private ObjectAnimator createAlphaAnimator(final View v, float... values) {
        ObjectAnimator alphaAnimator = ObjectAnimator.ofFloat(v, View.ALPHA, values);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

            }
        });
        return alphaAnimator;
    }

    private ValueAnimator createIntAnimator(final View v, int start, int end) {
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

        public Builder(View target) {
            this.target = target;
        }

        public View target() {
            return target;
        }

        public Builder target(View target) {
            this.target = target;
            return this;
        }

        public int duration() {
            return duration;
        }

        public Builder duration(int duration) {
            this.duration = duration;
            return this;
        }

        public int startValue() {
            return startValue;
        }

        public Builder startValue(int startValue) {
            this.startValue = startValue;
            return this;
        }

        public int endValue() {
            return endValue;
        }

        public Builder endValue(int endValue) {
            this.endValue = endValue;
            return this;
        }

        public float[] alphaValues() {
            return alphaValues;
        }

        public Builder alphaValues(float... alphaValues) {
            this.alphaValues = alphaValues;
            return this;
        }

        public Listener listener() {
            return listener;
        }

        public Builder listener(Listener listener) {
            this.listener = listener;
            return this;
        }

        public CommonAnimator build() {
            return new CommonAnimator(this);
        }
    }

}
