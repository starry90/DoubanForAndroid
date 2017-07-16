package com.starry.douban.util;

import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * @author lee
 * @version create time:2015/10/2917:55
 * @Email email
 * @Description ${TODO}
 */

public class CountTimerUtils extends CountDownTimer {

    private TextView getAuthCodeBtn;
    private boolean isCance = false;
    private boolean isSendEvent = false;

    public void setIsSendEvent(boolean isSendEvent){
        this.isSendEvent = isSendEvent;
    }

    public CountTimerUtils(long millisInFuture, long countDownInterval, TextView getAuthCodeBtn) {
        super(millisInFuture, countDownInterval);
        this.getAuthCodeBtn = getAuthCodeBtn;
    }

    public CountTimerUtils(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long l) {
        getAuthCodeBtn.setText("已提交更新，请" + (l / (60 * 1000)) + "分钟" + ((l/1000)%60) + "以后再试");
        isCance= true;
    }

    @Override
    public void onFinish() {
        getAuthCodeBtn.setText("");
        isCance = false;
        if(isSendEvent){
            //TODO do something
        }
    }

    public boolean isCance(){
        return isCance;
    }
}
