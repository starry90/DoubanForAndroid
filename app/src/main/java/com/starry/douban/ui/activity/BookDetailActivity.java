package com.starry.douban.ui.activity;

import android.view.LayoutInflater;

import com.starry.douban.R;
import com.starry.douban.base.BaseActivity;
import com.starry.douban.constant.Apis;
import com.starry.douban.databinding.ActivityBookDetailBinding;
import com.starry.douban.image.ImageManager;
import com.starry.douban.model.BookDetail;
import com.starry.douban.util.ToastUtil;
import com.starry.http.HttpManager;
import com.starry.http.callback.StringCallback;
import com.starry.http.error.ErrorModel;

import java.util.List;

/**
 * 书籍详情页
 *
 * @author Starry Jerry
 * @since 2016/12/31.
 */
public class BookDetailActivity extends BaseActivity<ActivityBookDetailBinding> {

    public static final String EXTRA_BOOK_ID = "extra_book_id";

    private String url;

    @Override
    public ActivityBookDetailBinding getViewBinding(LayoutInflater layoutInflater) {
        return ActivityBookDetailBinding.inflate(layoutInflater);
    }

    @Override
    public void initData() {
        url = Apis.BookDetail + getIntent().getStringExtra(EXTRA_BOOK_ID);
        loadData();
    }

    @Override
    public void loadData() {
        HttpManager.get(url)
                .tag(this)
                .build()
                .enqueue(new StringCallback<BookDetail>() {

                    @Override
                    public void onSuccess(BookDetail response, Object... obj) {
                        showBookDetail(response);
                    }

                    @Override
                    public void onFailure(ErrorModel errorModel) {
                        ToastUtil.showToast(errorModel.getMessage());
                    }

                    @Override
                    public void onAfter(boolean success) {
                        super.onAfter(success);
                        hideLoading(success);
                    }
                });
    }

    public void showBookDetail(BookDetail response) {
        ImageManager.getBitmap(viewBinding.ivBookDetail, response.getImages().getLarge(), viewBinding.ivBookDetailBg);
        viewBinding.collapsingToolbar.setTitle(response.getTitle());
        viewBinding.tvBookDetailTitle.setText(response.getTitle());
        viewBinding.tvBookDetailRating.setText(response.getRating().getAverage());
        viewBinding.tvBookDetailRatingCount.setText(getString(R.string.book_number_raters, response.getRating().getNumRaters()));
        List<String> author = response.getAuthor();
        viewBinding.tvBookDetailOtherInfo.setText(getString(R.string.book_author_info, author.size() == 0 ? "" : author.get(0), response.getPublisher(), response.getPubdate()));
        viewBinding.tvBookDetailSummary.setText(response.getSummary());
        viewBinding.tvBookDetailAuthorSummary.setText(response.getAuthor_intro());
        viewBinding.tvBookDetailCatalog.setText(response.getCatalog());
    }

}
