package com.mt.bbdj.baseconfig.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.DestroyEvent;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.activity.RegisterAggreementActivity;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterAccountActivity extends BaseActivity {

    @BindView(R.id.tv_identify_number)
    TextView mIdentifyNumber;    //验证码
    @BindView(R.id.bt_register_next)
    Button mBtRegisterNext;     //下一步
    @BindView(R.id.iv_back)
    LinearLayout mBack;             //返回
    @BindView(R.id.et_main_register_name)
    EditText mRegisterName;     //用户名
    @BindView(R.id.et_main_register_code)
    EditText mRegisterCode;     //验证码
    @BindView(R.id.et_main_register_password)
    EditText mRegisterPassword;  //密码
    @BindView(R.id.et_main_bussiess_number)
    EditText mBusinessNumber;   //工号
    @BindView(R.id.cb_check)
    CheckBox cbCheck;    //选中阅读协议
    @BindView(R.id.tv_read_agreement)
    TextView tvReadAgreement;

    private MyCountDownTimer mCountDownTimer;
    private RequestQueue mRequestQueue;    //请求队列
    private String mRandCode = "";      //验证码
    private String mRegisterPhone = "";     //注册手机号


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_account);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initListener();
    }

    private void initListener() {
        cbCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                }
            }
        });
    }

    private void initData() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        //初始化计时器
        mCountDownTimer = new MyCountDownTimer(60000, 1000, mIdentifyNumber);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleMessage(DestroyEvent destroyEvent) {
        if (1 == destroyEvent.getType()) {
            finish();
        }
    }

    @OnClick({R.id.tv_identify_number, R.id.bt_register_next, R.id.iv_back, R.id.tv_read_agreement})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_identify_number:
                handleIdentifyNumberEvent();   //获取验证码
                break;
            case R.id.bt_register_next:
                handleCompleteRegisterEvent();    //完善注册信心
                break;
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_read_agreement:       //跳转协议
                handleReadAgreementEvent();
                break;
        }
    }

    private void handleReadAgreementEvent() {
        Intent intent = new Intent(this, RegisterAggreementActivity.class);
        startActivity(intent);
    }

    private void handleCompleteRegisterEvent() {
        String currentPhone = mRegisterName.getText().toString();
        String currentCode = mRegisterCode.getText().toString();
        String currentPassword = mRegisterPassword.getText().toString();
        String businessNumber = mBusinessNumber.getText().toString();

        if (!mRegisterPhone.equals(currentPhone)) {
            ToastUtil.showShort("注册账号发生变化！");
            return;
        }
        if (!mRandCode.equals(currentCode)) {
            ToastUtil.showShort("验证码有误！");
            return;
        }
        if ("".equals(currentPassword) || currentPassword.length() < 6 || currentPassword.length() > 16) {
            ToastUtil.showShort("请输入6~16位数的密码！");
            return;
        }
        if (!cbCheck.isChecked()) {
            ToastUtil.showShort("请先阅读协注册协议");
            return;
        }
        //保存账号信息
        SharedPreferences.Editor mEditor = SharedPreferencesUtil.getEditor();
        mEditor.putString("phone", currentPhone);
        mEditor.putString("identifyCode", currentCode);
        mEditor.putString("password", currentPassword);
        mEditor.putString("businessNumber", businessNumber);
        mEditor.commit();

        Intent intent = new Intent(this, RegisterCompleteActivity.class);
        startActivity(intent);

    }

    private void handleIdentifyNumberEvent() {
        String phoneNumber = mRegisterName.getText().toString();
        //验证手机号码的合法性
        boolean isRight = StringUtil.isMobile(phoneNumber);
        if (!isRight) {
            ToastUtil.showShort("请输入正确的手机号码！");
            return;
        }

        mRegisterPhone = phoneNumber;

        Request<String> request = NoHttpRequest.getIdentifyCodeRequest(phoneNumber, "1");
        mRequestQueue.add(0, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                mCountDownTimer.start();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "RegisterAccountActivity::" + response.get());
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
                //
                // mCountDownTimer.cancel();
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

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
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
                int currentTime = (int) (millisUntilFinished / 1000);
                if (currentTime == 0) {
                    identifyTv.setClickable(true);
                    identifyTv.setText("获取验证码");
                    cancel();
                } else {
                    identifyTv.setText(millisUntilFinished / 1000 + "s");
                }
            }
        }

        @Override
        public void onFinish() {
            TextView identifyTv = mViewRefrence.get();
            identifyTv.setClickable(true);
            identifyTv.setText("获取验证码");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }
}
