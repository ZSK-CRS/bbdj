package com.mt.bbdj.community.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/3/6
 * Description :
 */
public class EnterManagerAdapter extends RecyclerView.Adapter<EnterManagerAdapter.EnterManagerViewHolder> {

    private List<HashMap<String, String>> mList;

    public EnterManagerAdapter(List<HashMap<String,String>> mList) {
        this.mList = mList;
    }

    private onDeleteClickListener deleteClickListener;

    private onItemClickListener onItemClickListener;

    //删除接口
    public void setDeleteClickListener(onDeleteClickListener deleteClickListener) {
        this.deleteClickListener = deleteClickListener;
    }

    //item点击事件
    public void setOnItemClickListener(onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    @Override
    public EnterManagerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_enter,parent,false);
        return new EnterManagerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(EnterManagerViewHolder holder, final int position) {
        HashMap<String,String> map = mList.get(position);
        holder.tvPackageCode.setText(map.get("package_code"));
        holder.tvWailNumber.setText(map.get("wail_number"));
        holder.tvExpress.setText(map.get("express_name"));
        //删除
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (deleteClickListener != null) {
                    deleteClickListener.onDelete(position);
                }
            }
        });

        //item点击
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class EnterManagerViewHolder extends RecyclerView.ViewHolder {
        TextView tvPackageCode;
        TextView tvWailNumber;
        TextView tvExpress;
        RelativeLayout delete;
        public EnterManagerViewHolder(View itemView) {
            super(itemView);
            tvPackageCode = itemView.findViewById(R.id.tv_package_number);
            tvExpress = itemView.findViewById(R.id.tv_express);
            tvWailNumber = itemView.findViewById(R.id.tv_wail_number);
            delete = itemView.findViewById(R.id.rl_delete);
        }
    }

    //点击事件
    public interface onItemClickListener{
        void onClick(int position);
    }

    //删除事件
    public interface onDeleteClickListener{
        void onDelete(int position);
    }
}
