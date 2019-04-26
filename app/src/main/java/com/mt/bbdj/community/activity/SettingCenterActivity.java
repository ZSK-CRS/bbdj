package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.utls.PackageUtils;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SettingCenterActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.ll_change_password)
    LinearLayout llChangePassword;
    @BindView(R.id.rb_school_around)
    ToggleButton rbSchoolAround;
    @BindView(R.id.bt_take_out)
    Button btTakeOut;
    @BindView(R.id.tv_app_version)
    TextView appVersion;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_center);
        ButterKnife.bind(this);
        initParams();
        initView();
        initListener();
    }

    private void initParams() {
        editor = SharedPreferencesUtil.getEditor();
    }

    private void initListener() {
        rbSchoolAround.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putBoolean("isPlaySound",isChecked);
                editor.commit();
            }
        });
    }

    private void initView() {
        String version = PackageUtils.getVersionName(this);
        appVersion.setText(version);
        SharedPreferences sharedPreferences = SharedPreferencesUtil.getSharedPreference();
        boolean isCheck = sharedPreferences.getBoolean("isPlaySound",true);
        rbSchoolAround.setChecked(isCheck);
    }


    @OnClick({R.id.iv_back, R.id.ll_change_password, R.id.bt_take_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_change_password:
                showChangePasswordPannel();     //修改密码
                break;
            case R.id.bt_take_out:
                break;
        }
    }

    private void showChangePasswordPannel() {
        Intent intent = new Intent(this,ChangePasswordActivity.class);
        startActivity(intent);
    }
}
