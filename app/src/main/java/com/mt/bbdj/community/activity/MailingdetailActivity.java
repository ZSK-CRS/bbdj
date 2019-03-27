package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MailingdetailActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;     //返回
    @BindView(R.id.tv_order_number)
    TextView tvOrderNumber;   //订单号
    @BindView(R.id.tv_order_express)
    TextView tvOrderExpress;    //快递公司
    @BindView(R.id.tv_order_time)
    TextView tvOrderTime;    //下单时间
    @BindView(R.id.tv_order_money)
    TextView tvOrderMoney;     //官网报价
    @BindView(R.id.tv_send_name)
    TextView tvSendName;     //寄件人
    @BindView(R.id.tv_send_phone)
    TextView tvSendPhone;    //寄件电话
    @BindView(R.id.tv_send_address)
    TextView tvSendAddress;    //寄件地址
    @BindView(R.id.tv_receive_name)
    TextView tvReceiveName;    //收件人
    @BindView(R.id.tv_receive_phone)
    TextView tvReceivePhone;    //收件电话
    @BindView(R.id.tv_receive_address)
    TextView tvReceiveAddress;    //收件地址
    @BindView(R.id.tv_goods_name)
    TextView tvGoodsName;    //物品名称
    @BindView(R.id.tv_goods_weiht)
    TextView tvGoodsWeiht;     //物品重量
    @BindView(R.id.tv_goods_mark)
    TextView tvGoodsMark;     //备注
    @BindView(R.id.bt_first_save)
    TextView btPrintAgain;      //原单重打
    @BindView(R.id.bt_cannel_order)
    TextView btCannel;    //取消订单


    private String user_id;    //用户id
    private String mail_id;    //订单id

    private RequestQueue mRequestQueue;
    private HkDialogLoading dialogLoading;
    private int mType;    //0 ： 表示待收件进入  1： 已处理进入

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mailingdetail);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initParams();
        initData();
        initView();
        requestOrederDetail();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(TargetEvent targetEvent) {
        if (1 == targetEvent.getTarget()) {
            finish();
        }
    }

    private void initView() {
        if (mType == 1) {
            btPrintAgain.setVisibility(View.VISIBLE);
        } else {
            btPrintAgain.setVisibility(View.GONE);
        }
    }

    private void initData() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        dialogLoading = new HkDialogLoading(this, "请稍后...");
    }

    private void initParams() {
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        mail_id = intent.getStringExtra("mail_id");
        mType = intent.getIntExtra("type", 0);
    }

    private void requestOrederDetail() {
        Request<String> request = NoHttpRequest.getOrderDetailRequest(user_id, mail_id);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                // dialogLoading.show();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "MaiLingDetailActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    LogUtil.i("photoFile", "MaiLingDetailActivity::" + jsonObject.toString());
                    String code = jsonObject.get("code").toString();
                    JSONObject data = jsonObject.getJSONObject("data");
                    if ("5001".equals(code)) {
                        savePannelMessage(data);
                    } else {
                        ToastUtil.showShort("查询失败，请重试！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // dialogLoading.cancel();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                //dialogLoading.cancel();
            }

            @Override
            public void onFinish(int what) {
                //  dialogLoading.cancel();
            }
        });
    }

    @OnClick({R.id.iv_back,R.id.bt_first_save,R.id.bt_cannel_order})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_first_save:
                Intent intent = new Intent(MailingdetailActivity.this,BluetoothSearchActivity.class);
                intent.putExtra("printType","3");
                startActivity(intent);
                finish();
                break;
            case R.id.bt_cannel_order:
                selectCannelReason();
                break;
        }
    }

    private void selectCannelReason() {
        Intent intent1 = new Intent(MailingdetailActivity.this,CauseForcannelOrderActivity.class);
        intent1.putExtra("user_id",user_id);
        intent1.putExtra("mail_id",mail_id);
        intent1.putExtra("type","MailingdetailActivity");
        startActivity(intent1);
    }

    private void savePannelMessage(JSONObject jsonObject) throws JSONException {
        String express_name = jsonObject.getString("express_name");
        String time = jsonObject.getString("time");
        String dingdanhao = jsonObject.getString("dingdanhao");
        String send_name = jsonObject.getString("send_name");
        String send_phone = jsonObject.getString("send_phone");
        String send_region = jsonObject.getString("send_region");
        String send_address = jsonObject.getString("send_address");
        String collect_name = jsonObject.getString("collect_name");
        String collect_phone = jsonObject.getString("collect_phone");
        String collect_region = jsonObject.getString("collect_region");
        String collect_address = jsonObject.getString("collect_address");
        String goods_name = jsonObject.getString("goods_name");
        String weight = jsonObject.getString("weight");
        String content = jsonObject.getString("content");
        tvOrderExpress.setText(express_name);   //快递公司
        tvOrderTime.setText(DateUtil.changeStampToStandrdTime("yyyy-MMM-dd HH:mm", time));  //下单时间
        tvSendName.setText(send_name);
        tvOrderNumber.setText(dingdanhao);
        tvSendPhone.setText(send_phone);
        tvSendAddress.setText(send_region + send_address);
        tvReceiveName.setText(collect_name);
        tvReceivePhone.setText(collect_phone);
        tvReceiveAddress.setText(collect_region + collect_address);
        tvGoodsName.setText(goods_name);
        tvGoodsWeiht.setText(weight);
        tvGoodsMark.setText(content);

      //  JSONObject dataObj = jsonObject.getJSONObject("data");
        String mail_id = jsonObject.getString("mail_id");       //订单id
        String express_id = jsonObject.getString("express_id");     //快递公司id
        String number = jsonObject.getString("number");      //驿站代码
        String yundanhao = jsonObject.getString("yundanhao");    //运单号
        String code = jsonObject.getString("code");   //标识码
        String place = jsonObject.getString("place");      //中转地
        String transit = jsonObject.getString("transit");     //中转地标识码和时间

        SharedPreferences.Editor editor = SharedPreferencesUtil.getEditor();
        editor.putString("mail_id", StringUtil.handleNullResultForString(mail_id));
        editor.putString("express_id", StringUtil.handleNullResultForString(express_id));
        editor.putString("express_name", StringUtil.handleNullResultForString(express_name));
        editor.putString("number", StringUtil.handleNullResultForString(number));
        editor.putString("yundanhao", StringUtil.handleNullResultForString(yundanhao));
        editor.putString("code", StringUtil.handleNullResultForString(code));
        editor.putString("place", StringUtil.handleNullResultForString(place));
        editor.putString("transit", StringUtil.handleNullResultForString(transit));
        editor.putString("send_name", StringUtil.handleNullResultForString(send_name));
        editor.putString("send_phone", StringUtil.handleNullResultForString(send_phone));
        editor.putString("send_region", StringUtil.handleNullResultForString(send_region));
        editor.putString("send_address", StringUtil.handleNullResultForString(send_address));
        editor.putString("collect_name", StringUtil.handleNullResultForString(collect_name));
        editor.putString("collect_phone", StringUtil.handleNullResultForString(collect_phone));
        editor.putString("collect_region", StringUtil.handleNullResultForString(collect_region));
        editor.putString("collect_address", StringUtil.handleNullResultForString(collect_address));
        editor.putString("goods_name", StringUtil.handleNullResultForString(goods_name));
        editor.putString("weight", StringUtil.handleNullResultForString(weight));
        editor.putString("content", StringUtil.handleNullResultForString(content));
        editor.apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
    }
}
