package com.starry.douban.base;

/**
 * Activity, Fragment接口
 */
public interface IBaseUI {

    /**
     * 获取视图
     */
    int getLayoutResID();

    /**
     * 初始化数据 在{@link #getLayoutResID()} 之后
     */
    void initData();

}