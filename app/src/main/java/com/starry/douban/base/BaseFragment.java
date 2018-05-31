package com.starry.douban.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.starry.douban.R;
import com.starry.douban.ui.ILoadingView;
import com.starry.douban.widget.LoadingDataLayout;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * 正常的Framgent
 * Fragment基类
 */
public abstract class BaseFragment extends Fragment implements IBaseUI, ILoadingView {


    protected final String TAG = getClass().getSimpleName();

    private boolean isSuccess;

    /**
     * 使用mActivity代替getActivity()，保证Fragment即使在onDetach后，仍持有Activity的引用<p>
     * 有引起内存泄露的风险，但相比空指针应用闪退，这种做法更“安全”
     */
    protected Activity mActivity;

    /**
     * 网络请求各种状态显示容器
     * <p>Required view 'view_loading_container' with ID 2131427348 for field 'mLoadingDataLayout' was not found. If this view is optional add '@Nullable' (fields) or '@Optional' (methods) annotation.
     */
    @Nullable
    @BindView(R.id.view_loading_container)
    protected LoadingDataLayout mLoadingDataLayout;

    /**
     * 是否允许懒加载
     */
    private boolean allowLazyLoading = true;
    /**
     * Fragment视图是否已初始化完成
     */
    private boolean isViewCreated = false;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isViewCreated = true;
        View view = inflater.inflate(getLayoutResID(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLoadingDataLayout(view);

        initData();
        setListener();
        //Fragment初始化时setUserVisibleHint方法会先于onCreateView执行
        prepareLazyLoading(getUserVisibleHint(), isViewCreated);
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

    /**
     * @param view
     */
    private void initLoadingDataLayout(View view) {
        mLoadingDataLayout = (LoadingDataLayout) view.findViewById(R.id.view_loading_container);
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
     * 加载数据，如请求网络，读取本地缓存等
     */
    public void loadData() {

    }

    public void setListener() {

    }

    /**
     * 展示网络请求各种状态
     *
     * @param networkStatus 网络请求状态
     */
    protected void showLoadingStatus(int networkStatus) {
        if (mLoadingDataLayout == null || isSuccess) return;
        mLoadingDataLayout.setStatus(networkStatus);
        if (LoadingDataLayout.STATUS_SUCCESS == networkStatus) isSuccess = true;
    }

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
