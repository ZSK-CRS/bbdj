package com.mt.bbdj.community.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mt.bbdj.R;

import java.io.ObjectStreamException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2018/12/26
 * Description :  MyGridView的适配器
 */
public class MyGridViewAdapter extends BaseAdapter{

    private List<HashMap<String,Object>> mData;

    @Override
    public int getCount() {
        return mData.size();
    }

    public MyGridViewAdapter(List<HashMap<String,Object>> list) {
        this.mData = list;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (viewHolder == null) {
            convertView = LayoutInflater
                    .from(parent.getContext()).inflate(R.layout.main_item,parent,false);
            viewHolder = new ViewHolder();
            viewHolder.ic = convertView.findViewById(R.id.img_main_item);
            viewHolder.name = convertView.findViewById(R.id.tv_main_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.ic.setImageResource((int) mData.get(position).get("ic"));
        viewHolder.name.setText(mData.get(position).get("name").toString());
        return convertView;
    }

    public class ViewHolder{
        TextView name;
        ImageView ic;
    }
}
