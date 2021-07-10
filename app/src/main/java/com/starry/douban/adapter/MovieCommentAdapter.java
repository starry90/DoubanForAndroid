package com.starry.douban.adapter;

import com.starry.douban.R;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.model.MovieComment;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 21-7-10.
 */
public class MovieCommentAdapter extends BaseRecyclerAdapter<MovieComment> {

    public MovieCommentAdapter(List<MovieComment> beans) {
        super(beans);
    }

    @Override
    public int getItemLayout(int viewType) {
        return R.layout.item_movie_comment;
    }

    @Override
    public void onBindData(RecyclerViewHolder holder, MovieComment itemData, int position) {
        holder.setImageFromInternet(R.id.iv_movie_comment_user_photo, itemData.getUserImageUrl());
        holder.setText(R.id.tv_movie_comment_username, itemData.getUserName());
        holder.setText(R.id.tv_movie_comment_time, itemData.getCommentTime());
        holder.setText(R.id.tv_movie_comment_title, itemData.getCommentTitle());
        holder.setText(R.id.tv_movie_comment_content, itemData.getCommentShortContent());
    }
}
