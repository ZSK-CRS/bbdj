package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.CauseForcannelOrderAdapter;
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

//取消订单原因
public class CauseForcannelOrderActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.rl_cause_order)
    RecyclerView rlCauseOrder;
    @BindView(R.id.bt_commit_next)
    Button btCommitNext;

    private String user_id;    //用户id
    private String mail_id;     //订单id
    private String reason_id;    //取消原因id
    private String content;   //备注

    private List<HashMap<String, String>> mList = new ArrayList<>();

    private final int REQUEST_CAUSE_CANNEL = 101;    //请求取消订单原因
    private final int REQUEST_COMMIT_CAUSE = 102;   //提交取消订单


    private CauseForcannelOrderAdapter mAdapter;

    private RequestQueue mRequestQueue;
    private HkDialogLoading dialogLoading;
    private EditText contentEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cause_forcannel_order);
        ButterKnife.bind(this);
        initParams();
        initData();
        initList();
        initClickListener();
        RequestCauseData();    //获取取消原因
    }

    @OnClick({R.id.iv_back, R.id.bt_commit_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_commit_next:
                commitCauseRequest();
                break;
        }
    }

    private void handleResult(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case REQUEST_CAUSE_CANNEL:
                handleCannelCause(jsonObject);    //获取取消消息
                break;
            case REQUEST_COMMIT_CAUSE:
                ToastUtil.showShort("提交成功！");
                EventBus.getDefault().post(new TargetEvent(1));
                finish();
                break;
        }
    }

    private void handleCannelCause(JSONObject jsonObject) throws JSONException {
        JSONArray data = jsonObject.getJSONArray("data");
        mList.clear();
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonObject1 = data.getJSONObject(i);
            String reason_id = jsonObject1.getString("reason_id");
            String reason_name = jsonObject1.getString("reason_name");
            HashMap<String, String> map = new HashMap<>();
            map.put("reason_id", reason_id);
            map.put("reason_name", reason_name);
            mList.add(map);
        }
        mAdapter.notifyDataSetChanged();
    }

    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            dialogLoading.show();
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "CauseCannelOrderActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                if ("5001".equals(code)) {
                    handleResult(what, jsonObject);
                } else {
                    ToastUtil.showShort("查询失败，请重试！");
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
    };

    private void initClickListener() {

        mAdapter.setOnItemClickListener(new CauseForcannelOrderAdapter.OnItemClickListener() {
            @Override
            public void OnClick(int position) {
                reason_id = mList.get(position).get("reason_id");
                View childItem = rlCauseOrder.getChildAt(position);
                //获取备注控件
                contentEt = childItem.findViewById(R.id.item_et_marke);
                mAdapter.setCurrentClickPosition(position);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void initData() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        dialogLoading = new HkDialogLoading(this, "请稍后...");
    }

    private void RequestCauseData() {
        Request<String> request = NoHttpRequest.getCauseForCannelOrderRequest(user_id);
        mRequestQueue.add(REQUEST_CAUSE_CANNEL, request, onResponseListener);
    }


    private void initList() {
        mAdapter = new CauseForcannelOrderAdapter(mList);
        rlCauseOrder.setFocusable(false);
        rlCauseOrder.setNestedScrollingEnabled(false);
        rlCauseOrder.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,
                false));
        rlCauseOrder.setAdapter(mAdapter);
    }

    private void initParams() {
        Intent intent = getIntent();
        user_id = intent.getStringExtra("user_id");
        mail_id = intent.getStringExtra("mail_id");
    }


    private void commitCauseRequest() {
        content = contentEt.getText().toString();
        Request<String> request = NoHttpRequest.commitCannelOrderCauseRequest(user_id, mail_id, reason_id, content);
        mRequestQueue.add(REQUEST_COMMIT_CAUSE, request, onResponseListener);
    }
}
