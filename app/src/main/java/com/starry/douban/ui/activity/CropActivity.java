package com.starry.douban.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.MimeTypeMap;

import com.starry.cropiwa.AspectRatio;
import com.starry.cropiwa.config.CropIwaSaveConfig;
import com.starry.cropiwa.shape.CropIwaOvalShape;
import com.starry.douban.R;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.databinding.ActivityCropBinding;
import com.starry.douban.env.AppWrapper;
import com.starry.douban.util.FileUtils;

import java.io.File;

/**
 * 裁剪照片
 *
 * @author Starry Jerry
 * @since 2019-04-11.
 */
public class CropActivity extends BaseActivity<ActivityCropBinding> {

    public static final String EXTRA_IMAGE_URI = "extra_image_uri";

    public static final String CROP_FORMAT = "JPEG";

    /**
     * 裁剪的图片用jpeg格式，这样可以减小图片所占用的磁盘空间的大小，
     * 由于png是无损压缩，所以设置quality无效
     */
    public static final String CROP_PHOTO_NAME = "crop_photo." + CROP_FORMAT;


    private CropIwaSaveConfig.Builder saveConfig;

    public static void showActivity(Context context, Uri uri) {
        Intent intent = new Intent(context, CropActivity.class);
        intent.putExtra(EXTRA_IMAGE_URI, uri);
        context.startActivity(intent);
    }

    @Override
    public ActivityCropBinding getViewBinding(LayoutInflater layoutInflater) {
        return ActivityCropBinding.inflate(layoutInflater);
    }

    @Override
    public void initData() {
        Uri uri = getIntent().getParcelableExtra(EXTRA_IMAGE_URI);
        viewBinding.cropView.setImageUri(uri);

        initCropView();
    }

    private void initCropView() {
        viewBinding.cropView.configureOverlay().setCropShape(new CropIwaOvalShape(viewBinding.cropView.configureOverlay())).apply();//圆形切割
        viewBinding.cropView.configureOverlay().setShouldDrawGrid(false).apply(); //不显示网格线
//        viewBinding.cropView.configureImage().setScale(10 / 100f).apply(); //设置缩放比
        viewBinding.cropView.configureImage().setImageScaleEnabled(true); //允许缩放
        viewBinding.cropView.configureImage().setImageTranslationEnabled(true); //允许移动
        viewBinding.cropView.configureOverlay().setAspectRatio(new AspectRatio(1, 1)).apply(); //比例 1：1

        File cropFile = getCropFile();
        FileUtils.deleteQuietly(cropFile); //先删除旧照片
        saveConfig = new CropIwaSaveConfig.Builder(Uri.fromFile(cropFile)); //文件保存位置
        saveConfig.setCompressFormat(Bitmap.CompressFormat.valueOf(CROP_FORMAT)); //格式
        saveConfig.setQuality(30); //裁剪质量 0-100
    }

    /**
     * 获取裁剪后的文件
     */
    public static File getCropFile() {
        return new File(AppWrapper.getPictureDir(), CROP_PHOTO_NAME);
    }

    /**
     * 获取裁剪后的文件Mime Type
     *
     * @return Mime Type
     */
    public static String getCropFileMimeType() {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(CROP_FORMAT);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_crop, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_done) {
            viewBinding.cropView.crop(saveConfig.build());
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
