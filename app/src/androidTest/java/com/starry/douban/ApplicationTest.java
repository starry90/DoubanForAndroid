package com.starry.douban;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.starry.douban.http.HttpManager;
import com.starry.douban.http.callback.FileCallback;
import com.starry.douban.http.callback.StringCallback;
import com.starry.douban.http.error.ErrorModel;
import com.starry.douban.log.Logger;
import com.starry.douban.model.BookDetail;
import com.starry.douban.util.FileUtils;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.util.concurrent.Executors;

import static junit.framework.Assert.assertEquals;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ApplicationTest {

    private String TAG = getClass().getSimpleName();

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.starry.douban", appContext.getPackageName());
    }

    @Test
    public void httpExecute() throws Exception {
        final String url = "https://api.douban.com//v2/book/26926209";
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Logger.e(TAG, "http start");
                BookDetail bookDetail = HttpManager.get()
                        .tag(this)
                        .url(url)
                        .build()
                        .execute(new StringCallback<BookDetail>() {

                            @Override
                            public void onSuccess(BookDetail response, Object... obj) {
                                Logger.e(TAG, "onSuccess");
                            }

                            @Override
                            public void onFailure(ErrorModel errorModel) {
                                Logger.e(TAG, "onFailure");
                            }
                        });
                Logger.e(TAG, bookDetail.getTitle());
                Logger.e(TAG, "http end");
            }
        });
        /*
         06-27 00:08:44.500 17088-17119/com.starry.douban E/ApplicationTest: Thread: pool-1-thread-1
         06-27 00:08:44.500 17088-17119/com.starry.douban E/ApplicationTest: ApplicationTest$1.run  (ApplicationTest.java:44)
         06-27 00:08:44.501 17088-17119/com.starry.douban E/ApplicationTest: http start
         06-27 00:08:44.721 17088-17119/com.starry.douban E/ApplicationTest: Thread: pool-1-thread-1
         06-27 00:08:44.721 17088-17119/com.starry.douban E/ApplicationTest: ApplicationTest$1.run  (ApplicationTest.java:61)
         06-27 00:08:44.721 17088-17119/com.starry.douban E/ApplicationTest: 轩辕诀3：龙图骇世
         06-27 00:08:44.722 17088-17119/com.starry.douban E/ApplicationTest: Thread: pool-1-thread-1
         06-27 00:08:44.722 17088-17119/com.starry.douban E/ApplicationTest: ApplicationTest$1.run  (ApplicationTest.java:62)
         06-27 00:08:44.722 17088-17119/com.starry.douban E/ApplicationTest: http end
         06-27 00:08:44.723 17088-17088/com.starry.douban E/ApplicationTest: Thread: main
         06-27 00:08:44.723 17088-17088/com.starry.douban E/ApplicationTest: ApplicationTest$1$1.onSuccess  (ApplicationTest.java:53)
         06-27 00:08:44.723 17088-17088/com.starry.douban E/ApplicationTest: onSuccess
         */
    }

    @Test
    public void download() {
        String url = "http://img.hb.aicdn.com/1b4494daa59e72ead4d5db77cf8b8216ff6f82951b0ca3-2l1uOm_fw658";
        HttpManager.get()
                .url(url)
                .build()
                .enqueue(new FileCallback(FileUtils.getFileDir().getAbsolutePath(), "andy.jpg") {
                    @Override
                    public void onSuccess(File response, Object... obj) {
                        Logger.i(TAG, response.getAbsolutePath());
                    }

                    @Override
                    public void onFailure(ErrorModel errorModel) {
                        Logger.i(TAG, errorModel.toString());
                    }
                });
    }
}