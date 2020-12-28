package com.starry.douban.ui.activity;

import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.starry.douban.adapter.BeautyAdapter;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.constant.Apis;
import com.starry.douban.databinding.ActivityBeautyBinding;
import com.starry.douban.model.BeautyModel;
import com.starry.douban.model.GankBaseModel;
import com.starry.douban.util.StringUtils;
import com.starry.douban.util.ToastUtil;
import com.starry.http.HttpManager;
import com.starry.http.callback.FinalCallback;
import com.starry.http.error.ErrorModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Starry Jerry
 * @since 2018/12/30.
 */

public class BeautyActivity extends BaseActivity<ActivityBeautyBinding> {

    private BeautyAdapter mAdapter;

    private final ArrayList<BeautyModel> beautyList = new ArrayList<>();

    private int pageNo = 1;

    private final int pageSize = 20;

    @Override
    public ActivityBeautyBinding getViewBinding(LayoutInflater layoutInflater) {
        return ActivityBeautyBinding.inflate(layoutInflater);
    }

    @Override
    public void initData() {
        setTitle("Beauty");

        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new BeautyAdapter(beautyList);
        mAdapter.addOnScrollListener(viewBinding.rvBeauty);
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                BeautyDetailActivity.showActivity(BeautyActivity.this, beautyList, position);
            }
        });

        viewBinding.rvBeauty.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        viewBinding.rvBeauty.setAdapter(mAdapter);
        viewBinding.rvBeauty.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                pageNo = 1;
                loadData();
            }

            @Override
            public void onLoadMore() {
                loadData();
            }
        });
        viewBinding.rvBeauty.setRefreshing(true);
    }

    @Override
    public void loadData() {
        String url = StringUtils.format(Apis.GANK_BEAUTY, pageSize, pageNo);
        HttpManager.get(url)
                .build()
                .enqueue(new FinalCallback<GankBaseModel<BeautyModel>>() {

                    @Override
                    public void onSuccess(GankBaseModel<BeautyModel> response, Object... obj) {
                        refreshList(response.getResults());
                    }

                    @Override
                    public void onFailure(ErrorModel errorModel) {
                        ToastUtil.showToast(errorModel.getMessage());
                    }

                    @Override
                    public void onAfter(boolean success) {
                        viewBinding.rvBeauty.refreshComplete();
                        viewBinding.rvBeauty.loadMoreComplete();
                        hideLoading(success);
                    }
                });
    }

    public void refreshList(List<BeautyModel> results) {
        //1、如果是第一页先清空数据 books不用做非空判断，不可能为空
        if (pageNo++ == 1) {
            beautyList.clear();
        }
        //2、拿到数据
        boolean hasData = results != null && !results.isEmpty();
        if (hasData) {
            beautyList.addAll(results);
        }
        //3、刷新RecyclerView
        mAdapter.notifyDataSetChanged();
        //5、如果没有数据了，禁用加载更多功能
        viewBinding.rvBeauty.setLoadingMoreEnabled(hasData);

        if (beautyList.isEmpty()) {
            showEmpty();
        }
    }

}
