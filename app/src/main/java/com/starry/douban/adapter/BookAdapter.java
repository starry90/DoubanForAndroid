package com.starry.douban.adapter;

import android.content.Intent;
import android.view.View;

import com.starry.douban.R;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.model.BookBean;
import com.starry.douban.ui.activity.BookDetailActivity;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2016/12/10.
 */
public class BookAdapter extends BaseRecyclerAdapter<BookBean> {

    public BookAdapter(List<BookBean> beans) {
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
    protected void onItemClick(View itemView, int position) {
        Intent intent = new Intent(mContext, BookDetailActivity.class);
        intent.putExtra("bookId", dataSet.get(position).getId());
        mContext.startActivity(intent);
    }

    @Override
    public void onBindData(BaseRecyclerAdapter.RecyclerViewHolder holder, BookBean itemData, int position) {
        holder.setText(R.id.tv_title, itemData.getTitle());
        holder.setText(R.id.tv_num_rating, "豆瓣评分：" + itemData.getRating().getAverage());
        holder.setImage(R.id.iv_image, R.drawable.image_bg_default);
        if (allowLoadImage(position)) {
            holder.setImageFromInternet(R.id.iv_image, itemData.getImage());
        }
    }
}
