package com.starry.douban.ui.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.viewbinding.ViewBinding;

import com.starry.douban.base.BaseActivity;
import com.starry.douban.env.AppWrapper;
import com.starry.douban.util.UiUtils;

/**
 * 应用升级提示框
 *
 * @author Starry Jerry
 * @since 2018-10-27.
 */

public class UpdateDialogActivity extends BaseActivity {

    private static final String DIALOG_MESSAGE = "message";

    private AlertDialog dialog;

    @Override
    public ViewBinding getViewBinding(LayoutInflater layoutInflater) {
        return null;
    }

    @Override
    public void initData() {
        showDialog();
    }

    /**
     * 该Activity启动模式为singleTask，保证只存在一个实例<p>
     * 但有可能会被多次启动，再次启动时会调用该方法，保证每次拿到最新的提示内容
     *
     * @param intent New Intent
     */
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        showDialog();
    }

    /**
     * @param message 提示内容
     */
    public static void startPage(String message) {
        Intent intent = new Intent(AppWrapper.getContext(), UpdateDialogActivity.class);
        intent.putExtra(DIALOG_MESSAGE, message);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppWrapper.getContext().startActivity(intent);
    }

    private void showDialog() {
        String message = getIntent().getStringExtra(DIALOG_MESSAGE);
        if (dialog == null) {
            dialog = new AlertDialog.Builder(this)
                    .setTitle("更新")
                    .setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("下载安装", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UiUtils.dismissDialog(dialog);
                            startActivity(new Intent(UpdateDialogActivity.this, AppUpdateActivity.class));
                            finish();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UiUtils.dismissDialog(dialog);
                            finish();
                        }
                    })
                    .create();
        } else {
            dialog.setMessage(message);
        }
        dialog.show();
    }

}
