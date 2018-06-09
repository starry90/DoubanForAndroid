package com.starry.douban.rx;


import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Rx管理器
 * <p>背压策略
 * <p>{@link BackpressureStrategy#BUFFER}:缓存所有事件，直到下游消费（如果事件太多会产生OOM）
 * <p>{@link BackpressureStrategy#DROP}:如果下游速度跟不上（缓存区满了），丢掉上游后续发送的事件
 * <p>{@link BackpressureStrategy#LATEST}:如果下游速度跟不上，总是缓存最新的128个事件
 *
 * @author Starry Jerry
 * @since 2018-6-8.
 */
public class RxManager {
    /**
     * 创建Rx任务，支持非阻塞式背压
     * <p>最大事件缓存数128 {@link Flowable#BUFFER_SIZE}
     * <p>背压策略{@link BackpressureStrategy#LATEST}
     *
     * @param task {@link RxCommonTask}公共任务
     * @param <T>  the element type
     * @return {@link Disposable}
     */
    public static <T> Disposable create(final RxCommonTask<T> task) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> e) throws Exception {
                e.onNext(task.doIOWork());
                e.onComplete();
            }
        }, BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<T>() {
                    @Override
                    public void accept(T t) throws Exception {
                        task.doUIWork(t);
                    }
                });
    }

    /**
     * 创建Rx任务，运行于UI线程
     *
     * @param task Runnable
     * @return {@link Disposable}
     */
    public static Disposable justUITask(Runnable task) {
        return Flowable.just(task)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Runnable>() {
                    @Override
                    public void accept(Runnable t) throws Exception {
                        t.run();
                    }
                });
    }

    /**
     * 创建Rx延时任务，运行于UI线程
     *
     * @param task  Runnable
     * @param delay 延时时长（单位：毫秒）
     * @return {@link Disposable}
     */
    public static Disposable justUITask(Runnable task, long delay) {
        return Flowable.just(task)
                .delay(delay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Runnable>() {
                    @Override
                    public void accept(Runnable t) throws Exception {
                        t.run();
                    }
                });
    }

    /**
     * 创建Rx任务，运行于IO线程
     *
     * @param task Runnable
     * @return {@link Disposable}
     */
    public static Disposable justIOTask(Runnable task) {
        return Flowable.just(task)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Runnable>() {
                    @Override
                    public void accept(Runnable t) throws Exception {
                        t.run();
                    }
                });
    }

    /**
     * 创建Rx延时任务，运行于IO线程
     *
     * @param task  Runnable
     * @param delay 延时时长（单位：毫秒）
     * @return {@link Disposable}
     */
    public static Disposable justIOTask(Runnable task, long delay) {
        return Flowable.just(task)
                .delay(delay, TimeUnit.MILLISECONDS)
                .observeOn(Schedulers.io())
                .subscribe(new Consumer<Runnable>() {
                    @Override
                    public void accept(Runnable t) throws Exception {
                        t.run();
                    }
                });
    }

    /**
     * 停止处理事件
     */
    public static void dispose(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
