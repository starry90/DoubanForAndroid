package com.starry.douban.image;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.starry.douban.R;
import com.starry.douban.base.BaseApp;
import com.starry.rx.RxManager;
import com.starry.rx.RxTask;

import java.io.File;

import io.reactivex.functions.Consumer;

/**
 * 图片管理器
 * <li>Glide默认会缓存转换后的图片到SD卡（即第4种缓存策略），若要缓存原始图片，设置缓存策略为第1种或者第3种</li>
 * <li>Glide默认会开启内存缓存，如果需要关闭,请参考{@link #loadImageAndCache(ImageView, String, int)}  </li>
 * <li></li>Glide缓存策略：<li>
 * <li>1.{@linkplain DiskCacheStrategy#ALL} 既缓存原始图片也缓存转换后的图片</li>
 * <li>2{@linkplain DiskCacheStrategy#NONE} 不缓存图片</li>
 * <li>3.{@linkplain DiskCacheStrategy#SOURCE} 只缓存原始图片</li>
 * <li>4.{@linkplain DiskCacheStrategy#RESULT} 只缓存转换后的图片</li>
 *
 * @author Starry Jerry
 * @since 2016/12/11.
 */
public class ImageManager {

    //Glide.with(context).resumeRequests()和 Glide.with(context).pauseRequests()
    // 当列表在滑动的时候，调用pauseRequests()取消请求，滑动停止时，调用resumeRequests()恢复请求。这样是不是会好些呢？

    public static Context getContext() {
        return BaseApp.getContext();
    }

    /**
     * @param imageView
     * @param url
     * @param bgView
     */
    public static void getBitmap(final ImageView imageView, String url, final ImageView bgView) {
        Glide.with(getContext()).load(url).asBitmap().into(new SimpleTarget<Bitmap>(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL) {
            @SuppressLint("CheckResult")
            @Override
            public void onResourceReady(final Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
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
        Glide.with(getContext())
                .load(url)
                .error(R.drawable.image_bg_default)
                .placeholder(R.drawable.image_bg_default)
                .crossFade()
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
            File file = Glide.with(getContext())
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
     * 加载图片并缓存图片
     *
     * @param imageView 要设置图片的ImageView
     * @param url       图片URL
     * @param type      缓存策略请看{@link ImageManager} 说明  1.ALL, 2.NONE, 3.SOURCE,4.RESULT
     */
    public static void loadImageAndCache(ImageView imageView, String url, int type) {
        DiskCacheStrategy strategy = DiskCacheStrategy.RESULT;
        switch (type) {
            case 1:
                strategy = DiskCacheStrategy.ALL;
                break;

            case 2:
                strategy = DiskCacheStrategy.NONE;
                break;

            case 3:
                strategy = DiskCacheStrategy.SOURCE;
                break;

            case 4:
                strategy = DiskCacheStrategy.RESULT;
                break;
        }
        Glide.with(getContext())
                .load(url)
                .error(R.drawable.image_bg_default)
                .placeholder(R.drawable.image_bg_default)
                .diskCacheStrategy(strategy)
                .skipMemoryCache(false)//false为开启内存缓存，默认就是开启状态
                .crossFade()
                .into(imageView);
    }

    /**
     * 优先级加载图片
     *
     * @param url
     * @param imageView
     * @param type      1.IMMEDIATE, 2.HIGH, 3.NORMAL,4.LOW
     */
    public static void loadImageWithPriority(ImageView imageView, String url, int type) {
        Priority priority = Priority.NORMAL;
        switch (type) {
            case 1:
                priority = Priority.IMMEDIATE;
                break;

            case 2:
                priority = Priority.HIGH;
                break;

            case 3:
                priority = Priority.NORMAL;
                break;

            case 4:
                priority = Priority.LOW;
                break;
        }
        Glide.with(getContext()).load(url).priority(priority).into(imageView);
    }


    /**
     * 清除内存中的缓存 必须在UI线程中调用
     */
    public static void clearMemoryCache() {
        Glide.get(getContext()).clearMemory();
    }

    /**
     * 清除磁盘中的缓存，必须在子线程中调用，建议同时clearMemory()
     */
    public static void clearDiskCache() {
        Glide.get(getContext()).clearDiskCache();
    }

}
