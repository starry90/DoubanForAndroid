package com.starry.douban.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.starry.douban.R;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.databinding.ActivityWebViewBinding;
import com.starry.douban.util.UiUtils;
import com.starry.douban.widget.CoolIndicatorLayout;
import com.starry.overscroll.OverScrollWebView;
import com.starry.overscroll.style.WebHostOverScrollStyle;

import java.net.URL;

/**
 * WebView页面
 *
 * @author Starry Jerry
 * @since 2018/11/30.
 */
public class WebViewActivity extends BaseActivity<ActivityWebViewBinding> {

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
    private WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            //do you  work
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

    };

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //do you work
        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            setTitle(title);
        }
    };

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