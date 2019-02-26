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
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.ClientDetailAdapter;
import com.mt.bbdj.community.adapter.ClientMessageAdapter;
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

public class ClientMessageDetail extends BaseActivity implements XRecyclerView.LoadingListener {
    private RelativeLayout icBack;
    private XRecyclerView recyclerView;
    private TextView tvAddTextView;
    private TextView tvNoAddress;
    private List<HashMap<String,String>> mapList = new ArrayList<>();
    private RequestQueue mRequestQueue;
    private String user_id;
    private ClientMessageAdapter mAdapter;
    private WaitDialog waitDialog;
    private boolean isFresh = true;


    private final int REQUEST_GET_LIST = 100;     //获取列表
    private final int REQEUST_EDIT_MESSAGE = 200;   //编辑信息

    private final int EDIT_CLIENT_MESSAGE = 101;  //编辑短信

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_message_detail);
        initView();
        initParams();
        initListener();
    }

    private void initListener() {
        //点击条目
        tvAddTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClientMessageDetail.this,ClientMessageChangeActivity.class);
                startActivity(intent);
            }
        });

        //删除
        mAdapter.setOnItemDeleteListener(new ClientMessageAdapter.OnItemDeleteListener() {
            @Override
            public void onItemDelete(int posiiton) {
                HashMap<String,String> map = mapList.get(posiiton);
                String customer_id = map.get("customer_id");
                deleteClient(customer_id);
            }
        });

        //编辑
        mAdapter.setOnItemEditListener(new ClientMessageAdapter.OnItemEditListener() {
            @Override
            public void onItemEdit(int position) {
                HashMap<String,String> map = mapList.get(position);
                Intent intent = new Intent(ClientMessageDetail.this,ClientMessageChangeActivity.class);
                intent.putExtra("customer_id",map.get("customer_id"));
                intent.putExtra("customer_realname",map.get("customer_realname"));
                intent.putExtra("customer_telephone",map.get("customer_telephone"));
                intent.putExtra("company_name",map.get("company_name"));
                intent.putExtra("customer_region",map.get("customer_region"));
                intent.putExtra("customer_address",map.get("customer_address"));
                intent.putExtra("content",map.get("content"));
                intent.putExtra("isEdit",true);
                startActivityForResult(intent,EDIT_CLIENT_MESSAGE);
            }
        });

        //返回
        icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
       if (requestCode != EDIT_CLIENT_MESSAGE || resultCode != RESULT_OK) {
           return;
       }
        recyclerView.refresh();
    }

    private void deleteClient(String customer_id) {
        Request<String> request = NoHttpRequest.deleteClientRequest(user_id,customer_id);
        mRequestQueue.add(REQEUST_EDIT_MESSAGE, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
             //   waitDialog = WaitDialog.show(ClientMessageDetail.this,"请稍后...").setCanCancel(true);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "ClientMessageDetail::" + response.get());
                try {
                    recyclerView.refresh();
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    JSONArray data = jsonObject.getJSONArray("data");
                    if ("5001".equals(code)) {
                        setData(data);
                    } else {
                        ToastUtil.showShort(msg);
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
               // waitDialog.doDismiss();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
               // waitDialog.doDismiss();
            }

            @Override
            public void onFinish(int what) {
                //waitDialog.doDismiss();
            }
        });
    }

    private void initParams() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        DaoSession mDaoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = mDaoSession.getUserBaseMessageDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        mAdapter = new ClientMessageAdapter(this,mapList);
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        initRecycler();
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.refresh();
    }

    private void requestData() {
        Request<String> request = NoHttpRequest.getClientManagerListRequest(user_id);
        mRequestQueue.add(REQUEST_GET_LIST, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
            //    waitDialog = WaitDialog.show(ClientMessageDetail.this,"请稍后...").setCanCancel(true);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "ClientMessageDetail::" + response.get());
                try {
                    if (isFresh) {
                        recyclerView.refreshComplete();
                        mapList.clear();
                    } else {
                        recyclerView.loadMoreComplete();
                    }
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    JSONArray data = jsonObject.getJSONArray("data");
                    if ("5001".equals(code)) {
                        setData(data);
                    } else {
                        ToastUtil.showShort(msg);
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
               // waitDialog.doDismiss();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
               // waitDialog.doDismiss();
            }

            @Override
            public void onFinish(int what) {
             //   waitDialog.doDismiss();
            }
        });
    }

    private void setData(JSONArray data) throws JSONException {
        if (data.length() == 0) {
            tvNoAddress.setVisibility(View.VISIBLE);
        } else {
            tvNoAddress.setVisibility(View.GONE);
        }
        for (int i = 0;i<data.length();i++) {
            JSONObject object = data.getJSONObject(i);
            String customer_id = object.getString("customer_id");
            String customer_realname = object.getString("customer_realname");
            String customer_telephone = object.getString("customer_telephone");
            String company_name = object.getString("company_name");
            String customer_region = object.getString("customer_region");
            String content = object.getString("content");
            String customer_address = object.getString("customer_address");
            HashMap<String,String> map = new HashMap<>();
            map.put("customer_id",customer_id);
            map.put("customer_realname",customer_realname);
            map.put("customer_telephone",customer_telephone);
            map.put("company_name",company_name);
            map.put("customer_region",customer_region);
            map.put("content",content);
            map.put("customer_address",customer_address);
            mapList.add(map);
            map = null;
        }
    }

    private void initView() {
        icBack = findViewById(R.id.iv_back);
        tvAddTextView = findViewById(R.id.bt_add_client);
        recyclerView = findViewById(R.id.rl_client_manager);
        tvNoAddress = findViewById(R.id.tv_no_address);

    }

    private void initRecycler() {
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLoadingListener(this);
        recyclerView.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL,
                Color.parseColor("#f4f4f4"), 1));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onRefresh() {
        isFresh = true;
        requestData();
    }

    @Override
    public void onLoadMore() {
        isFresh = false;
        requestData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mapList = null;
    }
}
