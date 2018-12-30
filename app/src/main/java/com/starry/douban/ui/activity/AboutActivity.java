package com.starry.douban.ui.activity;

import android.transition.Slide;
import android.view.Gravity;
import android.widget.TextView;

import com.starry.douban.BuildConfig;
import com.starry.douban.R;
import com.starry.douban.base.BaseNFCActivity;

import butterknife.BindView;

/**
 * 关于
 *
 * @author Starry Jerry
 * @since 2018/5/25.
 */

public class AboutActivity extends BaseNFCActivity {

    @BindView(R.id.tv_about_app_name)
    TextView tvAboutAppName;
    @BindView(R.id.tv_version_name)
    TextView tvVersionName;
    @BindView(R.id.tv_about_build_time)
    TextView tvAboutBuildTime;
    @BindView(R.id.tv_about_git_branch)
    TextView tvAboutGitBranch;
    @BindView(R.id.tv_about_git_commit_time)
    TextView tvAboutGitCommitTime;

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

        String versionName = String.format("V %s", BuildConfig.VERSION_NAME);
        tvVersionName.setText(versionName);
        String buildTime = String.format("BuildTime %s", BuildConfig.BUILD_TIME);
        tvAboutBuildTime.setText(buildTime);

        String branch = String.format("GitBranch %s", BuildConfig.GIT_BRANCH);
        tvAboutGitBranch.setText(branch);
        String commitTime = String.format("CommitTime %s", BuildConfig.GIT_COMMIT_TIME);
        tvAboutGitCommitTime.setText(commitTime);

        Slide slide = new Slide(Gravity.TOP);
        slide.addTarget(tvAboutAppName);
        getWindow().setEnterTransition(slide);
    }

}
