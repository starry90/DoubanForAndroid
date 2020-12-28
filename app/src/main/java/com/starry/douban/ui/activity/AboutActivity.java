package com.starry.douban.ui.activity;

import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;

import com.starry.douban.BuildConfig;
import com.starry.douban.base.BaseNFCActivity;
import com.starry.douban.databinding.ActivityAboutBinding;

/**
 * 关于
 *
 * @author Starry Jerry
 * @since 2018/5/25.
 */

public class AboutActivity extends BaseNFCActivity<ActivityAboutBinding> {

    @Override
    protected Class<?> getNFCClass() {
        return getClass();
    }

    @Override
    protected void responseNFCResult(String nfcResult) {
        NFCActivity.startPage(this, nfcResult);
    }

    @Override
    public ActivityAboutBinding getViewBinding(LayoutInflater layoutInflater) {
        return ActivityAboutBinding.inflate(layoutInflater);
    }

    @Override
    public void initData() {
        setTitle("关于");

        String versionName = String.format("V %s", BuildConfig.VERSION_NAME);
        viewBinding.tvVersionName.setText(versionName);
        String buildTime = String.format("BuildTime %s", BuildConfig.BUILD_TIME);
        viewBinding.tvAboutBuildTime.setText(buildTime);

        String branch = String.format("GitBranch %s", BuildConfig.GIT_BRANCH);
        viewBinding.tvAboutGitBranch.setText(branch);
        String commitTime = String.format("CommitTime %s", BuildConfig.GIT_COMMIT_TIME);
        viewBinding.tvAboutGitCommitTime.setText(commitTime);

        Slide slide = new Slide(Gravity.TOP);
        slide.addTarget(viewBinding.tvAboutAppName);
        getWindow().setEnterTransition(slide);
    }

}
