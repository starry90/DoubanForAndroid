package com.starry.douban.adapter;

import android.content.Intent;
import android.view.View;

import com.starry.douban.R;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.model.MovieBean;
import com.starry.douban.ui.activity.MovieDetailActivity;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2016/12/10.
 */
public class MovieAdapter extends BaseRecyclerAdapter<MovieBean> {

    public MovieAdapter(List<MovieBean> beans) {
        super(beans);
    }

    @Override
    public int getItemLayout(int viewType) {
        return R.layout.item_movie;
    }

    @Override
    public int getHeaderLayoutCount() {
        return 1;
    }

    @Override
    protected void onItemClick(View itemView, int position) {
        Intent intent = new Intent(mContext, MovieDetailActivity.class);
        intent.putExtra("movieId", dataSet.get(position).getId());
        mContext.startActivity(intent);
    }

    @Override
    public void onBindData(BaseRecyclerAdapter.RecyclerViewHolder holder, MovieBean itemData, int position) {
        holder.setText(R.id.tv_title, itemData.getTitle());
        if (!itemData.getDirectors().isEmpty())
            holder.setText(R.id.tv_author, "导       演：" + itemData.getDirectors().get(0).getName());
        holder.setText(R.id.tv_date, "上映日期：" + itemData.getYear());
        holder.setText(R.id.tv_publisher, "电影剧情：" + itemData.getGenres().toString());
        holder.setText(R.id.tv_num_rating, "观众评分：" + itemData.getRating().getAverage());
        holder.setImage(R.id.iv_image, R.drawable.image_bg_default);
        if (allowLoadImage(position)) {
            holder.setImageFromInternet(R.id.iv_image, itemData.getImages().getMedium());
        }
    }
}
