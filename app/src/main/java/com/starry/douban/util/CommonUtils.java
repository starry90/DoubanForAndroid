package com.starry.douban.util;

import android.os.Build;
import android.text.format.Formatter;

import com.starry.douban.BuildConfig;
import com.starry.douban.env.AppWrapper;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Field;

/**
 * @author Starry Jerry
 * @since 18-5-10.
 */

public class CommonUtils {

    /**
     * 保存崩溃信息
     */
    public static void saveCrashInfo(Throwable ex, File crashDir, String crashName) {
        StringBuilder sb = new StringBuilder();

        String format = "BuildType:%s \nVersionName:%s \nBuildTime:%s \n";
        sb.append(String.format(format, BuildConfig.BUILD_TYPE, BuildConfig.VERSION_NAME, BuildConfig.BUILD_TIME));
        sb.append("====================================================").append("\n");

        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        Field[] declaredFields = Build.class.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            try {
                String name = declaredField.getName();
                sb.append(name).append(":").append(declaredField.get(name)).append("\n");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sb.append("====================================================").append("\n");

        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        sb.append(info.toString());
        printWriter.close();
        File crashFile = new File(crashDir, crashName);
        FileWriter writer = null;
        try {
            writer = new FileWriter(crashFile, false);
            writer.write(sb.toString());
        } catch (Throwable e) {
            e.printStackTrace();
        } finally {
            IOUtils.close(writer);
        }
    }

    /**
     * 根据文件byte大小格式化成 KB, MB, 等等<p>
     * Formats a content size to be in the form of bytes, kilobytes, megabytes, etc.
     *
     * @param sizeBytes size value to be formatted, in bytes
     * @return 12.99 MB
     */
    public static String formatFileSize(long sizeBytes) {
        return Formatter.formatFileSize(AppWrapper.getContext(), sizeBytes);
    }
}
