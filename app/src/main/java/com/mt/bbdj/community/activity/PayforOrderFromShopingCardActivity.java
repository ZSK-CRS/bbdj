package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.GoodsMessage;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.GoodsOrderAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PayforOrderFromShopingCardActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_address)
    AppCompatTextView tvAddress;
    @BindView(R.id.rl_goods_order)
    RecyclerView rlGoodsOrder;
    @BindView(R.id.tv_all_money)
    TextView tvAllMoney;
    @BindView(R.id.tv_payfor)
    Button btPayFor;

    private String orders_id;
    private RequestQueue mRequestQueue;
    private UserBaseMessageDao userBaseMessageDao;
    private UserBaseMessage userBaseMessage;
    private String user_id;
    private WaitDialog dialogLoading;
    private String express_id;
    private String express_name;
    private int type = 1;
    private GoodsOrderAdapter mAdapter;
    private List<GoodsMessage.Goods> mapList = new ArrayList<>();

    private int payforType = 0;     //表示的是立即结算（单个清算）
    private String payforCart_id;
    private String myaddress_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payfor_shopping_card_detail);
        ButterKnife.bind(this);

        initParams();
        initRecycler();
        initListener();
    }

    public static void  getInstance(Context context, int payforType) {
        Intent intent = new Intent(context,PayforOrderFromShopingCardActivity.class);
        intent.putExtra("payforType",payforType);
        context.startActivity(intent);
    }

    private void initListener() {
        btPayFor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitOrder();
            }
        });
    }

    private void commitOrder() {
        Request<String> request = NoHttpRequest.payForMoreGoodsRequest(user_id,payforCart_id,myaddress_id,"");
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                dialogLoading = WaitDialog.show(PayforOrderFromShopingCardActivity.this,"提交中...").setCanCancel(true);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "PayforOrderFromShopingCardActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");
                    if ("5001".equals(code)) {
                        EventBus.getDefault().post(new TargetEvent(TargetEvent.DESTORY_GOODS_FROM_CART));
                        finish();
                    }
                    ToastUtil.showShort(msg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialogLoading.doDismiss();
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

    private void initRecycler() {
        rlGoodsOrder.setFocusable(false);
        //initTemparayData();   //模拟数据
        mAdapter = new GoodsOrderAdapter(this, mapList);
        rlGoodsOrder.setNestedScrollingEnabled(false);
        rlGoodsOrder.setLayoutManager(new LinearLayoutManager(this));
        rlGoodsOrder.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL, Color.parseColor("#e9e9e9"), 1));
        rlGoodsOrder.setAdapter(mAdapter);
    }

    private void initParams() {
        Intent intent = getIntent();
        payforType = intent.getIntExtra("payforType",0);
        payforCart_id = intent.getStringExtra("cart_id");
        GoodsMessage goodsMessage = (GoodsMessage) intent.getSerializableExtra("goods");
        mapList = goodsMessage.getGoodsList();
        mRequestQueue = NoHttp.newRequestQueue();
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        userBaseMessageDao = daoSession.getUserBaseMessageDao();
        List<UserBaseMessage> userBaseMessages = userBaseMessageDao.queryBuilder().list();
        if (userBaseMessages.size() != 0) {
            userBaseMessage = userBaseMessages.get(0);
            user_id = userBaseMessage.getUser_id();
        }
        initAddress();    //设置地址
    }

    private void initAddress() {
        SharedPreferences preferences = SharedPreferencesUtil.getSharedPreference();
        String myaddress_name = preferences.getString("myaddress_name","");
        String myaddress_phone = preferences.getString("myaddress_phone","");
        String myaddress_address = preferences.getString("myaddress_address","");
        myaddress_id = preferences.getString("myaddress_id","");
        tvName.setText(myaddress_name);
        tvAddress.setText(myaddress_address);
    }

    @OnClick({R.id.iv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
        }
    }

}
