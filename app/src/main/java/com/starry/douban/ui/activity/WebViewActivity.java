package com.starry.douban.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.starry.douban.R;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.databinding.ActivityWebViewBinding;
import com.starry.douban.util.AppUtil;
import com.starry.douban.util.PermissionUtils;
import com.starry.douban.util.ToastUtil;
import com.starry.douban.util.UiUtils;
import com.starry.douban.widget.CoolIndicatorLayout;
import com.starry.log.Logger;
import com.starry.overscroll.OverScrollWebView;
import com.starry.overscroll.style.WebHostOverScrollStyle;

import java.io.File;
import java.net.URL;

/**
 * WebView页面
 *
 * @author Starry Jerry
 * @since 2018/11/30.
 */
public class WebViewActivity extends BaseActivity<ActivityWebViewBinding> {

    /**
     * 打开照相机
     */
    public static final int FILE_CHOOSER_OPEN_CAMERA = 1;
    /**
     * 打开录音机
     */
    public static final int FILE_CHOOSER_OPEN_MICROPHONE = 2;
    /**
     * 打开摄像机
     */
    public static final int FILE_CHOOSER_OPEN_CAMCORDER = 3;
    /**
     * 打开通用类型
     */
    public static final int FILE_CHOOSER_OPEN_COMMON_TYPE = 4;

    /**
     * 上传文件到网站的操作句柄
     */
    private ValueCallback<Uri[]> uploadCallback;

    /**
     * 临时文件的Uri
     */
    private Uri tempUri;

    private AgentWeb mAgentWeb;

    public static final String EXTRA_URL = "url";

    public static void showActivity(Activity activity, String url) {
        Intent intent = new Intent(activity, WebViewActivity.class);
        intent.putExtra(EXTRA_URL, url);
        activity.startActivity(intent);
    }

    @Override
    public ActivityWebViewBinding getViewBinding(LayoutInflater layoutInflater) {
        return ActivityWebViewBinding.inflate(layoutInflater);
    }

    @Override
    public void initData() {
        initWebView();
    }

