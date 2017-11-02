package com.starry.douban.ui.fragment;

import android.support.v7.widget.LinearLayoutManager;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.starry.douban.R;
import com.starry.douban.adapter.BookAdapter;
import com.starry.douban.base.BaseLazyFragment;
import com.starry.douban.model.BookBean;
import com.starry.douban.model.Books;
import com.starry.douban.presenter.MainPresenter;
import com.starry.douban.ui.view.MainView;
import com.starry.douban.widget.LoadingDataLayout;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import butterknife.BindView;

/**
 * @author Starry Jerry
 * @since 2016/12/4.
 */
public class HomeFragment extends BaseLazyFragment implements MainView {

    @BindView(R.id.XRecyclerView_home)
    XRecyclerView mRecyclerView;

    private int start = 0;
    private int count = 20;

    private String tag = "热门";

    private BookAdapter mAdapter;

    private List<BookBean> books = new ArrayList<>();

    private MainPresenter mPresenter;

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_home;
    }

    @Override
    public void initData() {
        mPresenter = new MainPresenter(this);
        initRecyclerView();
    }

    @Override
    public void setListener() {

    }

    private void initRecyclerView() {
        mAdapter = new BookAdapter(getActivity(), books);
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
        mRecyclerView.setRefreshing(true);
    }

    @Override
    public void loadData() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("tag", tag);
        params.put("start", start + "");
        params.put("count", count + "");
        mPresenter.getBookList(params);
    }

    @Override
    public void onRefreshList(Books response) {
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

    @Override
    public void onFailure(String message, int code, Object... obj) {
    }

    @Override
    public void onLoadingComplete() {
        mRecyclerView.refreshComplete();
        mRecyclerView.loadMoreComplete();
    }

}
