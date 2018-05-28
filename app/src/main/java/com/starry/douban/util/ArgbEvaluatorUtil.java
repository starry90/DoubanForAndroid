package com.starry.douban.util;

import android.animation.ArgbEvaluator;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.widget.RadioButton;

import com.starry.douban.R;
import com.starry.douban.base.BaseApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Starry Jerry
 * @since 18-5-28.
 */

public class ArgbEvaluatorUtil {

    private static ArgbEvaluatorUtil instance = new ArgbEvaluatorUtil();

    private ArgbEvaluatorUtil() {
    }

    public static ArgbEvaluatorUtil get() {
        return instance;
    }

    /**
     * 存放tab对象的集合
     */
    private List<RadioButton> tabViews = new ArrayList<>();
    /**
     * 存放tab对应的图片资源集合
     */
    private List<Drawable> tabDrawables = new ArrayList<>();
    /**
     * 渐变效果
     */
    private ArgbEvaluator mArgbEvaluator = new ArgbEvaluator();
    private int colorSelect = ContextCompat.getColor(BaseApp.getContext(), R.color.colorPrimary);
    private int colorUnSelect = ContextCompat.getColor(BaseApp.getContext(), R.color.darker_gray);

    /**
     * 添加tab
     *
     * @param rbs RadioButton list
     */
    public void addTab(RadioButton... rbs) {
        if (rbs == null || rbs.length == 0) return;

        tabViews.clear();
        tabViews.addAll(Arrays.asList(rbs));
    }

    /**
     * 添加tab Drawable
     *
     * @param drawableIds drawable id list
     */
    public void addTabDrawable(int... drawableIds) {
        if (drawableIds == null || drawableIds.length == 0) return;

        tabDrawables.clear();
        for (int drawableId : drawableIds) {
            tabDrawables.add(ContextCompat.getDrawable(BaseApp.getContext(), drawableId).mutate());
        }
    }

    /**
     * 设置选中tab
     *
     * @param position tab 索引
     */
    public void setChecked(int position) {
        tabViews.get(position).setChecked(true);
    }

    /**
     * 改变tab图片
     *
     * @param position       Position index of the first page currently being displayed.
     *                       Page position+1 will be visible if positionOffset is nonzero.
     * @param positionOffset Value from [0, 1) indicating the offset from the page at position.
     */
    public void changeTabDrawable(int position, float positionOffset) {
        RadioButton fromTab;
        RadioButton toTab;

        Drawable drawableFrom;
        Drawable drawableTo;

        fromTab = tabViews.get(position);
        drawableFrom = tabDrawables.get(position);

        if (position != tabDrawables.size() - 1) {
            toTab = tabViews.get(position + 1);
            drawableTo = tabDrawables.get(position + 1);
        } else {
            toTab = null;
            drawableTo = null;
        }

        if (fromTab != null) {
            updateTabView(positionOffset, colorSelect, colorUnSelect, drawableFrom, fromTab);
        }
        if (toTab != null) {
            updateTabView(positionOffset, colorUnSelect, colorSelect, drawableTo, toTab);
        }
    }

    private void updateTabView(float positionOffset, int startColor, int endColor, Drawable drawable, RadioButton tab) {
        int colorStart = (int) mArgbEvaluator.evaluate(positionOffset, startColor, endColor);
        Drawable drawableColorStart = tintDrawable(drawable, ColorStateList.valueOf(colorStart));
        tab.setTextColor(colorStart);
        drawableColorStart.setBounds(0, 0, drawableColorStart.getIntrinsicWidth(), drawableColorStart.getIntrinsicHeight());
        tab.setCompoundDrawables(null, drawableColorStart, null, null);
    }

    /**
     * 修改切换tab的显示
     *
     * @param selectIndex 选中的Tab索引
     */
    public void setTabSelect(int selectIndex) {
        for (int index = 0; index < tabViews.size(); index++) {
            RadioButton imageView = tabViews.get(index);
            Drawable drawable = tabDrawables.get(index);
            int resultColor;
            if (index == selectIndex) {
                resultColor = colorSelect;
            } else {
                resultColor = colorUnSelect;
            }
            Drawable resultDrawable = tintDrawable(drawable, ColorStateList.valueOf(resultColor));
            resultDrawable.setBounds(0, 0, resultDrawable.getIntrinsicWidth(), resultDrawable.getIntrinsicHeight());
            imageView.setTextColor(resultColor);
            imageView.setCompoundDrawables(null, resultDrawable, null, null);
        }
    }

    private Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }


}
