package com.starry.douban.base;

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
public abstract class BaseFragment extends Fragment implements IBaseActivity, ILoadingView {


    protected final String TAG = getClass().getSimpleName();

    private boolean isSuccess;

    /**
     * 网络请求各种状态显示容器
     * <p>Required view 'view_loading_container' with ID 2131427348 for field 'mLoadingDataLayout' was not found. If this view is optional add '@Nullable' (fields) or '@Optional' (methods) annotation.
     */
    @Nullable
    @BindView(R.id.view_loading_container)
    protected LoadingDataLayout mLoadingDataLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
     * 展示网络请求各种状态
     *
     * @param networkStatus 网络请求状态
     */
    protected void showLoadingStatus(int networkStatus) {
        if (mLoadingDataLayout == null || isSuccess) return;
        mLoadingDataLayout.setStatus(networkStatus);
        if (LoadingDataLayout.STATUS_SUCCESS == networkStatus) isSuccess = true;
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

    /**
     * 退出Activity
     */
    public void finish() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }


}
