package com.starry.rx;


import org.reactivestreams.Publisher;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.FlowableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.schedulers.Schedulers;

/**
 * Rx管理器
 * <p>背压策略
 * <p>{@link BackpressureStrategy#BUFFER}:缓存所有事件，直到下游消费（如果事件太多会产生OOM）
 * <p>{@link BackpressureStrategy#DROP}:如果下游速度跟不上（缓存区满了），丢掉上游后续发送的事件
 * <p>{@link BackpressureStrategy#LATEST}:如果下游速度跟不上，总是缓存最新的128个事件
 * <p> RxJava操作符 https://www.jianshu.com/p/cd984dd5aae8
 * <p> 避免打断链式结构 https://www.jianshu.com/p/e9e03194199e
 *
 * @author Starry Jerry
 * @since 2018-6-8.
 */
public class RxManager {
    /**
     * 创建运行在IO线程的Rx任务，支持非阻塞式背压，下一步操作运行在UI线程
     * <p>最大事件缓存数128 {@link Flowable#BUFFER_SIZE}
     * <p>背压策略{@link BackpressureStrategy#LATEST}
     *
     * @param task {@link RxTask}
     * @param <T>  the element type
     * @return {@link Disposable}
     */
    public static <T> Flowable<T> createIO(final RxTask<T> task) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> e) {
                e.onNext(task.run());
                e.onComplete();
            }
        }, BackpressureStrategy.LATEST)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    /**
     * 创建运行在主线程的Rx任务，支持非阻塞式背压，下一步操作运行在IO线程
     * <p>最大事件缓存数128 {@link Flowable#BUFFER_SIZE}
     * <p>背压策略{@link BackpressureStrategy#LATEST}
     *
     * @param task {@link RxTask}
     * @param <T>  the element type
     * @return {@link Disposable}
     */
    public static <T> Flowable<T> createMain(final RxTask<T> task) {
        return Flowable.create(new FlowableOnSubscribe<T>() {
            @Override
            public void subscribe(FlowableEmitter<T> e) {
                e.onNext(task.run());
                e.onComplete();
            }
        }, BackpressureStrategy.LATEST)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io());
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
                    public void accept(Runnable t) {
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
                    public void accept(Runnable t) {
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

    /**
     * 不切换线程，转换数据
     * <p> Function返回值不能为空，否则事件流会中断
     *
     * @param mapper Function
     * @param <T>    type of T
     * @param <R>    type of R
     * @return FlowableTransformer
     */
    public static <T, R> FlowableTransformer<T, R> map(final Function<? super T, ? extends R> mapper) {
        return new FlowableTransformer<T, R>() {
            @Override
            public Publisher<R> apply(Flowable<T> upstream) {
                return upstream.map(mapper);
            }
        };
    }

    /**
     * 切换到IO线程中，转换数据，下一步操作运行在UI线程
     * <p> Function返回值不能为空，否则事件流会中断
     *
     * @param mapper Function
     * @param <T>    type of T
     * @param <R>    type of R
     * @return FlowableTransformer
     */
    public static <T, R> FlowableTransformer<T, R> mapIO(final Function<? super T, ? extends R> mapper) {
        return new FlowableTransformer<T, R>() {
            @Override
            public Publisher<R> apply(Flowable<T> upstream) {
                return upstream.observeOn(Schedulers.io()).map(mapper).observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * 切换到UI线程中，转换数据，下一步操作运行在IO线程
     * <p> Function返回值不能为空，否则事件流会中断
     *
     * @param mapper Function
     * @param <T>    type of T
     * @param <R>    type of R
     * @return FlowableTransformer
     */
    public static <T, R> FlowableTransformer<T, R> mapMain(final Function<? super T, ? extends R> mapper) {
        return new FlowableTransformer<T, R>() {
            @Override
            public Publisher<R> apply(Flowable<T> upstream) {
                return upstream.observeOn(AndroidSchedulers.mainThread()).map(mapper).observeOn(Schedulers.io());
            }
        };
    }

    /**
     * 按条件过滤操作符，如果Predicate内test方法返回false时截断事件流,后续的事件不再执行
     * <p> Predicate返回值不能为空，否则事件流会中断
     *
     * @param predicate Predicate
     * @param <T>       type of T
     * @return FlowableTransformer
     */
    public static <T> FlowableTransformer<T, T> filter(final Predicate<? super T> predicate) {
        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream.filter(predicate);
            }
        };
    }
}
