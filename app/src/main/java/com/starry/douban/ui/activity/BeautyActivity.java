package com.starry.douban.ui.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.starry.douban.adapter.BeautyAdapter;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.databinding.ActivityBeautyBinding;
import com.starry.douban.jetpack.httpstatuslivedata.HttpStatusData;
import com.starry.douban.jetpack.viewmodel.BeautyViewModel;
import com.starry.douban.model.BeautyModel;
import com.starry.douban.model.GankBaseModel;
import com.starry.douban.model.PhotoModel;
import com.starry.douban.util.DensityUtil;
import com.starry.douban.util.ToastUtil;
import com.starry.douban.widget.recyclerview.GridItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Starry Jerry
 * @since 2018/12/30.
 */

public class BeautyActivity extends BaseActivity<ActivityBeautyBinding> {

    private BeautyAdapter mAdapter;

    private int pageNo = 1;

    private final int pageSize = 20;

    private BeautyViewModel beautyViewModel;

    @Override
    public ActivityBeautyBinding getViewBinding(LayoutInflater layoutInflater) {
        return ActivityBeautyBinding.inflate(layoutInflater);
    }

    @Override
    public void initData() {
        setTitle("Beauty");

        beautyViewModel = ViewModelProviders.of(this).get(BeautyViewModel.class);
        beautyViewModel.beautyLiveData.observe(this, new Observer<HttpStatusData<GankBaseModel<BeautyModel>>>() {
            @Override
            public void onChanged(@Nullable HttpStatusData<GankBaseModel<BeautyModel>> gankBaseModelHttpStatusData) {
                viewBinding.rvBeauty.refreshComplete();
                viewBinding.rvBeauty.loadMoreComplete();
                switch (gankBaseModelHttpStatusData.getStatus()) {
                    case DATA_ERROR:
                        hideLoading(false);
                        ToastUtil.showToast(gankBaseModelHttpStatusData.getErrorModel().getMessage());
                        break;

                    case DATA_SUCCESS:
                        hideLoading(true);
                        refreshList(gankBaseModelHttpStatusData.getData().getResults());
                        break;
                }
            }
        });

        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new BeautyAdapter();
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                PhotoDetailActivity.showActivity(BeautyActivity.this, (ArrayList<? extends PhotoModel>) mAdapter.getAll(), position);
            }
        });

        viewBinding.rvBeauty.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        viewBinding.rvBeauty.addItemDecoration(new GridItemDecoration(DensityUtil.dip2px(this, 8)));
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
        beautyViewModel.loadBeauty(pageSize, pageNo);
    }

    private void refreshList(List<BeautyModel> results) {
        boolean hasData = results != null && !results.isEmpty();
        //如果没有数据了，禁用加载更多功能
        viewBinding.rvBeauty.setLoadingMoreEnabled(hasData);

        //如果是第一页先清空数据
        if (pageNo++ == 1) {
            mAdapter.setAllNotifyItemInserted(results);
        } else {
            mAdapter.addAllNotifyItemInserted(results);
        }

        if (mAdapter.getItemCount() == 0) {
            showEmpty();
        }
    }

}
