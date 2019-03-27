package com.mt.bbdj.baseconfig.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.community.activity.CommunityActivity;

import org.greenrobot.eventbus.EventBus;

//启动界面
public class LoadActivity extends AppCompatActivity {

    private int defauleSecond = 2;   //默认两秒
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            defauleSecond--;
            if (defauleSecond == 0) {
                //跳转应用
                startApplication();
            } else {
                handler.sendEmptyMessageDelayed(1, 1000);
            }
        }
    };

    private SharedPreferences mShare;
    private SharedPreferences.Editor mEditor;

    private void startApplication() {
        Intent intent = new Intent();
        boolean firstStart = mShare.getBoolean("firstStart",true);

        if (firstStart) {   //若是第一次启动进入滑动引导页
            intent.setClass(this,SlideActivity.class);
            mEditor.putBoolean("firstStart",false);
            mEditor.commit();
        } else {
            //不是第一次则判断是否已经登录了
            String userName = mShare.getString("userName","");
            String password = mShare.getString("password","");
            if (!"".equals(userName) && !"".equals(password)) {   //若是已经登陆过了，直接到程序主页
                intent.setClass(this,CommunityActivity.class);
            } else {    //未登录则跳转到登录界面
                intent.setClass(this,LoginActivity.class);
            }
        }
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mShare = SharedPreferencesUtil.getSharedPreference();
        mEditor = SharedPreferencesUtil.getEditor();
        //倒计时2s
        handler.sendEmptyMessageDelayed(1,1000);
    }


}
