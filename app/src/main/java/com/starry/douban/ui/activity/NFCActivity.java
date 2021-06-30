package com.starry.douban.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;

import com.starry.douban.base.BaseNFCActivity;
import com.starry.douban.databinding.ActivityNfcBinding;

/**
 * NFC识别
 *
 * @author Starry Jerry
 * @since 18-8-26
 */

public class NFCActivity extends BaseNFCActivity<ActivityNfcBinding> {

    private static final String EXTRA_NFC_RESULT = "nfcResult";

    @Override
    public ActivityNfcBinding getViewBinding(LayoutInflater layoutInflater) {
        return ActivityNfcBinding.inflate(layoutInflater);
    }

    @Override
    protected Class<?> getNFCClass() {
        return getClass();
    }

    public static void startPage(Context context, String nfcResult) {
        Intent intent = new Intent(context, NFCActivity.class);
        intent.putExtra(EXTRA_NFC_RESULT, nfcResult);
        context.startActivity(intent);
    }

    @Override
    public void initData() {
        setTitle("NFC识别");

        String nfcResult = getIntent().getStringExtra(EXTRA_NFC_RESULT);
        viewBinding.tvNfcContent.setText(nfcResult);
    }

    @Override
    public void loadData() {

    }

    @Override
    protected void responseNFCResult(final String nfcResult) {
        if (TextUtils.isEmpty(nfcResult)) {
            return;
        }
        viewBinding.tvNfcContent.setText(nfcResult);
    }

}
