package com.mt.bbdj.baseconfig.utls;

import android.content.Context;

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
}
