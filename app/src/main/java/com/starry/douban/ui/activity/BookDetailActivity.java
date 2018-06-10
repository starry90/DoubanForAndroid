package com.starry.douban.ui.activity;

import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.starry.douban.R;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.constant.Apis;
import com.starry.douban.image.ImageManager;
import com.starry.douban.model.BookDetail;
import com.starry.douban.model.ErrorModel;
import com.starry.douban.presenter.BookDetailPresenter;
import com.starry.douban.ui.view.BookDetailView;

import java.util.List;

import butterknife.BindView;

/**
 * 书籍详情页
 *
 * @author Starry Jerry
 * @since 2016/12/31.
 */
public class BookDetailActivity extends BaseActivity implements BookDetailView {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsing_toolbar;

    @BindView(R.id.iv_book_detail_bg)
    ImageView ivBookDetailBg;
    @BindView(R.id.iv_book_detail)
    ImageView ivBookDetail;
    @BindView(R.id.tv_book_detail_title)
    TextView tv_book_detail_title;
    @BindView(R.id.tv_book_detail_rating)
    TextView tv_book_detail_rating;
    @BindView(R.id.tv_book_detail_rating_count)
    TextView tv_book_detail_rating_count;
    @BindView(R.id.tv_book_detail_other_info)
    TextView tv_book_detail_other_info;
    @BindView(R.id.tv_book_detail_summary)
    TextView tv_book_detail_summary;
    @BindView(R.id.tv_book_detail_author_summary)
    TextView tv_book_detail_author_summary;
    @BindView(R.id.tv_book_detail_catalog)
    TextView tv_book_detail_catalog;
    private String url;

    private BookDetailPresenter mPresenter;

    @Override
    public int getLayoutResID() {
        return R.layout.activity_book_detail;
    }

    @Override
    public void initData() {
        url = Apis.BookDetail + getIntent().getStringExtra("bookId");
        mPresenter = new BookDetailPresenter(this);
        loadData();
    }

    @Override
    public void loadData() {
        mPresenter.getData(url);
    }


    @Override
    public void showBookDetail(BookDetail response) {
        ImageManager.getBitmap(ivBookDetail, response.getImages().getLarge(), ivBookDetailBg);
        collapsing_toolbar.setTitle(response.getTitle());
        tv_book_detail_title.setText(response.getTitle());
        tv_book_detail_rating.setText(response.getRating().getAverage());
        tv_book_detail_rating_count.setText(getString(R.string.book_number_raters, response.getRating().getNumRaters()));
        List<String> author = response.getAuthor();
        tv_book_detail_other_info.setText(getString(R.string.book_author_info, author.size() == 0 ? "" : author.get(0), response.getPublisher(), response.getPubdate()));
        tv_book_detail_summary.setText(response.getSummary());
        tv_book_detail_author_summary.setText(response.getAuthor_intro());
        tv_book_detail_catalog.setText(response.getCatalog());
    }

    @Override
    public void onFailure(ErrorModel errorModel) {

    }

}
