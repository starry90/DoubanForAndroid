package com.starry.douban.ui.fragment;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;

import com.starry.douban.BuildConfig;
import com.starry.douban.R;
import com.starry.douban.base.BaseFragment;
import com.starry.douban.constant.Apis;
import com.starry.douban.constant.PreferencesName;
import com.starry.douban.databinding.FragmentSettingBinding;
import com.starry.douban.image.ImageManager;
import com.starry.douban.service.WorkService;
import com.starry.douban.ui.activity.AboutActivity;
import com.starry.douban.ui.activity.AppUpdateActivity;
import com.starry.douban.ui.activity.BeautyActivity;
import com.starry.douban.ui.activity.PortraitsSettingsActivity;
import com.starry.douban.ui.activity.VideoListActivity;
import com.starry.douban.ui.activity.WebViewActivity;
import com.starry.douban.util.ActivityAnimationUtils;
import com.starry.douban.util.AppOpsManagerUtils;
import com.starry.douban.util.SPUtil;
import com.starry.douban.util.SoftKeyBoardHelper;
import com.starry.douban.util.ToastUtil;
import com.starry.douban.util.UiUtils;
import com.starry.log.Logger;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SettingFragment extends BaseFragment<FragmentSettingBinding> implements View.OnClickListener {

    private boolean latestVersion;

    @Override
    public FragmentSettingBinding getViewBinding(LayoutInflater layoutInflater) {
        return FragmentSettingBinding.inflate(layoutInflater);
    }

    @Override
    public void initData() {
        SoftKeyBoardHelper.assistActivity(getActivity());
        setTitle("设置");
        ImageManager.loadImage(viewBinding.ivSettingTop, R.drawable.ic_wallhaven);

        latestVersion = SPUtil.getInt(PreferencesName.APP_UPDATE_VERSION_CODE) <= BuildConfig.VERSION_CODE;
        WorkService.startCheckAppVersion();
        if (TextUtils.isEmpty(Apis.VIDEO_HOST)) {
            viewBinding.tvSettingVideo.setVisibility(View.GONE);
            viewBinding.viewVideoDivider.setVisibility(View.GONE);
        }
    }

    @Override
    public void setListener() {
        viewBinding.tvSettingPortraits.setOnClickListener(this);
        viewBinding.tvSettingBeauty.setOnClickListener(this);
        viewBinding.tvSettingAbout.setOnClickListener(this);
        viewBinding.tvSettingVersionUpdate.setOnClickListener(this);
        viewBinding.tvSettingVideo.setOnClickListener(this);
        viewBinding.tvSettingGithub.setOnClickListener(this);
        viewBinding.tvSettingCheckPermission.setOnClickListener(this);
        viewBinding.switchSettingBlackWhite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                UiUtils.switchBlackWhiteStyle(mActivity, isChecked);
            }
        });
    }

    private long[] mClicks = new long[5];

    /**
     * 2s内连续敲打5次打开关于页面
     */
    private void startAbout() {
        System.arraycopy(mClicks, 1, mClicks, 0, mClicks.length - 1);
        mClicks[mClicks.length - 1] = SystemClock.uptimeMillis();
        if (mClicks[0] >= (SystemClock.uptimeMillis() - 2000)) {
            Intent intent = new Intent(mActivity, AboutActivity.class);
            ActivityAnimationUtils.transition(mActivity, intent, viewBinding.tvSettingAbout);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_setting_portraits:
                startActivity(new Intent(mActivity, PortraitsSettingsActivity.class));
                break;

            case R.id.tv_setting_beauty:
                startActivity(new Intent(mActivity, BeautyActivity.class));
                break;

            case R.id.tv_setting_about:
                startAbout();
                break;

            case R.id.tv_setting_version_update:
                if (!latestVersion) {
                    startActivity(new Intent(mActivity, AppUpdateActivity.class));
                } else {
                    ToastUtil.showToast("当前已是最新版本！");
                }
                break;

            case R.id.tv_setting_video:
                VideoListActivity.showActivity(mActivity);
                break;

            case R.id.tv_setting_github:
                WebViewActivity.showActivity(mActivity, Apis.GITHUB_AUTHOR_HOME);
                break;

            case R.id.tv_setting_check_permission:
                boolean b = AppOpsManagerUtils.checkCallPhonePermission(getActivity());
                Logger.e("权限电话检查：" + b);
                break;
        }
    }

}
