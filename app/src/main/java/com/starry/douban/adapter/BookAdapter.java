package com.starry.douban.adapter;

import com.starry.douban.R;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.model.BookItemBean;
import com.starry.douban.util.StringUtils;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2016/12/10.
 */
public class BookAdapter extends BaseRecyclerAdapter<BookItemBean> {

    public BookAdapter() {
    }

    public BookAdapter(List<BookItemBean> beans) {
        super(beans);
    }

    @Override
    public int getItemLayout(int viewType) {
        return R.layout.item_book;
    }

    @Override
    public int getHeaderLayoutCount() {
        return 1;
    }

    @Override
    public void onBindData(BaseRecyclerAdapter.RecyclerViewHolder holder, BookItemBean itemData, int position) {
        holder.setText(R.id.tv_item_book_title, itemData.getTitle());
        holder.setText(R.id.tv_item_book_publish, itemData.getPublish());
        holder.setText(R.id.tv_item_book_summary, itemData.getSummary());
        holder.setText(R.id.tv_item_book__rating, StringUtils.format("豆瓣评分：%s %s ", itemData.getRating(), itemData.getRatingNumber()));
        holder.setImage(R.id.iv_item_book_image, R.drawable.image_bg_default);
        if (allowLoadImage(position)) {
            holder.setImageFromInternet(R.id.iv_item_book_image, itemData.getImage());
        }
    }
}
