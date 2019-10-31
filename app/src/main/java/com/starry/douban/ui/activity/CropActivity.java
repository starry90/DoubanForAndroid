package com.starry.douban.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuItem;

import com.starry.cropiwa.AspectRatio;
import com.starry.cropiwa.CropIwaView;
import com.starry.cropiwa.config.CropIwaSaveConfig;
import com.starry.cropiwa.shape.CropIwaOvalShape;
import com.starry.douban.R;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.env.AppWrapper;
import com.starry.douban.util.FileUtils;

import java.io.File;

/**
 * 裁剪照片
 *
 * @author Starry Jerry
 * @since 2019-04-11.
 */
public class CropActivity extends BaseActivity {

    public static final String EXTRA_IMAGE_URI = "extra_image_uri";

    public static final String CROP_FORMAT = "JPEG";

    /**
     * 裁剪的图片用jpeg格式，这样可以减小图片所占用的磁盘空间的大小，
     * 由于png是无损压缩，所以设置quality无效
     */
    public static final String CROP_PHOTO_NAME = "crop_photo." + CROP_FORMAT;


    CropIwaView cropView;

    private CropIwaSaveConfig.Builder saveConfig;

    public static void showActivity(Context context, Uri uri) {
        Intent intent = new Intent(context, CropActivity.class);
        intent.putExtra(EXTRA_IMAGE_URI, uri);
        context.startActivity(intent);
    }

    @Override
    public int getLayoutResID() {
        return R.layout.activity_crop;
    }

    @Override
    public void initData() {
        Uri uri = getIntent().getParcelableExtra(EXTRA_IMAGE_URI);
        cropView = findViewById(R.id.crop_view);
        cropView.setImageUri(uri);

        initCropView();
    }

    private void initCropView() {
        cropView.configureOverlay().setCropShape(new CropIwaOvalShape(cropView.configureOverlay())).apply();//圆形切割
        cropView.configureOverlay().setShouldDrawGrid(false).apply(); //不显示网格线
//        cropView.configureImage().setScale(10 / 100f).apply(); //设置缩放比
        cropView.configureImage().setImageScaleEnabled(true); //允许缩放
        cropView.configureImage().setImageTranslationEnabled(true); //允许移动
        cropView.configureOverlay().setAspectRatio(new AspectRatio(1, 1)).apply(); //比例 1：1

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_crop, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_done) {
            cropView.crop(saveConfig.build());
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
