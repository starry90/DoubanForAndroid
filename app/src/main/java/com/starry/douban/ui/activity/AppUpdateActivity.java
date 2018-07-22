package com.starry.douban.ui.activity;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.starry.douban.R;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.base.BaseApp;
import com.starry.douban.constant.PreferencesName;
import com.starry.douban.http.HttpManager;
import com.starry.douban.http.callback.FileCallback;
import com.starry.douban.http.error.ErrorModel;
import com.starry.douban.util.AppUtil;
import com.starry.douban.util.CommonAnimator;
import com.starry.douban.util.FileUtils;
import com.starry.douban.util.SPUtil;

import java.io.File;

import butterknife.BindView;

/**
 * @author Starry Jerry
 * @since 2018/7/7.
 */

public class AppUpdateActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.iv_app_update)
    ImageView ivAppUpdate;
    @BindView(R.id.tv_app_update_version)
    TextView tvAppUpdateVersion;
    @BindView(R.id.tv_app_update_size)
    TextView tvAppUpdateSize;
    @BindView(R.id.pb_app_update)
    ProgressBar pbAppUpdate;
    @BindView(R.id.tv_app_update_message)
    TextView tvAppUpdateMessage;
    @BindView(R.id.tv_app_update_other)
    TextView tvAppUpdateOther;
    @BindView(R.id.tv_app_update_install)
    TextView tvAppUpdateInstall;

    private String dirPath;
    private String fileName;
    private String versionFull = String.format("%s.%s",
            SPUtil.getString(PreferencesName.APP_UPDATE_VERSION_NAME),
            String.valueOf(SPUtil.getInt(PreferencesName.APP_UPDATE_VERSION_CODE)));

    @Override
    public int getLayoutResID() {
        return R.layout.activity_app_update;
    }

    @Override
    public void initData() {
        setTitle("版本更新");

        tvAppUpdateVersion.setText(String.format("App %s", versionFull));
        tvAppUpdateMessage.setText(SPUtil.getString(PreferencesName.APP_UPDATE_MESSAGE));
        tvAppUpdateOther.setText(SPUtil.getString(PreferencesName.APP_UPDATE_OTHER));
    }

    @Override
    protected void onResume() {
        super.onResume();
        dirPath = BaseApp.getDownloadDir().getAbsolutePath();
        fileName = String.format("db-%s-release.apk", versionFull);
        File file = new File(dirPath, fileName);
        checkFile(file);
    }

    private void checkFile(File file) {
        if (file.exists()) {
            tvAppUpdateSize.setText("已下载");
            tvAppUpdateInstall.setText("安装应用");
            pbAppUpdate.setVisibility(View.GONE);
            pbAppUpdate.setProgress(0);
        } else {
            tvAppUpdateSize.setText(FileUtils.formatFileSize(SPUtil.getLong(PreferencesName.APP_UPDATE_CONTENT_LENGTH)));
            tvAppUpdateInstall.setText("下载并安装");
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        tvAppUpdateInstall.setOnClickListener(this);
        tvAppUpdateMessage.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_app_update_install:
                downloadFile();
                break;

            case R.id.tv_app_update_message:
                boolean visible = tvAppUpdateOther.getVisibility() == View.VISIBLE;
                if (visible) {
                    hide();
                } else {
                    show();
                }
                break;

        }
    }

    CommonAnimator hideAnimator;

    private void hide() {
        if (hideAnimator == null) {
            hideAnimator = new CommonAnimator.Builder(tvAppUpdateOther)
                    .build();
        }
        hideAnimator.foldWithDefault();
    }

    CommonAnimator showAnimator;

    private void show() {
        if (showAnimator == null) {
            showAnimator = new CommonAnimator.Builder(tvAppUpdateOther)
                    .build();
        }
        showAnimator.unfoldWithDefault();
    }

    private void downloadFile() {
        String url = SPUtil.getString(PreferencesName.APP_UPDATE_APK_URL);
        if (TextUtils.isEmpty(url)) {
            return;
        }

        File file = new File(dirPath, fileName);
        if (file.exists()) { //已下载，直接安装
            installApp(file);
            return;
        }

        pbAppUpdate.setVisibility(View.VISIBLE);
        HttpManager.get(url)
                .tag(this)
                .build()
                .enqueue(new FileCallback(dirPath, fileName) {
                    @Override
                    public void onSuccess(File response, Object... obj) {
                        checkFile(response);
                        installApp(response);
                    }

                    @Override
                    public void onFailure(ErrorModel errorModel) {
                    }

                    @Override
                    public void inProgress(float progress, long total) {
                        super.inProgress(progress, total);
                        pbAppUpdate.setProgress((int) (progress * 100));
                    }
                });
    }

    private void installApp(File file) {
        AppUtil.installApk(BaseApp.getContext(), file.getAbsolutePath());
    }
}
