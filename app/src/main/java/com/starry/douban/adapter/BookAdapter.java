package com.starry.douban.adapter;

import android.content.Context;
import android.content.Intent;

import com.starry.douban.R;
import com.starry.douban.ui.activity.BookDetailActivity;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.model.BookBean;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2016/12/10.
 */
public class BookAdapter extends BaseRecyclerAdapter<BookBean> {

    public BookAdapter(Context context, List<BookBean> beans) {
        super(context, beans);
    }

    @Override
    public int getItemLayout(int viewType) {
        return R.layout.item_book;
    }

    @Override
    protected void onItemClick(int position) {
        super.onItemClick(position);
        Intent intent = new Intent(mContext, BookDetailActivity.class);
        intent.putExtra("bookId", mBeans.get(position).getId());
        mContext.startActivity(intent);
    }

    @Override
    public void onBindData(RecyclerViewHolder holder, BookBean bean, int position) {
        holder.setText(R.id.tv_title, bean.getTitle());
        holder.setText(R.id.tv_price, bean.getPrice());
        holder.setText(R.id.tv_author, "作       者：" + bean.getAuthor());
        holder.setText(R.id.tv_publisher, "出版组织：" + bean.getPublisher());
        holder.setText(R.id.tv_num_rating, "豆瓣评分：" + bean.getRating().getAverage());
        holder.setImageFromInternet(R.id.iv_image, bean.getImage());
    }
}
