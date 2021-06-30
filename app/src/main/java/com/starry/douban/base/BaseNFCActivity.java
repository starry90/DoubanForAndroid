package com.starry.douban.base;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcB;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Bundle;
import android.text.TextUtils;
import android.viewbinding.ViewBinding;

import com.starry.douban.util.NfcUtils;
import com.starry.douban.util.ToastUtil;
import com.starry.log.Logger;

/**
 * NFC基类，其子类拥有直接识别NFC功能
 *
 * @author Starry Jerry
 * @since 18-8-26
 */
public abstract class BaseNFCActivity<T extends ViewBinding> extends BaseActivity<T> {

    private NfcAdapter mNfcAdapter;

    private IntentFilter[] mIntentFilter;

    private PendingIntent mPendingIntent;

    private String[][] mTechList;
    /**
     * NFC是否可用 默认为false不可用
     */
    private boolean nfcAvailable;

    private String nfcResult = "";

    protected abstract Class<?> getNFCClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initNFC();
    }

    /**
     * 放在super.onCreate（）之后
     * 保证其子类都能初始化NFC
     */
    private void initNFC() {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null) {
            Logger.i("NFC is not available");
        } else {
            nfcAvailable = true;
            // 一般设置为: SingleTop模式 ，并且锁死竖屏，以避免屏幕旋转Intent丢失
            Intent intent = new Intent(this, getNFCClass());

            final int REQUEST_CODE = 20180826;//不能和其它app的相同 （Private request code for the sender）
            final int FLAG = 0;
            mPendingIntent = PendingIntent.getActivity(this, REQUEST_CODE, intent, FLAG);

            IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
            IntentFilter tech = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);
            IntentFilter tag = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
            mIntentFilter = new IntentFilter[]{ndef, tech, tag};

            mTechList = new String[][]{
                    {IsoDep.class.getName(), NfcA.class.getName(), NfcB.class.getName()},
                    {NfcV.class.getName(), NfcF.class.getName(), Ndef.class.getName()}
            };
        }
    }

    protected void showUnSupport() {
        if (mNfcAdapter == null) {
            ToastUtil.showToast("该手机不支持NFC或未启用NFC");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mNfcAdapter != null) {
            mNfcAdapter.enableForegroundDispatch(this, mPendingIntent, mIntentFilter, mTechList);
            nfcResult = NfcUtils.processIntent(getIntent());
            if (!TextUtils.isEmpty(nfcResult)) {
                responseNFCResult(nfcResult);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mNfcAdapter != null) {
            mNfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (nfcAvailable) {
            nfcResult = NfcUtils.processIntent(intent);
            responseNFCResult(nfcResult);
        }
    }

    protected abstract void responseNFCResult(String nfcResult);

}
