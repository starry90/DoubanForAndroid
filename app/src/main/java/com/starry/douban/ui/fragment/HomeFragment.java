package com.starry.douban.ui.fragment;

import android.support.v7.widget.GridLayoutManager;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.starry.douban.R;
import com.starry.douban.adapter.BookAdapter;
import com.starry.douban.base.BaseFragment;
import com.starry.douban.constant.Apis;
import com.starry.douban.http.HttpManager;
import com.starry.douban.http.callback.StringCallback;
import com.starry.douban.http.error.ErrorModel;
import com.starry.douban.model.BookBean;
import com.starry.douban.model.Books;
import com.starry.douban.util.ToastUtil;
import com.starry.douban.widget.LoadingDataLayout;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

/**
 * @author Starry Jerry
 * @since 2016/12/4.
 */
public class HomeFragment extends BaseFragment {

    @BindView(R.id.XRecyclerView_home)
    XRecyclerView mRecyclerView;

    private int start = 0;
    private int count = 20;

    private String tag = "热门";

    private BookAdapter mAdapter;

    private List<BookBean> books = new ArrayList<>();

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_home;
    }

    @Override
    public void initData() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new BookAdapter(getActivity(), books);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
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
        mRecyclerView.setRefreshing(true);
    }

    @Override
    public void loadData() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("tag", tag);
        params.put("start", start + "");
        params.put("count", count + "");

        HttpManager.get()
                .tag(this)
                .url(Apis.BookSearch)
                .params(params)
                .build()
                .enqueue(new StringCallback<Books>() {

                    @Override
                    public void onSuccess(Books response, Object... obj) {
                        refreshBookList(response);
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

    public void refreshBookList(Books response) {
        showLoadingStatus(LoadingDataLayout.STATUS_SUCCESS);
        //1、如果是第一页先清空数据 books不用做非空判断，不可能为空
        if (start == 0) {
            books.clear();
        }
        //2、拿到数据
        books.addAll(response.getBooks());
        //3、刷新RecyclerView
        mAdapter.notifyDataSetChanged();
        //4、页码自增
        start += count;
        //5、如果没有数据了，禁用加载更多功能
        mRecyclerView.setLoadingMoreEnabled(start < response.getTotal());
    }

}
