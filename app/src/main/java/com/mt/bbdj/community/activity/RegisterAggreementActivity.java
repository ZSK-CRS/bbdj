package com.mt.bbdj.community.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;

public class RegisterAggreementActivity extends BaseActivity {

    private RelativeLayout icBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_aggreement);

        initView();
        initListener();
    }

    private void initListener() {
        icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
        icBack = findViewById(R.id.iv_back);
    }
}
