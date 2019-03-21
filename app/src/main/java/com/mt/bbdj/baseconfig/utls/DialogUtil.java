package com.mt.bbdj.baseconfig.utls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Author : ZSK
 * Date : 2019/3/21
 * Description :
 */
public class DialogUtil {
    //提示框
    public static void promptDialog(final Context context
            , String msg ,final Class<?> contextTo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                context).setMessage("提示").setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(context,contextTo);
                        context.startActivity(intent);
                        System.exit(0);
                    }
                });
        builder.create().show();
    }

    //
    public static void promptDialog(final Context context, String msg ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                context).setMessage("提示").setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.create().show();
    }
    public static void prompt(final Context context, String msg ) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                context).setMessage("提示").setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
    //
    public static void promptDialog(final Context context, String tishi, String msg, DialogInterface.OnClickListener Determine, DialogInterface.OnClickListener cancel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(tishi).setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton("确定", Determine);
      //     builder.setNegativeButton("取消", cancel);
        builder.create().show();
    }
    public static void promptDialog1(final Context context, String tishi, String msg, DialogInterface.OnClickListener Determine, DialogInterface.OnClickListener cancel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(tishi).setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton("现在升级", Determine);
     //   builder.setNegativeButton("稍后再升级", cancel);
        builder.create().show();
    }
    public static void promptDialog(final Context context,String msg,DialogInterface.OnClickListener Determine) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage("提示").setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton("确定", Determine);
        builder.create().show();
    }

    public static void promptDialog(final Context context, String msg, String btnStr1, String btnStr2, DialogInterface.OnClickListener Determine, DialogInterface.OnClickListener cancel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage("提示").setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton(btnStr1, Determine);
        builder.setNegativeButton(btnStr2, cancel);
        builder.create().show();
    }

    public static void promptDialog(final Context context, String msg, String btnStr1, String btnStr2, String btnStr3, DialogInterface.OnClickListener Determine, DialogInterface.OnClickListener exit, DialogInterface.OnClickListener cancel) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage("提示").setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton(btnStr1, Determine);
        builder.setNeutralButton(btnStr2, exit);
        builder.setNegativeButton(btnStr3, cancel);
        builder.create().show();
    }
    public static void promptDialog(final Context context, String msg, String btnStr1, String btnStr2, DialogInterface.OnClickListener Determine, DialogInterface.OnClickListener cancel, int location) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setMessage("提示").setCancelable(false);
        builder.setMessage(msg);
        builder.setPositiveButton(btnStr1, Determine);
        builder.setNegativeButton(btnStr2, cancel);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setGravity(location);
        dialog.show();
    }
    public static boolean isNetworkAvailable(Context activity)
    {
        Context context = activity.getApplicationContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
//					System.out.println(i + "===状态===" + networkInfo[i].getState());
//					System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
