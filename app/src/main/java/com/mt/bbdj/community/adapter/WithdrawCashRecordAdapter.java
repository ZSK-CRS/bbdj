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

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/1/29
 * Description :
 */
public class WithdrawCashRecordAdapter extends RecyclerView.Adapter<WithdrawCashRecordAdapter.WithdrawViewHolder> {

    private List<HashMap<String,String>>  mList ;

    private Context mContext;

    public WithdrawCashRecordAdapter(Context context,List<HashMap<String,String>> mList) {
        this.mContext = context;
        this.mList = mList;
    }
    @Override
    public WithdrawViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_withdraw_record,parent,false);
        return new WithdrawViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WithdrawViewHolder holder, int position) {
        HashMap<String,String> map = mList.get(position);
        String type = map.get("type");
        String Flag = map.get("Flag");
        String card_number = map.get("card_number");
        String pay_account = map.get("pay_account");
        card_number = StringUtil.splitStringFromLast(card_number,4);
        pay_account = StringUtil.splitStringFromLast(pay_account,4);

        if ("1".equals(type)) {
            holder.bankName.setText("银行卡提现 "+"("+card_number+")");
        } else {
            holder.bankName.setText("支付宝提现 "+"("+pay_account+")");
        }
        if ("1".equals(Flag)) {
            holder.state.setText("待审核");
        } else if ("2".equals(Flag)) {
            holder.state.setText("提现成功");
        } else {
            holder.state.setText("提现失败");
        }
        holder.moneyNumber.setText(map.get("money")+"元");
        String time = map.get("Time");

        time =  DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm",time);
        holder.time.setText(time);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class WithdrawViewHolder extends RecyclerView.ViewHolder {
        TextView bankName;    //银行账户
        TextView moneyNumber;   //转账金额
        TextView time;    //转账时间
        TextView state;   //状态
        public WithdrawViewHolder(View itemView) {
            super(itemView);
            bankName = itemView.findViewById(R.id.item_bank_name);
            moneyNumber = itemView.findViewById(R.id.item_bank_money);
            time = itemView.findViewById(R.id.item_bank_time);
            state = itemView.findViewById(R.id.tv_state);
        }
    }
}
