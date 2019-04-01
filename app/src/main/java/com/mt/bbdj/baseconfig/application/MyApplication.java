package com.mt.bbdj.baseconfig.application;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.multidex.MultiDex;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.kongzue.dialog.v2.DialogSettings;
import com.mt.bbdj.BuildConfig;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.Constant;
import com.mt.bbdj.baseconfig.utls.CrashHandler;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.SoundHelper;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.yanzhenjie.nohttp.InitializationConfig;
import com.yanzhenjie.nohttp.Logger;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.URLConnectionNetworkExecutor;
import com.yanzhenjie.nohttp.cache.DBCacheStore;
import com.yanzhenjie.nohttp.cookie.DBCookieStore;

import java.util.UUID;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

import static com.kongzue.dialog.v2.DialogSettings.STYLE_IOS;
import static com.kongzue.dialog.v2.DialogSettings.STYLE_MATERIAL;

/**
 * Author : ZSK
 * Date : 2018/12/25
 * Description :
 */
public class MyApplication extends Application {
    private static MyApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        //初始化数据存储
        initDatabase();

        initNoHttp();


        initOcr();
        //设置提示框的样式
        DialogSettings.style = STYLE_IOS;

        initPushSetting();    //初始化推送

        //bug收集
       // CrashHandler.getInstance().init(this);

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private void initPushSetting() {

        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush

        //设置推送样式
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(this);
        builder.statusBarDrawable = R.drawable.ic_logo_;
        builder.notificationFlags = Notification.FLAG_AUTO_CANCEL//
                | Notification.FLAG_SHOW_LIGHTS; // 设置为自动消失和呼吸灯闪烁
        builder.notificationDefaults = //
                //	Notification.DEFAULT_SOUND | // 设置为铃声
                Notification.DEFAULT_VIBRATE | // 设置为、震动
                        Notification.DEFAULT_LIGHTS; // 设置为呼吸灯闪烁
        JPushInterface.setPushNotificationBuilder(1, builder);

        //初始化语音提示播放
        SoundHelper.init();
    }

    private void initOcr() {
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken result) {
                // 调用成功，返回AccessToken对象
                String token = result.getAccessToken();
            }

            @Override
            public void onError(OCRError error) {
                // 调用失败，返回OCRError子类SDKError对象
            }
        }, getApplicationContext());
    }

    private void initDatabase() {
        SharedPreferencesUtil.init(this);
        GreenDaoManager.getInstance().init(this);
    }

    public static MyApplication getInstance() {
        return mInstance;
    }

    private void initNoHttp() {
        Logger.setDebug(true);
        Logger.setTag("photoFile");
        // 如果你需要自定义配置：
        InitializationConfig config = InitializationConfig.newBuilder(this)
                // 全局连接服务器超时时间，单位毫秒，默认10s。
                .connectionTimeout(30 * 1000)
                // 全局等待服务器响应超时时间，单位毫秒，默认10s。
                .readTimeout(30 * 1000)
                // 配置缓存，默认保存数据库DBCacheStore，保存到SD卡使用DiskCacheStore。
                .cacheStore(
                        // 如果不使用缓存，setEnable(false)禁用。
                        new DBCacheStore(this).setEnable(true)
                )
                // 配置Cookie，默认保存数据库DBCookieStore，开发者可以自己实现CookieStore接口。
                .cookieStore(
                        // 如果不维护cookie，setEnable(false)禁用。
                        new DBCookieStore(this).setEnable(true)
                )
                .networkExecutor(new URLConnectionNetworkExecutor())
                .build();
        NoHttp.initialize(config);
    }
}
