package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.view.AddView;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/3/1
 * Description :
 */
public class ShopCardAdapter extends RecyclerView.Adapter<ShopCardAdapter.ShopCardViewHolder> {

    private List<HashMap<String, String>> mList;

    private OnItemClickListener onItemClickListener;

    private OnItemSelectListener onItemSelectListener ;

    private OnItemNumberChangeListener itemNumberChangeListener;

    private Context context;

    public ShopCardAdapter(Context context, List<HashMap<String, String>> mList) {
        this.context = context;
        this.mList = mList;
    }

    //设置数量变换的监听
    public void setOnItemNumberChangeListener(OnItemNumberChangeListener itemNumberChangeListener) {
        this.itemNumberChangeListener = itemNumberChangeListener;
    }

    //设置选中的点击事件
    public void setOnItemSelectListener(OnItemSelectListener onItemSelectListener) {
        this.onItemSelectListener = onItemSelectListener;
    }

    //设置item点击事件
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public ShopCardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_goods_cart, parent, false);
        return new ShopCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ShopCardViewHolder holder, final int position) {
        HashMap<String, String> map = mList.get(position);
        holder.goodsName.setText(map.get("product_name"));
        holder.goodsMoney.setText("￥ "+map.get("price"));
        holder.goodsType.setText(map.get("genre_name"));
        String logoPicture = map.get("thumb");
        Glide.with(context).load(logoPicture).error(R.drawable.ic_no_picture).into(holder.logo);
        String number = map.get("number");
        String selectState = map.get("selectState");
        int goodsNumber = IntegerUtil.getStringChangeToNumber(number);
        holder.addView.setValue(goodsNumber);

        if ("0".equals(selectState)) {
            holder.ivCheck.setBackgroundResource(R.drawable.shap_circle_grey);
        } else {
            holder.ivCheck.setBackgroundResource(R.drawable.ic_check_all);
        }

        //选中
        holder.ivCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemSelectListener != null) {
                    onItemSelectListener.onItemSelect(position);
                }
            }
        });

        //数量变化
        holder.addView.setOnValueChangeListene(new AddView.OnValueChangeListener() {
            @Override
            public void onValueChange(int value) {
                if (itemNumberChangeListener != null) {
                    itemNumberChangeListener.onChange(position,value);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ShopCardViewHolder extends RecyclerView.ViewHolder {
        TextView goodsName;    //商品名称
        TextView goodsType;    //商品类型
        TextView goodsMoney;    //商品价格
        ImageView logo;    //商品图片
        AddView addView;    //数量加减
        ImageView ivCheck;     //选中

        public ShopCardViewHolder(View itemView) {
            super(itemView);
            goodsName = itemView.findViewById(R.id.tv_goods_name);
            goodsType = itemView.findViewById(R.id.tv_goods_type);
            goodsMoney = itemView.findViewById(R.id.tv_goods_money);
            logo = itemView.findViewById(R.id.iv_logo);
            addView = itemView.findViewById(R.id.addview);
            ivCheck = itemView.findViewById(R.id.iv_select_check);
        }
    }

    //##################################################################################
    public interface OnItemClickListener{
        void onItemClick(int position);
    }

    public interface OnItemSelectListener{
        void onItemSelect(int position);
    }

    public interface OnItemNumberChangeListener{
        void onChange(int position,int value);
    }
}
