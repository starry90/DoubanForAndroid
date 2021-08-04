package com.starry.douban.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.starry.douban.R;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.databinding.ItemVideoBinding;
import com.starry.douban.image.ImageManager;
import com.starry.douban.model.VideoItemBean;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2016/12/10.
 */
public class VideoAdapter extends BaseRecyclerAdapter<VideoItemBean, ItemVideoBinding> {

    public VideoAdapter() {
    }

    public VideoAdapter(List<VideoItemBean> dataSet) {
        super(dataSet);
    }

    @Override
    public ItemVideoBinding getViewBinding(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        return ItemVideoBinding.inflate(inflater, parent, attachToParent);
    }

    @Override
    public int getHeaderLayoutCount() {
        return 1;
    }

    @Override
    public void onBindData(RecyclerViewHolder<ItemVideoBinding> holder, VideoItemBean itemData, int position) {
        ItemVideoBinding viewBinding = holder.viewBinding;
        viewBinding.tvItemVideoTitle.setText(itemData.getVideoTitle());
        viewBinding.ivItemVideoImage.setImageResource(R.drawable.image_bg_default);
        if (allowLoadImage(position)) {
            ImageManager.loadImage(viewBinding.ivItemVideoImage, itemData.getVideoImage());
        }
    }
}
