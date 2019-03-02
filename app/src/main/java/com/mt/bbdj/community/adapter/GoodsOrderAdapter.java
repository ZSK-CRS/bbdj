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
import com.mt.bbdj.baseconfig.model.GoodsMessage;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.view.AddView;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/2/28
 * Description :
 */
public class GoodsOrderAdapter extends RecyclerView.Adapter<GoodsOrderAdapter.GoodsOrderViewHolder> {

    private List<GoodsMessage.Goods> mapList;

    private Context context;

    public GoodsOrderAdapter(Context context,List<GoodsMessage.Goods> mapList) {
        this.context = context;
        this.mapList = mapList;
    }
    @Override
    public GoodsOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_goods_order,parent,false);
        return new GoodsOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(GoodsOrderViewHolder holder, int position) {
        GoodsMessage.Goods map = mapList.get(position);
        holder.goodName.setText(map.getGoodsName());
        holder.goodsType.setText(map.getGoodsTypeName());
        holder.goodsPrice.setText("￥"+map.getGoodsPrice());
        Glide.with(context).load(map.getGoodsPicture()).error(R.drawable.ic_no_picture).into(holder.logo);
        int number = IntegerUtil.getStringChangeToNumber(map.getGoodsNumber());
        holder.addView.setValue(number);
    }

    @Override
    public int getItemCount() {
        return mapList.size();
    }

    class GoodsOrderViewHolder extends RecyclerView.ViewHolder{
        ImageView logo;
        TextView goodName;
        TextView goodsType;
        TextView goodsPrice;
        AddView addView;
        public GoodsOrderViewHolder(View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.iv_logo);
            goodName = itemView.findViewById(R.id.tv_goods_name);
            goodsPrice = itemView.findViewById(R.id.tv_goods_money);
            goodsType = itemView.findViewById(R.id.tv_goods_type);
            addView = itemView.findViewById(R.id.addview);
        }
    }
}
