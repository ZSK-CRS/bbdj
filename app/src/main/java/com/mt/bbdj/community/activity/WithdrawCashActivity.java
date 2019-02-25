package com.mt.bbdj.community.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kongzue.dialog.v2.MessageDialog;
import com.kongzue.dialog.v2.SelectDialog;
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.activity.RegisterAccountActivity;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.BindAccountModel;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WithdrawCashActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.tv_select_account)
    TextView tvSelectAccount;
    @BindView(R.id.ll_withdraw_cash_account)
    LinearLayout llWithdrawCashAccount;
    @BindView(R.id.et_get_money)
    EditText etGetMoney;
    @BindView(R.id.ll_withdraw_record)
    LinearLayout llWithdrawRecord;
    @BindView(R.id.et_phone)
    TextView etPhone;
    @BindView(R.id.ll_consume_record)
    LinearLayout llConsumeRecord;
    @BindView(R.id.et_verofy_number)
    EditText etVerofyNumber;
    @BindView(R.id.bt_get_verify)
    Button btGetVerify;
    @BindView(R.id.ll_bind_account)
    LinearLayout llBindAccount;
    @BindView(R.id.bt_commit)
    Button btCommit;

    private MyCountDownTimer mCountDownTimer;
    private RequestQueue mRequestQueue;    //请求队列
    private String mRandCode = "";      //验证码
    private String mRegisterPhone = "";     //注册手机号
    private UserBaseMessage userBaseMessage;
    private String user_id;
    private int type = 1;     // 1：银行卡  2：支付宝
    private WaitDialog waitDialog;
    private String[] bindAccountArray;
    ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_cash);
        ButterKnife.bind(this);
        initView();
        initParams();
        requestAccountMessage();
    }

    private void initParams() {
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao userBaseMessageDao = daoSession.getUserBaseMessageDao();
        List<UserBaseMessage> userBaseMessages = userBaseMessageDao.queryBuilder().list();
        if (userBaseMessages.size() != 0) {
            userBaseMessage = userBaseMessages.get(0);
            etPhone.setText(userBaseMessage.getContact_number());
            user_id = userBaseMessage.getUser_id();
        }
    }

    private void initView() {
        //初始化计时器
        mCountDownTimer = new MyCountDownTimer(60000, 1000, btGetVerify);
        mRequestQueue = NoHttp.newRequestQueue();
    }


    @OnClick({R.id.tv_select_account, R.id.bt_get_verify, R.id.bt_commit, R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_select_account:
                showAccountDialog();
                break;
            case R.id.bt_get_verify:
                sendVerify();
                break;
            case R.id.bt_commit:
                commitMessage();
                break;
            case R.id.iv_back:
                finish();
                break;
        }
    }

    private void commitMessage() {
        String currentCode = etVerofyNumber.getText().toString();
        String slectAccount = tvSelectAccount.getText().toString();
        String money = etGetMoney.getText().toString();
        if (!mRandCode.equals(currentCode)) {
            ToastUtil.showShort("验证码有误！");
            return;
        }
        if ("".equals(slectAccount)) {
            ToastUtil.showShort("请选择提现账户！");
            return;
        }
        if ("".equals(money)) {
            ToastUtil.showShort("请输入提现金额！");
            return;
        }
        Request<String> request = NoHttpRequest.getApplyGetMoneyRequest(user_id, type+"", money);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                waitDialog = WaitDialog.show(WithdrawCashActivity.this, "提交中...");
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "WithDrawCashActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        ToastUtil.showShort(msg);
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                waitDialog.doDismiss();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                waitDialog.doDismiss();
            }

            @Override
            public void onFinish(int what) {
                waitDialog.doDismiss();
            }
        });
    }

    private void sendVerify() {
        String phoneNumber = etPhone.getText().toString();
        //验证手机号码的合法性
        boolean isRight = StringUtil.isMobile(phoneNumber);
        if (!isRight) {
            ToastUtil.showShort("请输入正确的手机号码！");
            return;
        }

        mRegisterPhone = phoneNumber;

        Request<String> request = NoHttpRequest.getIdentifyCodeRequest(phoneNumber, "3");
        mRequestQueue.add(0, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                mCountDownTimer.start();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "WithDrawCashActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        JSONObject dataObject = jsonObject.getJSONObject("data");
                        //验证码
                        mRandCode = dataObject.get("rand").toString();
                        ToastUtil.showShort(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mCountDownTimer.cancel();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                mCountDownTimer.cancel();
            }

            @Override
            public void onFinish(int what) {
                mCountDownTimer.onFinish();
            }
        });
    }


    private void showAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择提现方式:");
        builder.setSingleChoiceItems(bindAccountArray, 2, new DialogInterface.OnClickListener() {/*设置单选条件的点击事件*/
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvSelectAccount.setText(bindAccountArray[which]);
                type = which + 1;
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private static class MyCountDownTimer extends CountDownTimer {

        private WeakReference<TextView> mViewRefrence;

        public MyCountDownTimer(long millisInFuture, long countDownInterval, TextView textView) {
            super(millisInFuture, countDownInterval);
            mViewRefrence = new WeakReference<TextView>(textView);
        }

        //计时过程
        @Override
        public void onTick(long millisUntilFinished) {
            if (mViewRefrence != null && mViewRefrence.get() != null) {
                TextView identifyTv = mViewRefrence.get();
                identifyTv.setClickable(false);
                identifyTv.setText(millisUntilFinished / 1000 + "s");
            }
        }

        @Override
        public void onFinish() {
            TextView identifyTv = mViewRefrence.get();
            identifyTv.setClickable(true);
            identifyTv.setText("获取验证码");
        }
    }

    private void requestAccountMessage() {
        Request<String> request = NoHttpRequest.checkisBindAccountRequest(user_id);
        mRequestQueue.add(3, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "BindAccountActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.getString("code");
                    if ("4001".equals(code)) {
                        // 表示没有绑定任何账号
                        showPromitDialog();
                    } else if ("5001".equals(code)) {
                        // 都已经绑定
                        handleBindAll(jsonObject);
                    } else if ("5002".equals(code)) {
                        // 银行卡绑定
                        handleBindBank(jsonObject);
                    } else if ("5003".equals(code)) {
                        // 支付宝绑定
                        handleBindAli(jsonObject);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    private void showPromitDialog() {
        MessageDialog.show(this, "提示", "请先绑定提现账户！", "确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(WithdrawCashActivity.this, BindAccountActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                        finish();
                    }
                });
    }

    private void handleBindAll(JSONObject jsonObject) throws JSONException {
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject cardObj = data.getJSONObject("card");
        String bankName = cardObj.getString("realname");
        String bankNumber = cardObj.getString("number");
        String bank = cardObj.getString("bank");
        bankNumber = StringUtil.encryptBankNumber(bankNumber);

        JSONObject payObj = data.getJSONObject("pay");
        String realname = payObj.getString("realname");
        String account = payObj.getString("account");
        account = StringUtil.encryptPhone(account);
        bindAccountArray = new String[]{bankNumber, account};
    }

    private void handleBindBank(JSONObject jsonObject) throws JSONException {
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject cardObj = data.getJSONObject("card");
        String bankName = cardObj.getString("realname");
        String bankNumber = cardObj.getString("number");
        String bank = cardObj.getString("bank");
        bankNumber = StringUtil.encryptBankNumber(bankNumber);
        bindAccountArray = new String[]{bankNumber};
    }

    private void handleBindAli(JSONObject jsonObject) throws JSONException {
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject payObj = data.getJSONObject("pay");
        String realname = payObj.getString("realname");
        String account = payObj.getString("account");
        account = StringUtil.encryptPhone(account);
        bindAccountArray = new String[]{account};
    }

}
