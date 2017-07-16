package com.starry.douban.adapter;

import android.content.Context;

import com.starry.douban.R;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.model.MovieDetail;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2016/12/10.
 */
public class MoviePhotoAdapter extends BaseRecyclerAdapter<MovieDetail.PerformerBean> {

    public MoviePhotoAdapter(Context context, List<MovieDetail.PerformerBean> beans) {
        super(context, beans);
    }

    @Override
    public int getItemLayout(int viewType) {
        return R.layout.item_movie_photo;
    }

    @Override
    public void onBindData(RecyclerViewHolder holder, MovieDetail.PerformerBean bean, int position) {
        if (bean.getAvatars() != null)
            holder.setImageFromInternet(R.id.iv_movie_photo, bean.getAvatars().getMedium());
        holder.setText(R.id.tv_movie_photo, bean.getName());
        if (position == 0) {
            holder.setText(R.id.tv_movie_photo_type, "导演");
        } else if (position == 1) {
            holder.setText(R.id.tv_movie_photo_type, "演员");
        } else {
            holder.setText(R.id.tv_movie_photo_type, "");
        }
    }
}
