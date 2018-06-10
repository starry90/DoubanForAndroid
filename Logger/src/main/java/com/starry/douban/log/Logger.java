package com.starry.douban.log;

/**
 * Logger is a wrapper of {@link android.util.Log}
 * But more pretty, simple and powerful
 */
public final class Logger {
    private static LoggerPrinter printer = new LoggerPrinter();

    //no instance
    private Logger() {
    }

    public static void clear() {
        printer.clear();
        printer = null;
    }

    public static void v(String message, Object... args) {
        printer.v(message, args);
    }

    public static void v(String tag, String message, Object... args) {
        printer.init(tag);
        printer.v(message, args);
    }

    public static void d(String message, Object... args) {
        printer.d(message, args);
    }

    public static void d(String tag, String message, Object... args) {
        printer.init(tag);
        printer.d(message, args);
    }

    public static void i(String message, Object... args) {
        printer.i(message, args);
    }

    public static void i(String tag, String message, Object... args) {
        printer.init(tag);
        printer.i(message, args);
    }

    public static void w(String message, Object... args) {
        printer.w(message, args);
    }

    public static void w(String tag, String message, Object... args) {
        printer.init(tag);
        printer.w(message, args);
    }

    public static void e(String message, Object... args) {
        printer.e(null, message, args);
    }

    public static void e(String tag, String message, Object... args) {
        printer.init(tag);
        printer.e(null, message, args);
    }

    public static void e(Throwable throwable, String message, Object... args) {
        printer.e(throwable, message, args);
    }

    public static void e(String tag, Throwable throwable, String message, Object... args) {
        printer.init(tag);
        printer.e(throwable, message, args);
    }

    public static Printer t(String tag) {
        return printer.t(tag, printer.getSettings().getMethodCount());
    }

    public static Printer t(int methodCount) {
        return printer.t(null, methodCount);
    }

    public static Printer t(String tag, int methodCount) {
        return printer.t(tag, methodCount);
    }

    public static void wtf(String message, Object... args) {
        printer.wtf(message, args);
    }

    /**
     * Formats the json content and print it
     *
     * @param json the json content
     */
    public static void json(String json) {
        printer.json(json);
    }

    /**
     * Formats the json content and print it
     *
     * @param xml the xml content
     */
    public static void xml(String xml) {
        printer.xml(xml);
    }

}
