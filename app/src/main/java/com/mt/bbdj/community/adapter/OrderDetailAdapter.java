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
public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.OrderDetailViewHolder> {

    private Context context;
    private List<HashMap<String,String>> mList;

    public OrderDetailAdapter(Context context, List<HashMap<String,String>> mList) {
        this.mList = mList;
        this.context = context;
    }

    @Override
    public OrderDetailViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order,parent,false);
        return new OrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(OrderDetailViewHolder holder, int position) {
        HashMap<String,String> map = mList.get(position);
        holder.money.setText(map.get("money"));
        holder.yundanhao.setText(map.get("yundanhao"));
        holder.time.setText(map.get("time"));
        holder.send.setText(map.get("send_name"));
        holder.receive.setText(map.get("collect_name"));
        Glide.with(context).load(map.get("expressLogo")).error(R.drawable.ic_no_picture).into(holder.logo);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class OrderDetailViewHolder extends RecyclerView.ViewHolder{
        ImageView logo;
        TextView yundanhao;
        TextView time;
        TextView send;
        TextView receive;
        TextView money;

        public OrderDetailViewHolder(View itemView) {
            super(itemView);
            logo = itemView.findViewById(R.id.iv_logo);
            yundanhao = itemView.findViewById(R.id.tv_yundan);
            time = itemView.findViewById(R.id.tv_time);
            send = itemView.findViewById(R.id.tv_send);
            receive = itemView.findViewById(R.id.tv_receive);
            money = itemView.findViewById(R.id.tv_money);
        }
    }
}
