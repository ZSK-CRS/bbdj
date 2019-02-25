package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
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
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.MessagePannelAdapter;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//短信充值面板
public class MessageRechargePannelActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.com_rl_recharge_pannel)
    XRecyclerView comRlRechargePannel;
    @BindView(R.id.bt_recharge)
    Button btRecharge;
    private MessagePannelAdapter mAdapter;
    private List<HashMap<String, String>> mList;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;

    private RequestQueue mRequestQueue;
    private HkDialogLoading dialogLoading;


    private final int REQUEST_PANNEL = 1;     //请求短信价格列表
    private final int RECHARGE_MESSAGE = 2;    //短信充值

    private String message_id = "0";     //表示的是短信商品的id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_recharge_pannel);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initView();
        requestData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void destoryView(DestroyEvent destroyEvent) {
        if (1 == destroyEvent.getType()) {
            finish();
        }
    }

    private void initData() {
        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        dialogLoading = new HkDialogLoading(MessageRechargePannelActivity.this, "请稍后...");
    }


    @OnClick({R.id.iv_back, R.id.bt_recharge})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_recharge:
                rechargeMessage();       //充值
                break;
        }
    }

    private void rechargeMessage() {
        String user_id = "";
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        Request<String> request = NoHttpRequest.getRechargeMoneyRequest(user_id,message_id);
        mRequestQueue.add(RECHARGE_MESSAGE, request,mResponseListener);
    }


    private void handleData(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case REQUEST_PANNEL:     //请求面板
                //设置数值
                setRequestData(jsonObject);
                break;
            case RECHARGE_MESSAGE:   //充值
                handleRechargeEvent();
                break;
        }
    }

    private void handleRechargeEvent() {
        Intent intent = new Intent(this,RechargeFinishActivity.class);
        startActivity(intent);
    }


    private void requestData() {
        String user_id = "";
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        Request<String> request = NoHttpRequest.getRechargePannelRequest(user_id);
        mRequestQueue.add(REQUEST_PANNEL, request,mResponseListener);
    }

    private void setRequestData(JSONObject jsonObject) throws JSONException {
        mList.clear();
        JSONArray dataArray = jsonObject.getJSONArray("data");
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject jsonObject1 = dataArray.getJSONObject(i);
            String messageid = jsonObject1.getString("message_id");
            String messageName = jsonObject1.getString("message_name");
            String messageNumber = jsonObject1.getString("message_number");
            String messageMoney = jsonObject1.getString("message_money");
            HashMap<String,String> map = new HashMap<>();
            map.put("messageid",messageid);
            map.put("messageName",messageName);
            map.put("messageNumber",messageNumber+"条");
            map.put("messageMoney","售价： "+messageMoney+"元");
            mList.add(map);
            mAdapter.notifyDataSetChanged();
            map = null;
        }
    }

    private void initView() {
        mList = new ArrayList<>();
        mAdapter = new MessagePannelAdapter(mList);
        comRlRechargePannel.setFocusable(false);
        comRlRechargePannel.setNestedScrollingEnabled(false);
        comRlRechargePannel.addItemDecoration(new MarginDecoration(this));
        comRlRechargePannel.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        comRlRechargePannel.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new MessagePannelAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                message_id = mList.get(position).get("messageid");
                mAdapter.setClickPosition(position);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private OnResponseListener<String> mResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
          //  dialogLoading.show();
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "MessageRechargePannelActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    handleData(what,jsonObject);
                } else {
                    ToastUtil.showShort(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
          //  dialogLoading.cancel();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
         //   dialogLoading.cancel();
        }

        @Override
        public void onFinish(int what) {
         //  dialogLoading.cancel();
        }
    };


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mList = null;
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
    }
}
