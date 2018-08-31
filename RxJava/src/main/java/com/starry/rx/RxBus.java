package com.starry.rx;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/**
 * @author Starry Jerry
 * @since 2018/5/17.
 */
public final class RxBus {

    private Subject<Object> subjectBus;

    private RxBus() {
        subjectBus = PublishSubject.create().toSerialized();
    }

    /**
     * 获取RxBus
     */
    public static RxBus getDefault() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private final static RxBus INSTANCE = new RxBus();
    }

    /**
     * 注册事件
     *
     * @param eventType 事件类型
     * @param observer  事件消费者
     * @return Disposable
     */
    public <T> Disposable register(Class<T> eventType, Consumer<T> observer) {
        return toObservable(eventType).subscribe(observer);
    }

    /**
     * @param eventType 事件类型
     * @param observer  事件消费者
     * @param scheduler 可设置在主线程或是独立线程执行
     * @return Disposable
     */
    public <T> Disposable register(Class<T> eventType, Consumer<T> observer, Scheduler scheduler) {
        return toObservable(eventType).observeOn(scheduler).subscribe(observer);
    }

    private <T> Observable<T> toObservable(Class<T> eventType) {
        return subjectBus.ofType(eventType);
    }

    /**
     * 取消注册
     *
     * @param disposable Disposable
     */
    public void unRegister(Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    /**
     * 发布一个事件
     *
     * @param event 事件对象
     */
    public void post(Object event) {
        subjectBus.onNext(event);
    }
}
