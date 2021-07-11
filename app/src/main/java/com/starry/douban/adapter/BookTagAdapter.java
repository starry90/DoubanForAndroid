package com.starry.douban.adapter;

import com.starry.douban.R;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.model.BookTag;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2021/07/11.
 */
public class BookTagAdapter extends BaseRecyclerAdapter<BookTag> {

    public BookTagAdapter(List<BookTag> beans) {
        super(beans);
    }

    @Override
    public int getItemLayout(int viewType) {
        return R.layout.item_book_tag;
    }

    @Override
    public void onBindData(RecyclerViewHolder holder, BookTag itemData, int position) {
        holder.setText(R.id.tv_title, itemData.getTag());
    }
}
