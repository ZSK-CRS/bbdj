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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Author : ZSK
 * Date : 2019/1/9
 * Description :   待收件适配器
 */
public class WaitCollectAdapter extends RecyclerView.Adapter<WaitCollectAdapter.WaitCollectViewHolder> {

    private List<HashMap<String, String>> mList;

    private Context mContext;

    private OnItemClickListener itemClickListener;    //item点击

    private OnCannelOrderClickListener onCannelOrderClickListener;     //取消订单

    private OnSaveFirstClickListener  onSaveFirstClickListener;   //先存后打

    private OnPrintatOnceClickListener onPrintatOnceClickListener;   //立刻打印

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setOnPrintatOnceClickListener(OnPrintatOnceClickListener onPrintatOnceClickListener){
        this.onPrintatOnceClickListener = onPrintatOnceClickListener;
    }

    public void setOnCannelOrderClickListener(OnCannelOrderClickListener cannelOrderClickListener) {
        this.onCannelOrderClickListener = cannelOrderClickListener;
    }

    public void setOnSaveFirstClickListener(OnSaveFirstClickListener  onSaveFirstClickListener) {
        this.onSaveFirstClickListener = onSaveFirstClickListener;
    }

    public WaitCollectAdapter(Context context,List<HashMap<String, String>> list) {
        this.mList = list;
        this.mContext = context;
    }


    @Override
    public WaitCollectViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.wait_collect, parent, false);
        return new WaitCollectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WaitCollectViewHolder holder, final int position) {
        if (holder == null) {
            return;
        }
        HashMap<String,String> map = mList.get(position);
        holder.sendName.setText(map.get("send_name"));
        holder.sendPhone.setText(map.get("send_phone"));
        holder.sendAddress.setText(map.get("send_raddress"));
        String create_time = map.get("create_time");
        create_time = DateUtil.changeStampToStandrdTime("MM-dd HH:mm",create_time);
        holder.sendTime.setText(create_time);
        holder.receivePerson.setText(map.get("collect_name"));
        holder.receiveAddress.setText(map.get("collect_address"));
        String express_logo = map.get("express_logo");
        Glide.with(mContext)
                .load(express_logo)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.ic);

        //点击事件
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    itemClickListener.OnItemClick(position);
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

        //先存后打
        holder.btFistSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSaveFirstClickListener != null) {
                    onSaveFirstClickListener.OnClick(position);
                }
            }
        });

        //立刻打印
        holder.btSealOnce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPrintatOnceClickListener != null) {
                    onPrintatOnceClickListener.OnPrint(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class WaitCollectViewHolder extends RecyclerView.ViewHolder {
        private ImageView ic;    //快递图标
        private TextView sendName;    //寄件人姓名
        private TextView sendPhone;    //寄件人电话
        private TextView sendAddress;   //寄件地址
        private TextView sendTime;   // 寄件时间
        private TextView receivePerson;  //收件人
        private TextView receiveAddress;   //收件地址
        private Button btCannel;   //取消订单
        private Button btFistSave;   //先存后打
        private Button btSealOnce;   //立刻打印

        public WaitCollectViewHolder(View itemView) {
            super(itemView);
            ic = itemView.findViewById(R.id.ic_deliver);
            sendName = itemView.findViewById(R.id.id_send_person);
            sendPhone = itemView.findViewById(R.id.id_send_phone);
            sendAddress = itemView.findViewById(R.id.tv_send_person_address);
            sendTime = itemView.findViewById(R.id.tv_send_time);
            receivePerson = itemView.findViewById(R.id.tv_receive_person);
            receiveAddress = itemView.findViewById(R.id.tv_receive_address);
            btCannel = itemView.findViewById(R.id.bt_cannel_order);
            btFistSave = itemView.findViewById(R.id.bt_first_save);
            btSealOnce = itemView.findViewById(R.id.bt_immediately_seal);
        }
    }

    //##############################  接口回调  ####################################
    //点击事件
    public interface OnItemClickListener{
        void OnItemClick(int position);
    }

    //取消订单的点击事件
    public interface  OnCannelOrderClickListener{
        void OnCannelOrderClick(int position);
    }

    //先存后打
    public interface OnSaveFirstClickListener{
        void OnClick(int position);
    }

    //立刻打印
    public interface OnPrintatOnceClickListener{
        void OnPrint(int position);
    }
}
