package com.starry.douban.widget.recyclerview;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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
        GridLayoutManager.LayoutParams layoutParams = (GridLayoutManager.LayoutParams) view.getLayoutParams();
        GridLayoutManager layoutManager = (GridLayoutManager) parent.getLayoutManager();
        int spanIndex = layoutParams.getSpanIndex();
        int spanCount = layoutManager.getSpanCount();
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
