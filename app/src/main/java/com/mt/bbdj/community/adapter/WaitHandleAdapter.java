package com.mt.bbdj.community.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.ProductModel;
import com.mt.bbdj.baseconfig.utls.DateUtil;

import java.util.ArrayList;
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

    private ExpressInterfaceManager expressInterfaceManager;    //快递订单接口

    private int TYPE_EXPRESS_ORDER = 0;   //快递订单
    private int TYPE_WATER = 1;    //桶装水
    private int TYPE_CLEAR_SEND = 2;  //干洗
    private int TYPE_MARGIN = 3;  //空白
    private int TYPE_EXPRESS_ORDER_NULL = 4;  //快递订单无数据
    private int TYPE_WATER_NULL = 5;  //桶装水无数据
    private int TYPE_CLEAR_NULL = 6;  //干洗无数据


    public WaitHandleAdapter(Context context, List<ProductModel> mList) {
        this.mList = mList;
        this.context = context;
    }

    public void setOnCheckDetailListener(ExpressInterfaceManager expressInterfaceManager) {
        this.expressInterfaceManager = expressInterfaceManager;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_EXPRESS_ORDER) {     //快递
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_express_order, parent, false);
            return new ExpressViewHolder(view);
        } else if (viewType == TYPE_WATER) {   //桶装水
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_water_order, parent, false);
            return new WaterViewHolder(view);
        } else if (viewType == TYPE_CLEAR_SEND) {  //干洗
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_clear_order, parent, false);
            return new ClearViewHolder(view);
        } else if (viewType == TYPE_MARGIN) {          //空白
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_margin, parent, false);
            return new MarginViewHolder(view);
        } else if (viewType == TYPE_EXPRESS_ORDER_NULL) {    //订单无数据
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_express_null_order, parent, false);
            return new ExpressNullViewHolder(view);
        } else if (viewType == TYPE_WATER_NULL) {   //桶装水无数据
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_water_null_order, parent, false);
            return new WaterViewNullHolder(view);
        } else {             //干洗无数据
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
        } else if (holder instanceof ClearViewHolder) {
            setClearData(holder, position);     //设置干洗数据
        } else if (holder instanceof ExpressNullViewHolder) {
            setNullExpressData(holder, position);   //设置快递订单无数据
        }
    }

    private void setNullExpressData(RecyclerView.ViewHolder holder, int position) {
        ExpressNullViewHolder expressNullViewHolder = (ExpressNullViewHolder) holder;
        expressNullViewHolder.iv_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnPlaceOrder(position);
                }
            }
        });
    }

    private void setExpressData(RecyclerView.ViewHolder holder, int position) {
        ProductModel productModel = mList.get(position);
        ExpressViewHolder expressViewHolder = (ExpressViewHolder) holder;
        boolean isshowType = productModel.isShowType();
        boolean isshowBottom = productModel.isShowBottom();

        if (isshowType) {
            expressViewHolder.ll_express_title.setVisibility(View.VISIBLE);
            expressViewHolder.tv_splite.setVisibility(View.VISIBLE);
            expressViewHolder.tv_type.setText("快递订单");
        } else {
            expressViewHolder.ll_express_title.setVisibility(View.GONE);
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
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnReceiveExpressClick(position);
                }
            }
        });

        //查看订单
        expressViewHolder.tv_order_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnCheckDetailOrderClick(position);
                }
            }
        });

        //寄快递
        expressViewHolder.iv_place_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnPlaceOrder(position);
                }
            }
        });

        //打电话
        expressViewHolder.iv_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnCallClick(position);
                }
            }
        });

        //取消订单
        expressViewHolder.tv_cannel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnCannelOrder(position);
                }
            }
        });


    }

    private void setWaterData(RecyclerView.ViewHolder holder, int position) {
        ProductModel productModel = mList.get(position);
        WaterViewHolder waterViewHolder = (WaterViewHolder) holder;
        boolean isshowType = productModel.isShowType();
        boolean isshowBottom = productModel.isShowBottom();
        String states = productModel.getStates();

        if ("1".equals(states)) {
            waterViewHolder.tv_confirm_receive.setVisibility(View.VISIBLE);
            waterViewHolder.tv_confirm_send.setVisibility(View.GONE);
        } else if ("2".equals(states)) {
            waterViewHolder.tv_confirm_receive.setVisibility(View.GONE);
            waterViewHolder.tv_confirm_send.setVisibility(View.VISIBLE);
        }

        if (isshowType) {
            waterViewHolder.tv_type.setVisibility(View.VISIBLE);
            waterViewHolder.tv_splite.setVisibility(View.VISIBLE);
            waterViewHolder.tv_type.setText("桶装水");
        } else {
            waterViewHolder.tv_type.setVisibility(View.GONE);
            waterViewHolder.tv_splite.setVisibility(View.GONE);
        }

        if (isshowBottom) {
            waterViewHolder.v_bottom.setVisibility(View.VISIBLE);
        } else {
            waterViewHolder.v_bottom.setVisibility(View.GONE);
        }

        //打电话
        waterViewHolder.tv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnCallClick(position);
                }
            }
        });

        //桶装水的详情
        waterViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnCheckWaterOrderClick(position);
                }
            }
        });

        //桶装水取消订单
        waterViewHolder.tv_cannel_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnConfirWaterCannelClick(position);
                }
            }
        });

        //确认桶装水接单
        waterViewHolder.tv_confirm_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnConfirmWaterReceiveClick(position);
                }
            }
        });

        //确认送达
        waterViewHolder.tv_confirm_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnConfirmWaterSendClick(position);
                }
            }
        });

        waterViewHolder.tv_product_name.setText(productModel.getProductName());
        waterViewHolder.tv_address.setText(productModel.getAddress());
        waterViewHolder.tv_content.setText(DateUtil.changeStampToStandrdTime("HH:mm",productModel.getContext())+"上门");
        waterViewHolder.tv_time_state.setText(productModel.getJuli_time());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        waterViewHolder.product_list.setLayoutManager(linearLayoutManager);
        waterViewHolder.product_list.setFocusable(false);
        waterViewHolder.product_list.setNestedScrollingEnabled(false);
        waterViewHolder.product_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    waterViewHolder.itemView.performClick();    //模拟点击
                }
                return false;
            }
        });
        WaterlistAdapter waterAdapter = new WaterlistAdapter(context, productModel.getWaterMessageList());
        waterViewHolder.product_list.setAdapter(waterAdapter);
    }

    private void setClearData(RecyclerView.ViewHolder holder, int position) {
        ProductModel productModel = mList.get(position);
        ClearViewHolder clearViewHolder = (ClearViewHolder) holder;
        boolean isshowType = productModel.isShowType();
        boolean isshowBottom = productModel.isShowBottom();
        int clearState = productModel.getClearState();     //干洗状态

        if (isshowType) {
            clearViewHolder.tv_type.setVisibility(View.VISIBLE);
            clearViewHolder.tv_splite.setVisibility(View.VISIBLE);
            clearViewHolder.tv_type.setText("干洗");
        } else {
            clearViewHolder.tv_type.setVisibility(View.GONE);
            clearViewHolder.tv_splite.setVisibility(View.GONE);
        }

        if (isshowBottom) {
            clearViewHolder.v_bottom.setVisibility(View.VISIBLE);
        } else {
            clearViewHolder.v_bottom.setVisibility(View.GONE);
        }

        //设置状态
        setClearView(clearState, clearViewHolder);
        clearViewHolder.tv_order_state.setText(productModel.getClearStateName());

        clearViewHolder.tv_product_name.setText(productModel.getProductName());
        clearViewHolder.tv_address.setText(productModel.getAddress());
        clearViewHolder.tv_content.setText((DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm",productModel.getContext())+"上门"));
        clearViewHolder.tv_time_state.setText(productModel.getJuli_time());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        clearViewHolder.product_list.setLayoutManager(linearLayoutManager);
        clearViewHolder.product_list.setFocusable(false);
        clearViewHolder.product_list.setNestedScrollingEnabled(false);
        clearViewHolder.product_list.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    clearViewHolder.itemView.performClick();    //模拟点击
                }
                return false;
            }
        });
        WaterlistAdapter waterAdapter = new WaterlistAdapter(context, productModel.getClearMessageList());
        clearViewHolder.product_list.setAdapter(waterAdapter);

        clearViewHolder.tv_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnCallClick(position);
                }
            }
        });

        //提交报价
        clearViewHolder.tv_confirm_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnCommitPrice(position);
                }
            }
        });

        //接单
        clearViewHolder.tv_confirm_receive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(expressInterfaceManager != null) {
                    expressInterfaceManager.OnClearReceiveClick(position);
                }
            }
        });

        //查看干洗详情
        clearViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnCheckClearOrderClick(position);
                }
            }
        });

        //干洗确认送达
        clearViewHolder.tv_confirm_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnConfirmClearSendClick(position);
                }
            }
        });

        //拒绝接单
        clearViewHolder.tv_confirm_refuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expressInterfaceManager != null) {
                    expressInterfaceManager.OnConfirmRefauseClick(position);
                }
            }
        });
    }

    private void setClearView(int clearState, ClearViewHolder clearViewHolder) {
        switch (clearState) {
            case 1:
                clearViewHolder.ll_action.setVisibility(View.VISIBLE);
                clearViewHolder.tv_confirm_refuse.setVisibility(View.VISIBLE);
                clearViewHolder.tv_call.setVisibility(View.VISIBLE);
                clearViewHolder.tv_confirm_receive.setVisibility(View.VISIBLE);
                clearViewHolder.tv_confirm_price.setVisibility(View.GONE);
                clearViewHolder.tv_confirm_send.setVisibility(View.VISIBLE);
                clearViewHolder.ll_product.setVisibility(View.GONE);
                clearViewHolder.v_splite_product.setVisibility(View.GONE);
                clearViewHolder.tv_order_state.setVisibility(View.GONE);
                clearViewHolder.tv_content.setVisibility(View.VISIBLE);
                break;
            case 2:
                clearViewHolder.ll_action.setVisibility(View.VISIBLE);
                clearViewHolder.tv_call.setVisibility(View.VISIBLE);
                clearViewHolder.tv_confirm_refuse.setVisibility(View.GONE);
                clearViewHolder.tv_confirm_price.setVisibility(View.VISIBLE);
                clearViewHolder.tv_confirm_receive.setVisibility(View.GONE);
                clearViewHolder.tv_confirm_send.setVisibility(View.GONE);
                clearViewHolder.ll_product.setVisibility(View.GONE);
                clearViewHolder.v_splite_product.setVisibility(View.GONE);
                clearViewHolder.tv_content.setVisibility(View.VISIBLE);
                clearViewHolder.tv_order_state.setVisibility(View.GONE);
                break;
            case 5:
            case 6:
                clearViewHolder.ll_action.setVisibility(View.GONE);
                clearViewHolder.ll_product.setVisibility(View.VISIBLE);
                clearViewHolder.tv_confirm_refuse.setVisibility(View.GONE);
                clearViewHolder.v_splite_product.setVisibility(View.GONE);
                clearViewHolder.tv_order_state.setVisibility(View.VISIBLE);
                clearViewHolder.tv_content.setVisibility(View.GONE);
                clearViewHolder.v_splite_product.setVisibility(View.VISIBLE);
                clearViewHolder.tv_order_state.setVisibility(View.VISIBLE);
                break;
            case 7:
                clearViewHolder.ll_action.setVisibility(View.VISIBLE);
                clearViewHolder.tv_call.setVisibility(View.VISIBLE);
                clearViewHolder.tv_confirm_receive.setVisibility(View.GONE);
                clearViewHolder.tv_confirm_price.setVisibility(View.GONE);
                clearViewHolder.tv_confirm_refuse.setVisibility(View.GONE);
                clearViewHolder.tv_confirm_send.setVisibility(View.VISIBLE);
                clearViewHolder.ll_product.setVisibility(View.VISIBLE);
                clearViewHolder.tv_content.setVisibility(View.GONE);
                clearViewHolder.v_splite_product.setVisibility(View.VISIBLE);
                clearViewHolder.tv_order_state.setVisibility(View.VISIBLE);
                break;
            case 8:
                clearViewHolder.ll_action.setVisibility(View.GONE);
                clearViewHolder.ll_product.setVisibility(View.VISIBLE);
                clearViewHolder.tv_order_state.setVisibility(View.VISIBLE);
                clearViewHolder.tv_content.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        int type = mList.get(position).getType();
        if (type == 0) {    //快递
            return TYPE_EXPRESS_ORDER;
        } else if (type == 1) {   //桶装水
            return TYPE_WATER;
        } else if (type == 2) {   //干洗
            return TYPE_CLEAR_SEND;
        } else if (type == 3) {
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
        private LinearLayout ll_express_title;
        private ImageView iv_place_order;    //下订单
        private TextView tv_cannel_order;    //取消订单

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
            ll_express_title = itemView.findViewById(R.id.ll_express_title);
            iv_place_order = itemView.findViewById(R.id.iv_place_order);
            tv_cannel_order = itemView.findViewById(R.id.tv_cannel_order);
        }
    }

    //桶装水
    private class WaterViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_product_name;   //名称
        private TextView tv_address;   //小区地址
        private TextView tv_type;   //类别
        private TextView tv_phone;   //电话
        private TextView tv_confirm_send;   //确认送达
        private TextView tv_confirm_receive;   //确认接单
        private View tv_splite;
        private View v_bottom;
        private TextView tv_content;   //备注
        private RecyclerView product_list;  //桶装水品牌
        private TextView tv_call;   //打电话
        private TextView tv_cannel_order;    //取消
        private TextView tv_order_state;    //订单状态
        private TextView tv_time_state;   //时间状态 超时，，或者还剩多少时间

        public WaterViewHolder(View itemView) {
            super(itemView);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_phone = itemView.findViewById(R.id.tv_phone);
            tv_confirm_send = itemView.findViewById(R.id.tv_confirm_send);
            tv_confirm_receive = itemView.findViewById(R.id.tv_confirm_receive);
            tv_splite = itemView.findViewById(R.id.tv_splite);
            v_bottom = itemView.findViewById(R.id.v_bottom);
            tv_content = itemView.findViewById(R.id.tv_content);
            product_list = itemView.findViewById(R.id.product_list);
            tv_call = itemView.findViewById(R.id.tv_call);
            tv_cannel_order = itemView.findViewById(R.id.tv_cannel_order);
            tv_time_state = itemView.findViewById(R.id.tv_time_state);
        }
    }

    //干洗
    private class ClearViewHolder extends RecyclerView.ViewHolder {
        private TextView tv_product_name;   //名称
        private TextView tv_address;   //小区地址
        private TextView tv_type;   //类别
        private TextView tv_confirm_send;   //确认送达
        private TextView tv_call;   //确认送达
        private View tv_splite;
        private View v_bottom;
        private RecyclerView product_list;  //干洗类目
        private TextView tv_content;   //备注
        private TextView tv_order_state;   //订单状态
        private TextView tv_confirm_refuse;     //拒绝接单
        private TextView tv_confirm_price;     //确认报价
        private TextView tv_confirm_receive;     //确认报价
        private LinearLayout ll_action;     //确认报价
        private LinearLayout ll_product;     //产品列表布局
        private View v_splite_product;     //产品列表分割线
        private TextView tv_time_state;     //时间状态


        public ClearViewHolder(View itemView) {
            super(itemView);
            tv_product_name = itemView.findViewById(R.id.tv_product_name);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_type = itemView.findViewById(R.id.tv_type);
            tv_confirm_send = itemView.findViewById(R.id.tv_confirm_send);
            tv_splite = itemView.findViewById(R.id.tv_splite);
            v_bottom = itemView.findViewById(R.id.v_bottom);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_order_state = itemView.findViewById(R.id.tv_order_state);
            tv_call = itemView.findViewById(R.id.tv_call);
            product_list = itemView.findViewById(R.id.product_list);
            tv_confirm_refuse = itemView.findViewById(R.id.tv_confirm_refuse);
            tv_confirm_price = itemView.findViewById(R.id.tv_confirm_price);
            tv_confirm_receive = itemView.findViewById(R.id.tv_confirm_receive);
            ll_action = itemView.findViewById(R.id.ll_action);
            ll_product = itemView.findViewById(R.id.ll_product);
            v_splite_product = itemView.findViewById(R.id.v_splite_product);
            tv_time_state = itemView.findViewById(R.id.tv_time_state);
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

        private ImageView iv_place_order;

        public ExpressNullViewHolder(View itemView) {
            super(itemView);

            iv_place_order = itemView.findViewById(R.id.iv_place_order);
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


    /**
     * ########################################接口##########################################
     */
    public interface ExpressInterfaceManager {

        void OnCheckDetailOrderClick(int position);    //查看订单详情

        void OnCheckClearOrderClick(int position);   //查看干洗订单详情

        void OnCheckWaterOrderClick(int position);   //查看桶装水详情

        void OnReceiveExpressClick(int postion);    //订单揽收

        void OnConfirmWaterReceiveClick(int position);  //确认桶装水接单

        void OnConfirmWaterSendClick(int position);  //确认桶装水送达

        void OnConfirWaterCannelClick(int position);   //桶装水取消订单

        void OnConfirmClearSendClick(int position);   //干洗送达

        void OnConfirmRefauseClick(int position);    //干洗拒绝接单

        void OnCallClick(int position);   //打电话

        void OnPlaceOrder(int position);   //寄快递订单

        void OnCannelOrder(int position);    //取消订单

        void OnCommitPrice(int position);    //提交报价

        void OnClearReceiveClick(int position);  //干洗接单

    }


}
