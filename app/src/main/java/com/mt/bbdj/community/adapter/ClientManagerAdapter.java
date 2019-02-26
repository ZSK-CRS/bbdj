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
 * Date : 2019/2/25
 * Description :
 */
public class ClientManagerAdapter extends RecyclerView.Adapter<ClientManagerAdapter.ClientViewHolder> {

    private Context context;
    private List<HashMap<String, String>> mapList;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public ClientManagerAdapter(Context context, List<HashMap<String, String>> mapList) {
        this.context = context;
        this.mapList = mapList;
    }

    @Override
    public ClientViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_client_manager, parent, false);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClientViewHolder holder, final int position) {
        HashMap<String, String> map = mapList.get(position);
        holder.name.setText(map.get("customer_realname"));
        holder.tvTodaySum.setText(map.get("todaysum"));
        holder.tvTodayMoney.setText(map.get("todaymoney"));
        holder.tvMonthSum.setText(map.get("monthsum"));
        holder.tvMonthMoney.setText(map.get("monthmoney"));
        holder.tvContent.setText(map.get("content"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.OnClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mapList.size();
    }

    class ClientViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView tvTodaySum;
        TextView tvTodayMoney;
        TextView tvMonthSum;
        TextView tvMonthMoney;
        TextView tvContent;

        public ClientViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tv_name);
            tvTodaySum = itemView.findViewById(R.id.tv_today_sume);
            tvTodayMoney = itemView.findViewById(R.id.tv_today_money);
            tvMonthSum = itemView.findViewById(R.id.tv_month_sum);
            tvMonthMoney = itemView.findViewById(R.id.tv_month_money);
            tvContent = itemView.findViewById(R.id.tv_content);
        }
    }

    //***************************** 接口回调  ********************************************
    public interface OnItemClickListener{
        void OnClick(int position);
    }
}
