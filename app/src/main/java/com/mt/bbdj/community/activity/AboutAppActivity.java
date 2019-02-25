package com.mt.bbdj.community.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;

public class AboutAppActivity extends BaseActivity {

    private RelativeLayout iv_Back;
    private AppCompatTextView appCompatTextView;

    private String message = "   “兵兵到家”是一家专注于解决“社区快递最后100米末端配送问题”的互联网公司，" +
            "主要向社区提供快递代收、代寄服务，并以快递服务为入口，为社区用户提供线上线下（O2O）综合性生活服务，" +
            "从而打造社区O2O一站式生活服务平台";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        initView();
    }

    private void initView() {
        iv_Back = findViewById(R.id.iv_back);
        iv_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        appCompatTextView = findViewById(R.id.tv_about_app);
        appCompatTextView.setText(message);
    }
}
