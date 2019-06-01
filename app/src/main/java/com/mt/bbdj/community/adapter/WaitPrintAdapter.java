package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/1/9
 * Description :  待打印适配器
 */
public class WaitPrintAdapter extends RecyclerView.Adapter<WaitPrintAdapter.HaveFinishViewHolder> {

    private List<HashMap<String, String>> mList;

    private Context context;

    public  OnPrintatOnceListner onPrintatOnceListner;    //打印接口

    private OnCannelOrderClickListener onCannelOrderClickListener;     //取消订单

    public void setOnPrintatOnceListner(OnPrintatOnceListner onPrintatOnceListner) {
       this.onPrintatOnceListner = onPrintatOnceListner;
    }

    public void setOnCannelOrderClickListener(OnCannelOrderClickListener cannelOrderClickListener) {
        this.onCannelOrderClickListener = cannelOrderClickListener;
    }

    public WaitPrintAdapter(Context context, List<HashMap<String, String>> list) {
        this.context = context;
        this.mList = list;
    }

    @Override
    public HaveFinishViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fragment_wait_print,parent,false);
        return new HaveFinishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HaveFinishViewHolder holder, final int position) {
        if (holder == null) {
            return ;
        }
        HashMap<String,String> map = mList.get(position);
        String express_logo = map.get("express_logo");
        Glide.with(context)
                .load(express_logo)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.ic);
        holder.billNumber.setText(map.get("number"));
        holder.sendPerson.setText(map.get("send_name"));
        holder.receivePerson.setText(map.get("collect_name"));
        String createTime = map.get("create_time");
        createTime = DateUtil.changeStampToStandrdTime("MM-dd  HH:mm ",createTime);
        holder.sendTime.setText(createTime);

        holder.btPrintOnce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPrintatOnceListner != null) {
                    onPrintatOnceListner.onPrint(position);
                }
            }
        });

        //取消订单
        holder.btCannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCannelOrderClickListener != null) {
                    onCannelOrderClickListener.OnCannelOrderClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class HaveFinishViewHolder extends RecyclerView.ViewHolder {
        private ImageView ic;     //图标
        private TextView billNumber;  //运单号
        private TextView sendPerson;     //寄件人
        private TextView receivePerson;   //收件人
        private TextView sendTime;   //寄件时间
        private Button btPrintOnce;  //立刻打印
        private Button btCannel;   //取消

        public HaveFinishViewHolder(View itemView) {
            super(itemView);
            ic = itemView.findViewById(R.id.ic_deliver);
            billNumber = itemView.findViewById(R.id.tv_bill_number);
            sendPerson = itemView.findViewById(R.id.tv_send_person);
            receivePerson = itemView.findViewById(R.id.tv_receive_person);
            sendTime = itemView.findViewById(R.id.tv_receive_time);
            btPrintOnce = itemView.findViewById(R.id.bt_immediately_seal);
            btCannel = itemView.findViewById(R.id.bt_cannel_order);
        }
    }


    //#########################    接口  #################################
    //立即打印
    public interface OnPrintatOnceListner{
        void onPrint(int positon);
    }

    //取消订单的点击事件
    public interface  OnCannelOrderClickListener{
        void OnCannelOrderClick(int position);
    }
}
