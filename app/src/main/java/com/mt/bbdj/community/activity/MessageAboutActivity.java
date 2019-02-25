package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.activity.LoginActivity;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.DestroyEvent;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.RechargeRecodeAdapter;
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

//短息(面单)充值记录界面
public class MessageAboutActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;      //返回
    @BindView(R.id.com_bt_message_bt)
    Button mBtrecharge;        //充值
    @BindView(R.id.com_rl_message)
    XRecyclerView mMessageRl;  //充值记录列表
    @BindView(R.id.tv_residue_number)
    TextView tvResidueNumber;
    @BindView(R.id.tv_recharge_title)
    TextView tvRechargeTitle;    //标题

    private RequestQueue mRequestQueue;
    private HkDialogLoading dialogLoading;

    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;

    private int mPage = 1;      //表示的是请求数据页数

    private List<HashMap<String, String>> mData = new ArrayList<>();
    private RechargeRecodeAdapter mAdapter;

    private int mType = 1;    //1 : 短信   2： 面单

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_about);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();

        initClick();

    }

    //type : 1:短信   2：面单
    public static void startAction(Context context,int type) {
        Intent intent = new Intent(context,MessageAboutActivity.class);
        intent.putExtra("type",type);
        context.startActivity(intent);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void destoryView(DestroyEvent destroyEvent) {
        if (1 == destroyEvent.getType()) {
            finish();
        }
    }

    private void initClick() {
        mBtrecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if (mType == 1) {    //短信充值页面
                    intent.setClass(MessageAboutActivity.this, MessageRechargePannelActivity.class);
                }else {           //面单充值页面
                    intent.setClass(MessageAboutActivity.this, PannelRechargeActivity.class);
                }
                startActivity(intent);
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initData() {
        initParams();           //初始化参数
        initRecordList();       //初始化列表
        requestData();         //请求数据
    }

    private void requestData() {
        String user_id = "";
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        Request<String> request = NoHttpRequest.getRechargeRecodeRequst(mType+"", mPage + "", user_id);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
               // dialogLoading.show();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "MessageAboutActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();

                    JSONObject data = jsonObject.getJSONObject("data");
                    if ("5001".equals(code)) {
                        JSONArray list = data.getJSONArray("list");
                        String sum = data.get("sum").toString();

                        tvResidueNumber.setText(StringUtil.handleNullResultForNumber(sum));
                        if (mPage == 1) {
                            mData.clear();
                        }
                        for (int i = 0;i < list.length();i++) {
                            JSONObject jsonObject1 = list.getJSONObject(i);
                            String message_money = jsonObject1.getString("money");
                            String message_number = jsonObject1.getString("number");
                            String message_time = jsonObject1.getString("time");
                            message_time = DateUtil.changeStampToStandrdTime("yyyy - MM - dd  HH:mm",message_time);
                            HashMap<String,String> map = new HashMap<>();
                            map.put("message_money",message_money+"元");
                            map.put("message_number",message_number+(mType == 1?"条":"个"));
                            map.put("message_time",message_time);
                            mData.add(map);
                        }
                        mAdapter.notifyDataSetChanged();

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
               // dialogLoading.cancel();
            }

            @Override
            public void onFinish(int what) {
             //   dialogLoading.cancel();
            }
        });
    }

    private void initParams() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        dialogLoading = new HkDialogLoading(MessageAboutActivity.this, "请稍后...");

        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        Intent intent = getIntent();
        mType = intent.getIntExtra("type",1);

        if (1 == mType) {
            tvRechargeTitle.setText("短信余额");
        } else {
            tvRechargeTitle.setText("面单余额");
        }
    }

    private void initRecordList() {
        mMessageRl.setFocusable(false);
        mMessageRl.setNestedScrollingEnabled(false);
        //设置线性布局 Creates a vertical LinearLayoutManager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mMessageRl.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL, Color.parseColor("#e9e9e9"), 1));
        mMessageRl.setLayoutManager(mLayoutManager);
        mMessageRl.setLoadingListener(this);
        mAdapter = new RechargeRecodeAdapter(mData);
        mMessageRl.setAdapter(mAdapter);
    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mPage = 1;
                requestData();
                mMessageRl.refreshComplete();
            }
        }, 100);
    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                mPage++;
                requestData();
                mMessageRl.loadMoreComplete();
            }
        }, 100);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
    }
}
