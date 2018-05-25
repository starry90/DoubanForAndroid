package com.starry.douban.ui.activity;

import android.widget.TextView;

import com.starry.douban.BuildConfig;
import com.starry.douban.R;
import com.starry.douban.base.BaseActivity;

import java.util.Locale;

import butterknife.BindView;

/**
 * 关于
 *
 * @author Starry Jerry
 * @since 2018/5/25.
 */

public class AboutActivity extends BaseActivity {

    @BindView(R.id.tv_about_github)
    TextView tvAboutGithub;
    @BindView(R.id.tv_about_app_name)
    TextView tvAboutAppName;
    @BindView(R.id.tv_about_build)
    TextView tvAboutBuild;
    @BindView(R.id.tv_about_version_code)
    TextView tvAboutVersionCode;
    @BindView(R.id.tv_version_name)
    TextView tvVersionName;

    @Override
    public int getLayoutResID() {
        return R.layout.activity_about;
    }

    @Override
    public void initData() {
        setTitle("关于");

        String versionCode = "VersionCode %d";
        tvAboutVersionCode.setText(String.format(Locale.getDefault(), versionCode, BuildConfig.VERSION_CODE));
        String versionName = "VersionName %s";
        tvVersionName.setText(String.format(versionName, BuildConfig.VERSION_NAME));
        String buildTime = "BuildTime %s";
        tvAboutBuild.setText(String.format(buildTime, BuildConfig.BUILD_TIME));
    }

    @Override
    public void setListener() {

    }

    @Override
    public void loadData() {

    }
}
