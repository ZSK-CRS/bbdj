package com.mt.bbdj.baseconfig.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.application.MyApplication;
import com.mt.bbdj.baseconfig.db.City;
import com.mt.bbdj.baseconfig.db.County;
import com.mt.bbdj.baseconfig.db.MingleArea;
import com.mt.bbdj.baseconfig.db.Province;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.CityDao;
import com.mt.bbdj.baseconfig.db.gen.CountyDao;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.MingleAreaDao;
import com.mt.bbdj.baseconfig.db.gen.ProvinceDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.Constant;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.activity.CommunityActivity;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.bt_main_login)
    Button btMainLogin;
    @BindView(R.id.et_loging_usrname)
    EditText mUsername;
    @BindView(R.id.et_loging_password)
    EditText mPassword;
    private RequestQueue mRequestQueue;
    private HkDialogLoading dialogLoading;

    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;


    private final int REQUEST_LOGIN = 1;     //表示登录

    private ProvinceDao mProvinceDao;     //省
    private CityDao mCityDao;     //市
    private CountyDao mCountyDao;   //县
    private MingleAreaDao mMingleAreaDao;    //混合地区
    private String userName;
    private String password;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        applyPermission();    //申请权限
        setPushSetting();     //设置推送别名
        initData();
        mUsername.setCursorVisible(false);
        mPassword.setCursorVisible(false);
        mUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUsername.setCursorVisible(true);
            }
        });

        mPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPassword.setCursorVisible(true);
            }
        });
    }

    private void setPushSetting() {
        String aliasApply = StringUtil.getMixString(15);    //设置别名
        int i = IntegerUtil.getRandomInteger(1, 100);
        //设置别名
        JPushInterface.setAlias(this, i, aliasApply);
        Constant.alias = aliasApply;
    }

    private void initData() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        dialogLoading = new HkDialogLoading(LoginActivity.this, "登陆中...");

        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();
        mProvinceDao = mDaoSession.getProvinceDao();
        mCityDao = mDaoSession.getCityDao();
        mCountyDao = mDaoSession.getCountyDao();
        mMingleAreaDao = mDaoSession.getMingleAreaDao();
        editor = SharedPreferencesUtil.getEditor();
        editor.putBoolean("isPlaySound",true);
        editor.commit();
    }

    @OnClick({R.id.bt_main_login, R.id.tv_login_nocount, R.id.tv_login_forget})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_main_login:
                 handleLoginEvent();    //处理登录事件
                /*Intent intent = new Intent();
                intent.setClass(LoginActivity.this, CommunityActivity.class);
                startActivity(intent);
                finish();*/
                break;
            case R.id.tv_login_nocount:
                applyAccountEvent();    //申请账号
                break;
            case R.id.tv_login_forget:
                searchPassword();       //找回密码
                break;
        }

    }

    private void handleEvent(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case REQUEST_LOGIN:     //登录
                handleLogin(jsonObject);
                break;
        }
    }


    private void handleLogin(JSONObject jsonObject) throws JSONException {
        JSONObject dataObject = jsonObject.getJSONObject("data");
        //保存登录信息
        saveUserMessage(dataObject);

        editor.putString("userName",userName);
        editor.putString("password",password);
        editor.putBoolean("update",true);
        editor.commit();
        Intent intent = new Intent();
        intent.setClass(LoginActivity.this, CommunityActivity.class);
        startActivity(intent);
        finish();
    }

    private void searchPassword() {
        Intent intent = new Intent(this, FindPasswordActivity.class);
        startActivity(intent);
    }

    private void applyAccountEvent() {
        Intent intent = new Intent(this, RegisterAccountActivity.class);
        startActivity(intent);
    }

    private void handleLoginEvent() {
        userName = mUsername.getText().toString();
        password = mPassword.getText().toString();
        if ("".equals(userName)) {
            ToastUtil.showShort("账号不可为空！");
            return;
        }
        if ("".equals(password)) {
            ToastUtil.showShort("密码不可为空！");
            return;
        }
        SharedPreferencesUtil.getEditor().putString("alias",Constant.alias);
        Request<String> request = NoHttpRequest.loginRequest(userName, password,Constant.alias,Constant.device);
        mRequestQueue.add(REQUEST_LOGIN, request, mResponseListener);
    }

    private OnResponseListener<String> mResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            dialogLoading.show();
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "LoginActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    handleEvent(what, jsonObject);
                } else {
                    ToastUtil.showShort(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                dialogLoading.cancel();
                ToastUtil.showShort("连接失败！");
            }
            dialogLoading.cancel();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            dialogLoading.cancel();
            ToastUtil.showShort("连接服务器失败！");
        }

        @Override
        public void onFinish(int what) {
            dialogLoading.cancel();
        }
    };


    private void saveUserMessage(JSONObject dataObject) throws JSONException {
        String user_id = dataObject.getString("user_id");
        String user_type = dataObject.getString("user_type");
        String headimg = dataObject.getString("headimg");
        String mingcheng = dataObject.getString("mingcheng");
        String contacts = dataObject.getString("contacts");
        String contact_number = dataObject.getString("contact_number");
        String contact_email = dataObject.getString("contact_email");
        String contact_account = dataObject.getString("contact_account");
        mUserMessageDao.deleteAll();
        UserBaseMessage userBaseMessage = new UserBaseMessage();
        userBaseMessage.setUser_id(user_id);
        userBaseMessage.setUser_type(user_type);
        userBaseMessage.setHeadimg(headimg);
        userBaseMessage.setMingcheng(mingcheng);
        userBaseMessage.setContacts(contacts);
        userBaseMessage.setContact_number(contact_number);
        userBaseMessage.setContact_email(contact_email);
        userBaseMessage.setContact_account(contact_account);
        mUserMessageDao.save(userBaseMessage);
    }

    // 成功回调的方法，用注解即可，里面的数字是请求时的requestCode。
    @PermissionYes(100)
    private void getMultiYes(List<String> grantedPermissions) {
        // TODO 申请权限成功。

    }

    // 失败回调的方法，用注解即可，里面的数字是请求时的requestCode。
    @PermissionNo(100)
    private void getMultiNo(List<String> deniedPermissions) {
        // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            // 第一种：用默认的提示语。
            AndPermission.defaultSettingDialog(this, 100).show();
        }
    }

    private void applyPermission() {
        if (AndPermission.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE
                , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                , Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE
                , Manifest.permission.SEND_SMS)) {
        } else {
            // 申请权限。
            AndPermission.with(this)
                    .requestCode(100)
                    .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE
                            , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA
                            , Manifest.permission.READ_PHONE_STATE, Manifest.permission.CALL_PHONE
                            , Manifest.permission.SEND_SMS)
                    .callback(this)
                    .start();
        }
    }
}
