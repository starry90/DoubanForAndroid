package com.starry.douban.ui.activity;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.RadioGroup;

import com.starry.douban.R;
import com.starry.douban.base.BaseFragmentPagerAdapter;
import com.starry.douban.base.BaseNFCActivity;
import com.starry.douban.databinding.ActivityMainBinding;
import com.starry.douban.env.AppWrapper;
import com.starry.douban.ui.fragment.HomeFragment;
import com.starry.douban.ui.fragment.MovieParentFragment;
import com.starry.douban.ui.fragment.SettingFragment;
import com.starry.douban.util.ArgbEvaluatorUtil;
import com.starry.douban.util.PermissionUtils;
import com.starry.douban.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Main
 * Created by Starry Jerry on 2016/12/1.
 */
public class MainActivity extends BaseNFCActivity<ActivityMainBinding> {

    private final String[] titles = new String[]{"首页", "电影", "设置"};

    private final ArgbEvaluatorUtil argbEvaluatorUtil = ArgbEvaluatorUtil.get();

    @Override
    protected Class<?> getNFCClass() {
        return getClass();
    }

    @Override
    protected void responseNFCResult(String nfcResult) {
        NFCActivity.startPage(this, nfcResult);
    }

    @Override
    public ActivityMainBinding getViewBinding(LayoutInflater layoutInflater) {
        return ActivityMainBinding.inflate(layoutInflater);
    }

    @Override
    protected boolean isDarkTextWhiteBgStatusBar() {
        return false;
    }

    @Override
    public void initData() {
        PermissionUtils.requestPermission(this);

        final List<Fragment> pages = new ArrayList<>();
        pages.add(new HomeFragment());
        pages.add(new MovieParentFragment());
        pages.add(new SettingFragment());
        viewBinding.viewpagerMain.setAdapter(new BaseFragmentPagerAdapter(getSupportFragmentManager(), pages));
        viewBinding.viewpagerMain.setOffscreenPageLimit(3);

        argbEvaluatorUtil.addTab(viewBinding.rbtnMainHome, viewBinding.rbtnMainBook, viewBinding.rbtnMainSetting);
        argbEvaluatorUtil.addTabDrawable(R.drawable.selector_main_home, R.drawable.selector_main_movie, R.drawable.selector_main_setting);
    }

    @Override
    public void setListener() {
        viewBinding.radioGroupMain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbtn_main_home:
                        viewBinding.viewpagerMain.setCurrentItem(0, false);
                        break;
                    case R.id.rbtn_main_book:
                        viewBinding.viewpagerMain.setCurrentItem(1, false);
                        break;
                    case R.id.rbtn_main_setting:
                        viewBinding.viewpagerMain.setCurrentItem(2, false);
                        break;

                }
            }
        });

        viewBinding.viewpagerMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

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
                AppWrapper.getInstance().exitApp();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
