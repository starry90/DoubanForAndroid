package com.starry.douban.util;

import android.os.CountDownTimer;
import android.widget.TextView;

import java.lang.reflect.Field;

/**
 * 倒计时工具类
 *
 * @since 2018-06-22
 */
public class CountDownTimerImpl extends CountDownTimer {

    private TextView codeBtn;
    private boolean finish = true;

    private String format;

    private OnFinishListener listener;

    public interface OnFinishListener {

        void onFinish();
    }

    public void setOnFinishListener(OnFinishListener mOnFinishListener) {
        this.listener = mOnFinishListener;
    }

    /**
     * @param millisInFuture    倒计时长 单位：毫秒
     * @param countDownInterval 倒计间隔 单位：毫秒
     * @param codeBtn           TextView
     * @param format            显示格式
     */
    public CountDownTimerImpl(long millisInFuture, long countDownInterval, TextView codeBtn, String format) {
        super(millisInFuture, countDownInterval);
        this.codeBtn = codeBtn;
        this.format = format;
    }

    public CountDownTimerImpl(long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
    }

    @Override
    public void onTick(long l) {
        finish = false;
        //偏移1毫秒
        codeBtn.setText(String.format(format, (l - 1) / 1000));
    }

    @Override
    public void onFinish() {
        codeBtn.setText("重新获取");
        finish = true;

        if (listener != null) {
            listener.onFinish();
        }
    }

    public boolean isFinish() {
        return finish;
    }

    /**
     * 总的倒计时是变化的，需要反射修改父类mMillisInFuture的值
     *
     * @param millisInFuture Millis since epoch when alarm should stop.
     */
    public void setMillisInFuture(long millisInFuture) {
        try {
            Field mMillisInFuture = getClass().getSuperclass().getDeclaredField("mMillisInFuture");
            mMillisInFuture.setAccessible(true);
            mMillisInFuture.setLong(this, millisInFuture);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
