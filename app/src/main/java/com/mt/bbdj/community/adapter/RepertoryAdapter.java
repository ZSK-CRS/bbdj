package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;

import java.util.HashMap;
import java.util.List;

import butterknife.OnClick;

/**
 * Author : ZSK
 * Date : 2019/3/12
 * Description :
 */
public class RepertoryAdapter extends RecyclerView.Adapter<RepertoryAdapter.RepertoryViewHolder> {

    private List<HashMap<String, String>> mList;

    private Context context;

    private OnItemClickListener onItemClickListener;

    private boolean isDelete =  false;

    //设置点击事件
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public RepertoryAdapter(Context context, List<HashMap<String, String>> mList,boolean isDelete) {
        this.context = context;
        this.mList = mList;
        this.isDelete = isDelete;
    }

    @Override
    public RepertoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_repertory,parent,false);
        return new RepertoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RepertoryViewHolder holder, final int position) {
        HashMap<String,String> map = mList.get(position);
        holder.tvOrderNumber.setText(map.get("order"));
        holder.tvExpressName.setText(map.get("express"));
        holder.tvTime.setText(map.get("time"));
        holder.tvTagNumber.setText(map.get("tag_number"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });

        if (isDelete) {
            holder.rl_delete.setVisibility(View.VISIBLE);
        } else {
            holder.rl_delete.setVisibility(View.GONE);
        }

        holder.rl_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemDelate(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class RepertoryViewHolder extends RecyclerView.ViewHolder {

        TextView tvOrderNumber;    //订单号
        TextView tvExpressName;   //快递公司
        TextView tvTime;   //时间
        TextView tvTagNumber;   //取货码
        RelativeLayout rl_delete;

        public RepertoryViewHolder(View itemView) {
            super(itemView);
            tvOrderNumber = itemView.findViewById(R.id.tv_item_order_number);
            tvExpressName = itemView.findViewById(R.id.tv_item_express);
            tvTime = itemView.findViewById(R.id.tv_item_time);
            tvTagNumber = itemView.findViewById(R.id.tv_item_tag_number);
            rl_delete = itemView.findViewById(R.id.rl_delete);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(int position);
        void onItemDelate(int position);
    }
}
