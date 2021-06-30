package com.starry.douban.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    /**
     * The listener that receives notifications when an item is clicked.
     */
    private OnItemClickListener mOnItemClickListener;

    /**
     * The listener that receives notifications when an item is long clicked.
     */
    private OnItemLongClickListener mOnItemLongClickListener;

    /**
     * 子View点击事件集合
     */
    private HashMap<Integer, OnItemChildClickListener> childClickListenerMap = new HashMap<>();

    /**
     * 为了保证滑动流畅及不浪费流量，此时不加载图片,true表示列表滑动中
     */
    private boolean isScrolling;

    /**
     * 已加载过的数据集合
     */
    private SparseBooleanArray loadedMap = new SparseBooleanArray();

    public BaseRecyclerAdapter(List<T> dataSet) {
        this.dataSet = dataSet;
    }

    @NonNull
    @Override
    public BaseRecyclerAdapter.RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            }
        });

        // item长按事件
        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //position只有在列表展示后才有值
                int position = holder.getLayoutPosition() - getHeaderLayoutCount();
                if (mOnItemLongClickListener != null) {
                    return mOnItemLongClickListener.onItemLongClick(holder.itemView, position);
                }
                return false;
            }
        });

        //item 子View点击事件
        for (final Map.Entry<Integer, OnItemChildClickListener> entry : childClickListenerMap.entrySet()) {
            final View childView = view.findViewById(entry.getKey());
            childView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnItemChildClickListener value = entry.getValue();
                    //position只有在列表展示后才有值
                    int position = holder.getLayoutPosition() - getHeaderLayoutCount();
                    value.onItemChildClick(childView, position);
                }
            });
        }

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecyclerAdapter.RecyclerViewHolder holder, int position) {
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

    public T getItem(int position) {
        return dataSet.get(position);
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
     * Interface definition for a callback to be invoked when an item child view in this
     * view has been clicked
     */
    public interface OnItemChildClickListener {
        /**
         * callback method to be invoked when an item child view in this view has been
         * click and held
         *
         * @param view     The item child view within the adapter that was clicked
         * @param position The position of the view in the adapter.
         */
        void onItemChildClick(View view, int position);
    }

    /**
     * 添加item 子View点击事件
     *
     * @param viewId   子View id
     * @param listener 点击事件监听器
     */
    public void addOnItemChildClickListener(int viewId, OnItemChildClickListener listener) {
        childClickListenerMap.put(viewId, listener);
    }

    /**
     * Interface definition for a callback to be invoked when an item in this
     * Adapter has been clicked.
     */
    public interface OnItemClickListener {

        /**
         * Callback method to be invoked when an item in this Adapter has
         * been clicked.
         *
         * @param view     The view within the adapter that was clicked
         * @param position The position of the view in the adapter.
         */
        void onItemClick(View view, int position);

    }

    /**
     * Register a callback to be invoked when an item in this RecyclerView has
     * been clicked.
     *
     * @param listener The callback that will be invoked.
     */
    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    /**
     * Interface definition for a callback to be invoked when an item in this
     * Adapter has been long clicked and held.
     */
    public interface OnItemLongClickListener {
        /**
         * Callback method to be invoked when an item in this view has been
         * clicked and held.
         *
         * @param view     The view within the adapter that was clicked
         * @param position The position of the view in the adapter.
         * @return true if the callback consumed the long click, false otherwise
         */
        boolean onItemLongClick(View view, int position);

    }

    /**
     * Register a callback to be invoked when an item in this RecyclerView has
     * been long clicked and held
     *
     * @param listener The callback that will run
     */
    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        mOnItemLongClickListener = listener;
    }

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