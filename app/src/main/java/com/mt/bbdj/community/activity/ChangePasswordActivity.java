package com.mt.bbdj.community.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
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

public class ChangePasswordActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.et_old_password)
    EditText etOldPassword;
    @BindView(R.id.et_new_password)
    EditText etNewPassword;
    @BindView(R.id.et_comfirm_password)
    EditText etComfirmPassword;
    @BindView(R.id.bt_complete)
    Button btComplete;
    private RequestQueue mRequestQueue;
    private UserBaseMessageDao userBaseMessageDao;
    private UserBaseMessage userBaseMessage;
    private String user_id;
    private WaitDialog dialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mRequestQueue = NoHttp.newRequestQueue();
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        userBaseMessageDao = daoSession.getUserBaseMessageDao();
        List<UserBaseMessage> userBaseMessages = userBaseMessageDao.queryBuilder().list();
        if (userBaseMessages.size() != 0) {
            userBaseMessage = userBaseMessages.get(0);
            user_id = userBaseMessage.getUser_id();
        }
    }


    @OnClick({R.id.iv_back, R.id.bt_complete})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_complete:
                commitData();
                break;
        }
    }

    private void commitData() {
        //验证信息是否正确
        if (isRightMessage()) {
            changePassword();
        }
    }

    private void changePassword() {
        String olePaaword = etOldPassword.getText().toString();
        String newPassword = etNewPassword.getText().toString();
        Request<String> request = NoHttpRequest.changeNewPasswordRequst(user_id,olePaaword, newPassword);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                dialogLoading = WaitDialog.show(ChangePasswordActivity.this,"请稍后...").setCanCancel(true);
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

    private boolean isRightMessage() {
        String oldPassword = etOldPassword.getText().toString();
        if ("".equals(oldPassword) || oldPassword.length() < 6 ||oldPassword.length() > 16 ) {
            ToastUtil.showShort("请输入6~16位数旧密码！");
            return false;
        }

        String newPassword = etNewPassword.getText().toString();
        if ("".equals(newPassword) || newPassword.length() < 6 ||newPassword.length() > 16 ) {
            ToastUtil.showShort("请输入6~16位数新密码！");
            return false;
        }

        String againPassword = etComfirmPassword.getText().toString();
        if (!againPassword.equals(newPassword)) {
            if ("".equals(againPassword)) {
                ToastUtil.showShort("两次输入密码不一致！");
                return false;
            }
        }
        return true;
    }
}
