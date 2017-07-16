package com.starry.douban.log;


import com.starry.douban.BuildConfig;

public final class Settings {

    private int methodCount = 3;
    private boolean showThreadInfo = false;
    private int methodOffset = 0;

    /**
     * Determines how logs will printed
     */
    private LogLevel logLevel = LogLevel.FULL;

    public int getMethodCount() {
        return methodCount;
    }

    public boolean isShowThreadInfo() {
        return showThreadInfo;
    }

    public LogLevel getLogLevel() {
        if(!BuildConfig.DEBUG){//  No log will be printed for release environment
            logLevel = LogLevel.NONE;
        }
        return logLevel;
    }

    public int getMethodOffset() {
        return methodOffset;
    }

}
