package com.starry.douban.ui.fragment;

import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.starry.douban.BuildConfig;
import com.starry.douban.R;
import com.starry.douban.base.BaseLazyFragment;
import com.starry.douban.ui.activity.AboutActivity;

import butterknife.BindView;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class SettingFragment extends BaseLazyFragment {

    @BindView(R.id.tv_blank_version)
    TextView tv_blank_version;

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_setting;
    }

    @Override
    public void initData() {
        tv_blank_version.setText(BuildConfig.VERSION_NAME);
    }

    @Override
    public void setListener() {
        tv_blank_version.setOnClickListener(new View.OnClickListener() {
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
            startActivity(new Intent(mActivity, AboutActivity.class));
        }
    }
}
