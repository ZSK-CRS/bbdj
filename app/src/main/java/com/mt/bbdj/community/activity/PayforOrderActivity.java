package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kongzue.dialog.v2.SelectDialog;
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.CategoryBean;
import com.mt.bbdj.baseconfig.model.GoodsMessage;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.AddView;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.GoodsOrderAdapter;
import com.mt.bbdj.community.adapter.HaveFinishAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PayforOrderActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_address)
    AppCompatTextView tvAddress;
    @BindView(R.id.tv_all_money)
    TextView tvAllMoney;
    @BindView(R.id.tv_payfor)
    Button btPayFor;
    @BindView(R.id.addview)
    AddView addView;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;    //商品图标
    @BindView(R.id.tv_goods_name)
    TextView tvGoodName;   //商品名称
    @BindView(R.id.tv_goods_type)
    TextView tvGoodsType;    //商品类型
    @BindView(R.id.tv_goods_money)
    TextView tvGoodsMoney;     //价格
    private final int SELECT_ADDRESS = 1;    //选择地址

    private String orders_id;
    private RequestQueue mRequestQueue;
    private UserBaseMessageDao userBaseMessageDao;
    private UserBaseMessage userBaseMessage;
    private String user_id;
    private WaitDialog dialogLoading;
    private String express_id;
    private String express_name;
    private int type = 1;
    private int goodsNumber = 1;
    private GoodsOrderAdapter mAdapter;
    private List<GoodsMessage.Goods> mapList = new ArrayList<>();

    private int payforType = 0;     //表示的是立即结算（单个清算）
    private GoodsMessage.Goods payForGoods;
    private String myaddress_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payfor_order_detail);
        ButterKnife.bind(this);
        initParams();
        initListener();

    }


    public static void getInstance(Context context, int payforType) {
        Intent intent = new Intent(context, PayforOrderActivity.class);
        intent.putExtra("payforType", payforType);
        context.startActivity(intent);
    }

    private void initListener() {
        btPayFor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commitOrder();
            }
        });
        //数量变化
        addView.setOnValueChangeListene(new AddView.OnValueChangeListener() {
            @Override
            public void onValueChange(int value) {
                goodsNumber = value;

                float singlePrice = Float.parseFloat(payForGoods.getGoodsPrice());
                float allprice = value * singlePrice;
                tvAllMoney.setText(allprice+"");
            }
        });
    }


    private void commitOrder() {
        Request<String> request = NoHttpRequest.payForMoneyRightNowRequest(user_id, payForGoods.getGoodsID()
                , payForGoods.getGenre_id(), myaddress_id, goodsNumber);

        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                dialogLoading = WaitDialog.show(PayforOrderActivity.this, "提交中...").setCanCancel(true);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "PayforOrderActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");
                    if ("5001".equals(code)) {
                        EventBus.getDefault().post(new TargetEvent(TargetEvent.DESTORY_GOODS_DETAIL));
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

    private void initParams() {
        Intent intent = getIntent();
        payforType = intent.getIntExtra("payforType", 0);
        GoodsMessage goodsMessage = (GoodsMessage) intent.getSerializableExtra("goods");
        mapList = goodsMessage.getGoodsList();
        payForGoods = mapList.get(0);

        setGoodsMessage();

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

    private void setGoodsMessage() {
        Glide.with(this).load(payForGoods.getGoodsPicture()).error(R.drawable.ic_no_picture).into(ivLogo);
        tvGoodName.setText(payForGoods.getGoodsName());
        tvGoodsType.setText(payForGoods.getGoodsTypeName());
        tvGoodsMoney.setText("￥" + payForGoods.getGoodsPrice());
        String goodsNumber = payForGoods.getGoodsNumber();
        int number = 1;
        if (null != goodsNumber && !"".equals(goodsNumber) && !"null".equals(goodsNumber)) {
            number = Integer.parseInt(payForGoods.getGoodsNumber());
        }
        float singlePrice = Float.parseFloat(payForGoods.getGoodsPrice());
        float allprice = number * singlePrice;
        tvAllMoney.setText(allprice+"");
    }

    private void initAddress() {
        SharedPreferences preferences = SharedPreferencesUtil.getSharedPreference();
        String myaddress_name = preferences.getString("myaddress_name", "");
        String myaddress_phone = preferences.getString("myaddress_phone", "");
        String myaddress_address = preferences.getString("myaddress_address", "");
        myaddress_id = preferences.getString("myaddress_id", "");
        tvName.setText(myaddress_name);
        tvAddress.setText(myaddress_address);
    }

    @OnClick({R.id.iv_back,R.id.ll_select_receive_address})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_select_receive_address:   //选择地址
                Intent intent = new Intent(PayforOrderActivity.this,MyAddressActivity.class);
                intent.putExtra("type",true);
                startActivityForResult(intent,SELECT_ADDRESS);
                break;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return ;
        }
        if (requestCode == SELECT_ADDRESS) {
            initAddress();
        }
    }


}
