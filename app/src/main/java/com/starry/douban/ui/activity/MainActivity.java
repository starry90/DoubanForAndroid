package com.starry.douban.ui.activity;

import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.starry.douban.R;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.base.BaseApp;
import com.starry.douban.ui.fragment.HomeFragment;
import com.starry.douban.ui.fragment.MovieFragment;
import com.starry.douban.ui.fragment.MovieParentFragment;
import com.starry.douban.ui.fragment.SettingFragment;
import com.starry.douban.util.ArgbEvaluatorUtil;
import com.starry.douban.util.PermissionUtils;
import com.starry.douban.util.ToastUtil;
import com.starry.douban.util.viewpager.v4.FragmentPagerItem;
import com.starry.douban.util.viewpager.v4.FragmentPagerItemAdapter;
import com.starry.douban.util.viewpager.v4.FragmentPagerItems;

import butterknife.BindView;


/**
 * Main
 * Created by Starry Jerry on 2016/12/1.
 */
public class MainActivity extends BaseActivity {

    @BindView(R.id.viewpager_main)
    ViewPager viewpagerMain;
    @BindView(R.id.rbtn_main_home)
    RadioButton rbMainHome;
    @BindView(R.id.rbtn_main_book)
    RadioButton rbtnMainBook;
    @BindView(R.id.rbtn_main_setting)
    RadioButton rbtnMainSetting;
    @BindView(R.id.radioGroup_main)
    RadioGroup radioGroupMain;

    private String[] titles = new String[]{"首页", "电影", "设置"};

    private ArgbEvaluatorUtil argbEvaluatorUtil = ArgbEvaluatorUtil.get();

    @Override
    public int getLayoutResID() {
        return R.layout.activity_main;
    }

    @Override
    protected boolean displayHomeAsUpEnabled() {
        return false;
    }

    @Override
    public void initData() {
        setTitle(titles[0]);
        PermissionUtils.requestPermission(this);

        FragmentPagerItems pages = new FragmentPagerItems(this);
        pages.add(FragmentPagerItem.of(HomeFragment.class.getSimpleName(), HomeFragment.class));
        pages.add(FragmentPagerItem.of(MovieFragment.class.getSimpleName(), MovieParentFragment.class));
        pages.add(FragmentPagerItem.of(SettingFragment.class.getSimpleName(), SettingFragment.class));

        viewpagerMain.setOffscreenPageLimit(3);
        FragmentPagerItemAdapter adapter = new FragmentPagerItemAdapter(getSupportFragmentManager(), pages);
        viewpagerMain.setAdapter(adapter);

        argbEvaluatorUtil.addTab(rbMainHome, rbtnMainBook, rbtnMainSetting);
        argbEvaluatorUtil.addTabDrawable(R.drawable.selector_main_home, R.drawable.selector_main_movie, R.drawable.selector_main_setting);
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
                argbEvaluatorUtil.changeTabDrawable(position, positionOffset);
            }

            @Override
            public void onPageSelected(final int position) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setTitle(titles[position]);
                        argbEvaluatorUtil.setTabSelect(position);
                    }
                }, DELAY_TIME);
                argbEvaluatorUtil.setChecked(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    /**
     * 退出时间
     */
    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                ToastUtil.showToast("再按一次退出应用");
                exitTime = System.currentTimeMillis();
            } else {
                BaseApp.getInstance().exitApp();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
