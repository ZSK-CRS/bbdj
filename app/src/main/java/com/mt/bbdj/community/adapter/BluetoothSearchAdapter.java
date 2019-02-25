package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/1/14
 * Description :
 */
public class BluetoothSearchAdapter extends RecyclerView.Adapter<BluetoothSearchAdapter.BluetoothViewHolder> {

    private List<String> mList;

    private Context mContext;

    private OnItemDeleteClickListener onItemDeleteClickListener;

    private OnItemConnectClickListener connectClickListener;

    public void setData(List<String> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    //点击打印
    public void setConnectClickListener(OnItemConnectClickListener connectClickListener) {
        this.connectClickListener = connectClickListener;
    }

    //设置删除的点击事件
    public void setOnItemDeleteClickListener(OnItemDeleteClickListener onItemDeleteClickListener) {
        this.onItemDeleteClickListener = onItemDeleteClickListener;
    }

    public BluetoothSearchAdapter(Context context,List<String> list) {
        this.mList = list;
        this.mContext = context;
    }

    @Override
    public BluetoothViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth_scan,parent,false);
        return new BluetoothViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BluetoothViewHolder holder, final int position) {
        if (holder == null) {
            return ;
        }

        holder.bluetoothName.setText(mList.get(position));

        //设置取消点击事件
        holder.deleteBluetooth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemDeleteClickListener != null) {
                    onItemDeleteClickListener.onClick(position);
                }
            }
        });

        //点击连接打印
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (connectClickListener != null) {
                    connectClickListener.onConnect(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class BluetoothViewHolder extends RecyclerView.ViewHolder{

        TextView bluetoothName;    //蓝牙设备名称
        RelativeLayout deleteBluetooth;   //删除蓝牙设备

        public BluetoothViewHolder(View itemView) {
            super(itemView);
            deleteBluetooth = itemView.findViewById(R.id.item_delete_bluetooth);
            bluetoothName = itemView.findViewById(R.id.item_bluetooth_name);
        }
    }


    //##############################   接口回调  ##################################
    public interface OnItemDeleteClickListener{
        void onClick(int position);
    }

    //连接
    public interface OnItemConnectClickListener{
        void onConnect(int position);
    }
}
