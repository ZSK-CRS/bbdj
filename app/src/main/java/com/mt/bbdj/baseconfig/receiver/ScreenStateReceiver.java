package com.mt.bbdj.baseconfig.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ScreenStateManager;

/**
 * Author : ZSK
 * Date : 2019/1/28
 * Description :
 */
public class ScreenStateReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtil.d("ndh--", "action=" + action);
        if (Intent.ACTION_SCREEN_ON.equals(action)) { // 开屏
            ScreenStateManager.screenState=1;
            LogUtil.d("ndh--", "screen on");
        } else if (Intent.ACTION_SCREEN_OFF.equals(action)) { // 锁屏
            ScreenStateManager.screenState=-1;
            LogUtil.d("ndh--", "screen off");
          /*  Intent intent1 = new Intent(context, ScreenLockActivity.class);
            intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);*/
        } else if (Intent.ACTION_USER_PRESENT.equals(action)) { // 解锁
            ScreenStateManager.screenState=0;
            LogUtil.d("ndh--", "user_present");
        }
    }

}
