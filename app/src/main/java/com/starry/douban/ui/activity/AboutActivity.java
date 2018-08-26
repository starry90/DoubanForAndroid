package com.starry.douban.ui.activity;

import android.content.Intent;
import android.transition.Slide;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.starry.douban.BuildConfig;
import com.starry.douban.R;
import com.starry.douban.base.BaseNFCActivity;
import com.starry.douban.constant.PreferencesName;
import com.starry.douban.util.SPUtil;

import butterknife.BindView;

/**
 * 关于
 *
 * @author Starry Jerry
 * @since 2018/5/25.
 */

public class AboutActivity extends BaseNFCActivity implements View.OnClickListener {

    @BindView(R.id.tv_about_app_name)
    TextView tvAboutAppName;
    @BindView(R.id.tv_about_build)
    TextView tvAboutBuild;
    @BindView(R.id.tv_version_name)
    TextView tvVersionName;
    @BindView(R.id.tv_about_version_update)
    TextView tvAboutVersionUpdate;
    @BindView(R.id.tv_about_update_hint)
    TextView tvAboutUpdateHint;
    @BindView(R.id.tv_about_github)
    TextView tvAboutGithub;

    private boolean latestVersion;

    @Override
    protected Class<?> getNFCClass() {
        return getClass();
    }

    @Override
    protected void responseNFCResult(String nfcResult) {
        NFCActivity.startPage(this, nfcResult);
    }

    @Override
    public int getLayoutResID() {
        return R.layout.activity_about;
    }

    @Override
    public void initData() {
        setTitle("关于");

        latestVersion = SPUtil.getInt(PreferencesName.APP_UPDATE_VERSION_CODE) <= BuildConfig.VERSION_CODE;
        if (latestVersion) {
            tvAboutUpdateHint.setVisibility(View.VISIBLE);
        }

        String versionName = "V %s";
        tvVersionName.setText(String.format(versionName, BuildConfig.VERSION_NAME));
        String buildTime = "BuildTime %s";
        tvAboutBuild.setText(String.format(buildTime, BuildConfig.BUILD_TIME));

        Slide slide = new Slide(Gravity.TOP);
        slide.addTarget(tvAboutGithub);
        slide.addTarget(tvAboutAppName);
        slide.addTarget(tvAboutBuild);
        getWindow().setEnterTransition(slide);
    }

    @Override
    public void setListener() {
        super.setListener();
        tvAboutVersionUpdate.setOnClickListener(this);
        tvAboutGithub.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_about_version_update:
                if (!latestVersion) {
                    startActivity(new Intent(this, AppUpdateActivity.class));
                }
                break;

            case R.id.tv_about_github:

                break;

        }
    }
}
