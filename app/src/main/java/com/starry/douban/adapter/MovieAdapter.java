package com.starry.douban.adapter;

import com.starry.douban.R;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.model.MovieItemBean;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2016/12/10.
 */
public class MovieAdapter extends BaseRecyclerAdapter<MovieItemBean> {

    public MovieAdapter(List<MovieItemBean> beans) {
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
    public void onBindData(BaseRecyclerAdapter.RecyclerViewHolder holder, MovieItemBean itemData, int position) {
        holder.setText(R.id.tv_title, itemData.getTitle());
        holder.setText(R.id.tv_author, "导       演：" + itemData.getTitle());
        holder.setText(R.id.tv_date, "上映日期：" + itemData.getTitle());
        holder.setText(R.id.tv_publisher, "电影剧情：" + itemData.getTitle());
        holder.setText(R.id.tv_num_rating, "观众评分：" + itemData.getRate());
        holder.setImage(R.id.iv_image, R.drawable.image_bg_default);
        if (allowLoadImage(position)) {
            holder.setImageFromInternet(R.id.iv_image, itemData.getCover());
        }
    }
}
