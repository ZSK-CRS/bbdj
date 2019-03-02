package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.graphics.Color;
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
 * Date : 2019/1/7
 * Description :
 */
public class GoodsTypeAdapter extends RecyclerView.Adapter<GoodsTypeAdapter.GoodsViewHolder> {

    private List<HashMap<String, String>> mData = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    private Context mContext;

    private int currentPosition = 0;

    public GoodsTypeAdapter(Context mContext, List<HashMap<String, String>> list) {
        this.mData = list;
        this.mContext = mContext;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    //设置当前的选中的位置
    public void setCheckPosition(int position){
        this.currentPosition = position;
        notifyDataSetChanged();
    }

    @Override
    public GoodsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pop_goods_type, parent, false);
        return new GoodsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GoodsViewHolder holder, final int position) {
        if (holder == null) {
            return;
        }

        if (currentPosition == position) {
            holder.goodsName.setBackgroundResource(R.drawable.shap_check);
            holder.goodsName.setTextColor(Color.parseColor("#0da95f"));
        } else {
            holder.goodsName.setBackgroundResource(R.drawable.shap_un_check);
            holder.goodsName.setTextColor(Color.parseColor("#353535"));
        }

        holder.goodsName.setText(mData.get(position).get("genre_name"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class GoodsViewHolder extends RecyclerView.ViewHolder {

        private TextView goodsName;

        public GoodsViewHolder(View itemView) {
            super(itemView);

            goodsName = itemView.findViewById(R.id.tv_item_goods_name);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
    }
}
