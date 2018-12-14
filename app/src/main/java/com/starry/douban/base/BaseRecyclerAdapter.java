package com.starry.douban.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.starry.douban.image.ImageManager;

import java.util.List;


/**
 * 通用的RecyclerView的适配器
 * <p/>
 * 参考了Hongyang的 http://blog.csdn.net/lmj623565791/article/details/38902805这篇博客
 * <p/>
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerAdapter.RecyclerViewHolder> {

    protected List<T> dataSet;
    protected Context mContext;
    private OnItemClickListener<T> mOnItemViewClickListener;
    private OnItemLongClickListener<T> mOnItemLongClickListener;

    public BaseRecyclerAdapter(List<T> dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public BaseRecyclerAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(getItemLayout(viewType), parent, false);
        final BaseRecyclerAdapter.RecyclerViewHolder holder = new RecyclerViewHolder(view);
        // item点击事件
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //position只有在列表展示后才有值
                int position = holder.getLayoutPosition() - getHeaderLayoutCount();
                //设置监听器优先级高于实现onItemClick方法
                if (mOnItemViewClickListener != null) {
                    mOnItemViewClickListener.onItemClick(holder.itemView, position, dataSet.get(position));
                } else {
                    onItemClick(holder.itemView, position);
                }
            }
        });

        // item长按事件
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //position只有在列表展示后才有值
                int position = holder.getLayoutPosition() - getHeaderLayoutCount();
                //设置监听器优先级高于实现onLongItemClick方法
                if (mOnItemLongClickListener != null) {
                    mOnItemLongClickListener.onItemLongClick(holder.itemView, position, dataSet.get(position));
                } else {
                    onItemLongClick(holder.itemView, position);
                }
                return false;
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerAdapter.RecyclerViewHolder holder, int position) {
        onBindData(holder, dataSet.get(position), position);
    }

    /**
     * 获取ItemView的布局文件
     *
     * @param viewType The view type of the new View
     * @return 布局id
     */
    public abstract int getItemLayout(int viewType);

    /**
     * 绑定数据
     *
     * @param holder   ViewHolder
     * @param itemData 数据bean
     * @param position 当前位置
     */
    public abstract void onBindData(BaseRecyclerAdapter.RecyclerViewHolder holder, T itemData, int position);


    @Override
    public int getItemCount() {
        return dataSet != null ? dataSet.size() : 0;
    }

    /**
     * if addHeaderView will be return 1, if not will be return 0
     */
    public int getHeaderLayoutCount() {
        return 0;
    }

    public void setDataSet(List<T> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }

    public void clearDataSet() {
        if (dataSet != null) {
            dataSet.clear();
            notifyDataSetChanged();
        }
    }

    public void removeItem(int position) {
        dataSet.remove(position);
        notifyItemRemoved(position);//第一项是header 需要做下偏移
        notifyDataSetChanged();
    }

    public List<T> getDataSet() {
        return dataSet;
    }

    /**
     * ItemView的单击事件(如果需要，重写此方法就行)
     */
    protected void onItemClick(View itemView, int position) {
    }

    /**
     * ItemView的长按事件(如果需要，重写此方法就行)
     */
    protected void onItemLongClick(View itemView, int position) {
    }

    //#####################################################################################

    public interface OnItemClickListener<T> {
        void onItemClick(View itemView, int position, T t);
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.mOnItemViewClickListener = listener;
    }

    //#####################################################################################

    public interface OnItemLongClickListener<T> {
        void onItemLongClick(View itemView, int position, T t);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> listener) {
        this.mOnItemLongClickListener = listener;
    }

    //#####################################################################################

    public class RecyclerViewHolder extends
            RecyclerView.ViewHolder {
        private final SparseArray<View> viewHolder;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            this.viewHolder = new SparseArray<>();
        }

        public <T extends View> T getView(int viewId) {
            View view = viewHolder.get(viewId);
            if (view == null) {
                view = itemView.findViewById(viewId);
                viewHolder.put(viewId, view);
            }
            return (T) view;
        }

        public void setText(int viewId, String text) {
            TextView tv = getView(viewId);
            tv.setText(text);
        }

        /**
         * 加载drawable中的图片
         *
         * @param viewId
         * @param resId
         */
        public void setImage(int viewId, int resId) {
            ImageView iv = getView(viewId);
            iv.setImageResource(resId);
        }

        /**
         * 加载网络上的图片
         *
         * @param viewId
         * @param url
         */
        public void setImageFromInternet(int viewId, String url) {
            ImageView iv = getView(viewId);
            ImageManager.loadImage(mContext, iv, url);
        }
    }

}