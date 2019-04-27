package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.ProductModel;

import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/4/26
 * Description :
 */
public class WaitHandleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ProductModel> mList;

    private Context context;

    private int TYPE_EXPRESS_ORDER = 0;   //快递订单
    private int TYPE_WATER = 1;    //桶装水
    private int TYPE_CLEAR = 2;  //干洗
    private int TYPE_MARGIN = 3;  //空白
    private int TYPE_EXPRESS_ORDER_NULL = 4;  //快递订单无数据
    private int TYPE_WATER_NULL = 5;  //桶装水无数据
    private int TYPE_CLEAR_NULL = 6;  //干洗无数据


    public WaitHandleAdapter(List<ProductModel> mList) {
        this.mList = mList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_EXPRESS_ORDER) {     //快递
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_express_order, parent, false);
            return new ExpressViewHolder(view);
        } else if (viewType == TYPE_WATER) {   //桶装水
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_water_order, parent, false);
            return new WaterViewHolder(view);
        } else if (viewType == TYPE_CLEAR) {  //干洗
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clear_order, parent, false);
            return new ClearViewHolder(view);
        } else if (viewType == TYPE_MARGIN){          //空白
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_margin, parent, false);
            return new MarginViewHolder(view);
        } else if (viewType == TYPE_EXPRESS_ORDER_NULL) {    //订单无数据
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_express_null_order, parent, false);
            return new ExpressNullViewHolder(view);
        } else if (viewType == TYPE_WATER_NULL) {   //桶装水无数据
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_water_null_order, parent, false);
            return new WaterViewNullHolder(view);
        } else{             //干洗无数据
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clear_null_order, parent, false);
            return new ClearNullViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ExpressViewHolder) {
            setExpressData(holder, position);    //设置快递数据
        } else if (holder instanceof WaterViewHolder) {
            setWaterData(holder, position);     //设置桶装水数据
        } else {
            setClearData(holder, position);     //设置干洗数据
        }
    }

    private void setExpressData(RecyclerView.ViewHolder holder, int position) {
        ProductModel productModel = mList.get(position);
        ExpressViewHolder expressViewHolder = (ExpressViewHolder) holder;
        boolean isshowType = productModel.isShowType();
        boolean isshowBottom = productModel.isShowBottom();

        if (isshowType) {
            expressViewHolder.tv_type.setVisibility(View.VISIBLE);
            expressViewHolder.tv_splite.setVisibility(View.VISIBLE);
            expressViewHolder.tv_type.setText("快递订单");
        } else {
            expressViewHolder.tv_type.setVisibility(View.GONE);
            expressViewHolder.tv_splite.setVisibility(View.GONE);
        }

        if (isshowBottom) {
            expressViewHolder.v_bottom.setVisibility(View.VISIBLE);
        } else {
            expressViewHolder.v_bottom.setVisibility(View.GONE);
        }


        expressViewHolder.tv_address.setText(productModel.getAddress());
        expressViewHolder.tv_product.setText(productModel.getProductName());

        //确认揽件
        expressViewHolder.tv_confirm_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* expressViewHolder.tv_order_detail.setBackgroundResource(R.drawable.tv_bg_green_2);
                expressViewHolder.tv_order_detail.setTextColor(Color.parseColor("#0da95f"));
                expressViewHolder.tv_confirm_receive.setBackgroundResource(R.drawable.tv_bg_green_3);
                expressViewHolder.tv_confirm_receive.setTextColor(Color.parseColor("#ffffff"));*/

            }
        });

        //查看订单
        expressViewHolder.tv_order_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  expressViewHolder.tv_confirm_receive.setBackgroundResource(R.drawable.tv_bg_green_2);
                expressViewHolder.tv_confirm_receive.setTextColor(Color.parseColor("#0da95f"));
                expressViewHolder.tv_order_detail.setBackgroundResource(R.drawable.tv_bg_green_3);
                expressViewHolder.tv_order_detail.setTextColor(Color.parseColor("#ffffff"));*/
            }
        });
    }

    private void setWaterData(RecyclerView.ViewHolder holder, int position) {

    }

    private void setClearData(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemViewType(int position) {
        int type = mList.get(position).getType();
        if (type == 0) {    //快递
            return TYPE_EXPRESS_ORDER;
        } else if (type == 1) {   //桶装水
            return TYPE_WATER;
        } else if (type == 2){   //干洗
            return TYPE_CLEAR;
        } else if (type == 3){
            return TYPE_MARGIN;
        } else if (type == 4) {
            return TYPE_EXPRESS_ORDER_NULL;  //快递公司无数据
        } else if (type == 5) {
            return TYPE_WATER_NULL;   //桶装水无数据
        } else {
            return TYPE_CLEAR_NULL;    //干洗无数据
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    //快递公司
    private class ExpressViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_type;     //最大的分类
        private ImageView iv_phone;    //电话
        private TextView tv_product;    //名称
        private AppCompatTextView tv_address;    //地址
        private TextView tv_order_detail;    //订单详情
        private TextView tv_confirm_receive;    //确认揽收
        private View tv_splite;
        private View v_bottom;

        public ExpressViewHolder(View itemView) {
            super(itemView);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_product = itemView.findViewById(R.id.tv_product);
            iv_phone = itemView.findViewById(R.id.iv_phone);
            v_bottom = itemView.findViewById(R.id.v_bottom);
            tv_splite = itemView.findViewById(R.id.tv_splite);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_order_detail = itemView.findViewById(R.id.tv_order_detail);
            tv_confirm_receive = itemView.findViewById(R.id.tv_confirm_receive);
        }
    }

    //桶装水
    private class WaterViewHolder extends RecyclerView.ViewHolder {

        public WaterViewHolder(View itemView) {
            super(itemView);
        }
    }

    //干洗
    private class ClearViewHolder extends RecyclerView.ViewHolder {

        public ClearViewHolder(View itemView) {
            super(itemView);
        }
    }

    //空白
    private class MarginViewHolder extends RecyclerView.ViewHolder {
        public MarginViewHolder(View itemView) {
            super(itemView);
        }
    }

    //快递无数据
    private class ExpressNullViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_type;     //最大的分类
        private ImageView iv_phone;    //电话
        private TextView tv_product;    //名称
        private AppCompatTextView tv_address;    //地址
        private TextView tv_order_detail;    //订单详情
        private TextView tv_confirm_receive;    //确认揽收
        private View tv_splite;
        private View v_bottom;

        public ExpressNullViewHolder(View itemView) {
            super(itemView);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_product = itemView.findViewById(R.id.tv_product);
            iv_phone = itemView.findViewById(R.id.iv_phone);
            v_bottom = itemView.findViewById(R.id.v_bottom);
            tv_splite = itemView.findViewById(R.id.tv_splite);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_order_detail = itemView.findViewById(R.id.tv_order_detail);
            tv_confirm_receive = itemView.findViewById(R.id.tv_confirm_receive);
        }
    }

    //桶装水无数据
    private class WaterViewNullHolder extends RecyclerView.ViewHolder {

        public WaterViewNullHolder(View itemView) {
            super(itemView);
        }
    }

    //干洗无数据
    private class ClearNullViewHolder extends RecyclerView.ViewHolder {

        public ClearNullViewHolder(View itemView) {
            super(itemView);
        }
    }



}