    private void initWebView() {
        OverScrollWebView webView = new OverScrollWebView(this);
        webView.getOverScrollDelegate().setOverScrollStyle(
                new WebHostOverScrollStyle(UiUtils.getColor(R.color.activity_bg), UiUtils.getColor(R.color.text_color_level_2), 12, 24) {
                    @Override
                    public String formatUrlHost(String url) {
                        return getHost(url);
                    }
                });

        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(viewBinding.llWebView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
                .setCustomIndicator(new CoolIndicatorLayout(this)) //自定义进度条
                .setWebView(webView)
                .setWebViewClient(mWebViewClient)
                .setWebChromeClient(mWebChromeClient)
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//打开其他应用时，弹窗咨询用户是否前往其他应用
                .createAgentWeb()
                .ready()
                .go(getIntent().getStringExtra(EXTRA_URL));
    }

    @Override
    public void loadData() {
    }

    /**
     * 设置WebViewClient，防止使用第三方浏览器打开页面
     */
    private final WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //do you  work
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            // 修复问题：在不忽略 SSL 错误的情况下，Android Web View 中握手失败
            // [ERROR:ssl_client_socket_impl.cc(980)] handshake failed; returned -1, SSL error code 1, net_error -201
            handler.proceed();
            Logger.e(error.toString());
        }

    };

    private final WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //do you work
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            setTitle(title);
        }

        // For Android 5.0+
        @Override
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP) //需要此注解 否则上传文件时无法响应
        public boolean onShowFileChooser(WebView webView,
                                         ValueCallback<Uri[]> filePathCallback,
                                         FileChooserParams fileChooserParams) {
            String mimeType = "*/*";
            boolean capture = false;
            if (Build.VERSION.SDK_INT >= 21) {
                String[] acceptTypes = fileChooserParams.getAcceptTypes();
                if (acceptTypes != null && acceptTypes.length > 0) {
                    mimeType = acceptTypes[0];
                }
                capture = fileChooserParams.isCaptureEnabled();
            }
            openFileSelector(filePathCallback, mimeType, capture);
            return true;
        }
    };

    /**
     * 该回调实现了对html中input标签(type=“file”)的解析,acceptType参数对应标签的accept属性(表示一种mimetype)，capture参数取值"true"/"false"标识是否直接打开媒体设备(对应input标签的capture属性含义)。
     * 目前实现了capture属性直接调用照相机(camera)、摄像机(camcorder)、录音机(microphone)的功能。
     * 参考文档:
     * https://segmentfault.com/a/1190000004084757
     * https://www.w3.org/TR/html-media-capture/#dom-htmlinputelement-capture
     * capture属性测试效果链接:https://mobilehtml5.org/ts/?id=23
     */
    private void openFileSelector(ValueCallback<Uri[]> valueCallback, String acceptType, boolean capture) {
        uploadCallback = valueCallback;
        try {
            File tmpFile = null;
            //如果mime为"image/*"则需要传递图片文件的临时路径，如果mime为"video/*"则需要传递视频文件的临时路径
            if ("image/*".equalsIgnoreCase(acceptType) || acceptType.startsWith("image/")) {
                tmpFile = AppUtil.getFileChooserTmpPicFile(this);
            } else if ("video/*".equalsIgnoreCase(acceptType)) {
                tmpFile = AppUtil.getFileChooserTmpVideoFile(this);
            }

            //直接打开类型检测,capture要有特定的accept属性匹配才有效
            if (capture) {
                if ("image/*".equalsIgnoreCase(acceptType) || acceptType.startsWith("image/")) {
                    //打开照相机
                    tempUri = AppUtil.openSystemCamera(this, FILE_CHOOSER_OPEN_CAMERA, tmpFile);
                } else if ("audio/*".equalsIgnoreCase(acceptType)) {
                    //调用录音机
                    AppUtil.openSystemMicrophone(this, FILE_CHOOSER_OPEN_MICROPHONE);
                } else if ("video/*".equalsIgnoreCase(acceptType)) {
                    //调用摄像机
                    tempUri = AppUtil.openSystemCamcorder(this, FILE_CHOOSER_OPEN_CAMCORDER, tmpFile);
                }
            } else {
                //"image/*"特别处理，如果是"image/*",则弹出照相机、相册两个选项。
                //基于acceptType发送Intent请求
                if ("image/*".equalsIgnoreCase(acceptType) || acceptType.startsWith("image/")) {
                    showFileUploadDialog(this, tmpFile);
                } else {
                    if (TextUtils.isEmpty(acceptType)) {
                        acceptType = "*/*";
                    }
                    AppUtil.requestGetFileIntent(this, acceptType, FILE_CHOOSER_OPEN_COMMON_TYPE);
                }

            }
        } catch (Throwable t) {
            t.printStackTrace();
            cancelUpload();
        }
    }

    private void showFileUploadDialog(Activity activity, final File tmpFile) {
        final AlertDialog dialog = new AlertDialog.Builder(activity).create();
        View contentView = LayoutInflater.from(activity).inflate(R.layout.dialog_select_photo, null);
        TextView galleryView = contentView.findViewById(R.id.tv_dialog_pick_photo);
        TextView cameraView = contentView.findViewById(R.id.tv_dialog_take_photo);
        galleryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtils.requestStorage(WebViewActivity.this, new PermissionUtils.PermissionCallback() {
                    @Override
                    public void success() {
                        AppUtil.openSystemGallery(WebViewActivity.this, FILE_CHOOSER_OPEN_COMMON_TYPE);
                    }

                    @Override
                    public void failure() {
                        ToastUtil.showToast("为保证您能正常使用该功能，需要前往设置授予设备权限。");
                        cancelUpload();
                    }
                });
                dialog.dismiss();
            }
        });
        cameraView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PermissionUtils.requestCamera(WebViewActivity.this, new PermissionUtils.PermissionCallback() {
                    @Override
                    public void success() {
                        tempUri = AppUtil.openSystemCamera(WebViewActivity.this, FILE_CHOOSER_OPEN_CAMERA, tmpFile);
                    }

                    @Override
                    public void failure() {
                        ToastUtil.showToast("为保证您能正常使用该功能，需要前往设置授予设备权限。");
                        cancelUpload();
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setContentView(contentView);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            //用户放弃了上传
            cancelUpload();
            return;
        }

        switch (requestCode) {
            case FILE_CHOOSER_OPEN_CAMERA: //照相机
            case FILE_CHOOSER_OPEN_CAMCORDER: //摄像机
                uploadFile(tempUri);
                break;
            case FILE_CHOOSER_OPEN_MICROPHONE: //录音机
            case FILE_CHOOSER_OPEN_COMMON_TYPE: //通用类型的文件上传
                Uri uriRecorder = data.getData();
                uploadFile(uriRecorder);
                break;

            default:
                break;
        }
        uploadCallback = null;
    }

    private void cancelUpload() {
        try {
            uploadCallback.onReceiveValue(null);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void uploadFile(Uri uri) {
        if (uri == null) {
            cancelUpload();
        }

        try {
            uploadCallback.onReceiveValue(new Uri[]{uri});
        } catch (Throwable t) {
            t.printStackTrace();
            cancelUpload();
        }
    }

    private String getHost(String url) {
        try {
            return "此网页由 " + new URL(url).getHost() + " 提供";
        } catch (Exception e) {
            // ignore
        }
        return "此网页由？？提供";
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mAgentWeb.handleKeyEvent(keyCode, event)) {
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();

    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroy();
    }

}