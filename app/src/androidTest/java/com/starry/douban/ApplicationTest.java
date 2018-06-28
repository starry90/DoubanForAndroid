package com.starry.douban;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.starry.douban.http.CommonCallback;
import com.starry.douban.http.HttpManager;
import com.starry.douban.log.Logger;
import com.starry.douban.model.BookDetail;
import com.starry.douban.http.error.ErrorModel;

import org.junit.Test;
import org.junit.runner.RunWith;

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
    private String url = "https://api.douban.com//v2/book/26926209";

    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.example.mylibrary.test", appContext.getPackageName());
    }

    @Test
    public void httpExecute() throws Exception {
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                Logger.e(TAG, "http start");
                BookDetail bookDetail = HttpManager.get()
                        .tag(this)
                        .url(url)
                        .build()
                        .execute(new CommonCallback<BookDetail>() {

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
}