package com.mt.bbdj.community.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/1/2
 * Description :  短信充值适配器
 */
public class MessagePannelAdapter extends RecyclerView.Adapter<MessagePannelAdapter.MessagePannelViewHolder> {

    private List<HashMap<String, String>> mList = new ArrayList<>();

    private OnItemClickListener mItemClickListener;

    private int mClickPosition = -1;    //点击的位置

    public MessagePannelAdapter(List<HashMap<String, String>> mList) {
        this.mList = mList;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.mItemClickListener = itemClickListener;
    }

    //记录点击的位置
    public void setClickPosition(int clickPosition ){
        this.mClickPosition = clickPosition;
    }

    @Override
    public MessagePannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_pannel, parent, false);
        return new MessagePannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessagePannelViewHolder holder, final int position) {
        if (holder == null) {
            return ;
        }

        HashMap<String,String> item = mList.get(position);
        holder.messageMoney.setText(item.get("messageMoney"));
        holder.messageNumber.setText(item.get("messageNumber"));

        if(mClickPosition == position) {   //表示的是点击的位置
            holder.messageLayout.setBackgroundResource(R.drawable.item_bg_check_true);
            holder.messageic.setVisibility(View.VISIBLE);
            holder.messageMoney.setTextColor(Color.parseColor("#0da95f"));
            holder.messageNumber.setTextColor(Color.parseColor("#0da95f"));

        } else {    //表示的是未点击的位置
            holder.messageLayout.setBackgroundResource(R.drawable.item_bg_check_false);
            holder.messageic.setVisibility(View.INVISIBLE);
            holder.messageMoney.setTextColor(Color.parseColor("#bbbbbb"));
            holder.messageNumber.setTextColor(Color.parseColor("#bbbbbb"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    mItemClickListener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class MessagePannelViewHolder extends RecyclerView.ViewHolder {
        private TextView messageNumber;     //数量
        private TextView messageMoney;     //金额
        private ImageView messageic;     //选中图标
        private RelativeLayout messageLayout;    //整体布局

        public MessagePannelViewHolder(View itemView) {
            super(itemView);
            messageNumber = itemView.findViewById(R.id.item_message_number);
            messageMoney = itemView.findViewById(R.id.item_message_money);
            messageic = itemView.findViewById(R.id.item_check_ic);
            messageLayout = itemView.findViewById(R.id.item_message_layout);
        }
    }


    // ################################  接口回调 #########################################

    //点击事件接口
    public interface OnItemClickListener {
        void onClick(int position);
    }
}
