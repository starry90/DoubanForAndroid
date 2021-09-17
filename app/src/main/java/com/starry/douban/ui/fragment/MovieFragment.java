package com.starry.douban.ui.fragment;

import android.content.Intent;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.starry.douban.adapter.MovieAdapter;
import com.starry.douban.base.BaseFragment;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.constant.Apis;
import com.starry.douban.databinding.FragmentMovieBinding;
import com.starry.douban.model.MovieItemBean;
import com.starry.douban.model.Movies;
import com.starry.douban.ui.activity.MovieDetailActivity;
import com.starry.douban.util.DensityUtil;
import com.starry.douban.util.ToastUtil;
import com.starry.douban.widget.recyclerview.GridItemDecoration;
import com.starry.http.HttpManager;
import com.starry.http.callback.StringCallback;
import com.starry.http.error.ErrorModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author Starry Jerry
 * @since 2016/12/4.
 */
public class MovieFragment extends BaseFragment<FragmentMovieBinding> {

    private int start = 0;
    private final int count = 20;

    private MovieAdapter mAdapter;

    private final List<MovieItemBean> books = new ArrayList<>();

    private final LinkedHashMap<String, Object> params = new LinkedHashMap<>();

    @Override
    public FragmentMovieBinding getViewBinding(LayoutInflater layoutInflater) {
        return FragmentMovieBinding.inflate(layoutInflater);
    }

    @Override
    public void initData() {
        params.put("tag", getArguments().getString("tag"));
        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new MovieAdapter();
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(mActivity, MovieDetailActivity.class);
                intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, mAdapter.getItem(position).getId());
                startActivity(intent);
            }
        });

        viewBinding.XRecyclerViewHome.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        viewBinding.XRecyclerViewHome.addItemDecoration(new GridItemDecoration(DensityUtil.dip2px(getActivity(), 8)));
        viewBinding.XRecyclerViewHome.setAdapter(mAdapter);
        viewBinding.XRecyclerViewHome.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                start = 0;
                loadData();
            }

            @Override
            public void onLoadMore() {
                loadData();
            }
        });
    }

    @Override
    public void onLazyLoadingData() {
        super.onLazyLoadingData();
        viewBinding.XRecyclerViewHome.setRefreshing(true);
    }

    @Override
    public void loadData() {
        params.put("type", "movie");
        params.put("sort", "recommend");
        params.put("page_start", start);
        params.put("page_limit", count);
        HttpManager.get(Apis.Movie)
                .tag(this)
                .params(params)
                .build()
                .enqueue(new StringCallback<Movies>() {

                    @Override
                    public void onSuccess(Movies response, Object... obj) {
                        refreshMovieList(response);
                    }

                    @Override
                    public void onFailure(ErrorModel errorModel) {
                        ToastUtil.showToast(errorModel.getMessage());
                    }

                    @Override
                    public void onAfter(boolean success) {
                        viewBinding.XRecyclerViewHome.refreshComplete();
                        viewBinding.XRecyclerViewHome.loadMoreComplete();
                        hideLoading(success);
                    }
                });
    }

    public void refreshMovieList(Movies response) {
        //1、如果是第一页先清空数据
        List<MovieItemBean> subjects = response.getSubjects();
        if (start == 0) {
            mAdapter.setAllNotifyItemInserted(subjects);
        } else {
            mAdapter.addAllNotifyItemInserted(subjects);
        }
        //2、页码自增
        start += count;
        //3、如果没有数据了，禁用加载更多功能
        viewBinding.XRecyclerViewHome.setLoadingMoreEnabled(start < response.getTotal());
    }

}
