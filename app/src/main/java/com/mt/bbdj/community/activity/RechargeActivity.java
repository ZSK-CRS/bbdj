package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.alipay.sdk.app.PayTask;
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.Constant;
import com.mt.bbdj.baseconfig.model.PayResult;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.MD5Util;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class RechargeActivity extends BaseActivity {

    private RelativeLayout ivBack;
    private Button rechargeBt;   //充值
    private EditText etMoney;
    private LinearLayout weChatLayout, aliLayout;     //微信支付、支付宝支付
    private RequestQueue mRequestQueue;
    private UserBaseMessageDao userBaseMessageDao;
    private UserBaseMessage userBaseMessage;
    private String user_id;

    private final int REQUEST_WECHAT_PAY = 100;   //微信支付请求
    private WaitDialog dialogLoading;
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(RechargeActivity.this);
        EventBus.getDefault().register(this);
        initParams();
        initView();
        initListener();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receivMessage(TargetEvent targetEvent) {
        if (targetEvent.getTarget() == TargetEvent.DESTORY_RECHAR) {
            finish();
        }
    }

    private void initParams() {
        mRequestQueue = NoHttp.newRequestQueue();
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        userBaseMessageDao = daoSession.getUserBaseMessageDao();
        List<UserBaseMessage> userBaseMessages = userBaseMessageDao.queryBuilder().list();
        if (userBaseMessages.size() != 0) {
            userBaseMessage = userBaseMessages.get(0);
            user_id = userBaseMessage.getUser_id();
        }
        api = WXAPIFactory.createWXAPI(RechargeActivity.this, null);    //注册到微信
        api.registerApp(Constant.appid);
    }

    private void initListener() {
        rechargeBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //提交充值
                payfor();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //微信支付
        weChatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //支付宝支付
        aliLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ToastUtil.showShort("暂不支持支付宝支付");

            }
        });
    }

    private static final int SDK_PAY_FLAG = 1;
    // String orderInfo = "app_id=2019031963597551&biz_content=%7B%22body%22%3A%22%5Cu4f59%5Cu989d%5Cu5145%5Cu503c%22%2C%22subject%22%3A%22%5Cu4f59%5Cu989d%5Cu5145%5Cu503c%22%2C%22out_trade_no%22%3A%2215536811439709%22%2C%22timeout_express%22%3A%221d%22%2C%22total_amount%22%3A0.01%2C%22seller_id%22%3A%222088431757095973%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%2C%22store_id%22%3A%22001%22%7D&charset=utf-8&method=alipay.trade.app.pay¬ify_url=http%3A%2F%2Fwww.81dja.com%2FPayment%2Fali_notify&sign_type=RSA2×tamp=2019-03-27+18%3A05%3A43&version=1.0";
    String orderInfo = "alipay_sdk=alipay-sdk-php-20180705&app_id=2019031963597551&biz_content=%7B%22body%22%3A%22%22%2C%22subject%22%3A%22%22%2C%22out_trade_no%22%3A%22%22%2C%22timeout_express%22%3A%22m%22%2C%22total_amount%22%3A%22%22%2C%22product_code%22%3A%22QUICK_MSECURITY_PAY%22%7D&charset=UTF-8&format=json&method=alipay.trade.app.pay&notify_url=http%3A%2F%2Fwww.81dja.com%2FPayment%2Fali_notify&sign_type=RSA2&timestamp=2019-03-27+19%3A09%3A26&version=1.0&sign=";

    private void payfor() {
        String money = etMoney.getText().toString();
        if ("".equals(money) || "0".equals(money)) {
            ToastUtil.showShort("金额不可为空！");
            return;
        }
     /*     Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(RechargeActivity.this);
                Map <String,String> result = alipay.payV2(orderInfo,true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();*/
        requestPayforByWechat(money);
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            PayResult payResult = new PayResult((Map<String, String>) msg.obj);
            /**
             * 对于支付结果，请商户依赖服务端的异步通知结果。同步通知结果，仅作为支付结束的通知。
             */
            String resultInfo = payResult.getResult();// 同步返回需要验证的信息
            String resultStatus = payResult.getResultStatus();
            // 判断resultStatus 为9000则代表支付成功
            if (TextUtils.equals(resultStatus, "9000")) {
                // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                showAlert(RechargeActivity.this, "支付成功" + payResult);
            } else {
                // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                showAlert(RechargeActivity.this, "支付失败" + payResult);
            }
        }

        ;
    };

    private void requestPayforByWechat(String money) {
        Request<String> request = NoHttpRequest.getWeiChartPayforRequest(user_id, money);
        //  Request<String> request = NoHttp.createStringRequest("https://wxpay.wxutil.com/pub_v2/app/app_pay.php", RequestMethod.GET);
        mRequestQueue.add(REQUEST_WECHAT_PAY, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                dialogLoading = WaitDialog.show(RechargeActivity.this, "请稍后...").setCanCancel(true);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "RechargeActivity::" + response.get());

                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        JSONObject data = jsonObject.getJSONObject("data");

                        PayReq request = new PayReq();
                        request.prepayId = data.getString("prepayid");
                        request.appId = data.getString("appid");
                        request.packageValue = data.getString("package");
                        request.nonceStr = data.getString("noncestr");
                        request.timeStamp = data.getString("timestamp");
                        //request.sign = data.getString("sign");
                        request.partnerId = data.getString("partnerid");
                        SortedMap<String, Object> params = new TreeMap<String, Object>();
                        params.put("appid", request.appId);
                        params.put("partnerid", request.partnerId);
                        params.put("prepayid", request.prepayId);
                        params.put("package", request.packageValue);
                        params.put("noncestr", request.nonceStr);
                        params.put("timestamp", request.timeStamp);
                        String sign = createSign(params);
                        request.sign = sign;
                        boolean isSucceff = api.sendReq(request);

                    }
                    dialogLoading.doDismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialogLoading.doDismiss();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                dialogLoading.doDismiss();
            }

            @Override
            public void onFinish(int what) {
                dialogLoading.doDismiss();
            }
        });
    }


    private void requestPayforByAlia(String money) {
        Request<String> request = NoHttpRequest.getWeiChartPayforRequest(user_id, money);
        //  Request<String> request = NoHttp.createStringRequest("https://wxpay.wxutil.com/pub_v2/app/app_pay.php", RequestMethod.GET);
        mRequestQueue.add(REQUEST_WECHAT_PAY, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                dialogLoading = WaitDialog.show(RechargeActivity.this, "请稍后...").setCanCancel(true);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "RechargeActivity::" + response.get());

                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        JSONObject data = jsonObject.getJSONObject("data");

                        PayReq request = new PayReq();
                        request.prepayId = data.getString("prepayid");
                        request.appId = data.getString("appid");
                        request.packageValue = data.getString("package");
                        request.nonceStr = data.getString("noncestr");
                        request.timeStamp = data.getString("timestamp");
                        //request.sign = data.getString("sign");
                        request.partnerId = data.getString("partnerid");
                        SortedMap<String, Object> params = new TreeMap<String, Object>();
                        params.put("appid", request.appId);
                        params.put("partnerid", request.partnerId);
                        params.put("prepayid", request.prepayId);
                        params.put("package", request.packageValue);
                        params.put("noncestr", request.nonceStr);
                        params.put("timestamp", request.timeStamp);
                        String sign = createSign(params);
                        request.sign = sign;
                        boolean isSucceff = api.sendReq(request);

                    }
                    dialogLoading.doDismiss();
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialogLoading.doDismiss();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                dialogLoading.doDismiss();
            }

            @Override
            public void onFinish(int what) {
                dialogLoading.doDismiss();
            }
        });
    }

    public static String createSign(SortedMap<String, Object> parameters) {
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while (it.hasNext()) {
            Map.Entry entry = (Map.Entry) it.next();
            String k = (String) entry.getKey();
            Object v = entry.getValue();
            if (null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + "bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");
        // String sign = MD5Utils.encode(sb.toString()).toUpperCase();
        String sign = MD5Util.toMD5(sb.toString()).toUpperCase();
        return sign;
    }

    private void initView() {
        ivBack = findViewById(R.id.iv_back);
        rechargeBt = findViewById(R.id.bt_recharge);
        etMoney = findViewById(R.id.et_money);
        weChatLayout = findViewById(R.id.ll_wechat);
        aliLayout = findViewById(R.id.ll_ali);
        etMoney.setCursorVisible(false);
        etMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etMoney.setCursorVisible(true);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
    }

    private static void showAlert(Context ctx, String info) {
        showAlert(ctx, info, null);
    }

    private static void showAlert(Context ctx, String info, DialogInterface.OnDismissListener onDismiss) {
        new AlertDialog.Builder(ctx)
                .setMessage(info)
                .setPositiveButton("确定", null)
                .setOnDismissListener(onDismiss)
                .show();
    }


}
