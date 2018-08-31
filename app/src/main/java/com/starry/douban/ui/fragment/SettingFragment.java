package com.starry.douban.ui.fragment;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.starry.douban.BuildConfig;
import com.starry.douban.R;
import com.starry.douban.base.BaseFragment;
import com.starry.douban.constant.Apis;
import com.starry.http.HttpManager;
import com.starry.http.callback.StringCallback;
import com.starry.http.error.ErrorModel;
import com.starry.douban.model.AppUpdate;
import com.starry.douban.ui.activity.AboutActivity;
import com.starry.douban.util.ActivityAnimationUtils;
import com.starry.douban.util.ToastUtil;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SettingFragment extends BaseFragment {

    @BindView(R.id.tv_version)
    TextView tv_version;

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_setting;
    }

    @Override
    public void initData() {
        tv_version.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    public void onLazyLoadingData() {
        super.onLazyLoadingData();
        HttpManager.get(Apis.APP_UPDATE)
                .build()
                .enqueue(new StringCallback<AppUpdate>() {
                    @Override
                    public void onSuccess(AppUpdate response, Object... obj) {
                        response.savePreferences();
                    }

                    @Override
                    public void onFailure(ErrorModel errorModel) {
                        ToastUtil.showToast(errorModel.getMessage());
                    }
                });
    }

    @Override
    public void setListener() {
        tv_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAbout();
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
            ActivityAnimationUtils.transition(mActivity, intent, tv_version);
        }
    }
}
