package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.utls.ToastUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/1/7
 * Description :
 */
public class GoodsAdapter extends RecyclerView.Adapter<GoodsAdapter.GoodsViewHolder> {

    private List<HashMap<String, String>> mData = new ArrayList<>();

    private OnItemClickListener onItemClickListener;

    private Context mContext;

    private int mPosition = -1;   //当前选中位置

    public void setPosition(int position) {
        this.mPosition = position;
    }

    public GoodsAdapter(Context mContext, List<HashMap<String, String>> list) {
        this.mData = list;
        this.mContext = mContext;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
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
        holder.goodsName.setText(mData.get(position).get("name"));

        if (mPosition == position) {
            holder.goodsName.setBackgroundResource(R.drawable.bg_green_circle);
            holder.goodsName.setTextColor(Color.parseColor("#ffffff"));
        } else {
            holder.goodsName.setBackgroundResource(R.drawable.tv_bg_grey_circle);
            holder.goodsName.setTextColor(Color.parseColor("#353535"));
        }

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

    public interface OnItemClickListener {
        void onItemClick(int position);
    }
}
