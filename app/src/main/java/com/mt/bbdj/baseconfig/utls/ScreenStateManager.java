package com.mt.bbdj.baseconfig.utls;

import android.content.Context;
import android.os.PowerManager;

/**
 * Author : ZSK
 * Date : 2019/1/28
 * Description : 手机屏幕的管理类
 */
public class ScreenStateManager {

    public static int screenState = 0;     // 0 : 用户解锁  1 : 亮屏  -1 ：锁屏

    //判断屏幕是否处于打开状态
    public boolean isScreen(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (pm.isScreenOn()) {
            return  true;
        }
        return false;
    }
}
