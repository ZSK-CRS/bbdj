package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.simonpercic.oklog3.OkLogInterceptor;
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.InterApi;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.internet.RetrofitApi;
import com.mt.bbdj.baseconfig.internet.RetrofitConfig;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.CannelOrderAdapter;
import com.mt.bbdj.community.adapter.CauseForcannelOrderAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
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
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

//取消订单原因
public class CannelOrderActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.rl_cause_order)
    RecyclerView rlCauseOrder;
    @BindView(R.id.bt_commit_next)
    TextView btCommitNext;
    @BindView(R.id.bt_commit_no)
    TextView bt_commit_no;
    @BindView(R.id.tv_refause_title)
    TextView tvRefauseTitle;

    private String user_id;    //用户id
    private String mail_id;     //订单id
    private String reason_id;    //取消原因id
    private String content;   //备注

    private List<HashMap<String, String>> mList = new ArrayList<>();

    private final int REQUEST_CAUSE_CANNEL = 101;    //请求取消订单原因
    private final int REQUEST_COMMIT_CAUSE = 102;   //提交取消订单
    private final int REQUEST_CANNEL_ORDER_FROM_SEN_MANAGER = 103;    //从寄件管理界面跳转过来取消订单

    public static final String STATE_CANNEL_FOR_EXPRESS = "100";      //快递取消
    public static final String STATE_CANNEL_FOR_SERVICE = "200";    //服务类型的取消

    private CannelOrderAdapter mAdapter;

    private RequestQueue mRequestQueue;
    private String cannel_type;   //0 ：快递  1：桶装水  2: 干洗
    private OkHttpClient okHttpClient;
    private String orders_id;
    private WaitDialog dialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        setContentView(R.layout.activity_cause__order);
        ButterKnife.bind(this);
        initParams();
        initData();
        initList();
        initClickListener();
        RequestCauseData();    //获取取消原因
    }

    @OnClick({R.id.iv_back, R.id.bt_commit_next, R.id.bt_commit_no})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_commit_next:
                commitCauseRequest();
                break;
            case R.id.bt_commit_no:
                finish();
                break;
        }
    }

    private void handleResult(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case REQUEST_CAUSE_CANNEL:
                handleCannelCause(jsonObject);    //获取取消消息
                break;
            case REQUEST_COMMIT_CAUSE:     //取消
            case REQUEST_CANNEL_ORDER_FROM_SEN_MANAGER:    //取消从寄件管理来的订单
                ToastUtil.showShort("提交成功！");
                EventBus.getDefault().post(new TargetEvent(1));
                finish();
                break;
        }
    }

    private void handleCannelCause(JSONObject jsonObject) throws JSONException {
        JSONArray data = jsonObject.getJSONArray("data");
        mList.clear();
        int number = 1;
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonObject1 = data.getJSONObject(i);
            String reason_id = jsonObject1.getString("reason_id");
            String reason_name = jsonObject1.getString("reason_name");
            HashMap<String, String> map = new HashMap<>();
            map.put("reason_id", reason_id);
            map.put("reason_name", reason_name);
            map.put("reason_number", "0" + (number + i) + ".");
            mList.add(map);
        }
        mAdapter.notifyDataSetChanged();
    }

    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            dialogLoading = WaitDialog.show(CannelOrderActivity.this, "提交中...").setCanCancel(true);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "CauseCannelOrderActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    handleResult(what, jsonObject);
                } else {
                    ToastUtil.showShort(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            dialogLoading.doDismiss();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            dialogLoading.doDismiss();
        }

        @Override
        public void onFinish(int what) {
            dialogLoading.doDismiss();
        }
    };

    private void initClickListener() {

        mAdapter.setOnItemClickListener(new CannelOrderAdapter.OnItemClickListener() {
            @Override
            public void OnClick(int position) {
                reason_id = mList.get(position).get("reason_id");
                HashMap<String, String> map = mList.get(position);
                mAdapter.setCurrentClickPosition(position);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initData() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
    }

    private void RequestCauseData() {
        Request<String> request = null;
        if (STATE_CANNEL_FOR_EXPRESS.equals(cannel_type)) {   //快递
            request = NoHttpRequest.getCauseForCannelOrderRequest(user_id);
            mRequestQueue.add(REQUEST_CAUSE_CANNEL, request, onResponseListener);
        } else if (STATE_CANNEL_FOR_SERVICE.equals(cannel_type)) {   //桶装水和干洗
            getCannelCause();
        }
    }

    private void getCannelCause() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InterApi.BaseURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);
        Map<String, String> requestMap = NoHttpRequest.setHeaderParams(user_id);
        //传入请求参数
        Call<ResponseBody> call = retrofitApi.getCannelCause(requestMap);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    String json = response.body().string();
                    LogUtil.d("ComFirstFragment_new::", json);
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");

                    if ("5001".equals(code)) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        mList.clear();
                        int number = 1;
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsonObject1 = data.getJSONObject(i);
                            String reason_id = jsonObject1.getString("id");
                            String reason_name = jsonObject1.getString("reason_name");
                            HashMap<String, String> map = new HashMap<>();
                            map.put("reason_id", reason_id);
                            map.put("reason_name", reason_name);
                            map.put("reason_number", "0" + (number + i) + ".");
                            mList.add(map);
                        }
                        mAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showShort(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtil.d("ComFirstFragment_new::", t.getMessage());
                ToastUtil.showShort(t.getMessage());
            }
        });
    }


    private void initList() {
        mAdapter = new CannelOrderAdapter(mList);
        rlCauseOrder.setFocusable(false);
        rlCauseOrder.setNestedScrollingEnabled(false);
        rlCauseOrder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
        rlCauseOrder.setAdapter(mAdapter);
    }

    private void initParams() {
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        mail_id = intent.getStringExtra("mail_id");
        cannel_type = intent.getStringExtra("cannel_type");
        orders_id = intent.getStringExtra("orders_id");

        OkLogInterceptor okLogInterceptor = OkLogInterceptor.builder().build();
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.addInterceptor(okLogInterceptor);
        okHttpClient = okHttpBuilder.build();
    }

    private void commitCauseRequest() {
        if (STATE_CANNEL_FOR_EXPRESS.equals(cannel_type)) {     //快递取消
            Request<String> request = NoHttpRequest.commitCannelOrderCauseRequest(user_id, mail_id, reason_id, content);
            mRequestQueue.add(REQUEST_COMMIT_CAUSE, request, onResponseListener);
        } else {
            cannmeServiceOrder();    //取消服务类型的订单
        }
    }

    private void cannmeServiceOrder() {
        dialogLoading = WaitDialog.show(this, "提交中...").setCanCancel(true);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InterApi.BaseURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);
        Map<String, String> requestMap = NoHttpRequest.setHeaderParams(user_id);
        //传入请求参数
        Call<ResponseBody> call = retrofitApi.confirmCannelOrder(requestMap, orders_id, reason_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    String json = response.body().string();
                    LogUtil.d("CannelOrderActivity::", json);
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");
                    if ("5001".equals(code)) {
                        setResult(RESULT_OK);
                        finish();
                    }
                    ToastUtil.showShort(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtil.d("CannelOrderActivity::", t.getMessage());
                ToastUtil.showShort(t.getMessage());
            }
        });
    }
}
