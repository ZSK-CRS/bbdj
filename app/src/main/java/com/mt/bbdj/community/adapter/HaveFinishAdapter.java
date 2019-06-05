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
 * Description :  已处理适配器
 */
public class HaveFinishAdapter extends RecyclerView.Adapter<HaveFinishAdapter.HaveFinishViewHolder> {

    private List<HashMap<String, String>> mList;

    private Context context;

    private OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public HaveFinishAdapter(Context context, List<HashMap<String, String>> list) {
        this.context = context;
        this.mList = list;
    }

    @Override
    public HaveFinishViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_have_finish,parent,false);
        return new HaveFinishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HaveFinishViewHolder holder, final int position) {
        if (holder == null) {
            return ;
        }
        HashMap<String,String> map = mList.get(position);
        String express_logo = map.get("express_logo");
        String is_reminder = map.get("is_reminder");
        Glide.with(context)
                .load(express_logo)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.ic);
        holder.billNumber.setText(map.get("waybill_number"));
        holder.sendPerson.setText(map.get("send_name"));
        holder.receivePerson.setText(map.get("collect_name"));
        String createTime = map.get("create_time");
        createTime = DateUtil.changeStampToStandrdTime("MM-dd  HH:mm ",createTime);
        holder.sendTime.setText(createTime);

        if ("2".equals(is_reminder)) {
            holder.tv_state.setText("已催单");
            holder.handleNow.setEnabled(false);
            holder.handleNow.setBackgroundResource(R.drawable.bt_bg_2);
        } else {
            holder.tv_state.setText("已揽件");
            holder.handleNow.setEnabled(true);
            holder.handleNow.setBackgroundResource(R.drawable.bt_bg_1);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });

        //催单
        holder.handleNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onHandleNow(position);
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
        private Button handleNow;    //催单
        private TextView tv_state;    //催单状态

        public HaveFinishViewHolder(View itemView) {
            super(itemView);
            ic = itemView.findViewById(R.id.ic_deliver);
            billNumber = itemView.findViewById(R.id.tv_bill_number);
            sendPerson = itemView.findViewById(R.id.tv_send_person);
            receivePerson = itemView.findViewById(R.id.tv_receive_person);
            sendTime = itemView.findViewById(R.id.tv_receive_time);
            handleNow = itemView.findViewById(R.id.bt_hanlde_now);
            tv_state = itemView.findViewById(R.id.tv_state);
        }
    }

    //################################### 接口回调 #################################
    public interface OnItemClickListener{
        void onItemClick(int position);
        void onHandleNow(int position);    //催单
    }

}
