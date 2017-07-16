package com.starry.douban.util;

import android.os.CountDownTimer;
import android.widget.TextView;

/**
 * @author lee
 * @version create time:2015年4月17日_下午4:13:27
 * @Description TODO
 */
public class CountDownUtils extends CountDownTimer{
	private TextView getAuthCodeBtn;

	public CountDownUtils(long millisInFuture, long countDownInterval, TextView getAuthCodeBtn) {
		super(millisInFuture, countDownInterval);
		this.getAuthCodeBtn = getAuthCodeBtn;
	}

	public CountDownUtils(long millisInFuture, long countDownInterval) {
		super(millisInFuture, countDownInterval);
	}

	@Override
	public void onFinish() {
		getAuthCodeBtn.setText("重新获取");
		getAuthCodeBtn.setClickable(true);
	}

	@Override
	public void onTick(long millisUntilFinished) {
		getAuthCodeBtn.setClickable(false);
		getAuthCodeBtn.setBackgroundColor(0xff686b7b);
		getAuthCodeBtn.setText(millisUntilFinished / 1000 + " 秒");
	}

	
	
}
