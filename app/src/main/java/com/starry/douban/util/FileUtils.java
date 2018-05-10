package com.starry.douban.util;

import android.os.Environment;

import com.starry.douban.constant.Common;
import com.starry.douban.log.Logger;

import java.io.File;

/**
 * @author Starry Jerry
 * @since 18-5-10.
 */

public class FileUtils {

    public static File getCrashDir() {
        return buildPath(Environment.getExternalStorageDirectory(), Common.DIR_ROOT, Common.DIR_CRASH);
    }

    public static File buildPath(File base, String... segments) {
        File cur = base;
        for (String segment : segments) {
            if (cur == null) {
                cur = new File(segment);
            } else {
                cur = new File(cur, segment);
            }
        }
        if (!cur.exists()) {
            boolean mkdirs = cur.mkdirs();
            Logger.e("create dirs is " + mkdirs);
        }
        return cur;
    }
}
