package com.starry.douban.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.starry.douban.adapter.BookAdapter;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.constant.Apis;
import com.starry.douban.constant.Common;
import com.starry.douban.databinding.ActivityBookListBinding;
import com.starry.douban.model.BookItemBean;
import com.starry.douban.util.JsonUtil;
import com.starry.douban.util.RegexHelper;
import com.starry.douban.util.ToastUtil;
import com.starry.http.HttpManager;
import com.starry.http.callback.StringCallback;
import com.starry.http.error.ErrorModel;
import com.starry.log.Logger;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

/**
 * @author Starry Jerry
 * @since 2021-07-18
 */

public class BookListActivity extends BaseActivity<ActivityBookListBinding> {

    private static final String EXTRA_BOOK_TAG = "extra_book_tag";

    private BookAdapter mAdapter;

    private String bookTag;

    private int start = 0;

    public static void showActivity(Activity context, String bookTag) {
        Intent intent = new Intent(context, BookListActivity.class);
        intent.putExtra(EXTRA_BOOK_TAG, bookTag);
        context.startActivity(intent);
    }

    @Override
    public ActivityBookListBinding getViewBinding(LayoutInflater layoutInflater) {
        return ActivityBookListBinding.inflate(layoutInflater);
    }

    @Override
    public void initData() {
        bookTag = getIntent().getStringExtra(EXTRA_BOOK_TAG);
        setTitle(bookTag);

        initRecyclerView();
    }

    private void initRecyclerView() {
        mAdapter = new BookAdapter();
        mAdapter.addOnScrollListener(viewBinding.rvBookList);
        mAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View itemView, int position) {
                BookDetailActivity.showActivity(BookListActivity.this, mAdapter.getItem(position).getSubjectUrl());
            }
        });

        viewBinding.rvBookList.setLayoutManager(new LinearLayoutManager(getActivity()));
        viewBinding.rvBookList.setAdapter(mAdapter);
        viewBinding.rvBookList.setLoadingListener(new XRecyclerView.LoadingListener() {
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
        viewBinding.rvBookList.setRefreshing(true);
    }

    @Override
    public void loadData() {
        HttpManager.get(Apis.BOOK_TAG + bookTag)
                .tag(this)
                .params("start", start)
                .params("type", "T")
                .build()
                .enqueue(new StringCallback<String>() {

                    @Override
                    public void onSuccess(String response, Object... obj) {
                        //  <li class="subject-item">
                        //    <div class="pic">
                        //      <a class="nbg" href="https://book.douban.com/subject/4913064/"
                        //  onclick="moreurl(this,{i:'0',query:'',subject_id:'4913064',from:'book_subject_search'})">
                        //        <img class="" src="https://img9.doubanio.com/view/subject/s/public/s27279654.jpg"
                        //          width="90">
                        //      </a>
                        //    </div>
                        //    <div class="info">
                        //      <h2 class="">
                        //
                        //  <a href="https://book.douban.com/subject/4913064/" title="活着"
                        //  onclick="moreurl(this,{i:'0',query:'',subject_id:'4913064',from:'book_subject_search'})">
                        //    活着
                        //  </a>
                        //
                        //      </h2>
                        //      <div class="pub">
                        //  余华 / 作家出版社 / 2012-8-1 / 20.00元
                        //      </div>
                        //
                        //  <div class="star clearfix">
                        //        <span class="allstar45"></span>
                        //        <span class="rating_nums">9.4</span>
                        //
                        //    <span class="pl">
                        //        (607135人评价)
                        //    </span>
                        //  </div>
                        //
                        //    <p>《活着(新版)》讲述了农村人福贵悲惨的人生遭遇。福贵本是个阔少爷，可他嗜赌如命，终于赌光了家业，一贫如洗。他的父亲被他活活气死，母亲则在穷困中患了重病，福贵... </p>
                        //
                        //      <div class="ft">
                        //  <div class="collect-info">
                        //  </div>
                        //          <div class="cart-actions">
                        //          </div>
                        //      </div>
                        //    </div>
                        //  </li>
                        Document parse = Jsoup.parse(response);
                        Elements bookElements = parse.getElementsByClass("subject-item");

                        ArrayList<BookItemBean> bookList = new ArrayList<>();
                        for (Element bookElement : bookElements) {
                            String book = bookElement.toString();

                            BookItemBean bookItemBean = new BookItemBean();
                            Matcher imageMatcher = RegexHelper.matcher(book, "src=\"", "\"");
                            while (imageMatcher.find()) {
                                bookItemBean.setImage(imageMatcher.group());
                            }

                            Matcher subjectMatcher = RegexHelper.matcher(book, "class=\"nbg\" href=\"", "\"");
                            while (subjectMatcher.find()) {
                                bookItemBean.setSubjectUrl(subjectMatcher.group());
                            }

                            Matcher titleMatcher = RegexHelper.matcher(book, "title=\"", "\"");
                            while (titleMatcher.find()) {
                                bookItemBean.setTitle(titleMatcher.group());
                            }

                            Matcher titlePublish = RegexHelper.matcher(book, "class=\"pub\">", "<");
                            while (titlePublish.find()) {
                                bookItemBean.setPublish(titlePublish.group());
                            }

                            Matcher titleSummary = RegexHelper.matcher(book, "<p>", "<");
                            while (titleSummary.find()) {
                                bookItemBean.setSummary(titleSummary.group());
                            }

                            Matcher ratingMatcher = RegexHelper.matcher(book, "rating_nums\">", "<");
                            while (ratingMatcher.find()) {
                                bookItemBean.setRating(ratingMatcher.group());
                            }

                            Matcher ratingNumberMatcher = RegexHelper.matcher(book, "class=\"pl\">", "<");
                            while (ratingNumberMatcher.find()) {
                                bookItemBean.setRatingNumber(ratingNumberMatcher.group());
                            }

                            bookList.add(bookItemBean);
                        }
                        Logger.json(JsonUtil.toJson(bookList));
                        refreshList(bookList);
                    }

                    @Override
                    public void onFailure(ErrorModel errorModel) {
                        ToastUtil.showToast(errorModel.getMessage());
                    }

                    @Override
                    public void onAfter(boolean success) {
                        viewBinding.rvBookList.refreshComplete();
                        viewBinding.rvBookList.loadMoreComplete();
                        hideLoading(success);
                    }
                });
    }

    private void refreshList(List<BookItemBean> results) {
        //如果是第一页先清空数据
        viewBinding.rvBookList.setVisibility(View.VISIBLE);
        if (start == 0) {
            mAdapter.setAll(results);
        } else {
            mAdapter.addAll(results);
        }
        start += Common.PAGE_SIZE;

        //如果没有数据了，禁用加载更多功能
//        viewBinding.rvBookList.setLoadingMoreEnabled(hasData);

        if (mAdapter.getItemCount() == 0) {
            showEmpty();
        }
    }

}
