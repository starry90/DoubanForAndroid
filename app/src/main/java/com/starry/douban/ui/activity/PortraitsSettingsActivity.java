package com.starry.douban.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;

import com.starry.cropiwa.image.CropIwaResultReceiver;
import com.starry.douban.R;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.constant.Common;
import com.starry.douban.databinding.ActivityPortraitsSettingsBinding;
import com.starry.douban.env.AppWrapper;
import com.starry.douban.image.ImageManager;
import com.starry.douban.util.FileProviderUtil;
import com.starry.douban.util.FileUtils;
import com.starry.douban.util.ToastUtil;
import com.starry.douban.util.UiUtils;

import java.io.File;

/**
 * 个人头像设置
 *
 * @author Starry Jerry
 * @since 2020/08/09.
 */

public class PortraitsSettingsActivity extends BaseActivity<ActivityPortraitsSettingsBinding> {

    public static final int REQUEST_CODE_PICK_PHOTO = 10001;
    public static final int REQUEST_CODE_TAKE_PHOTO = 10002;

    /**
     * 拍照用PNG格式，png是无损压缩，拍出来的照片更清晰
     */
    public static final String TAKE_PHOTO_NAME = "take_photo.png";

    private CropIwaResultReceiver cropResultReceiver;

    @Override
    public ActivityPortraitsSettingsBinding getViewBinding(LayoutInflater layoutInflater) {
        return ActivityPortraitsSettingsBinding.inflate(layoutInflater);
    }

    @Override
    public void initData() {
        setTitle("设置个人头像");

        registerCropReceiver();
        loadPortraits();
    }

    private void loadPortraits() {
        File cropFile = CropActivity.getCropFile();
        ImageManager.loadImage(viewBinding.ivSettingPortraits, Uri.fromFile(cropFile), CropActivity.getCropFileMimeType(), cropFile.lastModified());
    }

    @Override
    public void setListener() {
        viewBinding.ivSettingPortraits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSelectDialog();
            }
        });
    }

    /**
     * 注册裁剪广播
     */
    private void registerCropReceiver() {
        cropResultReceiver = new CropIwaResultReceiver();
        cropResultReceiver.register(this);
        cropResultReceiver.setListener(new CropIwaResultReceiver.Listener() {

            @Override
            public void onCropStart() {
                ToastUtil.showToast("裁剪开始");
            }

            @Override
            public void onCropSuccess(Uri croppedUri) {
                ToastUtil.showToast("裁剪成功");
                loadPortraits();
            }

            @Override
            public void onCropFailed(Throwable e) {
                ToastUtil.showToast("裁剪失败");
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(cropResultReceiver);
    }

    private AlertDialog selectDialog;

    /**
     * 拍照和相册获取图片选择框
     */
    private void showSelectDialog() {
        if (selectDialog == null) {
            View view = View.inflate(this, R.layout.dialog_select_photo, null);
            View tv_dialog_take_photo = view.findViewById(R.id.tv_dialog_take_photo);
            tv_dialog_take_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UiUtils.dismissDialog(selectDialog);
                    takePhoto();
                }
            });
            View tv_dialog_pick_photo = view.findViewById(R.id.tv_dialog_pick_photo);
            tv_dialog_pick_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    UiUtils.dismissDialog(selectDialog);
                    pickPhoto();
                }
            });
            selectDialog = new AlertDialog.Builder(this).setView(view)
                    .create();
        }
        selectDialog.show();
    }

    /**
     * 从相册选择图片
     */
    private void pickPhoto() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK);
        pickIntent.setType("image/*");
        startActivityForResult(pickIntent, REQUEST_CODE_PICK_PHOTO);
    }

    /**
     * 拍照
     */
    private void takePhoto() {
        File imageFile = getTakePhotoFile();
        FileUtils.deleteQuietly(imageFile);//先删除旧照片
        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri photoUri = FileProviderUtil.getUriForFile(this, Common.FILE_PROVIDER_AUTHORITY, imageFile, takeIntent);
        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(takeIntent, REQUEST_CODE_TAKE_PHOTO);
    }

    public File getTakePhotoFile() {
        return new File(AppWrapper.getPictureDir(), TAKE_PHOTO_NAME);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) return;

        if (requestCode == REQUEST_CODE_PICK_PHOTO) {
            CropActivity.showActivity(this, data.getData());
        } else if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
            CropActivity.showActivity(this, Uri.fromFile(getTakePhotoFile()));
        }
    }
}
