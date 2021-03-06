package com.starry.douban.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.starry.douban.R;
import com.starry.douban.adapter.MovieAdapter;
import com.starry.douban.base.BaseFragment;
import com.starry.douban.constant.Apis;
import com.starry.douban.model.MovieBean;
import com.starry.douban.model.Movies;
import com.starry.douban.util.ToastUtil;
import com.starry.http.HttpManager;
import com.starry.http.callback.StringCallback;
import com.starry.http.error.ErrorModel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

/**
 * @author Starry Jerry
 * @since 2016/12/4.
 */
public class MovieFragment extends BaseFragment {

    @BindView(R.id.XRecyclerView_home)
    XRecyclerView mRecyclerView;

    private int start = 0;
    private int count = 20;

    private MovieAdapter mAdapter;

    private List<MovieBean> books = new ArrayList<>();

    private String url = Apis.MovieInTheaters;

    private LinkedHashMap<String, Object> params = new LinkedHashMap<>();

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_home;
    }

    @Override
    public void initData() {
        int type = getArguments().getInt("type");
        //0."正在热映", 1."即将上映", 2."Top250",3. "科幻电影",4. "喜剧电影"
        switch (type) {
            case 0:
                url = Apis.MovieInTheaters;
                break;
            case 1:
                url = Apis.MovieComingSoon;
                break;
            case 2:
                url = Apis.MovieTop250;
                break;
            case 3:
                url = Apis.MovieSearch;
                params.put("tag", "科幻");
                break;
            case 4:
                url = Apis.MovieSearch;
                params.put("tag", "喜剧");
                break;
        }

        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new MovieAdapter(books);
        mAdapter.addOnScrollListener(mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
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
        mRecyclerView.setRefreshing(true);
    }

    @Override
    public void loadData() {
        params.put("start", start);
        params.put("count", count);
        HttpManager.get(url)
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
                        mRecyclerView.refreshComplete();
                        mRecyclerView.loadMoreComplete();
                        hideLoading(success);
                    }
                });
    }

    public void refreshMovieList(Movies response) {
        //1、如果是第一页先清空数据 books不用做非空判断，不可能为空
        if (start == 0) {
            books.clear();
        }
        //2、拿到数据
        books.addAll(response.getSubjects());
        //3、刷新RecyclerView
        mAdapter.notifyDataSetChanged();
        //4、页码自增
        start += count;
        //5、如果没有数据了，禁用加载更多功能
        mRecyclerView.setLoadingMoreEnabled(start < response.getTotal());
    }

}
