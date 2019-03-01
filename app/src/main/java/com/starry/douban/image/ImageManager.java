package com.starry.douban.image;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.starry.douban.R;
import com.starry.douban.base.BaseApp;
import com.starry.log.Logger;
import com.starry.rx.RxManager;
import com.starry.rx.RxTask;

import java.io.File;

import io.reactivex.functions.Consumer;

/**
 * 图片管理器
 * <li>Glide默认会缓存转换后的图片到SD卡</li>
 *
 * @author Starry Jerry
 * @since 2016/12/11.
 */
public class ImageManager {

    public static Context getContext() {
        return BaseApp.getContext();
    }

    private static final RequestOptions requestOptions = new RequestOptions()
            .error(R.drawable.image_bg_default)
            .placeholder(R.drawable.image_bg_default);

    private static final RequestListener requestListener = new RequestListener<Drawable>() {
        @Override
        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
            Logger.e("Glide exception:" + e);
            return false;
        }

        @Override
        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
            return false;
        }
    };

    /**
     * @param imageView
     * @param url
     * @param bgView
     */
    public static void getBitmap(final ImageView imageView, String url, final ImageView bgView) {
        GlideApp.with(getContext())
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull final Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        imageView.setImageBitmap(resource);
                        RxManager.createIO(new RxTask<Bitmap>() {
                            @Override
                            public Bitmap run() {
                                //该方法耗时2秒，放在主线程会卡顿
                                return FastBlurUtil.doBlur(resource, 20, false);
                            }
                        }).subscribe(new Consumer<Bitmap>() {
                            @Override
                            public void accept(Bitmap blurBitmap) {
                                bgView.setImageBitmap(blurBitmap);

                            }
                        });
                    }
                });
    }

    /**
     * 加载图片
     *
     * @param imageView 要设置图片的ImageView
     * @param url       图片URL
     */
    public static void loadImage(ImageView imageView, String url) {
        GlideApp.with(getContext())
                .load(url)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade())
                .listener(requestListener)
                .into(imageView);
    }

    /**
     * 保存图片至系统相册
     *
     * @param url 图片url
     * @return 保存成功返回true
     */
    public static boolean downloadImage(String url) {
        try {
            File file = GlideApp.with(getContext())
                    .load(url)
                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
            Context context = getContext();
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), file.getName(), "beauty");
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 清除内存中的缓存 必须在UI线程中调用
     */
    public static void clearMemoryCache() {
        GlideApp.get(getContext()).clearMemory();
    }

    /**
     * 清除磁盘中的缓存，必须在子线程中调用，建议同时clearMemory()
     */
    public static void clearDiskCache() {
        GlideApp.get(getContext()).clearDiskCache();
    }

}
