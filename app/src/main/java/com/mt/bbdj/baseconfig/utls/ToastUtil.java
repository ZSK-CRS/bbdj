package com.mt.bbdj.baseconfig.utls;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.application.MyApplication;

import java.lang.ref.WeakReference;

/**
 * Author : ZSK
 * Date : 2018/12/27
 * Description :  封装的Toast
 */
public class ToastUtil {
    private static Toast toast;

    private static WeakReference<Application> mAppLication;

    public static void init(Application application) {
        mAppLication = new WeakReference<Application>(application);
    }

    private static Toast initToast(CharSequence message, int duration) {
        if (toast == null) {
            toast = Toast.makeText(MyApplication.getInstance(), message, duration);
        } else {
            toast.setText(message);
            toast.setDuration(duration);
        }
        return toast;
    }

    /**
     * 短时间显示Toast
     *
     * @param message
     */
    public static void showShort(CharSequence message) {

        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        toast = Toast.makeText(mAppLication.get(),message,Toast.LENGTH_SHORT);
        toast.show();
       // initToast(message, Toast.LENGTH_SHORT).show();
    }


    /**
     * 短时间显示Toast
     *
     * @param strResId
     */
    public static void showShort(int strResId) {
        initToast(MyApplication.getInstance().getResources().getText(strResId), Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param message
     */
    public static void showLong(CharSequence message) {
        initToast(message, Toast.LENGTH_LONG).show();
    }

    /**
     * 长时间显示Toast
     *
     * @param strResId
     */
    public static void showLong(int strResId) {
        initToast(MyApplication.getInstance().getResources().getText(strResId), Toast.LENGTH_LONG).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param message
     * @param duration
     */
    public static void show(CharSequence message, int duration) {
        initToast(message, duration).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * @param context
     * @param strResId
     * @param duration
     */
    public static void show(Context context, int strResId, int duration) {
        initToast(context.getResources().getText(strResId), duration).show();
    }


}
