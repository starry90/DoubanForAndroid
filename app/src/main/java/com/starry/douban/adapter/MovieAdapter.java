package com.starry.douban.adapter;

import android.content.Context;
import android.content.Intent;

import com.starry.douban.R;
import com.starry.douban.ui.activity.MovieDetailActivity;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.model.MovieBean;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2016/12/10.
 */
public class MovieAdapter extends BaseRecyclerAdapter<MovieBean> {

    public MovieAdapter(Context context, List<MovieBean> beans) {
        super(context, beans);
    }

    @Override
    public int getItemLayout(int viewType) {
        return R.layout.item_movie;
    }

    @Override
    protected void onItemClick(int position) {
        super.onItemClick(position);
        Intent intent = new Intent(mContext, MovieDetailActivity.class);
        intent.putExtra("movieId", mBeans.get(position).getId());
        mContext.startActivity(intent);
    }

    @Override
    public void onBindData(RecyclerViewHolder holder, MovieBean bean, int position) {
        holder.setText(R.id.tv_title, bean.getTitle());
        if (!bean.getDirectors().isEmpty())
            holder.setText(R.id.tv_author, "导       演：" + bean.getDirectors().get(0).getName());
        holder.setText(R.id.tv_date, "上映日期：" + bean.getYear());
        holder.setText(R.id.tv_publisher, "电影剧情：" + bean.getGenres().toString());
        holder.setText(R.id.tv_num_rating, "观众评分：" + bean.getRating().getAverage() + "分");
        holder.setImageFromInternet(R.id.iv_image, bean.getImages().getMedium());
    }
}
