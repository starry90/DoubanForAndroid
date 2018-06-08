package com.starry.douban.rx;

/**
 * IO线程获取数据，UI线程刷新界面的公共任务
 * <p>调用顺序:doIOWork --> doUIWork
 *
 * @param <T> the value type to emit
 * @author Starry Jerry
 * @since 2018-6-8.
 */
public interface RxCommonTask<T> {

    /**
     * do something on IO thread
     *
     * @return the value type to emit. Null values are generally not allowed in Rx 2.x operators and sources
     */
    T doIOWork();

    /**
     * do something on UI thread
     *
     * @param t the value type to emit
     */
    void doUIWork(T t);

}
