package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.simonpercic.oklog3.OkLogInterceptor;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.internet.RetrofitApi;
import com.mt.bbdj.baseconfig.internet.RetrofitConfig;
import com.mt.bbdj.baseconfig.model.ProductModel;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.GoodsOrderAdapter;
import com.mt.bbdj.community.adapter.ProductListAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WaterOrderDetailActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.tv_address_title)
    TextView tvAddressTitle;
    @BindView(R.id.tv_content)
    TextView tvContent;
    @BindView(R.id.tv_phone_title)
    TextView tvPhoneTitle;
    @BindView(R.id.tv_call)
    TextView tvCall;
    @BindView(R.id.tv_cannel_order)
    TextView tvCannelOrder;
    @BindView(R.id.tv_confirm_receive)
    TextView tvConfirmReceive;
    @BindView(R.id.tv_confirm_send)
    TextView tvConfirmSend;
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
    @BindView(R.id.tv_account_money)
    TextView tvAccountMoney;
    @BindView(R.id.rl_product)
    RecyclerView rlProduct;
    private ProductListAdapter mAdapter;

    private List<HashMap<String,String>> mList= new ArrayList<>();
    private OkHttpClient okHttpClient;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String user_id;
    private String order_id;
    private ProductModel productModel;
    private int REQUEST_CANNEL = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        setContentView(R.layout.activity_water_order_detail);
        ButterKnife.bind(this);
        initParams();
        initView();
    }

    private void initView() {
        rlProduct.setFocusable(false);
        //initTemparayData();   //模拟数据
        mAdapter = new ProductListAdapter(mList);
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

    private void initParams() {
        Intent intent = getIntent();
        productModel = (ProductModel) intent.getSerializableExtra("orderDetail");

        mList = productModel.getWaterMessageList();
        order_id = productModel.getOrders_id();
        String states = productModel.getStates();

        if ("1".equals(states)) {
            tvConfirmReceive.setVisibility(View.VISIBLE);
            tvConfirmSend.setVisibility(View.GONE);
        } else if ("2".equals(states)) {
            tvConfirmReceive.setVisibility(View.GONE);
            tvConfirmSend.setVisibility(View.VISIBLE);
        }

        tvAddressTitle.setText(productModel.getAddress());
        tvPhoneTitle.setText(productModel.getPhone());
        tvContent.setText((DateUtil.changeStampToStandrdTime("HH:mm", productModel.getContext())+"上门"));
        tvAddress.setText(productModel.getAddress());
        tvPhone.setText(productModel.getPhone());
        tvOrderNumber.setText(productModel.getOrderNumber());
        String createTime = productModel.getCreateTime();
        createTime = DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm", createTime);
        tvOrderTime.setText(createTime);
        tvAccountMoney.setText("￥"+ productModel.getAccountPrice());

        OkLogInterceptor okLogInterceptor = OkLogInterceptor.builder().build();
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.addInterceptor(okLogInterceptor);
        okHttpClient = okHttpBuilder.build();

        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }


    }

    @OnClick({R.id.iv_back, R.id.tv_call, R.id.tv_cannel_order, R.id.tv_confirm_receive, R.id.tv_confirm_send, R.id.iv_call_number})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_cannel_order:
                cannelWaterOlder();
                break;
            case R.id.tv_confirm_receive:
                confirmWaterReive();
                break;
            case R.id.tv_confirm_send:
                confirmWaterSend();
                break;
            case R.id.tv_call:
            case R.id.iv_call_number:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + tvPhone.getText().toString());
                intent.setData(data);
                startActivity(intent);
                break;
        }
    }

    private void cannelWaterOlder() {
        //桶装水取消订单
        Intent intent = new Intent(this, CannelOrderActivity.class);
        intent.putExtra("user_id", user_id);
        intent.putExtra("mail_id", productModel.getMail_id());
        intent.putExtra("orders_id", productModel.getOrders_id());
        intent.putExtra("cannel_type", CannelOrderActivity.STATE_CANNEL_FOR_SERVICE);
        startActivityForResult(intent,REQUEST_CANNEL);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return ;
        }
        finish();
    }

    private void confirmWaterSend() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitConfig.BaseURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);
        Map<String, String> requestMap = NoHttpRequest.setHeaderParams(user_id);
        //传入请求参数
        Call<ResponseBody> call = retrofitApi.confirmSendWaterOrder(requestMap,order_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String json = response.body().string();
                    LogUtil.d("WaterOrderDetailActivity::", json);
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");
                    if ("5001".equals(code)) {
                        tvConfirmSend.setEnabled(false);
                        tvCannelOrder.setEnabled(false);
                        tvConfirmSend.setBackgroundResource(R.drawable.shap_ll_bg);
                        tvCannelOrder.setBackgroundResource(R.drawable.shap_ll_bg);
                        tvCannelOrder.setTextColor(Color.WHITE);
                        tvConfirmSend.setText("已送达");
                    }
                    ToastUtil.showShort(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtil.d("WaterOrderDetailActivity::", t.getMessage());
                ToastUtil.showShort(t.getMessage());
            }
        });
    }

    private void confirmWaterReive() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitConfig.BaseURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);
        Map<String, String> requestMap = NoHttpRequest.setHeaderParams(user_id);
        //传入请求参数
        Call<ResponseBody> call = retrofitApi.confirmReceiveWaterOrder(requestMap,order_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String json = response.body().string();
                    LogUtil.d("WaterOrderDetailActivity::", json);
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");

                    if ("5001".equals(code)) {
                        tvConfirmReceive.setVisibility(View.GONE);
                        tvConfirmSend.setVisibility(View.VISIBLE);
                    }
                    ToastUtil.showShort(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtil.d("WaterOrderDetailActivity::", t.getMessage());
                ToastUtil.showShort(t.getMessage());
            }
        });
    }
}
