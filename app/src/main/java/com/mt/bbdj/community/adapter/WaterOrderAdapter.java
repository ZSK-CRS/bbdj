package com.mt.bbdj.community.adapter;

import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.ProductModel;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.community.activity.WaterOrderActivity;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/5/16
 * Description :
 */
public class WaterOrderAdapter extends RecyclerView.Adapter<WaterOrderAdapter.WaterViewHolder> {

    private List<ProductModel> mList;
    private WaterOrderClickListener waterOrderClickListener;

    public void setWaterOrderClickListener(WaterOrderClickListener waterOrderClickListener) {
        this.waterOrderClickListener = waterOrderClickListener;
    }

    public WaterOrderAdapter(List<ProductModel> mList) {
        this.mList = mList;
    }

    @Override
    public WaterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view  = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_water_search_,parent,false);
        return new WaterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WaterViewHolder holder, int position) {
        ProductModel productModel = mList.get(position);
        holder.tv_type.setText(productModel.getProductName());
        holder.tv_address.setText(productModel.getAddress());
        holder.tv_order_time.setText(DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm",
                productModel.getCreateTime()));
        int orderState = productModel.getOrderState();
        if (orderState == 1) {
            holder.tv_order_state.setText("已完成");
            holder.tv_order_state.setTextColor(Color.parseColor("#0da95f"));
        } else {
            holder.tv_order_state.setText("已取消");
            holder.tv_order_state.setTextColor(Color.parseColor("#909090"));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (waterOrderClickListener != null) {
                    waterOrderClickListener.OnItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class WaterViewHolder extends RecyclerView.ViewHolder{

        private TextView tv_type;
        private TextView tv_order_state;
        private AppCompatTextView tv_address;
        private TextView tv_order_time;

        public WaterViewHolder(View itemView) {
            super(itemView);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_order_state = itemView.findViewById(R.id.tv_order_state);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_order_time = itemView.findViewById(R.id.tv_order_time);
        }
    }


    //#############################################################################

    public interface WaterOrderClickListener{
        void OnItemClick(int position);
    }
}
