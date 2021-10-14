package com.starry.douban.ui.activity;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;

import com.starry.douban.R;
import com.starry.douban.base.BaseFragmentPagerAdapter;
import com.starry.douban.base.BaseNFCActivity;
import com.starry.douban.databinding.ActivityMainBinding;
import com.starry.douban.env.AppWrapper;
import com.starry.douban.ui.fragment.HomeFragment;
import com.starry.douban.ui.fragment.MovieParentFragment;
import com.starry.douban.ui.fragment.SettingFragment;
import com.starry.douban.util.PermissionUtils;
import com.starry.douban.util.ToastUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Main
 * Created by Starry Jerry on 2016/12/1.
 */
public class MainActivity extends BaseNFCActivity<ActivityMainBinding> {

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
    public void initData() {
        PermissionUtils.requestPermission(this);

        final List<Fragment> pages = new ArrayList<>();
        pages.add(new HomeFragment());
        pages.add(new MovieParentFragment());
        pages.add(new SettingFragment());
        viewBinding.viewpagerMain.setAdapter(new BaseFragmentPagerAdapter(getSupportFragmentManager(), pages));
        viewBinding.viewpagerMain.setOffscreenPageLimit(3);
    }

    @Override
    public void setListener() {
        final BottomNavigationView bnv = viewBinding.mainBnv;
        bnv.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.tab_home:
                        updateStatusBarTextBg(false);
                        viewBinding.viewpagerMain.setCurrentItem(0, false);
                        return true;
                    case R.id.tab_movie:
                        updateStatusBarTextBg(true);
                        viewBinding.viewpagerMain.setCurrentItem(1, false);
                        return true;
                    case R.id.tab_setting:
                        updateStatusBarTextBg(false);
                        viewBinding.viewpagerMain.setCurrentItem(2, false);
                        return true;
                    default:
                        return false;
                }
            }
        });

        viewBinding.viewpagerMain.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                if (position == 1) {
                    updateStatusBarTextBg(true);
                } else {
                    updateStatusBarTextBg(false);
                }
                updateSelectedItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void updateSelectedItem(int position) {
        MenuItem defaultMenuItem = viewBinding.mainBnv.getMenu().getItem(position);
        viewBinding.mainBnv.setSelectedItemId(defaultMenuItem.getItemId());
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
