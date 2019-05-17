package com.mt.bbdj.community.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mt.bbdj.R;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/5/8
 * Description :
 */
public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProcductViewHolder> {

    private List<HashMap<String,String>> mList;

    public ProductListAdapter(List<HashMap<String,String>> mList){
        this.mList = mList;
    }

    @Override
    public ProcductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_list,parent,false);
        return new ProcductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProcductViewHolder holder, int position) {
        HashMap<String,String> dataMap = mList.get(position);
        String product_name = dataMap.get("commodity_name");
        String product_number = dataMap.get("number");
        String product_price = dataMap.get("total");
        holder.product.setText(product_name);
        holder.accountPrice.setText("￥"+product_price);
        holder.number.setText("数量: "+product_number);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ProcductViewHolder extends RecyclerView.ViewHolder{
        private TextView product;   //产品名称
        private TextView number;   //产品数量
        private TextView accountPrice;   //总价格

        public ProcductViewHolder(View itemView) {
            super(itemView);

            product = itemView.findViewById(R.id.tv_product);
            number = itemView.findViewById(R.id.tv_number);
            accountPrice = itemView.findViewById(R.id.tv_price);
        }
    }
}
