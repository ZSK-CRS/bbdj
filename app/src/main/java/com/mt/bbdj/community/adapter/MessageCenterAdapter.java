package com.mt.bbdj.community.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mt.bbdj.R;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/2/19
 * Description :
 */
public class MessageCenterAdapter extends RecyclerView.Adapter<MessageCenterAdapter.MessageCenterViewHolder> {

    private List<HashMap<String, String>> datas;

    private OnItemClickListener itemClickListener;

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public MessageCenterAdapter(List<HashMap<String, String>> datas) {
        this.datas = datas;
    }

    @Override
    public MessageCenterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message,parent,false);
        return new MessageCenterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageCenterViewHolder holder, final int position) {
        HashMap<String,String> map = datas.get(position);
        String type = map.get("type");
        holder.tvTitle.setText(map.get("title"));
        holder.tvContent.setText(map.get("describes"));
        holder.tvTime.setText(map.get("time"));
        String states = map.get("states");    //是否已读

        if ("1".equals(states)) {
            holder.unReadPoint.setVisibility(View.VISIBLE);
        } else {
            holder.unReadPoint.setVisibility(View.GONE);
        }

        if ("0".equals(type)) {
            holder.bgView.setBackgroundResource(R.drawable.shape_point_green);
            holder.ivType.setBackgroundResource(R.drawable.ic_message_notification);
        } else if ("1".equals(type)) {
            holder.bgView.setBackgroundResource(R.drawable.shape_point_blue);
            holder.ivType.setBackgroundResource(R.drawable.ic_message_system);
        } else {
            holder.bgView.setBackgroundResource(R.drawable.shape_point_yellow);
            holder.ivType.setBackgroundResource(R.drawable.ic_message_exception);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.OnClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class MessageCenterViewHolder extends RecyclerView.ViewHolder {
        private ImageView bgView;    //背景
        private ImageView ivType;    //类型图片
        private TextView  tvTitle;   //标题
        private TextView tvContent;   //内容
        private TextView tvTime;   //时间
        private ImageView unReadPoint;    //未读状态
        public MessageCenterViewHolder(View itemView) {
            super(itemView);
            bgView = itemView.findViewById(R.id.iv_item_bg);
            ivType = itemView.findViewById(R.id.iv_item_type);
            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvContent = itemView.findViewById(R.id.tv_item_content);
            tvTime = itemView.findViewById(R.id.tv_item_time);
            unReadPoint = itemView.findViewById(R.id.item_iv_unread);
        }
    }

    //##########################################################################
    public interface OnItemClickListener{
        void OnClick(int position);
    }
}
