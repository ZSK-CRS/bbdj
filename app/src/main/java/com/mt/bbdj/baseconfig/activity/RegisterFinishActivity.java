package com.mt.bbdj.baseconfig.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.model.DestroyEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegisterFinishActivity extends BaseActivity {

    @BindView(R.id.bt_register_finish)
    Button mBtRegisterFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_finish);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.bt_register_finish)
    public void onViewClick(){

        finish();
    }
}
