package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.model.ProductModel;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.community.adapter.ProductListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class ClearStateActivity extends BaseActivity {

    @BindView(R.id.iv_state_logo)
    ImageView ivStateLogo;
    @BindView(R.id.tv_order_state)
    TextView tvOrderState;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.iv_call_number)
    ImageView ivCallNumber;
    @BindView(R.id.tv_order_number)
    TextView tvOrderNumber;
    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;
    @BindView(R.id.rl_product)
    RecyclerView rlProduct;
    @BindView(R.id.tv_account_money)
    TextView tvAccountMoney;
    private ProductListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        setContentView(R.layout.activity_clear_state);
        ButterKnife.bind(this);
        initParams();
    }

    private void initParams() {
        Intent intent = getIntent();
        ProductModel productModel = (ProductModel) intent.getSerializableExtra("productModel");
        int clearState = productModel.getClearState();
        tvOrderState.setText(productModel.getClearStateName());
        tvAddress.setText(productModel.getAddress());
        tvPhone.setText(productModel.getPhone());
        tvOrderNumber.setText(productModel.getOrderNumber());
        tvOrderTime.setText(DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm",productModel.getCreateTime()));
        tvAccountMoney.setText("￥"+productModel.getAccountPrice());
        setView(clearState);

        rlProduct.setFocusable(false);
        //initTemparayData();   //模拟数据
        mAdapter = new ProductListAdapter(productModel.getClearMessageList());
        rlProduct.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rlProduct.setLayoutManager(linearLayoutManager);
        rlProduct.setAdapter(mAdapter);
    }

    private void setView(int clearState) {
        switch (clearState) {
            case 1:     //显示电话联系、取消、确认接单
                break;
            case 2:
                break;
            case 7:
                break;
        }
    }

}
