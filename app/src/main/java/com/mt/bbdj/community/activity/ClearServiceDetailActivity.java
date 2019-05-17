package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.model.ProductModel;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.community.adapter.ProductListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ClearServiceDetailActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
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
    private ProductModel productModel;
    private ProductListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_service_detail);
        ButterKnife.bind(this);
        initParams();
        initProduct();
    }

    private void initProduct() {
        rlProduct.setFocusable(false);
        //initTemparayData();   //模拟数据
        mAdapter = new ProductListAdapter(productModel.getClearMessageList());
        rlProduct.setNestedScrollingEnabled(false);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        rlProduct.setLayoutManager(linearLayoutManager);
        rlProduct.setAdapter(mAdapter);
    }

    private void initParams() {
        Intent intent = getIntent();
        productModel = (ProductModel) intent.getSerializableExtra("productModel");

        tvAddress.setText(productModel.getAddress());
        tvPhone.setText(productModel.getPhone());
        tvOrderNumber.setText(productModel.getOrderNumber());
        String createTime = productModel.getCreateTime();
        createTime = DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm", createTime);
        tvOrderTime.setText(createTime);
        tvAccountMoney.setText("￥" + productModel.getAccountPrice());

        int orderState = productModel.getOrderState();
        if (orderState == 1) {
            ivStateLogo.setBackgroundResource(R.drawable.ic_complete);
            tvOrderState.setText("订单已完成");
            tvOrderState.setTextColor(Color.parseColor("#0da95f"));
        } else {
            ivStateLogo.setBackgroundResource(R.drawable.ic_cannle__);
            tvOrderState.setText("订单已取消");
            tvOrderState.setTextColor(Color.parseColor("#777777"));
        }

    }

    @OnClick(R.id.iv_call_number)
    public void onViewClicked() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        Uri data = Uri.parse("tel:" + tvPhone.getText().toString());
        intent.setData(data);
        startActivity(intent);
    }
}
