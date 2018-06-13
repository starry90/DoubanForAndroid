package com.starry.douban.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.starry.douban.image.ImageManager;
import com.starry.douban.util.AnimationController;

import java.util.List;


/**
 * 通用的RecyclerView的适配器
 * <p/>
 * 参考了Hongyang的 http://blog.csdn.net/lmj623565791/article/details/38902805这篇博客
 * <p/>
 */
public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerAdapter.RecyclerViewHolder> {

    protected List<T> mBeans;
    protected Context mContext;
    protected boolean mAnimateItems = true;
    protected int mLastAnimatedPosition = -1;

    public BaseRecyclerAdapter(Context context, List<T> beans) {
        mContext = context;
        mBeans = beans;
    }

    @Override
    public BaseRecyclerAdapter.RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(getItemLayout(viewType), parent, false);
        return new RecyclerViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final BaseRecyclerAdapter.RecyclerViewHolder holder, final int position) {
        //TODO 快速上拉加载更多动画时item有视图重叠bug
//        runEnterAnimation(holder.itemView, position);

        final T bean = mBeans.get(position);
        onBindData(holder, bean, position);

        // item点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置监听器优化级高于实现onItemClick方法
                if (mOnItemViewClickListener != null) {
                    mOnItemViewClickListener.onItemClick(bean, position);
                } else {
                    onItemClick(position, holder.itemView);
                }
            }
        });

        // item长按事件
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //设置监听器优化级高于实现onLongItemClick方法
                if (mOnLongItemClickListener != null) {
                    mOnLongItemClickListener.onLongClick(bean, position);
                } else {
                    onLongItemClick(position);
                }
                return false;
            }
        });
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
     * @param bean     数据bean
     * @param position 当前位置
     */
    public abstract void onBindData(RecyclerViewHolder holder, T bean, int position);


    @Override
    public int getItemCount() {
        return mBeans != null ? mBeans.size() : 0;
    }

    public void setData(List<T> beans) {
        mBeans = beans;
        notifyDataSetChanged();
    }

    public void clear() {
        if (mBeans != null) {
            mBeans.clear();
        }
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        mBeans.remove(position);
        notifyItemRemoved(position + 1);//第一项是header 需要做下偏移
        notifyDataSetChanged();
    }

    public List<T> getBeans() {
        return mBeans;
    }

    /***
     * item的加载动画
     *
     * @param view
     * @param position
     */
    private void runEnterAnimation(final View view, int position) {
        if (!mAnimateItems) {
            return;
        }
        if (position > mLastAnimatedPosition) {
            mLastAnimatedPosition = position;
            AnimationController.scaleIn(view, 300, 100);
        }
    }


    /**
     * ItemView的单击事件(如果需要，重写此方法就行)
     *
     * @param position
     */
    protected void onItemClick(int position, View item) {
    }

    /**
     * ItemView的长按事件(如果需要，重写此方法就行)
     *
     * @param position
     */
    protected void onLongItemClick(int position) {
    }

    public void startActivityWithAnimation(Context context, Intent intent, View item) {
        Pair squareParticipant = new Pair<>(item, ViewCompat.getTransitionName(item));
        Activity activity = (Activity) context;
        ActivityOptionsCompat transitionActivityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, squareParticipant);
        context.startActivity(intent, transitionActivityOptions.toBundle());
    }

    //#####################################################################################

    private OnItemViewClickListener<T> mOnItemViewClickListener;

    public void setOnItemViewClickListener(OnItemViewClickListener<T> mOnItemViewClickListener) {
        this.mOnItemViewClickListener = mOnItemViewClickListener;
    }

    public interface OnItemViewClickListener<T> {
        void onItemClick(T t, int position);
    }

    //#####################################################################################

    private OnLongItemClickListener<T> mOnLongItemClickListener;

    public void setOnLongItemClickListener(OnLongItemClickListener<T> mOnLongItemClickListener) {
        this.mOnLongItemClickListener = mOnLongItemClickListener;
    }

    public interface OnLongItemClickListener<T> {
        void onLongClick(T t, int position);
    }

    //#####################################################################################

    public class RecyclerViewHolder extends
            RecyclerView.ViewHolder {
        private final SparseArray<View> viewHolder;
        public View itemView;

        private RecyclerViewHolder(View itemView) {
            super(itemView);
            this.viewHolder = new SparseArray<>();
            this.itemView = itemView;
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