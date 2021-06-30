package com.starry.douban.ui.fragment;

import android.content.Intent;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.starry.douban.adapter.BookAdapter;
import com.starry.douban.base.BaseFragment;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.constant.Apis;
import com.starry.douban.databinding.FragmentHomeBinding;
import com.starry.douban.model.BookBean;
import com.starry.douban.model.Books;
import com.starry.douban.ui.activity.BookDetailActivity;
import com.starry.douban.util.ToastUtil;
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
public class HomeFragment extends BaseFragment<FragmentHomeBinding> {

    private int start = 0;
    private int count = 20;

    private String tag = "热门";

    private BookAdapter mAdapter;

    private List<BookBean> books = new ArrayList<>();

    @Override
    public FragmentHomeBinding getViewBinding(LayoutInflater layoutInflater) {
        return FragmentHomeBinding.inflate(layoutInflater);
    }

    @Override
    public void initData() {
        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new BookAdapter(books);
        mAdapter.addOnScrollListener(viewBinding.XRecyclerViewHome);
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                Intent intent = new Intent(mActivity, BookDetailActivity.class);
                intent.putExtra(BookDetailActivity.EXTRA_BOOK_ID, mAdapter.getItem(position).getId());
                startActivity(intent);
            }
        });

        viewBinding.XRecyclerViewHome.setLayoutManager(new GridLayoutManager(getActivity(), 3));
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
        viewBinding.XRecyclerViewHome.setRefreshing(true);
    }

    @Override
    public void loadData() {
        LinkedHashMap<String, Object> params = new LinkedHashMap<>();
        params.put("tag", tag);
        params.put("start", start);
        params.put("count", count);

        HttpManager.get(Apis.BookSearch)
                .tag(this)
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
                        viewBinding.XRecyclerViewHome.refreshComplete();
                        viewBinding.XRecyclerViewHome.loadMoreComplete();
                        hideLoading(success);
                    }
                });
    }

    public void refreshBookList(Books response) {
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
        viewBinding.XRecyclerViewHome.setLoadingMoreEnabled(start < response.getTotal());
    }

}
