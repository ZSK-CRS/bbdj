package com.mt.bbdj.community.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.mt.bbdj.R;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/2/15
 * Description : 短信适配器
 */
public class MessageSendAdapter extends RecyclerView.Adapter<MessageSendAdapter.MessageSendViewHolder> {

    private List<HashMap<String,String>> datas;

    private int type = 0;   //发送失败
    public MessageSendAdapter(List<HashMap<String,String>> datas,int type) {
        this.datas = datas;
        this.type = type;
    }

    private OnSendMessageListener sendMessageListener;

    //设置发送短信监听
    public void setSendMessageListener(OnSendMessageListener sendMessageListener) {
        this.sendMessageListener = sendMessageListener;
    }

    @Override
    public MessageSendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_send,parent,false);
        return new MessageSendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageSendViewHolder holder, final int position) {
        HashMap<String,String> map = datas.get(position);
        holder.tvDindan.setText(map.get("dingdan"));
        holder.tvYundan.setText(map.get("yundan"));
        holder.tvState.setText(map.get("sendstate"));
        holder.tvPhone.setText(map.get("phone"));
        holder.tvName.setText(map.get("name"));
        holder.tvContent.setText(map.get("content"));

        if (type == 2) {
            holder.btSend.setVisibility(View.VISIBLE);
            holder.tvState.setTextColor(Color.parseColor("#fe5e5e"));
        } else {
            holder.btSend.setVisibility(View.GONE);
            holder.tvState.setTextColor(Color.parseColor("#777777"));
        }

        holder.btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sendMessageListener != null) {
                    sendMessageListener.onSendMessage(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class MessageSendViewHolder extends RecyclerView.ViewHolder {
        TextView tvDindan;    //订单
        TextView tvYundan;   //运单
        TextView tvState;     //发送状态
        TextView tvPhone;    //电话号码
        TextView tvName;      //名字
        TextView tvContent;   //内容
        Button btSend;         //重新发送
        public MessageSendViewHolder(View itemView) {
            super(itemView);
            tvDindan = itemView.findViewById(R.id.tv_message_dingdan);
            tvYundan = itemView.findViewById(R.id.tv_message_yundan);
            tvState = itemView.findViewById(R.id.tv_message_state);
            tvPhone = itemView.findViewById(R.id.tv_message_phone);
            tvName = itemView.findViewById(R.id.tv_message_name);
            tvContent = itemView.findViewById(R.id.tv_message_content);
            btSend = itemView.findViewById(R.id.tv_message_again);
        }
    }


    //#############################   接口回调  #################################
    //发送短信
    public interface OnSendMessageListener{
        void onSendMessage(int position);
    }
}
