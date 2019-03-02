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

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/2/26
 * Description :
 */
public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyOrderViewHolder> {

    private List<HashMap<String, String>> mList;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    private Context context;

    public MyOrderAdapter(Context context,List<HashMap<String, String>> mList) {
        this.mList = mList;
        this.context = context;
    }

    @Override
    public MyOrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_order, parent, false);
        return new MyOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyOrderViewHolder holder, final int position) {
        HashMap<String,String> map = mList.get(position);
        holder.goodsName.setText(map.get("product_name"));
        holder.goodsState.setText(map.get("state"));
        holder.goodsNumber.setText("×"+map.get("number"));
        holder.goodsType.setText(map.get("genre_name"));
        holder.goodsMoney.setText(map.get("money"));
        Glide.with(context).load(map.get("thumb")).error(R.drawable.ic_no_picture).into(holder.goodsLogo);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MyOrderViewHolder extends RecyclerView.ViewHolder {

        ImageView goodsLogo;
        TextView goodsName;
        TextView goodsState;
        TextView goodsNumber;
        TextView goodsType;
        TextView goodsMoney;

        public MyOrderViewHolder(View itemView) {
            super(itemView);
            goodsLogo = itemView.findViewById(R.id.iv_logo);
            goodsName = itemView.findViewById(R.id.tv_goods_name);
            goodsState = itemView.findViewById(R.id.tv_goods_state);
            goodsNumber = itemView.findViewById(R.id.tv_tv_goods_number);
            goodsType = itemView.findViewById(R.id.tv_goods_type);
            goodsMoney = itemView.findViewById(R.id.tv_goods_money);
        }
    }

    public interface OnItemClickListener{
        void OnItemClick(int position);
    }
}
