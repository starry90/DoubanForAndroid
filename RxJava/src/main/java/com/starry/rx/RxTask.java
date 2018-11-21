package com.starry.rx;

/**
 *
 * @param <T> the value type to emit
 * @author Starry Jerry
 * @since 2018-6-8.
 */
public interface RxTask<T> {

    /**
     * do something
     *
     * @return the value type to emit. Null values are generally not allowed in Rx 2.x operators and sources
     */
    T run();

}
