package com.starry.douban.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.databinding.ItemMovieCommentBinding;
import com.starry.douban.image.ImageManager;
import com.starry.douban.model.CommentBean;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 21-7-10.
 */
public class CommentAdapter extends BaseRecyclerAdapter<CommentBean, ItemMovieCommentBinding> {

    public CommentAdapter(List<CommentBean> beans) {
        super(beans);
    }

    @Override
    public ItemMovieCommentBinding getViewBinding(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        return ItemMovieCommentBinding.inflate(inflater, parent, attachToParent);
    }

    @Override
    public void onBindData(BaseRecyclerAdapter.RecyclerViewHolder<ItemMovieCommentBinding> holder, CommentBean itemData, int position) {
        ItemMovieCommentBinding viewBinding = holder.viewBinding;
        ImageManager.loadImageTransformCircle(viewBinding.ivMovieCommentUserPhoto, itemData.getUserImageUrl());
        viewBinding.tvMovieCommentUsername.setText(itemData.getUserName());
        viewBinding.tvMovieCommentTime.setText(itemData.getCommentTime());
        viewBinding.tvMovieCommentTitle.setText(itemData.getCommentTitle());
        viewBinding.tvMovieCommentContent.setText(itemData.getCommentShortContent());
        viewBinding.tvMovieCommentUp.setText(itemData.getActionUp() + "有用");
        viewBinding.tvMovieCommentDown.setText(itemData.getActionDown() + "无用");
        viewBinding.tvMovieCommentReply.setText(itemData.getReply());
    }
}
