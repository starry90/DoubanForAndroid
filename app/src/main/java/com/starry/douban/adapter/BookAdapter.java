package com.starry.douban.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.starry.douban.R;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.model.BookBean;
import com.starry.douban.ui.activity.BookDetailActivity;
import com.starry.douban.util.ActivityAnimationUtils;

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
    protected void onItemClick(int position, View item) {
        super.onItemClick(position, item);
        Intent intent = new Intent(mContext, BookDetailActivity.class);
        intent.putExtra("bookId", mBeans.get(position).getId());
        ActivityAnimationUtils.transition((Activity) mContext, intent, item);
    }

    @Override
    public void onBindData(RecyclerViewHolder holder, BookBean bean, int position) {
        holder.setText(R.id.tv_title, bean.getTitle());
        holder.setText(R.id.tv_num_rating, "豆瓣评分：" + bean.getRating().getAverage());
        holder.setImageFromInternet(R.id.iv_image, bean.getImage());
    }
}
