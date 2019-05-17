package com.mt.bbdj.baseconfig.view;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mt.bbdj.R;

/**
 * Author : ZSK
 * Date : 2019/1/2
 * Description :
 */
public class MarginDecoration  extends RecyclerView.ItemDecoration {
    private int margin;
    private int top;
    private int right;
    private int left;
    private int bottom;


    public MarginDecoration(Context context) {
        margin = context.getResources().getDimensionPixelSize(R.dimen.dp_5);
        top = 5;
        bottom = 10;
    }

    public MarginDecoration(Context context,int left,int top,int right,int bottom) {
        margin = context.getResources().getDimensionPixelSize(R.dimen.dp_5);
        this.top = top;
        this.bottom = bottom;
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // outRect.set(margin, margin, margin, margin);
        outRect.set(margin, top, margin, bottom);
    }
}
