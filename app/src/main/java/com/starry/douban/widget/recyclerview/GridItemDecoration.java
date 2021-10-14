package com.starry.douban.widget.recyclerview;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

/**
 * RecyclerView 网格布局item间距
 *
 * @author Starry Jerry
 * @since 2021/8/13.
 */
public class GridItemDecoration extends RecyclerView.ItemDecoration {

    private final int divider;

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        int spanIndex = 0;
        int spanCount = 0;
        if (layoutParams instanceof GridLayoutManager.LayoutParams) {
            GridLayoutManager.LayoutParams gridLayoutParams = (GridLayoutManager.LayoutParams) layoutParams;
            GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
            spanIndex = gridLayoutParams.getSpanIndex();
            spanCount = layoutManager.getSpanCount();
        } else if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams gridLayoutParams = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
            StaggeredGridLayoutManager layoutManager = (StaggeredGridLayoutManager) parent.getLayoutManager();
            spanIndex = gridLayoutParams.getSpanIndex();
            spanCount = layoutManager.getSpanCount();
        }

        if (spanIndex == 0) {
            outRect.left = this.divider;
            outRect.right = this.divider / 2;
        } else if (spanIndex == spanCount - 1) {
            outRect.right = this.divider;
            outRect.left = this.divider / 2;
        } else {
            outRect.right = this.divider / 2;
            outRect.left = this.divider / 2;
        }

        outRect.bottom = this.divider;
    }

    public GridItemDecoration(float divider) {
        this.divider = (int) divider;
    }
}
