package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/1/29
 * Description :
 */
public class ConsumeRecordAdapter extends RecyclerView.Adapter<ConsumeRecordAdapter.WithdrawViewHolder> {

    private List<HashMap<String,String>>  mList ;

    private Context mContext;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public ConsumeRecordAdapter(Context context, List<HashMap<String,String>> mList) {
        this.mContext = context;
        this.mList = mList;
    }
    @Override
    public WithdrawViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_consume_record,parent,false);
        return new WithdrawViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WithdrawViewHolder holder, int position) {
        HashMap<String,String> map = mList.get(position);
        String budget = map.get("budget");
        String money = map.get("con_amount");
        if ("1".equals(budget)) {
            holder.consumeMoney.setText("-"+money);
        } else {
            holder.consumeMoney.setText("+"+money);
        }
        holder.consumeType.setText(map.get("title"));
        holder.time.setText(map.get("time"));
        holder.overplus.setText("余额："+map.get("con_balance"));

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

    class WithdrawViewHolder extends RecyclerView.ViewHolder {
        TextView consumeType;    //消费类型
        TextView consumeMoney;   //消费金额
        TextView time;    //消费时间
        TextView overplus;   //剩余金额
        public WithdrawViewHolder(View itemView) {
            super(itemView);
            consumeType = itemView.findViewById(R.id.item_consume_type);
            consumeMoney = itemView.findViewById(R.id.item_consume_money);
            time = itemView.findViewById(R.id.item_consume_time);
            overplus = itemView.findViewById(R.id.item_consume_overplus);
        }
    }


    //#############################################################

    public interface OnItemClickListener {
        void OnItemClick(int position);
    }
}
