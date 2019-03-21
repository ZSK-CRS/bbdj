package com.mt.bbdj.community.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.Constant;
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

public class RechargeActivity extends BaseActivity {

    private RelativeLayout ivBack;
    private Button rechargeBt;   //充值
    private EditText etMoney;
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
    }

    private void payfor() {
        String money = etMoney.getText().toString();
        if ("".equals(money) || "0".equals(money)) {
            ToastUtil.showShort("金额不可为空！");
            return;
        }
        requestPayfor(money);
    }

    private void requestPayfor(String money) {
        Request<String> request = NoHttpRequest.getWeiChartPayforRequest(user_id,money);
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
                        String sign =  createSign( params);
                        request.sign= sign;
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

    public static String createSign(SortedMap<String,Object> parameters){
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
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
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
    }
}
