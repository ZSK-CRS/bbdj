package com.mt.bbdj.community.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
public class CauseForcannelOrderAdapter extends RecyclerView.Adapter<CauseForcannelOrderAdapter.CauseForCannelViewHolder> {

    private List<HashMap<String,String>> mList = new ArrayList<>();

    public OnItemClickListener itemClickListener;

    public int clickPosition = -1;

    public CauseForcannelOrderAdapter(List<HashMap<String,String>> mList) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cause_cannel,parent,false);
        return new CauseForCannelViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CauseForCannelViewHolder holder, final int position) {
        if (holder == null) {
            return ;
        }


        if (clickPosition == position) {
            holder.selectIc.setBackgroundResource(R.drawable.ic_cause_select);
            holder.llMark.setVisibility(View.VISIBLE);
            holder.etMark.setText("");
        } else {
            holder.selectIc.setBackgroundResource(R.drawable.main_shap_cirle);
            holder.llMark.setVisibility(View.GONE);
        }

        HashMap<String,String> map = mList.get(position);
        String reason_name = map.get("reason_name");
        holder.tvCause.setText(reason_name);
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
        return mList.size();
    }

    class CauseForCannelViewHolder extends RecyclerView.ViewHolder{

        private TextView tvCause;     //取消原因
        private LinearLayout llMark;   //备注布局
        private EditText etMark;    //备注
        private ImageView selectIc;    //选中图标

        public CauseForCannelViewHolder(View itemView) {
            super(itemView);
            tvCause = itemView.findViewById(R.id.item_tv_cause);
            llMark = itemView.findViewById(R.id.ll_remark_layout);
            etMark = itemView.findViewById(R.id.item_et_marke);
            selectIc = itemView.findViewById(R.id.iv_select_ic);
        }
    }

    //########################      接口    ##################################

    public interface OnItemClickListener{
        void OnClick(int position);
    }
}
