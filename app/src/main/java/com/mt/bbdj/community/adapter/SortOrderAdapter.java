package com.mt.bbdj.community.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mt.bbdj.R;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/3/26
 * Description :  数据排行榜
 */
public class SortOrderAdapter extends RecyclerView.Adapter<SortOrderAdapter.YesterDayPayViewHolder> {

    private List<HashMap<String, String>> mList;

    public SortOrderAdapter(List<HashMap<String, String>> mList) {
        this.mList = mList;
    }

    @Override
    public YesterDayPayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sort_order, parent, false);
        return new YesterDayPayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(YesterDayPayViewHolder holder, int position) {
        HashMap<String, String> map = mList.get(position);
        holder.name.setText(map.get("userName"));
        holder.orderNumber.setText(map.get("total"));
        holder.sendNumber.setText(map.get("mailsum"));
        holder.paiNumber.setText(map.get("piesum"));
        holder.serviceNumber.setText(map.get("servicesum"));
        holder.sortTag.setVisibility(View.GONE);
        holder.sortLogo.setVisibility(View.GONE);

        if (position == 0) {
            holder.sortLogo.setVisibility(View.VISIBLE);
            holder.sortLogo.setBackgroundResource(R.drawable.ic_first);
        } else if (position == 1) {
            holder.sortLogo.setVisibility(View.VISIBLE);
            holder.sortLogo.setBackgroundResource(R.drawable.ic_second);
        } else if (position == 2) {
            holder.sortLogo.setVisibility(View.VISIBLE);
            holder.sortLogo.setBackgroundResource(R.drawable.ic_three);
        } else {
            holder.sortLogo.setVisibility(View.GONE);
            holder.sortTag.setVisibility(View.VISIBLE);
            holder.sortTag.setText(position + 1 + "");
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class YesterDayPayViewHolder extends RecyclerView.ViewHolder {
        ImageView sortLogo;    //设置图标
        TextView sortTag;    //顺序
        TextView name;    //店名
        TextView orderNumber;    //订单
        TextView sendNumber;    //寄件数
        TextView paiNumber;   //派件数
        TextView serviceNumber;   //服务数

        public YesterDayPayViewHolder(View itemView) {
            super(itemView);
            sortLogo = itemView.findViewById(R.id.item_sort_logo);
            name = itemView.findViewById(R.id.item_name);
            orderNumber = itemView.findViewById(R.id.item_order);
            sendNumber = itemView.findViewById(R.id.item_send);
            paiNumber = itemView.findViewById(R.id.item_pai);
            serviceNumber = itemView.findViewById(R.id.item_service);
            sortTag = itemView.findViewById(R.id.tv_order_tag);
        }
    }
}
