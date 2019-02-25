package com.mt.bbdj.baseconfig.utls;

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
}
