package com.starry.douban.base;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.viewbinding.ViewBinding;
import android.widget.TextView;

import com.starry.douban.R;
import com.starry.douban.widget.LoadingDataLayout;


/**
 * 正常的Framgent
 * Fragment基类
 */
public abstract class BaseFragment<T extends ViewBinding> extends Fragment implements IBaseUI {

    protected final String TAG = getClass().getSimpleName();

    /**
     * 使用mActivity代替getActivity()，保证Fragment即使在onDetach后，仍持有Activity的引用<p>
     * 有引起内存泄露的风险，但相比空指针应用闪退，这种做法更“安全”
     */
    protected Activity mActivity;

    /**
     * 网络请求各种状态显示容器
     */
    protected LoadingDataLayout mLoadingDataLayout;

    protected TextView tvToolbarTitle;

    protected Toolbar toolbar;

    /**
     * 是否允许懒加载
     */
    private boolean allowLazyLoading = true;
    /**
     * Fragment视图是否已初始化完成
     */
    private boolean isViewCreated = false;

    protected T viewBinding;

    public abstract T getViewBinding(LayoutInflater layoutInflater);

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isViewCreated = true;
        viewBinding = getViewBinding(inflater);
        return viewBinding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mLoadingDataLayout = view.findViewById(R.id.view_loading_container);
        initToolBar(view);
        initLoadingDataLayout();
        initData();
        setListener();
        //Fragment初始化时setUserVisibleHint方法会先于onCreateView执行
        prepareLazyLoading(getUserVisibleHint(), isViewCreated);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        prepareLazyLoading(!hidden, isViewCreated);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        prepareLazyLoading(isVisibleToUser, isViewCreated);
    }

    /**
     * 预备懒加载
     *
     * @param isVisibleToUser Fragment用户可见
     * @param isViewCreated   Fragment视图已初始化完成
     */
    private void prepareLazyLoading(boolean isVisibleToUser, boolean isViewCreated) {
        if (!allowLazyLoading) return;

        if (isVisibleToUser && isViewCreated) {
            allowLazyLoading = false;//保证onLazyLoadingData（）只调用一次
            onLazyLoadingData();
        }
    }

    /**
     * 懒加载数据，只加载一次
     */
    public void onLazyLoadingData() {

    }

    private void initToolBar(View view) {
        toolbar = view.findViewById(R.id.toolbar);
        if (toolbar != null) {
            tvToolbarTitle = view.findViewById(R.id.tv_toolbar_title);

            Drawable drawable = getToolbarBackground();
            if (drawable != null) {
                toolbar.setBackground(drawable);
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

    public void setTitle(int titleId) {
        if (tvToolbarTitle != null) tvToolbarTitle.setText(titleId);
    }

    public void setTitle(CharSequence title) {
        if (tvToolbarTitle != null) tvToolbarTitle.setText(title);
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
                mActivity.finish();
            }
        };
    }

    /**
     * 加载数据，如请求网络，读取本地缓存等
     */
    public void loadData() {

    }

    public void setListener() {

    }

    private void initLoadingDataLayout() {
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
    protected void showLoadingStatus(int status) {
        if (mLoadingDataLayout != null && !mLoadingDataLayout.isSuccess()) {
            mLoadingDataLayout.setStatus(status);
        }
    }

}
