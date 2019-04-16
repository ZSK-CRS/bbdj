package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RepertoryDetailActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.tv_wail_number)
    TextView tvWailNumber;
    @BindView(R.id.tv_logo)
    ImageView tvLogo;
    @BindView(R.id.tv_express_name)
    TextView tvExpressName;
    @BindView(R.id.tv_tag_number)
    TextView tvTagNumber;
    @BindView(R.id.tv_enter_time)
    TextView tvEnterTime;
    @BindView(R.id.tv_out_time)
    TextView tvOutTime;
    @BindView(R.id.bt_print)
    Button btPrint;
    @BindView(R.id.bt_out)
    Button btOut;
    @BindView(R.id.ll_out_layout)
    LinearLayout llOutLayout;

    @BindView(R.id.ll_sign_name)
    LinearLayout llSignName;     //签名人
    @BindView(R.id.ll_sign_layout)
    LinearLayout llSignTime;    //签收时间
    @BindView(R.id.tv_sign_name)
    TextView tvSingName;     //签收人
    @BindView(R.id.tv_sign_time)
    TextView tvSingTime;   //签收时间

    private boolean isEffective = false;

    private int mType;
    private RequestQueue mRequestQueue;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String user_id;
    private String pie_id;

    private final int GET_DETAIL_REQUEST = 100;     //获取详情
    private final int OUT_WAY_BILL_REQUEST = 200;     //获取详情

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repertory_detail);
        ButterKnife.bind(this);
        initParams();
        initView();
        requestData();
    }

    private void requestData() {
        Request<String> request = NoHttpRequest.getExpressDetailRequest(user_id,pie_id);
        mRequestQueue.add(GET_DETAIL_REQUEST,request,onResponseListener);
    }

    private WaitDialog dialogLoading;
    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            dialogLoading = WaitDialog.show(RepertoryDetailActivity.this, "请稍后...").setCanCancel(true);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "RepertoryDetailActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    JSONObject dataObj = jsonObject.getJSONObject("data");
                    String is_signatory = dataObj.getString("is_signatory");
                    String signatory = dataObj.getString("signatory");
                    String signatory_time = dataObj.getString("signatory_time");
                    String types = dataObj.getString("types");
                    signatory = StringUtil.handleNullResultForString(signatory);
                    signatory_time = DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm:ss",signatory_time);
                    if ("1".equals(is_signatory)) {
                        llSignName.setVisibility(View.GONE);
                        llSignTime.setVisibility(View.GONE);
                    } else {
                        llSignName.setVisibility(View.VISIBLE);
                        llSignTime.setVisibility(View.VISIBLE);
                        tvSingName.setText(signatory);
                        tvSingTime.setText(signatory_time);
                    }

                    if ("1".equals(types)) {
                        isEffective = true;
                    } else {
                        isEffective = false;
                    }

                } else {
                    ToastUtil.showShort(msg);
                }

            } catch (JSONException e) {
                e.printStackTrace();
                dialogLoading.doDismiss();
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

    private void initView() {
        //出库相关信息的控件显示
        if (mType == 0) {
            llOutLayout.setVisibility(View.GONE);
            btOut.setVisibility(View.VISIBLE);
        } else {
            llOutLayout.setVisibility(View.VISIBLE);
            btOut.setVisibility(View.GONE);
        }
    }

    private void initParams() {
        Intent intent = getIntent();
        mType = intent.getIntExtra("type", 0);
        pie_id = intent.getStringExtra("pie_id");

        String order = intent.getStringExtra("order");
        String express = intent.getStringExtra("express");
        String time = intent.getStringExtra("time");
        String tag_number = intent.getStringExtra("tag_number");

        tvWailNumber.setText(StringUtil.handleNullResultForString(order));
        tvExpressName.setText(StringUtil.handleNullResultForString(express));
        tvTagNumber.setText(StringUtil.handleNullResultForString(tag_number));
        tvEnterTime.setText(StringUtil.handleNullResultForString(time));

        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();

        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }

    }

    @OnClick({R.id.iv_back, R.id.bt_print, R.id.bt_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_print:
                // TODO: 2019/4/12 原单重打
                break;
            case R.id.bt_out:
                outOfRepertory();
                break;
        }
    }

    private void outOfRepertory() {
        if (!isEffective) {
            ToastUtil.showShort("不可多次出库！");
            return;
        }
        Request<String> request = NoHttpRequest.outOfRepertoryRequest(user_id, pie_id);
        mRequestQueue.add(OUT_WAY_BILL_REQUEST, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                 dialogLoading = WaitDialog.show(RepertoryDetailActivity.this, "请稍后...").setCanCancel(true);

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "RepertoryDetailActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    ToastUtil.showShort(msg);
                    if ("5001".equals(code)) {
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialogLoading.doDismiss();
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
        });
    }
}
