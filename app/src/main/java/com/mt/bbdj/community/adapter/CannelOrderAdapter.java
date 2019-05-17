package com.mt.bbdj.community.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/1/10
 * Description :
 */
public class CannelOrderAdapter extends RecyclerView.Adapter<CannelOrderAdapter.CauseForCannelViewHolder> {

    private List<HashMap<String,String>> mList = new ArrayList<>();

    public OnItemClickListener itemClickListener;

    public int clickPosition = -1;

    public CannelOrderAdapter(List<HashMap<String,String>> mList) {
        this.mList = mList;
    }

    public void setOnItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    //设置当前点击的位置
    public void setCurrentClickPosition(int position) {
        this.clickPosition = position;
    }

    @Override
    public CauseForCannelViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cannel,parent,false);
        return new CauseForCannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CauseForCannelViewHolder holder, final int position) {
        HashMap<String,String> map = mList.get(position);
        String reason_name = map.get("reason_name");
        String reason_number = map.get("reason_number");

        if (clickPosition == position) {
            holder.selectIc.setBackgroundResource(R.drawable.ic_cause_select);
        } else {
            holder.selectIc.setBackgroundResource(R.drawable.main_shap_cirle);
        }

        holder.tvCause.setText(reason_name);
        holder.item_tv_number.setText(reason_number);
        holder.rl_select_layout.setOnClickListener(new View.OnClickListener() {
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
        return mList.size();
    }

    class CauseForCannelViewHolder extends RecyclerView.ViewHolder{

        private TextView tvCause;     //取消原因
        private TextView item_tv_number;     //取消原因
        private ImageView selectIc;    //选中图标
        private RelativeLayout rl_select_layout;    //选择区域

        public CauseForCannelViewHolder(View itemView) {
            super(itemView);
            tvCause = itemView.findViewById(R.id.item_tv_cause);
            selectIc = itemView.findViewById(R.id.iv_select_ic);
            rl_select_layout = itemView.findViewById(R.id.rl_select_layout);
            item_tv_number = itemView.findViewById(R.id.item_tv_number);
        }
    }

    //########################      接口    ##################################

    public interface OnItemClickListener{
        void OnClick(int position);
    }
}
