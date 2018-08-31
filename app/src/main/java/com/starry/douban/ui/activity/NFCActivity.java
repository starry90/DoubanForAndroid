package com.starry.douban.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.TextView;

import com.starry.douban.R;
import com.starry.douban.base.BaseNFCActivity;

import butterknife.BindView;

/**
 * NFC识别
 *
 * @author Starry Jerry
 * @since 18-8-26
 */

public class NFCActivity extends BaseNFCActivity {

    private static final String EXTRA_NFC_RESULT = "nfcResult";

    @BindView(R.id.tv_nfc_content)
    TextView tv_nfc_content;

    @Override
    public int getLayoutResID() {
        return R.layout.activity_nfc;
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
        tv_nfc_content.setText(nfcResult);
    }

    @Override
    public void loadData() {

    }

    @Override
    protected void responseNFCResult(final String nfcResult) {
        if (TextUtils.isEmpty(nfcResult)) {
            return;
        }
        tv_nfc_content.setText(nfcResult);
    }

}
