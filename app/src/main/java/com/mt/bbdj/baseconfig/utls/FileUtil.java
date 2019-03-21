package com.mt.bbdj.baseconfig.utls;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Author : ZSK
 * Date : 2019/1/7
 * Description :
 */
public class FileUtil {
    public static File getSaveFile(Context context) {
        File file = new File(context.getFilesDir(), "pic.jpg");
        return file;
    }

    /**
     * 得到SD卡根目录.
     */
    public static File getRootPath(Context context) {
        if (FileUtil.sdCardIsAvailable()) {
            return Environment.getExternalStorageDirectory(); // 取得sdcard文件路径
        } else {
            return context.getFilesDir();
        }
    }

    /**
     * SD卡是否可用.
     */
    public static boolean sdCardIsAvailable() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File sd = new File(Environment.getExternalStorageDirectory().getPath());
            return sd.canWrite();
        } else
            return false;
    }
}
