package com.starry.douban.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * 懒加载的Fragment
 * Fragment基类
 */
public abstract class BaseLazyFragment extends BaseFragment implements IBaseActivity {

    protected boolean isViewCreated = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        isViewCreated = true;
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isViewCreated) {
            onLazyLoadingData();
        }
    }

    /**
     * 懒加载数据，只加载一次
     */
    public void onLazyLoadingData() {
        isViewCreated = false;
    }

}
