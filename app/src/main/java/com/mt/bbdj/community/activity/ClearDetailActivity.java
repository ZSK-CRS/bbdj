package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
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
import com.mt.bbdj.baseconfig.model.ProductModel;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClearDetailActivity extends BaseActivity {

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
    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.tv_confirm_price)
    TextView tvConfirmPrice;
    private ProductModel productModel;
    private OkHttpClient okHttpClient;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String user_id;
    private WaitDialog dialogLoading;

    private final int COMMIT_PRICE = 100;   //提交报价
    private final int REQUEST_CANNEL = 200;    //取消原因

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clear_detail);
        ButterKnife.bind(this);
        initParams();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == COMMIT_PRICE || requestCode == REQUEST_CANNEL) {
            finish();
        }
    }

    private void initParams() {
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

        Intent intent = getIntent();
        productModel = (ProductModel) intent.getSerializableExtra("productModel");
        int clearState = productModel.getClearState();
        //设置视图状态
        initView(clearState);

        tvAddressTitle.setText(productModel.getAddress());
        tvContent.setText((DateUtil.changeStampToStandrdTime("HH:mm",productModel.getContext())+"上门"));
        tvPhoneTitle.setText(productModel.getPhone());
    }

    private void initView(int clearState) {
        switch (clearState) {
            case 1:     //显示电话联系、取消、确认接单
                tvCall.setVisibility(View.VISIBLE);
                tvCannelOrder.setVisibility(View.VISIBLE);
                tvConfirmReceive.setVisibility(View.VISIBLE);
                tvConfirmSend.setVisibility(View.GONE);
                tvConfirmPrice.setVisibility(View.GONE);
                break;
            case 2:      //显示确认报价
                tvCall.setVisibility(View.VISIBLE);
                tvCannelOrder.setVisibility(View.VISIBLE);
                tvConfirmReceive.setVisibility(View.GONE);
                tvConfirmSend.setVisibility(View.GONE);
                tvConfirmPrice.setVisibility(View.VISIBLE);
                break;
            case 7:
                tvCall.setVisibility(View.VISIBLE);
                tvCannelOrder.setVisibility(View.GONE);
                tvConfirmReceive.setVisibility(View.GONE);
                tvConfirmSend.setVisibility(View.VISIBLE);
                tvConfirmPrice.setVisibility(View.GONE);
                break;
        }
    }

    @OnClick({R.id.tv_call, R.id.tv_cannel_order, R.id.tv_confirm_receive, R.id.tv_confirm_send,R.id.tv_confirm_price})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_call:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + tvPhoneTitle.getText().toString().trim());
                intent.setData(data);
                startActivity(intent);
                break;
            case R.id.tv_cannel_order:
                Intent intent1 = new Intent(this,CannelOrderActivity.class);
                String orders_id = productModel.getOrders_id();
                intent1.putExtra("user_id",user_id);
                intent1.putExtra("cannel_type",CannelOrderActivity.STATE_CANNEL_FOR_SERVICE);
                intent1.putExtra("orders_id",orders_id);
                startActivityForResult(intent1,REQUEST_CANNEL);
                break;
            case R.id.tv_confirm_receive:
                receiveClearOrder();
                break;
            case R.id.tv_confirm_send:

                break;
            case R.id.tv_confirm_price:
                //干洗提交报价
                Intent intent2 = new Intent(this, QuotedPriceActivity.class);
                intent2.putExtra("productModel",productModel);
                startActivityForResult(intent2,COMMIT_PRICE);
                break;
        }
    }

    private void receiveClearOrder() {
        dialogLoading = WaitDialog.show(this, "提交中...").setCanCancel(true);
        String order_id = productModel.getOrders_id();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitConfig.BaseURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);
        Map<String, String> requestMap = NoHttpRequest.setHeaderParams(user_id);
        //传入请求参数
        Call<ResponseBody> call = retrofitApi.confirmReceiveClearOrder(requestMap, order_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String json = response.body().string();
                    LogUtil.d("ClearDetailActivity::", json);
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");

                    if ("5001".equals(code)) {     //接单成功
                        //显示报价
                      finish();
                    }
                    ToastUtil.showShort(msg);
                    dialogLoading.doDismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    dialogLoading.doDismiss();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtil.d("ComFirstFragment_new::", t.getMessage());
                ToastUtil.showShort(t.getMessage());
                dialogLoading.doDismiss();
            }
        });

    }



}
