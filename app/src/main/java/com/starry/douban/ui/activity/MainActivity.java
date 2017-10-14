package com.starry.douban.ui.activity;

import android.animation.ArgbEvaluator;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.ViewPager;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.starry.douban.R;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.ui.fragment.BlankFragment;
import com.starry.douban.ui.fragment.HomeFragment;
import com.starry.douban.ui.fragment.MovieFragment;
import com.starry.douban.ui.fragment.MovieParentFragment;
import com.starry.douban.util.viewpager.v4.FragmentPagerItem;
import com.starry.douban.util.viewpager.v4.FragmentPagerItemAdapter;
import com.starry.douban.util.viewpager.v4.FragmentPagerItems;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * Main
 * Created by Starry Jerry on 2016/12/1.
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.viewpager_main)
    ViewPager viewpagerMain;
    @BindView(R.id.rbtn_main_home)
    RadioButton rbtnMainHome;
    @BindView(R.id.rbtn_main_book)
    RadioButton rbtnMainBook;
    @BindView(R.id.rbtn_main_setting)
    RadioButton rbtnMainSetting;
    @BindView(R.id.radioGroup_main)
    RadioGroup radioGroupMain;

    private String[] titles = new String[]{"首页", "电影", "设置"};

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
    private int colorSelect;
    private int colorUnSelect;

    @Override
    public int getLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        setTitle(titles[0]);

        FragmentPagerItems pages = new FragmentPagerItems(this);
        pages.add(FragmentPagerItem.of(HomeFragment.class.getSimpleName(), HomeFragment.class));
        pages.add(FragmentPagerItem.of(MovieFragment.class.getSimpleName(), MovieParentFragment.class));
        pages.add(FragmentPagerItem.of(BlankFragment.class.getSimpleName(), BlankFragment.class));

        viewpagerMain.setOffscreenPageLimit(3);
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);
        viewpagerMain.setAdapter(adapter);

        initTab();
    }

    @Override
    public void setListener() {
        radioGroupMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbtn_main_home:
                        setTitle(titles[0]);
                        viewpagerMain.setCurrentItem(0, false);
                        break;
                    case R.id.rbtn_main_book:
                        setTitle(titles[1]);
                        viewpagerMain.setCurrentItem(1, false);
                        break;
                    case R.id.rbtn_main_setting:
                        setTitle(titles[2]);
                        viewpagerMain.setCurrentItem(2, false);
                        break;

                }
            }
        });

        viewpagerMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            private final int DELAY_TIME = 100;
            private Handler handler = new Handler();

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                changeTabDrawable(position, positionOffset);
            }

            @Override
            public void onPageSelected(final int position) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setTitle(titles[position]);
                        setTabSelect(position);
                    }
                }, DELAY_TIME);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 初始化tab
     */
    private void initTab() {
        colorSelect = ContextCompat.getColor(this, R.color.colorPrimary);
        colorUnSelect = ContextCompat.getColor(this, R.color.darker_gray);

        tabViews.add(rbtnMainHome);
        tabViews.add(rbtnMainBook);
        tabViews.add(rbtnMainSetting);

        tabDrawables.add(ContextCompat.getDrawable(this, R.drawable.selector_main_home).mutate());
        tabDrawables.add(ContextCompat.getDrawable(this, R.drawable.selector_main_movie).mutate());
        tabDrawables.add(ContextCompat.getDrawable(this, R.drawable.selector_main_setting).mutate());
    }

    /**
     * 改变tab图片
     *
     * @param position       Position index of the first page currently being displayed.
     *                       Page position+1 will be visible if positionOffset is nonzero.
     * @param positionOffset Value from [0, 1) indicating the offset from the page at position.
     */
    private void changeTabDrawable(int position, float positionOffset) {
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
     * @param selectIndex
     */
    private void setTabSelect(int selectIndex) {
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

    public Drawable tintDrawable(Drawable drawable, ColorStateList colors) {
        final Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colors);
        return wrappedDrawable;
    }

    @Override
    public void loadData() {

    }
}
