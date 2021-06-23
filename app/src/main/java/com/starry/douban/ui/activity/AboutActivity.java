package com.starry.douban.ui.activity;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;

import com.starry.douban.BuildConfig;
import com.starry.douban.R;
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
    protected boolean isDarkTextWhiteBgStatusBar() {
        if (tvToolbarTitle != null) {
            //黑色文字
            tvToolbarTitle.setTextColor(getResources().getColor(R.color.text_color_level_1));
        }
        return true;
    }

    @Override
    protected int getToolbarNavigationIconRes() {
        return R.drawable.baseline_arrow_back_black_24; //黑色返回键
    }

    @Override
    protected Drawable getToolbarBackground() {
        return new ColorDrawable(0x0FFFFFFFF); //白色背景
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
