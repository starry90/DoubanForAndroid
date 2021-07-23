package com.starry.douban.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.databinding.ItemMoviePhotoBinding;
import com.starry.douban.image.ImageManager;
import com.starry.douban.model.MovieItemDetailBean;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2016/12/10.
 */
public class MoviePhotoAdapter extends BaseRecyclerAdapter<MovieItemDetailBean.PersonBean, ItemMoviePhotoBinding> {

    public MoviePhotoAdapter(List<MovieItemDetailBean.PersonBean> beans) {
        super(beans);
    }

    @Override
    public ItemMoviePhotoBinding getViewBinding(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        return ItemMoviePhotoBinding.inflate(inflater, parent, attachToParent);
    }

    @Override
    public void onBindData(BaseRecyclerAdapter.RecyclerViewHolder<ItemMoviePhotoBinding> holder, MovieItemDetailBean.PersonBean itemData, int position) {
        ItemMoviePhotoBinding viewBinding = holder.viewBinding;
        ImageManager.loadImage(viewBinding.ivMoviePhoto, itemData.getAvatarUrl());
        viewBinding.tvMoviePhoto.setText(itemData.getName());
        viewBinding.tvMovieRole.setText(itemData.getRole());
        String photoType = "";
        if (position == 0) {
            photoType = "导演";
        } else if (position == 1) {
            photoType = "演员";
        }
        viewBinding.tvMoviePhotoType.setText(photoType);
    }
}
