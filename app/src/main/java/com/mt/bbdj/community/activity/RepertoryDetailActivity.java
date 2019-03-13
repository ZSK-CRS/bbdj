package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;

public class RepertoryDetailActivity extends BaseActivity {

    private int mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repertory_detail);

        initParams();
    }

    private void initParams() {
        Intent intent = getIntent();
        mType = intent.getIntExtra("type",0);

    }
}
