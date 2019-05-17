package com.mt.bbdj.community.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.ClearGoodsModel;
import com.mt.bbdj.baseconfig.view.AddView;

import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/5/10
 * Description :
 */
public class ClearGoodsAdapter extends RecyclerView.Adapter<ClearGoodsAdapter.ClearGoodsViewHolder> {

    private List<ClearGoodsModel> modelList;

    private OnValueChanage onValueChanage;

    public void setOnValueChanage(OnValueChanage onValueChanage) {
        this.onValueChanage = onValueChanage;
    }

    public ClearGoodsAdapter(List<ClearGoodsModel> modelList) {
        this.modelList = modelList;
    }

    @Override
    public ClearGoodsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clear_goods, parent, false);
        return new ClearGoodsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ClearGoodsViewHolder holder, int position) {
        ClearGoodsModel data = modelList.get(position);
        String type = data.getType();
        if ("".equals(type)) {
            holder.type.setVisibility(View.GONE);
            holder.v_splite.setVisibility(View.GONE);
        } else {
            holder.type.setVisibility(View.VISIBLE);
            holder.v_splite.setVisibility(View.VISIBLE);
        }
        holder.type.setText(type);
        holder.product.setText(data.getTitle());
        holder.price.setText("￥"+data.getPrice());
        holder.addView.setValue(data.getNumber());
        holder.addView.setOnValueChangeListene(new AddView.OnValueChangeListener() {
            @Override
            public void onValueChange(int value) {
                if (onValueChanage != null) {
                    onValueChanage.onValueChanage(position,value);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    class ClearGoodsViewHolder extends RecyclerView.ViewHolder {

        private TextView type;
        private TextView product;
        private TextView price;
        private AddView addView;
        private View v_splite;

        public ClearGoodsViewHolder(View itemView) {
            super(itemView);

            type = itemView.findViewById(R.id.tv_type);
            product = itemView.findViewById(R.id.tv_product);
            price = itemView.findViewById(R.id.tv_price);
            addView = itemView.findViewById(R.id.addview);
            v_splite = itemView.findViewById(R.id.v_splite);
        }
    }


    public interface OnValueChanage{
        void onValueChanage(int position,int value);
    }
}
