package com.starry.douban.adapter;

import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.starry.douban.R;
import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.databinding.ItemMovieBinding;
import com.starry.douban.image.ImageManager;
import com.starry.douban.model.MovieItemBean;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2016/12/10.
 */
public class MovieAdapter extends BaseRecyclerAdapter<MovieItemBean, ItemMovieBinding> {

    public MovieAdapter() {
    }

    public MovieAdapter(List<MovieItemBean> dataSet) {
        super(dataSet);
    }

    @Override
    public ItemMovieBinding getViewBinding(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        return ItemMovieBinding.inflate(inflater, parent, attachToParent);
    }

    @Override
    public int getHeaderLayoutCount() {
        return 1;
    }

    @Override
    public void onBindData(BaseRecyclerAdapter.RecyclerViewHolder<ItemMovieBinding> holder, MovieItemBean itemData, int position) {
        ItemMovieBinding viewBinding = holder.viewBinding;
        ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) viewBinding.ivImage.getLayoutParams();
        //比例本地随机生成或服务器下发，避免瀑布流item高度变化闪动
        layoutParams.dimensionRatio = String.valueOf(itemData.getDimensionRatio());

        viewBinding.tvTitle.setText(itemData.getTitle());
        viewBinding.tvAuthor.setText("导       演：" + itemData.getTitle());
        viewBinding.tvDate.setText("上映日期：" + itemData.getTitle());
        viewBinding.tvPublisher.setText("电影剧情：" + itemData.getTitle());
        viewBinding.tvNumRating.setText("观众评分：" + itemData.getRate());

        viewBinding.ivImage.setImageResource(R.drawable.image_bg_default);
        ImageManager.loadImage(viewBinding.ivImage, itemData.getCover(), 16);
    }
}
