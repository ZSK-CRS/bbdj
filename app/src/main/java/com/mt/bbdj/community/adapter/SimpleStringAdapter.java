package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mt.bbdj.R;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/1/22
 * Description :
 */
public class SimpleStringAdapter extends RecyclerView.Adapter<SimpleStringAdapter.SimpleViewHolder> {

    private List<HashMap<String,String>> mList;

    private Context context;

    private OnItemClickListener itemClickListener;

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public SimpleStringAdapter(Context context, List<HashMap<String,String>> list) {

        this.context = context;

        this.mList = list;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_simple_string,parent,false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, final int position) {
        if (holder == null) {
             return;
        }
        HashMap<String,String> data = mList.get(position);
        holder.fastName.setText(data.get("express"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.OnItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class SimpleViewHolder extends RecyclerView.ViewHolder {

        private TextView fastName;    //快递名称

        public SimpleViewHolder(View itemView) {
            super(itemView);
            fastName = itemView.findViewById(R.id.item_fast_name);
        }
    }

    //################################ 接口回调 ###########################################
    public interface OnItemClickListener{
        void OnItemClick(int position);
    }
}
