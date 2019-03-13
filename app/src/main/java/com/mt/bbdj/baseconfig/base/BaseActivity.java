package com.mt.bbdj.baseconfig.base;

import android.app.ActivityManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.utls.LogUtil;

public class BaseActivity extends AppCompatActivity {

    private DisplayMetrics mMetrics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity
                .getClassName();
        LogUtil.i("当前界面",runningActivity);
    }

}
