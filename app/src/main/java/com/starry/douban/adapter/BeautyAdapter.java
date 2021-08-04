package com.starry.douban.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.starry.douban.R;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.databinding.ItemBeautyBinding;
import com.starry.douban.image.ImageManager;
import com.starry.douban.model.BeautyModel;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2016/12/10.
 */
public class BeautyAdapter extends BaseRecyclerAdapter<BeautyModel, ItemBeautyBinding> {

    public BeautyAdapter() {
    }

    public BeautyAdapter(List<BeautyModel> dataSet) {
        super(dataSet);
    }

    @Override
    public ItemBeautyBinding getViewBinding(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        return ItemBeautyBinding.inflate(inflater, parent, attachToParent);
    }

    @Override
    public int getHeaderLayoutCount() {
        return 1;
    }

    @Override
    public void onBindData(BaseRecyclerAdapter.RecyclerViewHolder<ItemBeautyBinding> holder, BeautyModel itemData, int position) {
        ItemBeautyBinding viewBinding = holder.viewBinding;
        viewBinding.tvTitle.setText(itemData.getDesc());
        viewBinding.ivImage.setImageResource(R.drawable.image_bg_default);
        if (allowLoadImage(position)) {
            ImageManager.loadImage(viewBinding.ivImage, itemData.getUrl());
        }
    }
}
