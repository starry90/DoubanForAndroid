package com.starry.douban.image.okhttp;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;
import com.starry.http.utils.HttpsUtils;

import java.io.InputStream;

import okhttp3.OkHttpClient;

/**
 * Ensures that Glide's generated API is created
 *
 * @author Starry Jerry
 * @see GlideApp
 * @since 19-3-1.
 */
@GlideModule
public class DBAppGlideModule extends AppGlideModule {
    // Intentionally empty.

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        // 注意这里用现有的Client实例传入即可
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(getOkHttpClient()));
    }

    /**
     * 初始化OkHttpClient,信任所有证书
     */
    private OkHttpClient getOkHttpClient() {
        return new OkHttpClient.Builder().
                sslSocketFactory(HttpsUtils.allowAllSSLSocketFactory(), HttpsUtils.allowAllTrustManager()).
                hostnameVerifier(HttpsUtils.allowAllHostnameVerifier())
                .build();
    }
}