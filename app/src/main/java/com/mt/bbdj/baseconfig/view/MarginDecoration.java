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

    public MarginDecoration(Context context) {
        margin = context.getResources().getDimensionPixelSize(R.dimen.dp_5);
    }

    @Override
    public void getItemOffsets(
            Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        // outRect.set(margin, margin, margin, margin);
        outRect.set(margin, 5, margin, 10);
    }
}
