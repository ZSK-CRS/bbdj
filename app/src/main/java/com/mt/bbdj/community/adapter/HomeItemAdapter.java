package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.CategoryBean;

import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/2/18
 * Description :
 */
public class HomeItemAdapter extends BaseAdapter {

    private Context context;
    private List<CategoryBean.DataBean.DataListBean> foodDatas;

    public HomeItemAdapter(Context context, List<CategoryBean.DataBean.DataListBean> foodDatas) {
        this.context = context;
        this.foodDatas = foodDatas;
    }

    @Override
    public int getCount() {
        if (foodDatas != null) {
            return foodDatas.size();
        } else {
            return 10;
        }
    }

    @Override
    public Object getItem(int position) {
        return foodDatas.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CategoryBean.DataBean.DataListBean subcategory = foodDatas.get(position);
        ViewHold viewHold = null;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_goods_home_item, null);
            viewHold = new ViewHold();
            viewHold.tv_name = (TextView) convertView.findViewById(R.id.item_home_name);
            viewHold.iv_icon = (ImageView) convertView.findViewById(R.id.item_album);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }
        String title = subcategory.getTitle();
        if ("短信".equals(title)) {
            viewHold.iv_icon.setBackgroundResource(R.drawable.ic_message_);
        } else if ("面单".equals(title)) {
            viewHold.iv_icon.setBackgroundResource(R.drawable.ic_pannel_);
        } else {
            Glide.with(context).load(subcategory.getImgURL()).error(R.drawable.ic_no_picture).into(viewHold.iv_icon);
        }
        viewHold.tv_name.setText(subcategory.getTitle());
        return convertView;
    }

    private static class ViewHold {
        private TextView tv_name;
        private ImageView iv_icon;
    }

}
