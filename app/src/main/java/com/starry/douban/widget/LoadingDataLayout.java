package com.starry.douban.widget;

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.starry.douban.R;
import com.starry.douban.util.CommonAnimator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Loading Data View
 *
 * @author Starry Jerry
 */
public class LoadingDataLayout extends RelativeLayout {


    /**
     * 加载中
     */
    public static final int STATUS_LOADING = 0;
    /**
     * 正常加载
     */
    public static final int STATUS_SUCCESS = 1;

    /**
     * 加载失败
     */
    public static final int STATUS_ERROR = 2;

    /**
     * 加载成功，数据为空
     */
    public static final int STATUS_EMPTY = 3;


    private int mStatus = STATUS_LOADING;
    /**
     * 重试监听器
     */
    private OnRetryListener mOnRetryListener;

    /**
     * 网络请求各种状态显示容器
     */
    private View container;
    /**
     * 网络加载中
     */
    @BindView(R.id.view_network_loading)
    View view_network_loading;
    /**
     * 加载成功，数据为空
     */
    @BindView(R.id.view_empty_data)
    View view_empty_data;
    /**
     * 加载失败
     */
    @BindView(R.id.view_network_error)
    View view_network_error;
    /**
     * 网络错误提示信息
     */
    @BindView(R.id.tv_network_error)
    TextView tv_network_error;
    /**
     * 内容为空提示信息
     */
    @BindView(R.id.tv_empty_data)
    TextView tv_empty_data;

    public LoadingDataLayout(Context context) {
        this(context, null);
    }

    public LoadingDataLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化
     */
    private void init(Context context) {
        container = View.inflate(context, R.layout.view_network_status, this);
        ButterKnife.bind(this);

        view_network_error.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStatus(STATUS_LOADING);
                if (mOnRetryListener != null) {
                    mOnRetryListener.onRetry();
                }
            }
        });
    }

    /**
     * 设置状态
     *
     * @param status
     */
    public void setStatus(@Status int status) {
        mStatus = status;
        switchView();
    }

    public boolean isSuccess() {
        return mStatus == STATUS_SUCCESS;
    }

    /**
     * 获取状态
     *
     * @return 状态
     */
    public int getNetworkStatus() {
        return mStatus;
    }

    /**
     * 设置异常消息
     *
     * @param msg 显示消息
     */
    public void setErrorMessage(String msg) {
        tv_network_error.setText(msg);
    }

    /**
     * 设置内容为空提示信息
     *
     * @param msg 显示消息
     */
    public void setEmptyMessage(String msg) {
        tv_empty_data.setText(msg);
    }

    /**
     * 切换视图
     */
    private void switchView() {
        container.setVisibility(View.VISIBLE);
        view_network_loading.setVisibility(View.GONE);
        view_network_error.setVisibility(View.GONE);
        view_empty_data.setVisibility(View.GONE);

        switch (mStatus) {
            case STATUS_LOADING:
                view_network_loading.setVisibility(View.VISIBLE);
                break;

            case STATUS_SUCCESS:
                new CommonAnimator.Builder(container)
                        .alphaValues(1, 0)
                        .duration(800)
                        .listener(new CommonAnimator.Listener() {
                            @Override
                            public void onAnimationEnd() {
                                container.setVisibility(GONE);
                            }
                        })
                        .build()
                        .foldWithAnimatorSet();
                break;

            case STATUS_EMPTY:
                view_empty_data.setVisibility(View.VISIBLE);
                break;

            case STATUS_ERROR:
                view_network_error.setVisibility(View.VISIBLE);
                break;

        }
    }

    /**
     * 设置重试监听器
     *
     * @param retryListener 监听器
     */
    public void setRetryListener(OnRetryListener retryListener) {
        this.mOnRetryListener = retryListener;
    }

    /**
     * 点击重试监听器
     */
    public interface OnRetryListener {
        void onRetry();
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_LOADING, STATUS_ERROR, STATUS_EMPTY})
    public @interface Status {
    }
}