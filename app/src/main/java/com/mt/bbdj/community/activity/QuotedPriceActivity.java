package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.simonpercic.oklog3.OkLogInterceptor;
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.internet.RetrofitApi;
import com.mt.bbdj.baseconfig.internet.RetrofitConfig;
import com.mt.bbdj.baseconfig.model.ClearGoodsModel;
import com.mt.bbdj.baseconfig.model.GoodsMessage;
import com.mt.bbdj.baseconfig.model.ProductModel;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.ClearGoodsAdapter;
import com.mt.bbdj.community.adapter.FastmailMessageAdapter;

import org.json.JSONArray;
import org.json.JSONException;
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

public class QuotedPriceActivity extends BaseActivity {
    @BindView(R.id.tv_product_name)
    TextView tvProductName;
    @BindView(R.id.tv_address)
    TextView tvAddress;
    @BindView(R.id.tv_phone)
    TextView tvPhone;
    @BindView(R.id.iv_place_order)
    ImageView ivPlaceOrder;
    @BindView(R.id.tv_order_number)
    TextView tvOrderNumber;
    @BindView(R.id.tv_create_time)
    TextView tvCreateTime;
    @BindView(R.id.bt_commit_no)
    TextView btCommitNo;
    @BindView(R.id.bt_commit_price)
    TextView btCommitPrice;
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String user_id;
    private OkHttpClient okHttpClient;
    private String orders_id;
    private List<ClearGoodsModel> modelList = new ArrayList<>();
    private ClearGoodsAdapter messageAdapter;
    private WaitDialog dialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        setContentView(R.layout.activity_quoted_price);
        ButterKnife.bind(this);
        initParams();
        initRecyclerView();
        requestClearType();    //获取清洗衣服
    }

    private void initRecyclerView() {
        messageAdapter = new ClearGoodsAdapter(modelList);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(messageAdapter);

        messageAdapter.setOnValueChanage(new ClearGoodsAdapter.OnValueChanage() {
            @Override
            public void onValueChanage(int position, int value) {
                ClearGoodsModel data = modelList.get(position);
                data.setNumber(value);
                messageAdapter.notifyDataSetChanged();
            }
        });
    }

    private void requestClearType() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitConfig.BaseURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);
        Map<String, String> requestMap = NoHttpRequest.setHeaderParams(user_id);

        //传入请求参数
        Call<ResponseBody> call = retrofitApi.getClearType(requestMap, orders_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String json = response.body().string();
                    LogUtil.d("QuotedPriceActivity::", json);
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    if ("5001".equals(code)) {
                        setClearType(dataArray);
                    } else {
                        ToastUtil.showShort(msg);
                    }
                    messageAdapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                ToastUtil.showShort(t.getMessage());
            }
        });
    }

    private void setClearType(JSONArray dataArray) throws JSONException {

        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject dataObj = dataArray.getJSONObject(i);
            String title = dataObj.getString("title");
            JSONArray goodsArray = dataObj.getJSONArray("goods");
            for (int j = 0;j < goodsArray.length() ;j++) {
                ClearGoodsModel clearGoodsModel = new ClearGoodsModel();
                JSONObject goodsObj = goodsArray.getJSONObject(j);
                String id = goodsObj.getString("id");
                String goodsTitle = goodsObj.getString("title");
                String price = goodsObj.getString("price");
                String states = goodsObj.getString("states");
                String flag = goodsObj.getString("flag");
                if (j == 0) {
                    clearGoodsModel.setType(title);
                } else {
                    clearGoodsModel.setType("");
                }

                clearGoodsModel.setId(id);
                clearGoodsModel.setTitle(goodsTitle);
                clearGoodsModel.setPrice(price);
                clearGoodsModel.setStates(states);
                clearGoodsModel.setFlag(flag);
                clearGoodsModel.setNumber(0);
                modelList.add(clearGoodsModel);
            }
        }
    }

    private void initParams() {
        Intent intent = getIntent();
        ProductModel productModel = (ProductModel) intent.getSerializableExtra("productModel");
        orders_id = productModel.getOrders_id();

        tvAddress.setText(productModel.getAddress());
        tvPhone.setText(productModel.getPhone());
        tvOrderNumber.setText(productModel.getOrderNumber());
        tvCreateTime.setText(DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm", productModel.getCreateTime()));

        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }

        OkLogInterceptor okLogInterceptor = OkLogInterceptor.builder().build();
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.addInterceptor(okLogInterceptor);
        okHttpClient = okHttpBuilder.build();


    }

    @OnClick({R.id.bt_commit_no, R.id.bt_commit_price, R.id.iv_place_order})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_commit_no:
                finish();
                break;
            case R.id.bt_commit_price:
                commitPrice();
                break;
            case R.id.iv_place_order:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + tvPhone.getText().toString().trim());
                intent.setData(data);
                startActivity(intent);
                break;
        }
    }

    private void commitPrice() {
        dialogLoading = WaitDialog.show(this, "提交中...").setCanCancel(true);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitConfig.BaseURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);
        Map<String, String> requestMap = NoHttpRequest.setHeaderParams(user_id);

        String commodity_id = getGoodsId();
        String goods_number = getGoodsNumber();
        //传入请求参数
        Call<ResponseBody> call = retrofitApi.commitGoodsCategory(requestMap, orders_id,commodity_id,goods_number);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String json = response.body().string();
                    LogUtil.d("QuotedPriceActivity::", json);
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");
                    if ("5001".equals(code)) {
                        setResult(RESULT_OK);
                        finish();
                    }
                    dialogLoading.doDismiss();
                    ToastUtil.showShort(msg);

                } catch (Exception e) {
                    e.printStackTrace();
                    dialogLoading.doDismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogLoading.doDismiss();
                ToastUtil.showShort(t.getMessage());
            }
        });
    }

    private String getGoodsNumber() {
        StringBuilder sb = new StringBuilder();
        for (ClearGoodsModel model : modelList) {
            if (model.getNumber() != 0) {
                sb.append(model.getNumber());
                sb.append(",");
            }
        }
        String result = sb.toString();
        String realResult = result.substring(0,result.lastIndexOf(","));
        return realResult;
    }

    private String getGoodsId() {
        StringBuilder sb = new StringBuilder();
        for (ClearGoodsModel model : modelList) {
            if (model.getNumber() != 0) {
                sb.append(model.getId());
                sb.append(",");
            }
        }
        String result = sb.toString();
        String realResult = result.substring(0,result.lastIndexOf(","));
        return realResult;
    }
}
