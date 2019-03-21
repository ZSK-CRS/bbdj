package com.mt.bbdj.baseconfig.utls;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

/**
 * Author : ZSK
 * Date : 2018/12/28
 * Description : 系统硬件相关的工具类
 */
public class SystemUtil {
    /**
     * 判断sdcard是否被挂载
     */
    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    //获取当前app的版本
    public static String getVersion(Context context) {
        String versionName = "";
        try{
            // 获取packagemanager的实例
            PackageManager packageManager = context.getPackageManager();
            // getPackageName()是你当前类的包名，0代表是获取版本信息
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            versionName = packInfo.versionName;
        }catch (Exception e){

        }
        return versionName;
    }
}
