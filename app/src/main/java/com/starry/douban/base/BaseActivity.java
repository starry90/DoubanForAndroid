package com.starry.douban.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.starry.douban.R;
import com.starry.douban.http.HttpManager;
import com.starry.douban.ui.ILoadingView;
import com.starry.douban.widget.LoadingDataLayout;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Activity基类
 * <p/>
 * 方法执行顺序
 * {@link #initData()} —> {@link #getLayoutResID()} —> {@link #setListener()}
 * <p/>
 */

public abstract class BaseActivity extends AppCompatActivity implements IBaseActivity, ILoadingView {

    protected final String TAG = getClass().getSimpleName();

    private BaseApplication mApplication;

    /**
     * 当前Activity的弱引用，防止内存泄露
     */
    private WeakReference<Activity> mActivity;

    /**
     * 网络请求各种状态显示容器
     * <p>Required view 'view_loading_container' with ID 2131427348 for field 'mLoadingDataLayout' was not found. If this view is optional add '@Nullable' (fields) or '@Optional' (methods) annotation.
     */
    @Nullable
    @BindView(R.id.view_loading_container)
    protected LoadingDataLayout mLoadingDataLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 将当前Activity压入栈
        mApplication = BaseApplication.getInstance();
        mActivity = new WeakReference<Activity>(this);
        mApplication.pushTask(mActivity);

        setContentView(getLayoutResID());
        ButterKnife.bind(this);//必须在setContentView()之后调用

        initLoadingDataLayout();

        initToolBar();

        initData();
        setListener();
    }

    private void initLoadingDataLayout() {
        if (mLoadingDataLayout != null) {
            mLoadingDataLayout.setRetryListener(new LoadingDataLayout.OnRetryListener() {
                @Override
                public void onRetry() {
                    loadData();
                }
            });
        }
    }

    /**
     * 展示网络请求各种状态
     *
     * @param status 网络请求状态
     */
    protected void showLoadingStatus(int status) {
        if (mLoadingDataLayout != null)
            mLoadingDataLayout.setStatus(status);
    }

    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");//标题内容
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);//显示返回键
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {//Toolbar返回键
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //如果关闭页面，取消请求
        HttpManager.getInstance().cancelTag(this);
        mApplication.removeTask(mActivity);
    }

    /**
     * 获取当前Activity
     *
     * @return 当前Activity
     */
    protected Activity getActivity() {
        if (null != mActivity)
            return mActivity.get();
        else
            return null;
    }


    /**
     * 加载数据，如请求网络，读取本地缓存等
     */
    public abstract void loadData();

    @Override
    public void showLoading() {
        showLoadingStatus(LoadingDataLayout.STATUS_LOADING);
    }

    @Override
    public void hideLoading(int status) {
        switch (status) {
            case LoadingDataLayout.STATUS_SUCCESS:
                showLoadingStatus(LoadingDataLayout.STATUS_SUCCESS);
                break;

            case LoadingDataLayout.STATUS_ERROR:
                showLoadingStatus(LoadingDataLayout.STATUS_ERROR);
                break;
        }
    }

    @Override
    public void onLoadingComplete() {

    }
}
