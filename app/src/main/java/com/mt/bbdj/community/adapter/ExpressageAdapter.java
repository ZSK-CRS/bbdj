package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mt.bbdj.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/1/7
 * Description :  物流公司的适配器
 */
public class ExpressageAdapter extends RecyclerView.Adapter<ExpressageAdapter.ExpressageViewHolder> {

    private List<HashMap<String,String>> mData = new ArrayList<>();

    private Context mContext;

    private OnItemClickListener onItemClickListener;   //点击事件

    public ExpressageAdapter(Context context,List<HashMap<String,String>> data) {
         this.mData = data;
         this.mContext = context;
    }

    public void setOnItemClickLister (OnItemClickListener itemClickLister) {
        this.onItemClickListener = itemClickLister;
    }

    @Override
    public ExpressageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expressage_layout,parent,false);
        return new ExpressageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ExpressageViewHolder holder, final int position) {
        if (holder == null) {
             return ;
        }
        HashMap<String,String> map = mData.get(position);
        holder.expressageName.setText(map.get("express_name"));
        String logoPath = map.get("express_logo");
        if (logoPath != null && !"null".equals(logoPath) && !"".equals(logoPath)) {
            Glide.with(mContext)
                    .load(logoPath)
                    .into(holder.expressageLogo);
        }


        //点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class ExpressageViewHolder extends RecyclerView.ViewHolder{

        private TextView expressageName;   //快递名称
        private ImageView expressageLogo;    //快递图标

        public ExpressageViewHolder(View itemView) {
            super(itemView);
            expressageLogo = itemView.findViewById(R.id.iv_item_expressage_logo);
            expressageName = itemView.findViewById(R.id.tv_item_expressage_name);
        }
    }


    //################################## 接口回调 ################################
    public interface OnItemClickListener{
        void onClick(int position);
    }
}
