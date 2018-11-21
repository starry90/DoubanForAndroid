package com.starry.douban.event;

/**
 * @author Starry Jerry
 * @since 2018/9/11.
 */

public class AppUpdateEvent {

    /**
     * 1 start 2 in progress 3.finish
     */
    public int type;

    public float progress;

    public AppUpdateEvent(int type) {
        this.type = type;
    }

    public AppUpdateEvent(int type, float progress) {
        this.type = type;
        this.progress = progress;
    }
}
