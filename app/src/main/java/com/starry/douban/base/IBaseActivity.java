package com.starry.douban.base;

/**
 * Activity接口
 */
public interface IBaseActivity {

    /**
     * 获取视图
     */
    int getLayoutResID();

    /**
     * 初始化数据 在{@link #getLayoutResID()} 之后
     */
    void initData();

    /**
     * 设置事件监听 在{@link #initData()}之后
     */
    void setListener();


}