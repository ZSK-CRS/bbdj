package com.mt.bbdj.baseconfig.utls;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Author : ZSK
 * Date : 2018/12/25
 * Description : 轻量级数据存储
 */
public class SharedPreferencesUtil {

    private static final String FILE_NAME = "bbdj";

    private static Context mContext;

    private static SharedPreferences mSharedPreferences;

    private static SharedPreferences.Editor mEditor;

    /**
     * 在Application中初始化
     * @param applicationContext
     */
    public static void init(Context applicationContext) {
        mContext = applicationContext;
        mSharedPreferences = mContext.getSharedPreferences(FILE_NAME, Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
    }

    public static SharedPreferences getSharedPreference() {
        return mSharedPreferences;
    }

    public static SharedPreferences.Editor getEditor() {
        return mEditor;
    }

}
