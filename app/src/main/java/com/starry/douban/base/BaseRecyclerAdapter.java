package com.starry.douban.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.starry.douban.image.ImageManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 通用的RecyclerView的适配器
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerAdapter.RecyclerViewHolder> {

    protected List<T> dataSet;
    protected Context mContext;
    private OnItemClickListener<T> mOnItemViewClickListener;
    private OnItemLongClickListener<T> mOnItemLongClickListener;

    /**
     * 为了保证滑动流畅及不浪费流量，此时不加载图片,true表示列表滑动中
     */
    private boolean isScrolling;

    /**
     * 已加载过的数据集合
     */
    private SparseBooleanArray loadedMap = new SparseBooleanArray();

    /**
     * 子View点击事件集合
     */
    private Map<Integer, OnChildViewClickListener> clickListenerMap;

    /**
     * 添加子View点击事件
     *
     * @param viewId   子View id
     * @param listener 点击事件监听器
     */
    public void addChildViewClickListener(int viewId, OnChildViewClickListener listener) {
        if (clickListenerMap == null) {
            clickListenerMap = new HashMap<>();
        }
        clickListenerMap.put(viewId, listener);
    }

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

        if (clickListenerMap != null) {
            for (final Map.Entry<Integer, OnChildViewClickListener> entry : clickListenerMap.entrySet()) {
                View childView = view.findViewById(entry.getKey());
                childView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        OnChildViewClickListener value = entry.getValue();
                        //position只有在列表展示后才有值
                        int position = holder.getLayoutPosition() - getHeaderLayoutCount();
                        value.onChildViewClick(holder, position);
                    }
                });
            }
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(BaseRecyclerAdapter.RecyclerViewHolder holder, int position) {
        onBindData(holder, dataSet.get(position), position);

        if (!isScrolling) {
            loadedMap.put(position, true);
        }
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

    /**
     * 是否允许加载图片，两种情况允许：1 滑动已停止，2 已经加载过图片
     *
     * @param position item位置
     * @return true表示允许
     */
    protected boolean allowLoadImage(int position) {
        return !isScrolling || loadedMap.get(position);
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
     * 设置RecyclerView滑动监听器
     *
     * @param recyclerView RecyclerView
     */
    public void addOnScrollListener(RecyclerView recyclerView) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                isScrolling = newState != RecyclerView.SCROLL_STATE_IDLE;
                if (!isScrolling) {
                    notifyDataSetChanged();
                }
            }
        });
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

    /**
     * item子View点击事件
     *
     * @param <T>
     */
    public interface OnChildViewClickListener<T> {
        void onChildViewClick(RecyclerViewHolder holder, int position);
    }

    //#####################################################################################

    public interface OnItemLongClickListener<T> {
        void onItemLongClick(View itemView, int position, T t);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> listener) {
        this.mOnItemLongClickListener = listener;
    }

    //#####################################################################################

    public static class RecyclerViewHolder extends RecyclerView.ViewHolder {

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
            ImageManager.loadImage(iv, url);
        }
    }

}