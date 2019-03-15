package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.DestroyEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
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

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//面单充值
public class PannelRechargeActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;        //返回
    @BindView(R.id.et_pannel_number)
    EditText etPannelNumber;    //面单数量
    @BindView(R.id.tv_pannel_money)
    TextView tvPannelMoney;     //合计金额
    @BindView(R.id.bt_recharge)
    Button btRecharge;         //充值

    private RequestQueue mRequestQueue;
    private HkDialogLoading dialogLoading;

    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;

    private float single_price = 0;

    private String face_id = "";    //面单id
    private int face_number = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pannel_recharge);
        ButterKnife.bind(this);

        initData();

        requestMoney();    //请求面单单价

        initListener();
    }

    private void initListener() {
        etPannelNumber.addTextChangedListener(textWatcher);
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String number = etPannelNumber.getText().toString();
            if ("".equals(number)) {
                return ;
            }
            int num = Integer.parseInt(number);
            if (num > 10000) {
                ToastUtil.showShort("超出范围！");
                return;
            }
            float allPrice = num * single_price;
            tvPannelMoney.setText(allPrice+"");
        }
    };

    private void requestMoney() {
        String user_id = "";
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        Request<String> request = NoHttpRequest.getPannelUnitePriceRequest(user_id);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
              //  dialogLoading.show();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "PannelRechargeActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    JSONObject data = jsonObject.getJSONObject("data");
                    if ("5001".equals(code)) {
                        String singlePrice = data.getString("single_price");
                        face_id = data.getString("single_id");
                        if (!"null".equals(single_price) && !"".equals(single_price)) {
                            single_price = Float.parseFloat(singlePrice);
                        }
                    } else {
                        ToastUtil.showShort(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialogLoading.cancel();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                dialogLoading.cancel();
            }

            @Override
            public void onFinish(int what) {
                dialogLoading.cancel();
            }
        });
    }

    private void handleRechargeEvent() {
        //判断充值的条件
        if (isRightForRecharge()) {
            pannelRechargeRequest();
        }
    }

    private void pannelRechargeRequest() {
        String user_id = "";
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        Request<String> request = NoHttpRequest.getPannelRechargeRequest(user_id,face_id,face_number+"");
        mRequestQueue.add(2, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                dialogLoading.show();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "PannelRechargeActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        Intent intent = new Intent(PannelRechargeActivity.this,RechargeFinishActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtil.showShort(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialogLoading.cancel();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                dialogLoading.cancel();
            }

            @Override
            public void onFinish(int what) {
                dialogLoading.cancel();
            }
        });
    }

    private boolean isRightForRecharge() {
        String price = tvPannelMoney.getText().toString();
        String numberStr = etPannelNumber.getText().toString();
        if ("0".equals(price) || "".equals(price)) {
            ToastUtil.showShort("单价获取失败，请重试！");
            return false;
        }
        if ("0".equals(numberStr) || "".equals(numberStr)) {
            ToastUtil.showShort("请输入购买数量！");
            return false;
        }
        face_number = Integer.parseInt(numberStr);
        if (face_number > 10000) {
            ToastUtil.showShort("购买数量超出范围！");
            return false;
        }
        return true;
    }




    private void initData() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        dialogLoading = new HkDialogLoading(PannelRechargeActivity.this, "加载中...");

        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void destoryView(DestroyEvent destroyEvent) {
        if (1 == destroyEvent.getType()) {
            finish();
        }
    }

    @OnClick({R.id.iv_back, R.id.bt_recharge})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_recharge:
                handleRechargeEvent();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
    }
}
