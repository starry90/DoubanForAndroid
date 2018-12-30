package com.starry.douban.adapter;

import android.view.View;

import com.starry.douban.R;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.model.BeautyModel;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2016/12/10.
 */
public class BeautyAdapter extends BaseRecyclerAdapter<BeautyModel> {

    public BeautyAdapter(List<BeautyModel> beans) {
        super(beans);
    }

    @Override
    public int getItemLayout(int viewType) {
        return R.layout.item_beauty;
    }

    @Override
    public int getHeaderLayoutCount() {
        return 1;
    }

    @Override
    protected void onItemClick(View itemView, int position) {
    }

    @Override
    public void onBindData(RecyclerViewHolder holder, BeautyModel itemData, int position) {
        holder.setText(R.id.tv_title, itemData.getDesc());
        holder.setImage(R.id.iv_image, R.drawable.image_bg_default);
        if (allowLoadImage(position)) {
            holder.setImageFromInternet(R.id.iv_image, itemData.getUrl());
        }
    }
}
