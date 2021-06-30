package com.starry.douban.ui.activity;

import android.view.LayoutInflater;
import android.view.View;

import com.starry.douban.R;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.constant.PreferencesName;
import com.starry.douban.databinding.ActivityAppUpdateBinding;
import com.starry.douban.env.AppWrapper;
import com.starry.douban.event.AppUpdateEvent;
import com.starry.douban.service.WorkService;
import com.starry.douban.util.CommonAnimator;
import com.starry.douban.util.CommonUtils;
import com.starry.douban.util.SPUtil;
import com.starry.rx.RxBus;

import java.io.File;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * @author Starry Jerry
 * @since 2018/7/7.
 */

public class AppUpdateActivity extends BaseActivity<ActivityAppUpdateBinding> implements View.OnClickListener {

    private String dirPath;
    private String fileName;
    private String versionFull = String.format("%s.%s",
            SPUtil.getString(PreferencesName.APP_UPDATE_VERSION_NAME),
            String.valueOf(SPUtil.getInt(PreferencesName.APP_UPDATE_VERSION_CODE)));

    @Override
    public ActivityAppUpdateBinding getViewBinding(LayoutInflater layoutInflater) {
        return ActivityAppUpdateBinding.inflate(layoutInflater);
    }

    @Override
    public void initData() {
        setTitle("版本更新");

        viewBinding.tvAppUpdateVersion.setText(String.format("App %s", versionFull));
        viewBinding.tvAppUpdateMessage.setText(SPUtil.getString(PreferencesName.APP_UPDATE_MESSAGE));
        viewBinding.tvAppUpdateOther.setText(SPUtil.getString(PreferencesName.APP_UPDATE_OTHER));
    }

    @Override
    protected void onResume() {
        super.onResume();
        dirPath = AppWrapper.getDownloadDir().getAbsolutePath();
        fileName = String.format("db-%s-release.apk", versionFull);
        File file = new File(dirPath, fileName);
        checkFile(file);
    }

    private void checkFile(File file) {
        if (file.exists()) {
            viewBinding.tvAppUpdateSize.setText("已下载");
            viewBinding.tvAppUpdateInstall.setText("安装应用");
            viewBinding.pbAppUpdate.setVisibility(View.GONE);
            viewBinding.pbAppUpdate.setProgress(0);
        } else {
            viewBinding.tvAppUpdateSize.setText(CommonUtils.formatFileSize(SPUtil.getLong(PreferencesName.APP_UPDATE_CONTENT_LENGTH)));
            viewBinding.tvAppUpdateInstall.setText("下载并安装");
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        viewBinding.tvAppUpdateInstall.setOnClickListener(this);
        viewBinding.tvAppUpdateMessage.setOnClickListener(this);

        RxBus.getDefault().register(AppUpdateEvent.class, new Consumer<AppUpdateEvent>() {
            @Override
            public void accept(AppUpdateEvent appUpdateEvent) throws Exception {
                if (appUpdateEvent.type == 1) {
                    viewBinding.pbAppUpdate.setVisibility(View.VISIBLE);
                } else if (appUpdateEvent.type == 2) {
                    viewBinding.pbAppUpdate.setProgress((int) (appUpdateEvent.progress * 100));
                }
            }
        }, AndroidSchedulers.mainThread());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_app_update_install:
                WorkService.startDownloadApp(dirPath, fileName);
                break;

            case R.id.tv_app_update_message:
                boolean visible = viewBinding.tvAppUpdateOther.getVisibility() == View.VISIBLE;
                if (visible) {
                    hide();
                } else {
                    show();
                }
                break;

        }
    }

    CommonAnimator.Builder hideAnimator;

    private void hide() {
        if (hideAnimator == null) {
            hideAnimator = new CommonAnimator.Builder(viewBinding.tvAppUpdateOther).defaultHideAnimator();
        }
        hideAnimator.hide();
    }

    CommonAnimator.Builder showAnimator;

    private void show() {
        if (showAnimator == null) {
            showAnimator = new CommonAnimator.Builder(viewBinding.tvAppUpdateOther).defaultShowAnimator();
        }
        showAnimator.show();
    }

}
