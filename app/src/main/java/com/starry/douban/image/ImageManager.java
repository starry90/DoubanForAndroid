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

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory;
import com.bumptech.glide.request.transition.Transition;
import com.bumptech.glide.signature.MediaStoreSignature;
import com.starry.douban.R;
import com.starry.douban.env.AppWrapper;
import com.starry.douban.image.okhttp.GlideApp;
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
        return AppWrapper.getContext();
    }

    private static final RequestOptions requestOptions = new RequestOptions()
            .error(R.drawable.image_bg_default)
            .placeholder(R.drawable.image_bg_default);

    /**
     * https://muyangmin.github.io/glide-docs-cn/doc/transitions.html
     * Glide 的默认交叉淡入(cross fade)效果使用了 TransitionDrawable 。它提供两种动画模式，由 setCrossFadeEnabled() 控制。
     * <p>
     * 当交叉淡入被禁用时，正在过渡的图片会在原先显示的图像上面淡入。当交叉淡入被启用时，原先显示的图片会从不透明过渡到透明，而正在过渡的图片则会从透明变为不透明。
     * <p>
     * 在 Glide 中，我们默认禁用了交叉淡入，这样通常看起来要好看一些。实际的交叉淡入，如上所述对两个图片同时改变 alpha 值，通常会在过渡的中间造成一个短暂的白色闪屏，这个时候两个图片都是部分不透明的。
     * 当占位符比实际加载的图片要大，或者图片部分为透明时，禁用交叉淡入会导致动画完成后占位符在图片后面仍然可见。
     * 如果你在加载透明图片时使用了占位符，你可以启用交叉淡入(setCrossFadeEnabled(true))。
     */
    private static final DrawableCrossFadeFactory crossFadeFactory = new DrawableCrossFadeFactory
            .Builder(300)
            .setCrossFadeEnabled(true)
            .build();

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
                .transition(DrawableTransitionOptions.withCrossFade(crossFadeFactory))
                .listener(requestListener)
                .into(imageView);
    }

    /**
     * 加载图片
     *
     * @param imageView 要设置图片的ImageView
     * @param uri       图片URI
     */
    public static void loadImage(ImageView imageView, Uri uri) {
        GlideApp.with(getContext())
                .load(uri)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade(crossFadeFactory))
                .listener(requestListener)
                .into(imageView);
    }

    /**
     * 加载图片,
     * Glide官方api中没有提供删除图片缓存的函数，
     * 修复图片被修改后，文件名称未变，加载图片仍显示缓存的问题，使用signature方法
     *
     * @param imageView    要设置图片的ImageView
     * @param uri          图片URI
     * @param mimeType     文件mime Type
     * @param dateModified 文件最后修改时间
     */
    public static void loadImage(ImageView imageView, Uri uri, String mimeType, long dateModified) {
        MediaStoreSignature mediaStoreSignature = new MediaStoreSignature(mimeType, dateModified, 0);
        GlideApp.with(getContext())
                .load(uri)
                .signature(mediaStoreSignature)
                .apply(requestOptions)
                .transition(DrawableTransitionOptions.withCrossFade(crossFadeFactory))
                .listener(requestListener)
                .into(imageView);
    }

    /**
     * 加载圆形图片
     * <p>
     * 不能用动画效果
     *
     * @param imageView 要设置图片的ImageView
     * @param url       图片Url
     */
    public static void loadCircleImage(ImageView imageView, String url) {
        GlideApp.with(getContext())
                .load(url)
                .apply(requestOptions)
//                .transition(DrawableTransitionOptions.withCrossFade()) //加载圆形图片不能用动画效果
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
