package com.starry.log;

import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.io.RandomAccessFile;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * Logger is a wrapper for logging utils
 * But more pretty, simple and powerful
 */
final class LoggerPrinter {

    public enum LogLevel {

        /**
         * Prints all logs
         */
        FULL,

        /**
         * No log will be printed
         */
        NONE
    }

    public class Settings {

        private int methodCount = 1;
        private boolean showThreadInfo = true;
        private int methodOffset = 0;

        public void initSettings(int methodCount, boolean showThreadInfo, int methodOffset) {
            this.methodCount = methodCount;
            this.showThreadInfo = showThreadInfo;
            this.methodOffset = methodOffset;
        }

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
            if (!BuildConfig.DEBUG) {//  No log will be printed for release environment
                logLevel = LogLevel.NONE;
            }
            return logLevel;
        }

        public int getMethodOffset() {
            return methodOffset;
        }

    }

    /**
     * tag is used for the Log, the name is a little different
     * in order to differentiate the logs easily with the filter
     */
    private String mTag = "LOGGER";

    private static final int VERBOSE = 2;
    private static final int DEBUG = 3;
    private static final int INFO = 4;
    private static final int WARN = 5;
    private static final int ERROR = 6;
    private static final int ASSERT = 7;

    /**
     * Android's max limit for a log entry is ~4076 bytes,
     * so 4000 bytes is used as chunk size since default charset
     * is UTF-8
     */
    private static final int CHUNK_SIZE = 4000;

    /**
     * It is used for json pretty print
     */
    private static final int JSON_INDENT = 4;

    /**
     * The minimum stack trace index, starts at this class after two native calls.
     */
    private static final int MIN_STACK_OFFSET = 3;

    /**
     * Localize single tag and method count for each thread
     */
    private final ThreadLocal<String> localTag = new ThreadLocal<>();
    private final ThreadLocal<Integer> localMethodCount = new ThreadLocal<>();

    /**
     * It is used to determine log settings such as method count, thread info visibility
     */
    private Settings settings = new Settings();

    /**
     * It is used to change the tag
     *
     * @param tag is the given string which will be used in Logger
     */
    public Settings init(String tag) {
        if (!TextUtils.isEmpty(tag))
            mTag = tag;
        return settings;
    }

    public Settings getSettings() {
        return settings;
    }

    public void v(String message, Object... args) {
        if (settings.getLogLevel() == LogLevel.NONE) {
            return;
        }
        log(VERBOSE, message, args);
    }

    public void d(String message, Object... args) {
        if (settings.getLogLevel() == LogLevel.NONE) {
            return;
        }
        log(DEBUG, message, args);
    }

    public void i(String message, Object... args) {
        if (settings.getLogLevel() == LogLevel.NONE) {
            return;
        }
        log(INFO, message, args);
    }

    public void w(String message, Object... args) {
        if (settings.getLogLevel() == LogLevel.NONE) {
            return;
        }
        log(WARN, message, args);
    }

    public void e(String message, Object... args) {
        if (settings.getLogLevel() == LogLevel.NONE) {
            return;
        }
        e(null, message, args);
    }

    public void e(Throwable throwable, String message, Object... args) {
        if (settings.getLogLevel() == LogLevel.NONE) {
            return;
        }

        if (throwable != null && message != null) {
            message += " : " + Log.getStackTraceString(throwable);
        }
        if (throwable != null && message == null) {
            message = throwable.toString();
        }
        if (message == null) {
            message = "No message/exception is set";
        }
        log(ERROR, message, args);
    }

    public void t(String tag, int methodCount) {
        if (tag != null) {
            localTag.set(tag);
        }
        localMethodCount.set(methodCount);
    }

    public void wtf(String message, Object... args) {
        if (settings.getLogLevel() == LogLevel.NONE) {
            return;
        }
        log(ASSERT, message, args);
    }

    public void f(String tag, String message) {
        if (tag != null) {
            localTag.set(tag);
        }
        log(INFO, tag, message);

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) || !Environment.isExternalStorageRemovable()) {
            DiskLogger.get().log(tag + ": " + message);
        }
    }

    /**
     * Formats the json content and print it
     *
     * @param json the json content
     */
    public void json(String json) {
        if (settings.getLogLevel() == LogLevel.NONE) {
            return;
        }

        if (TextUtils.isEmpty(json)) {
            d("Empty/Null json content");
            return;
        }
        try {
            json = json.trim();
            if (json.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(json);
                String message = jsonObject.toString(JSON_INDENT);
                i(message);
                return;
            }
            if (json.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(json);
                String message = jsonArray.toString(JSON_INDENT);
                i(message);
            }
        } catch (JSONException e) {
            e(e.getCause().getMessage() + "\n" + json);
        }
    }

    /**
     * Formats the json content and print it
     *
     * @param xml the xml content
     */
    public void xml(String xml) {
        if (settings.getLogLevel() == LogLevel.NONE) {
            return;
        }

        if (TextUtils.isEmpty(xml)) {
            d("Empty/Null xml content");
            return;
        }
        try {
            Source xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            d(xmlOutput.getWriter().toString().replaceFirst(">", ">\n"));
        } catch (TransformerException e) {
            e(e.getCause().getMessage() + "\n" + xml);
        }
    }

    public void clear() {
        settings = null;
    }

    /**
     * This method is synchronized in order to avoid messy of logs' order.
     */
    private synchronized void log(int logType, String msg, Object... args) {
        String tag = getTag();
        String message = createMessage(msg, args);
        int methodCount = getMethodCount();

        if (TextUtils.isEmpty(message)) {
            message = "Empty/NULL log message";
        }

        logHeaderContent(logType, tag, methodCount);

        //get bytes of message with system's default charset (which is UTF-8 for Android)
        byte[] bytes = message.getBytes();
        int length = bytes.length;
        if (length <= CHUNK_SIZE) {
            logContent(logType, tag, message);
            return;
        }
        for (int i = 0; i < length; i += CHUNK_SIZE) {
            int count = Math.min(length - i, CHUNK_SIZE);
            //create a new String with system's default charset (which is UTF-8 for Android)
            logContent(logType, tag, new String(bytes, i, count));
        }
    }


    private void logHeaderContent(int logType, String tag, int methodCount) {
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        if (settings.isShowThreadInfo()) {
            logContent(logType, tag, "Thread: " + Thread.currentThread().getName());
        }

        int stackOffset = getStackOffset(trace) + settings.getMethodOffset();

        //corresponding method count with the current stack may exceeds the stack trace. Trims the count
        if (methodCount + stackOffset > trace.length) {
            methodCount = trace.length - stackOffset - 1;
        }

        for (int i = methodCount; i > 0; i--) {
            int stackIndex = i + stackOffset;
            if (stackIndex >= trace.length) {
                continue;
            }
            StringBuilder builder = new StringBuilder();
            builder.append(getSimpleClassName(trace[stackIndex].getClassName()))
                    .append(".")
                    .append(trace[stackIndex].getMethodName())
                    .append(" ")
                    .append(" (")
                    .append(trace[stackIndex].getFileName())
                    .append(":")
                    .append(trace[stackIndex].getLineNumber())
                    .append(")");
            logContent(logType, tag, builder.toString());
        }
    }

    private void logContent(int logType, String tag, String chunk) {
        String finalTag = formatTag(tag);
        switch (logType) {
            case VERBOSE:
                Log.v(finalTag, chunk);
                break;
            case INFO:
                Log.i(finalTag, chunk);
                break;
            case WARN:
                Log.w(finalTag, chunk);
                break;
            case ERROR:
                Log.e(finalTag, chunk);
                break;
            case ASSERT:
                Log.wtf(finalTag, chunk);
                break;
            case DEBUG:
                // Fall through, log debug by default
            default:
                Log.d(finalTag, chunk);
                break;
        }
    }

    private String getSimpleClassName(String name) {
        int lastIndex = name.lastIndexOf(".");
        return name.substring(lastIndex + 1);
    }

    private String formatTag(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            return tag;
        }
        return this.mTag;
    }

    /**
     * @return the appropriate tag based on local or global
     */
    private String getTag() {
        String tag = localTag.get();
        if (tag != null) {
            localTag.remove();
            return tag;
        }
        return this.mTag;
    }

    private String createMessage(String message, Object... args) {
        return args.length == 0 ? message : String.format(message, args);
    }

    private int getMethodCount() {
        Integer count = localMethodCount.get();
        int result = settings.getMethodCount();
        if (count != null) {
            localMethodCount.remove();
            result = count;
        }
        if (result < 0) {
            throw new IllegalStateException("methodCount cannot be negative");
        }
        return result;
    }

    /**
     * Determines the starting index of the stack trace, after method calls made by this class.
     *
     * @param trace the stack trace
     * @return the stack offset
     */
    private int getStackOffset(StackTraceElement[] trace) {
        for (int i = MIN_STACK_OFFSET; i < trace.length; i++) {
            StackTraceElement e = trace[i];
            String name = e.getClassName();
            if (!name.equals(LoggerPrinter.class.getName()) && !name.equals(Logger.class.getName())) {
                return --i;
            }
        }
        return -1;
    }

    private static String logFolderPath;

    public void setLogFolderPath(String logFolderPath) {
        LoggerPrinter.logFolderPath = logFolderPath;
    }

    // 日志写sd卡
    private static class DiskLogger {
        private final Handler mHandler;

        private static class Holder {
            private static final DiskLogger DISK_LOGGER = new DiskLogger();
        }

        public static DiskLogger get() {
            return Holder.DISK_LOGGER;
        }

        public void log(String msg) {
            Message message = mHandler.obtainMessage(0, msg);
            mHandler.sendMessage(message);
        }

        private DiskLogger() {
            HandlerThread mHandlerThread = new HandlerThread("Logger");
            mHandlerThread.start();
            mHandler = new WorkHandler(mHandlerThread.getLooper(), new File(logFolderPath));
        }

        private static class WorkHandler extends Handler {

            private final File logFolder;

            private static final DateFormat LOG_FILE_NAME = new SimpleDateFormat("yyyyMMdd", Locale.US);

            private static final SimpleDateFormat LOG_FILE_CONTENT = new SimpleDateFormat("MM-dd HH:mm:ss", Locale.US);

            WorkHandler(Looper looper, File logFolder) {
                super(looper);
                this.logFolder = logFolder;
            }

            @Override
            public void handleMessage(Message msg) {
                try {
                    // 创建日志文件 yyyymmdd.log
                    final String logFile = LOG_FILE_NAME.format(new Date(System.currentTimeMillis()));
                    File f = new File(logFolder, String.format(Locale.US, "%s.log", logFile));
                    RandomAccessFile fileWriter = new RandomAccessFile(f, "rw");
                    fileWriter.seek(f.length());

                    // 日志内容
                    String ctLog = LOG_FILE_CONTENT.format(new Date(System.currentTimeMillis()));
                    String content = ctLog + " " + android.os.Process.myPid() + " " + msg.obj + "\n";
                    fileWriter.write(content.getBytes(StandardCharsets.UTF_8));
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class LogFileFilter implements FileFilter {
        static final long ONE_DAY = 24 * 3600 * 1000;
        static final int ONE_WEEK = 7;

        @Override
        public boolean accept(File pathname) {
            String filename = pathname.getName().toLowerCase();
            if (pathname.isFile() && filename.endsWith(".log")) {
                long lastTime = pathname.lastModified();
                long now = System.currentTimeMillis();
                return (now - lastTime) / ONE_DAY >= ONE_WEEK;
            }
            return false;
        }
    }

}
