package com.starry.douban.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.starry.douban.base.BaseRecyclerAdapter;
import com.starry.douban.databinding.ItemBookTagBinding;
import com.starry.douban.model.BookTag;

import java.util.List;

/**
 * @author Starry Jerry
 * @since 2021/07/11.
 */
public class BookTagAdapter extends BaseRecyclerAdapter<BookTag, ItemBookTagBinding> {

    public BookTagAdapter() {
    }

    public BookTagAdapter(List<BookTag> beans) {
        super(beans);
    }

    @Override
    public ItemBookTagBinding getViewBinding(LayoutInflater inflater, ViewGroup parent, boolean attachToParent) {
        return ItemBookTagBinding.inflate(inflater, parent, attachToParent);
    }

    @Override
    public void onBindData(BaseRecyclerAdapter.RecyclerViewHolder<ItemBookTagBinding> holder, BookTag itemData, int position) {
        holder.viewBinding.tvTitle.setText(itemData.getTag());
    }
}
