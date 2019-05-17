package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mt.bbdj.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/1/22
 * Description :
 */
public class WaterlistAdapter extends RecyclerView.Adapter<WaterlistAdapter.SimpleViewHolder> {

    private List<HashMap<String,String>> mList;

    private Context context;

    public WaterlistAdapter(Context context, List<HashMap<String,String>> list) {

        this.context = context;

        this.mList = list;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_simple_list_string,parent,false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {
        if (holder == null) {
             return;
        }
        HashMap<String,String> data = mList.get(position);
        holder.waterType.setText(data.get("commodity_name"));
        holder.waterNumber.setText("×"+data.get("number"));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        private TextView waterType;    //桶装水品牌
        private TextView waterNumber;   //桶装水数量

        public SimpleViewHolder(View itemView) {
            super(itemView);
            waterType = itemView.findViewById(R.id.id_water_type);
            waterNumber = itemView.findViewById(R.id.id_water_number);
        }
    }

}
