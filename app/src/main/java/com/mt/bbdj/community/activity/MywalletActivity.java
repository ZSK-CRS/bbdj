package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
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

public class MywalletActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.ll_withdraw_record)
    LinearLayout llWithdrawRecord;
    @BindView(R.id.ll_consume_record)
    LinearLayout llConsumeRecord;
    @BindView(R.id.ll_bind_account)
    LinearLayout llBindAccount;
    @BindView(R.id.tv_current_money)
    TextView tvCurrentMoney;
    @BindView(R.id.tv_ming_money)
    TextView tvMingMoney;   //警戒余额
    private UserBaseMessage userBaseMessage;
    private String user_id;
    private UserBaseMessageDao userBaseMessageDao;
    private RequestQueue mRequestQueue;

    @BindView(R.id.bt_recharge)
    Button recharge;
    private IWXAPI api;
    private String contact_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mywallet);
        ButterKnife.bind(this);
        initParams();
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestBaseMessage();
    }

    private void initParams() {
        mRequestQueue = NoHttp.newRequestQueue();
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        userBaseMessageDao = daoSession.getUserBaseMessageDao();
        List<UserBaseMessage> userBaseMessages = userBaseMessageDao.queryBuilder().list();
        if (userBaseMessages.size() != 0) {
            userBaseMessage = userBaseMessages.get(0);
            user_id = userBaseMessage.getUser_id();
        }
    }

    @OnClick({R.id.iv_back, R.id.bt_withdraw_cash, R.id.ll_withdraw_record,
            R.id.ll_consume_record, R.id.ll_bind_account, R.id.bt_recharge, R.id.ll_recharge_record})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_withdraw_cash:
                showWithdrawCashPannel();    //提现面板
                break;
            case R.id.ll_withdraw_record:
                showWithdrawCashRecordPannel();   //提现记录
                break;
            case R.id.ll_consume_record:
                showConsumeRecordPannel();    //消费记录
                break;
            case R.id.ll_bind_account:
                showBindAccountPannel();    //绑定提现账户
                break;
            case R.id.bt_recharge:    //充值
                handleRecharge();
                break;
            case R.id.ll_recharge_record:     //充值记录
                showRechargePannel();
                break;
        }
    }

    private void showRechargePannel() {
        Intent intent = new Intent(this, RechargeRecordActivity.class);
        startActivity(intent);
    }

    private void handleRecharge() {
        Intent intent = new Intent(this, RechargeActivity.class);
        startActivity(intent);
    }

    private void showBindAccountPannel() {
        Intent intent = new Intent(this, BindAccountActivity.class);
        startActivity(intent);
    }

    private void showConsumeRecordPannel() {
        Intent intent = new Intent(this, ConsumeRecordActivity.class);
        startActivity(intent);
    }

    private void showWithdrawCashRecordPannel() {
        Intent intent = new Intent(this, WithdrawCashRecordActivity.class);

        startActivity(intent);
    }

    private void showWithdrawCashPannel() {
        Intent intent = new Intent(this, WithdrawCashActivity.class);
        intent.putExtra("contact_number", contact_number);
        startActivity(intent);
    }

    private void requestBaseMessage() {
        DaoSession mDaoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = mDaoSession.getUserBaseMessageDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        Request<String> request = NoHttpRequest.getUserBaseMessageRequest(user_id);
        mRequestQueue.add(1, request, onResponseListener);
    }

    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "ComMymessageFragment::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                JSONObject dataObj = jsonObject.getJSONObject("data");
                if ("5001".equals(code)) {
                    userBaseMessageDao.deleteAll();
                    String user_id = dataObj.getString("user_id");
                    String headimg = dataObj.getString("headimg");
                    String mingcheng = dataObj.getString("mingcheng");
                    String contacts = dataObj.getString("contacts");
                    contact_number = dataObj.getString("contact_number");
                    String contact_email = dataObj.getString("contact_email");
                    String contact_account = dataObj.getString("contact_account");
                    String birthday = dataObj.getString("birthday");
                    String balance = dataObj.getString("balance");
                    String min_balance = dataObj.getString("min_balance");    //境界余额
                    UserBaseMessage userBaseMessage = new UserBaseMessage();
                    userBaseMessage.setUser_id(user_id);
                    userBaseMessage.setHeadimg(headimg);
                    userBaseMessage.setMingcheng(mingcheng);
                    userBaseMessage.setContacts(contacts);
                    userBaseMessage.setContact_number(contact_number);
                    userBaseMessage.setContact_email(contact_email);
                    userBaseMessage.setContact_account(contact_account);
                    userBaseMessage.setBirthday(birthday);
                    userBaseMessage.setBalance(balance);
                    tvCurrentMoney.setText("当前余额 :  " + balance);
                    userBaseMessageDao.save(userBaseMessage);
                    tvMingMoney.setText("警戒余额 : "+ min_balance);
                } else {
                    ToastUtil.showShort(msg);
                }
            } catch (JSONException e) {
                ToastUtil.showShort("连接服务器失败！");
            }

        }

        @Override
        public void onFailed(int what, Response<String> response) {

        }

        @Override
        public void onFinish(int what) {

        }
    };
}
