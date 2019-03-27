package com.mt.bbdj.community.adapter;

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
 * Date : 2019/3/26
 * Description :
 */
public class YesterDayPayAdapter extends RecyclerView.Adapter<YesterDayPayAdapter.YesterDayPayViewHolder> {

    private List<HashMap<String, String>> mList;

    public YesterDayPayAdapter(List<HashMap<String, String>> mList) {
        this.mList = mList;
    }

    @Override
    public YesterDayPayViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_yester_pay,parent,false);
        return new YesterDayPayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(YesterDayPayViewHolder holder, int position) {
        HashMap<String,String> map = mList.get(position);
        holder.orderNumbewr.setText(map.get("serialnumber"));
        holder.time.setText(map.get("time"));
        holder.type.setText(map.get("types"));
        holder.detail.setText(map.get("budget"));
        holder.money.setText(map.get("money"));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class YesterDayPayViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumbewr;
        TextView time;
        TextView type;
        TextView detail;
        TextView money;

        public YesterDayPayViewHolder(View itemView) {
            super(itemView);
            orderNumbewr = itemView.findViewById(R.id.item_order_number);
            time = itemView.findViewById(R.id.item_time);
            type = itemView.findViewById(R.id.item_type);
            detail = itemView.findViewById(R.id.item_detail);
            money = itemView.findViewById(R.id.item_money);
        }
    }
}
