package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.utls.DateUtil;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/1/9
 * Description :  已处理适配器
 */
public class GlobalReceiveAdapter extends RecyclerView.Adapter<GlobalReceiveAdapter.HaveFinishViewHolder> {

    private List<HashMap<String, String>> mList;

    private Context context;

    private OnItemClickListener onItemClickListener;

    private OnItemClickOutListener onItemClickOutListener;

    public void setOnItemClickOutListener(OnItemClickOutListener onItemClickOutListener){
        this.onItemClickOutListener = onItemClickOutListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }

    public GlobalReceiveAdapter(Context context, List<HashMap<String, String>> list) {
        this.context = context;
        this.mList = list;
    }

    @Override
    public HaveFinishViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receive_global,parent,false);
        return new HaveFinishViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HaveFinishViewHolder holder, final int position) {
        if (holder == null) {
            return ;
        }
        HashMap<String,String> map = mList.get(position);
        String express_name = map.get("express_name");
        String pie_id = map.get("pie_id");
        String waybill_number = map.get("waybill_number");
        String tagNumber = map.get("tagNumber");
        String warehousing_time = map.get("warehousing_time");
        String out_time = map.get("out_time");
        String types = map.get("types");

        holder.billNumber.setText(waybill_number);
        holder.expressName.setText(express_name);
        holder.tagNumber.setText(tagNumber);
        holder.tagNumber.setText(tagNumber);
        holder.enterTime.setText(DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm:ss",warehousing_time));
        if ("1".equals(types)) {
            holder.state.setText("已入库");
            holder.llOutLayout.setVisibility(View.GONE);
            holder.rlout.setVisibility(View.VISIBLE);
        } else {
            holder.state.setText("已出库");
            holder.llOutLayout.setVisibility(View.VISIBLE);
            holder.rlout.setVisibility(View.GONE);
            holder.outTime.setText(DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm:ss",out_time));
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(position);
                }
            }
        });

        //出库
        holder.btout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemClickOutListener != null) {
                    onItemClickOutListener.onItemOutClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class HaveFinishViewHolder extends RecyclerView.ViewHolder {
        private TextView billNumber;  //运单号
        private TextView enterTime;   //入库时间
        private TextView expressName;    //快递公司名称
        private TextView outTime;    //出库时间
        private TextView tagNumber;    //提货码
        private TextView state;    //状态
        private Button btout;   //出库
        private LinearLayout llOutLayout;    //出库布局
        private RelativeLayout rlout;


        public HaveFinishViewHolder(View itemView) {
            super(itemView);
            billNumber = itemView.findViewById(R.id.tv_wail_number);
            tagNumber = itemView.findViewById(R.id.tv_tag_number);
            state = itemView.findViewById(R.id.tv_state);
            enterTime = itemView.findViewById(R.id.tv_enter_time);
            outTime = itemView.findViewById(R.id.tv_out_time);
            llOutLayout = itemView.findViewById(R.id.ll_out_layout);
            rlout = itemView.findViewById(R.id.rl_function);
            btout = itemView.findViewById(R.id.bt_out);
            expressName = itemView.findViewById(R.id.tv_express_name);
        }
    }

    //################################### 接口回调 #################################
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    //出库
    public interface OnItemClickOutListener{
        void onItemOutClick(int position);
    }
}
