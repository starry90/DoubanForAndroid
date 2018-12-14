package com.starry.douban.adapter;

import com.starry.douban.R;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.model.MovieDetail;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2016/12/10.
 */
public class MoviePhotoAdapter extends BaseRecyclerAdapter<MovieDetail.PerformerBean> {

    public MoviePhotoAdapter(List<MovieDetail.PerformerBean> beans) {
        super(beans);
    }

    @Override
    public int getItemLayout(int viewType) {
        return R.layout.item_movie_photo;
    }

    @Override
    public void onBindData(BaseRecyclerAdapter.RecyclerViewHolder holder, MovieDetail.PerformerBean itemData, int position) {
        if (itemData.getAvatars() != null)
            holder.setImageFromInternet(R.id.iv_movie_photo, itemData.getAvatars().getMedium());
        holder.setText(R.id.tv_movie_photo, itemData.getName());
        if (position == 0) {
            holder.setText(R.id.tv_movie_photo_type, "导演");
        } else if (position == 1) {
            holder.setText(R.id.tv_movie_photo_type, "演员");
        } else {
            holder.setText(R.id.tv_movie_photo_type, "");
        }
    }
}
