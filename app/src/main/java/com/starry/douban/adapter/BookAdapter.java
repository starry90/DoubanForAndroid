package com.starry.douban.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.databinding.ItemBookBinding;
import com.starry.douban.image.ImageManager;
import com.starry.douban.model.BookItemBean;
import com.starry.douban.util.StringUtils;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2016/12/10.
 */
public class BookAdapter extends BaseRecyclerAdapter<BookItemBean, ItemBookBinding> {

    public BookAdapter() {
    }

    public BookAdapter(List<BookItemBean> beans) {
        super(beans);
    }

    @Override
    public ItemBookBinding getViewBinding(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        return ItemBookBinding.inflate(inflater, parent, attachToParent);
    }

    @Override
    public int getHeaderLayoutCount() {
        return 1;
    }

    @Override
    public void onBindData(BaseRecyclerAdapter.RecyclerViewHolder<ItemBookBinding> holder, BookItemBean itemData, int position) {
        ItemBookBinding viewBinding = holder.viewBinding;
        viewBinding.tvItemBookTitle.setText(itemData.getTitle());
        viewBinding.tvItemBookPublish.setText(itemData.getPublish());
        viewBinding.tvItemBookSummary.setText(itemData.getSummary());
        viewBinding.tvItemBookRating.setText(StringUtils.format("豆瓣评分：%s %s ", itemData.getRating(), itemData.getRatingNumber()));
        ImageManager.loadImage(viewBinding.ivItemBookImage, itemData.getImage());
    }
}
