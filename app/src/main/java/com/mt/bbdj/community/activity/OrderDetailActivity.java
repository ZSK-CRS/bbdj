package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.ExpressLogo;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.ClientManagerAdapter;
import com.mt.bbdj.community.adapter.OrderDetailAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderDetailActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    private RelativeLayout icBack;
    private XRecyclerView recyclerView;
    private TextView tv_no_address;
    private boolean isFresh;
    private RequestQueue mRequestQueue;
    private String user_id;
    private OrderDetailAdapter mAdapter;
    private List<HashMap<String, String>> mList = new ArrayList<>();
    private WaitDialog dialogLoading;
    private String customer_id;
    private int page = 1;
    private ExpressLogoDao mExpressLogoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        initView();
        initParams();
        initList();
        requestData();
    }

    private void initParams() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        DaoSession mDaoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = mDaoSession.getUserBaseMessageDao();
        mExpressLogoDao = mDaoSession.getExpressLogoDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }

        Intent intent = getIntent();
        customer_id = intent.getStringExtra("customer_id");
    }

    private void initList() {
        mAdapter = new OrderDetailAdapter(this, mList);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLoadingListener(this);
        recyclerView.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL,
                Color.parseColor("#f4f4f4"), 1));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);
    }

    private void requestData() {
        Request<String> request = NoHttpRequest.getClientOrderDetailRequest(user_id, customer_id, page);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
              //  dialogLoading = WaitDialog.show(OrderDetailActivity.this, "请稍后...").setCanCancel(true);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "OrderDetailActivity::" + response.get());
                try {
                    if (isFresh) {
                        recyclerView.refreshComplete();
                    } else {
                        recyclerView.loadMoreComplete();
                    }
                    if (page == 1) {
                        mList.clear();
                    }
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    JSONObject dataArry = jsonObject.getJSONObject("data");
                    JSONArray data = dataArry.getJSONArray("list");
                    if ("5001".equals(code)) {
                        if (data.length() == 0) {
                            tv_no_address.setVisibility(View.VISIBLE);
                        } else {
                            tv_no_address.setVisibility(View.GONE);
                            setData(data);
                        }
                    } else {
                        ToastUtil.showShort(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
              //  dialogLoading.doDismiss();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
             //   dialogLoading.doDismiss();
            }

            @Override
            public void onFinish(int what) {
           //     dialogLoading.doDismiss();
            }
        });
    }

    private void setData(JSONArray data) throws JSONException {
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            String yundanhao = jsonObject.getString("yundanhao");
            String send_name = jsonObject.getString("send_name");
            String collect_name = jsonObject.getString("collect_name");
            String money = jsonObject.getString("money");
            String time = jsonObject.getString("time");
            time = DateUtil.changeStampToStandrdTime("yyyy-MM-dd",time);
            String express_id = jsonObject.getString("express_id");
            ExpressLogo expressLogo = mExpressLogoDao.queryBuilder()
                    .where(ExpressLogoDao.Properties.Express_id.eq(express_id)).unique();
            HashMap<String,String> map = new HashMap<>();
            map.put("yundanhao",yundanhao);
            map.put("send_name",send_name);
            map.put("collect_name",collect_name);
            map.put("money",money);
            map.put("time",time);
            map.put("expressLogo",expressLogo.getLogoLocalPath());
            mList.add(map);
            map = null;
        }
        mAdapter.notifyDataSetChanged();
    }


    private void initView() {
        icBack = findViewById(R.id.iv_back);
        recyclerView = findViewById(R.id.rl_client_manager);
        tv_no_address = findViewById(R.id.tv_no_address);
        icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    public void onRefresh() {
        page = 1;
        isFresh = true;
        requestData();
    }

    @Override
    public void onLoadMore() {
        page++;
        isFresh = false;
        requestData();
    }
}
