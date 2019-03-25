package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.utls.StringUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/1/29
 * Description :
 */
public class RechargeRecordAdapter extends RecyclerView.Adapter<RechargeRecordAdapter.WithdrawViewHolder> {

    private List<HashMap<String, String>> mList;

    private Context mContext;

    public RechargeRecordAdapter(Context context, List<HashMap<String, String>> mList) {
        this.mContext = context;
        this.mList = mList;
    }

    @Override
    public WithdrawViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_recharge_record, parent, false);
        return new WithdrawViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WithdrawViewHolder holder, int position) {
        HashMap<String, String> map = mList.get(position);

        holder.consumeType.setText(map.get("title"));
        holder.consumeMoney.setText(map.get("money")+"元");
        String type = map.get("types");
        holder.time.setText(map.get("time"));
        if ("2".equals(type)) {
            holder.overplus.setText("微信");
        } else {
            holder.overplus.setText("支付宝");
        }
        holder.orderNumber.setText("("+map.get("order_number")+")");

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class WithdrawViewHolder extends RecyclerView.ViewHolder {
        TextView consumeType;    //消费类型
        TextView consumeMoney;   //消费金额
        TextView time;    //消费时间
        TextView overplus;   //支付方式
        TextView orderNumber;    //订单号

        public WithdrawViewHolder(View itemView) {
            super(itemView);
            consumeType = itemView.findViewById(R.id.item_consume_type);
            consumeMoney = itemView.findViewById(R.id.item_consume_money);
            time = itemView.findViewById(R.id.item_consume_time);
            overplus = itemView.findViewById(R.id.item_consume_overplus);
            orderNumber = itemView.findViewById(R.id.item_consume_order);
        }
    }
}
