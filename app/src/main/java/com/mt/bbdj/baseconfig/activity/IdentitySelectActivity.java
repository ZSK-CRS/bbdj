package com.mt.bbdj.baseconfig.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.community.activity.CommunityActivity;
import com.mt.bbdj.corporation.activity.CorporationActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class IdentitySelectActivity extends BaseActivity {

    @BindView(R.id.iv_main_corporation)
    ImageView ivMainCorporation;         //企业版
    @BindView(R.id.iv_main_community)
    ImageView ivMainCommunity;           //社区版

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identity_select);
        ButterKnife.bind(this);
    }

    @OnClick({R.id.iv_main_corporation, R.id.iv_main_community})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_main_corporation:
                startIntent(1);
                break;
            case R.id.iv_main_community:
                startIntent(0);
                break;
        }
    }

    /**
     *  选择不同的类型
     * @param selectType  1：企业版  0 ：社区版
     */
    private void startIntent(int selectType) {
        Intent intent = new Intent();
        if (1 == selectType) {
            intent.setClass(this,CorporationActivity.class);
        } else {
            intent.setClass(this,CommunityActivity.class);
        }
        SharedPreferencesUtil.getEditor().putInt("selectType",selectType);
        SharedPreferencesUtil.getEditor().commit();
        startActivity(intent);
        finish();
    }
}
