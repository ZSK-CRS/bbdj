package com.mt.bbdj.baseconfig.utls;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.mt.bbdj.R;

/**
 * Author : ZSK
 * Date : 2018/12/28
 * Description :  图片选择对话框工具类
 */
public class PictureSelectPopUtil {

    private View rootView;     //弹出框填充的布局

    private PopupWindow mPopuWindow;

    private PictureSelectPopUtil mDialog;

    private PictureSelectListener mPictureSelectListener;

    public void setOnItemClickListener(PictureSelectListener listener) {
        this.mPictureSelectListener = listener;
    }

    private PictureSelectPopUtil() {
    }

    public static PictureSelectPopUtil getInstance() {
        return DialogUtilHolder.INSTANCE;
    }

    private static class DialogUtilHolder {
        public static PictureSelectPopUtil INSTANCE = new PictureSelectPopUtil();
    }

    //弹出图片选择对话框
    public void showPictureSelectDialog(Context context, View parent) {
        createPictureSelectView(context);
        if (mPopuWindow != null && !mPopuWindow.isShowing()) {
            mPopuWindow.showAtLocation(parent, Gravity.NO_GRAVITY, 0, 0);
        }
    }

    //创建弹出框的内容
    private void createPictureSelectView(Context context) {
        rootView = LayoutInflater.from(context).inflate(R.layout.view_picture_select, null);
        mPopuWindow = new PopupWindow(rootView, LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        //设置为失去焦点 方便监听返回键的监听
        mPopuWindow.setFocusable(false);
        // 如果想要popupWindow 遮挡住状态栏可以加上这句代码
        //popupWindow.setClippingEnabled(false);
        mPopuWindow.setBackgroundDrawable(new BitmapDrawable());
        mPopuWindow.setOutsideTouchable(false);

        initLayout(context);
    }

    private void initLayout(Context context) {
        Button takeCamera = rootView.findViewById(R.id.bt_take_camera);
        Button takeAlbum = rootView.findViewById(R.id.bt_take_from_album);
        Button cannelBt = rootView.findViewById(R.id.bt_cancle);
        //拍照
        takeCamera.setOnClickListener(new ClickListener());
        //相册选择
        takeAlbum.setOnClickListener(new ClickListener());
        //取消
        cannelBt.setOnClickListener(new ClickListener());
    }



    public interface PictureSelectListener {
        void onClick(View view);
    }

    private class ClickListener implements View.OnClickListener{

        @Override
        public void onClick(View view) {
            if (mPictureSelectListener != null) {
                mPictureSelectListener.onClick(view);
            }
        }
    }
}
