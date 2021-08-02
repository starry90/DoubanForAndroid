package com.starry.douban.base;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.viewbinding.ViewBinding;
import android.widget.TextView;

import com.starry.douban.R;
import com.starry.douban.widget.LoadingDataLayout;
import com.starry.http.HttpManager;


/**
 * Activity基类
 * <p>
 * 方法执行顺序
 * {@link #initData()} —> {@link #setListener()}
 * <p>
 */

public abstract class BaseActivity<T extends ViewBinding> extends AppCompatActivity implements IBaseUI {

    protected final String TAG = getClass().getSimpleName();

    /**
     * 网络请求各种状态显示容器
     */
    protected LoadingDataLayout mLoadingDataLayout;

    protected TextView tvToolbarTitle;

    protected Toolbar toolbar;

    protected T viewBinding;

    public abstract T getViewBinding(LayoutInflater layoutInflater);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = getViewBinding(LayoutInflater.from(this));
        if (viewBinding != null) {
            setContentView(viewBinding.getRoot());
        }

        initToolBar();
        initStatusBar();
        initLoadingDataLayout();

        initData();
        setListener();
    }

    private void initLoadingDataLayout() {
        mLoadingDataLayout = findViewById(R.id.view_loading_container);
        if (mLoadingDataLayout != null) {
            showLoading();
            mLoadingDataLayout.setRetryListener(new LoadingDataLayout.OnRetryListener() {
                @Override
                public void onRetry() {
                    loadData();
                }
            });
        }
    }

    /**
     * 加载数据，如请求网络，读取本地缓存等
     */
    public void loadData() {

    }

    public void setListener() {

    }

    private void initToolBar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
            setSupportActionBar(toolbar);
            ActionBar ab = getSupportActionBar();
            if (ab != null) {
                Drawable drawable = getToolbarBackground();
                if (drawable != null) {
                    ab.setBackgroundDrawable(drawable);
                }
                ab.setDisplayShowTitleEnabled(false);//自定义标题居中需要关闭
                ab.setDisplayHomeAsUpEnabled(displayHomeAsUpEnabled());//显示返回键
            }

            if (displayHomeAsUpEnabled()) {
                int toolbarNavigationIconRes = getToolbarNavigationIconRes();
                //设置图片时会显示返回键
                if (toolbarNavigationIconRes != 0) {
                    toolbar.setNavigationIcon(toolbarNavigationIconRes);
                }
            }
            toolbar.setNavigationOnClickListener(getToolbarNavigationOnClickListener());
        }
    }

    private void initStatusBar() {
        //状态栏跟随标题栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        //沉浸式状态栏
        //SYSTEM_UI_FLAG_LAYOUT_STABLE required 16
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        //状态栏白底黑字
        updateStatusBarTextBg(isDarkTextWhiteBgStatusBar());
    }

    /**
     * 白底黑字模式
     *
     * @param isDarkTextWhiteBg 白底黑字
     */
    public void updateStatusBarTextBg(boolean isDarkTextWhiteBg) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = getWindow().getDecorView();
            if (decorView != null) {
                int systemUiVisibility = decorView.getSystemUiVisibility();
                if (isDarkTextWhiteBg) {
                    systemUiVisibility |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else {
                    systemUiVisibility &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(systemUiVisibility);
            }
        }
    }

    @Override
    public void setTitle(int titleId) {
        if (tvToolbarTitle != null) tvToolbarTitle.setText(titleId);
    }

    @Override
    public void setTitle(CharSequence title) {
        if (tvToolbarTitle != null) tvToolbarTitle.setText(title);
    }

    /**
     * Toolbar背景
     *
     * @return 背景
     */
    protected Drawable getToolbarBackground() {
        return null;
    }

    /**
     * 返回键图片
     *
     * @return 返回键图片资源
     */
    protected int getToolbarNavigationIconRes() {
        return 0;
    }

    /**
     * 返回键点击事件监听
     *
     * @return 返回键点击事件监听
     */
    protected View.OnClickListener getToolbarNavigationOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        };
    }

    /**
     * 显示返回键
     *
     * @return true为显示左上角返回键，反之为false
     */
    protected boolean displayHomeAsUpEnabled() {
        return true;
    }

    /**
     * 状态栏白底黑字
     *
     * @return true表示是白底黑字
     */
    protected boolean isDarkTextWhiteBgStatusBar() {
        return false;
    }

    @Override
    protected void onDestroy() {
        //如果关闭页面，取消请求
        HttpManager.cancelTag(this);

        super.onDestroy();
    }

    /**
     * 获取当前Activity
     *
     * @return 当前Activity
     */
    protected Activity getActivity() {
        return this;
    }

    public void showLoading() {
        showLoadingStatus(LoadingDataLayout.STATUS_LOADING);
    }

    public void hideLoading(boolean success) {
        if (success) {
            showLoadingStatus(LoadingDataLayout.STATUS_SUCCESS);
        } else {
            showLoadingStatus(LoadingDataLayout.STATUS_ERROR);
        }
    }

    public void showEmpty() {
        showLoadingStatus(LoadingDataLayout.STATUS_EMPTY);
    }

    /**
     * 展示网络请求各种状态
     *
     * @param status 网络请求状态
     */
    private void showLoadingStatus(int status) {
        if (mLoadingDataLayout != null && !mLoadingDataLayout.isSuccess()) {
            mLoadingDataLayout.setStatus(status);
        }
    }

}
