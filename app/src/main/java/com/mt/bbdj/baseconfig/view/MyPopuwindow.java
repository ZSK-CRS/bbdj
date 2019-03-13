package com.mt.bbdj.baseconfig.view;

import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.widget.PopupWindow;

/**
 * Author : ZSK
 * Date : 2019/3/11
 * Description :
 */
public class MyPopuwindow extends PopupWindow {

    public MyPopuwindow(View contentView, int width, int height) {
        super(contentView, width, height, false);
    }

    @Override
    public void showAsDropDown(View anchor) {
        if(Build.VERSION.SDK_INT >= 24){
            Rect visibleFrame = new Rect();
            anchor.getGlobalVisibleRect(visibleFrame);
            int height = anchor.getResources().getDisplayMetrics().heightPixels - visibleFrame.bottom;
            setHeight(height);
        }
        super.showAsDropDown(anchor);
    }
}
