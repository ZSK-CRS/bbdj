package com.mt.bbdj.community.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mt.bbdj.R;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/2/15
 * Description : 短信适配器
 */
public class ComplainAdapter extends RecyclerView.Adapter<ComplainAdapter.MessageSendViewHolder> {

    private List<HashMap<String, String>> datas;

    public ComplainAdapter(List<HashMap<String, String>> datas) {
        this.datas = datas;
    }

    @Override
    public MessageSendViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_complain_send, parent, false);
        return new MessageSendViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MessageSendViewHolder holder, int position) {
        HashMap<String, String> map = datas.get(position);
        String type = map.get("type");
        holder.tvPhone.setText(map.get("phone"));
        holder.tvType.setText(map.get("complain_type"));
        holder.tvName.setText(map.get("name"));
        holder.tvDescribe.setText(map.get("describe"));

        if ("1".equals(type)) {
            holder.ll_result.setVisibility(View.VISIBLE);
            holder.tvResult.setText(map.get("result"));
        } else {
            holder.ll_result.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    class MessageSendViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;    //姓名
        TextView tvType;     //类型
        TextView tvPhone;    //电话号码
        TextView tvDescribe;   //内容
        TextView tvResult;    //反馈结果
        LinearLayout ll_result;

        public MessageSendViewHolder(View itemView) {
            super(itemView);
            tvPhone = itemView.findViewById(R.id.tv_complain_phone);
            tvName = itemView.findViewById(R.id.tv_complain_name);
            tvType = itemView.findViewById(R.id.tv_complain_type);
            tvDescribe = itemView.findViewById(R.id.tv_complain_describe);
            tvResult = itemView.findViewById(R.id.tv_complain_result);
            ll_result = itemView.findViewById(R.id.ll_result);
        }
    }
}
