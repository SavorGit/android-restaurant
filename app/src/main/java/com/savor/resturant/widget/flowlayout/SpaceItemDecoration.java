package com.savor.resturant.widget.flowlayout;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by xiangcheng on 17/8/22.
 */

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int topbottom;
    private int leftrigh;

//    private int space;

//    public SpaceItemDecoration(int space) {
//        this.space = space;
//    }

    public SpaceItemDecoration(int leftrigh,int topbottom) {
        this.leftrigh = leftrigh;
        this.topbottom = topbottom;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = topbottom;
        outRect.left = leftrigh;
        outRect.right = leftrigh;
        outRect.bottom = topbottom;
    }
}