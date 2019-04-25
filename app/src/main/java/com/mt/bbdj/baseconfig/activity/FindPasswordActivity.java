package com.mt.bbdj.baseconfig.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
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

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FindPasswordActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    LinearLayout ivBack;
    @BindView(R.id.tv_identify_number)
    TextView mIdentifyNumber;
    @BindView(R.id.bt_find_password)
    Button mBtFindPassword;
    @BindView(R.id.et_findword_phone)
    EditText etFindwordPhone;
    @BindView(R.id.et_findword_code)
    EditText etFindwordCode;
    @BindView(R.id.et_findword_new_password)
    EditText etFindwordNewPassword;
    @BindView(R.id.et_findword_again_password)
    EditText etFindwordAgainPassword;
    private MyCountDownTimer mCountDownTimer;

    private RequestQueue mRequestQueue;
    private HkDialogLoading dialogLoading;
    private String mRegisterPhone;
    private String mRandCode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        dialogLoading = new HkDialogLoading(FindPasswordActivity.this, "提交中...");
        mCountDownTimer = new MyCountDownTimer(60000, 1000, mIdentifyNumber);
    }

    @OnClick({R.id.iv_back, R.id.tv_identify_number, R.id.bt_find_password})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_identify_number:
                handleIdentifyNumberEvent();    //倒计时
                break;
            case R.id.bt_find_password:
                findPasswordEvent();
                break;
        }
    }

    private void findPasswordEvent() {
        //验证信息是否正确
        if (isRightMessage()) {
            changePassword();
        }
    }

    private void changePassword() {
        String phone = etFindwordPhone.getText().toString();
        String password = etFindwordNewPassword.getText().toString();
        Request<String> request = NoHttpRequest.changePasswordRequst(phone, password);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                mCountDownTimer.start();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "FindPasswordActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    if ("5001".equals(code)) {
                        ToastUtil.showShort("修改成功");
                        finish();
                    } else {
                        ToastUtil.showShort("修改失败，请重试！");
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


    private void handleIdentifyNumberEvent() {
        String phoneNumber = etFindwordPhone.getText().toString();
        //验证手机号码的合法性
        boolean isRight = StringUtil.isMobile(phoneNumber);
        if (!isRight) {
            ToastUtil.showShort("请输入正确的手机号码！");
            return;
        }

        mRegisterPhone = phoneNumber;

        Request<String> request = NoHttpRequest.getIdentifyCodeRequest(phoneNumber, "2");
        mRequestQueue.add(0, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                mCountDownTimer.start();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "FindPasswordActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        JSONObject dataObject = jsonObject.getJSONObject("data");
                        //验证码
                        mRandCode = dataObject.get("rand").toString();
                    }
                    ToastUtil.showShort(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

    private boolean isRightMessage() {
        if (!mRandCode.equals(etFindwordCode.getText().toString())) {
            ToastUtil.showShort("验证码错误！");
            return false;
        }
        if (!mRegisterPhone.equals(etFindwordPhone.getText().toString())) {
            ToastUtil.showShort("手机号码错误！");
            return false;
        }
        String newPassword = etFindwordNewPassword.getText().toString();
        if ("".equals(newPassword) || newPassword.length() < 6 ||newPassword.length() > 16 ) {
            ToastUtil.showShort("请输入6~16位数新密码！");
            return false;
        }
        String againPassword = etFindwordAgainPassword.getText().toString();
        if ("".equals(againPassword)) {
            ToastUtil.showShort("请再次输入新密码");
            return false;
        }
        if (!againPassword.equals(newPassword)) {
            if ("".equals(againPassword)) {
                ToastUtil.showShort("两次输入密码不一致！");
                return false;
            }
        }
        return true;
    }

}
