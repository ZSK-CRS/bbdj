package com.mt.bbdj.community.adapter;

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
 * Date : 2019/1/2
 * Description :   充值记录适配器
 */
public class RechargeRecodeAdapter extends RecyclerView.Adapter<RechargeRecodeAdapter.RecodeAdapter> {

    private List<HashMap<String,String>> mData = new ArrayList<>();

    public RechargeRecodeAdapter(List<HashMap<String,String>> list) {
        this.mData = list;
    }
    @Override
    public RecodeAdapter onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recharge_recode,parent,false);
        return new RecodeAdapter(view);
    }

    @Override
    public void onBindViewHolder(RecodeAdapter holder, int position) {
        if (holder == null) {
            return ;
        }
        HashMap<String,String> item = mData.get(position);
        holder.rechargeMoney.setText(item.get("message_money"));
        holder.rechargeTime.setText(item.get("message_time"));
        holder.reachargeNumber.setText(item.get("message_number"));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class RecodeAdapter extends RecyclerView.ViewHolder{
        private TextView rechargeState;       //充值状态
        private TextView rechargeMoney;      //充值金额
        private TextView rechargeTime;        //充值时间
        private TextView reachargeNumber;    //充值数量
        public RecodeAdapter(View itemView) {
            super(itemView);
            rechargeState = itemView.findViewById(R.id.item_recharge_state);
            rechargeMoney = itemView.findViewById(R.id.item_recharge_money);
            rechargeTime = itemView.findViewById(R.id.item_recharge_time);
            reachargeNumber = itemView.findViewById(R.id.item_recharge_number);
        }
    }
}
