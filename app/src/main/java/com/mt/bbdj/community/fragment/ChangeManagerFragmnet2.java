package com.mt.bbdj.community.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseFragment;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.SignView;
import com.mt.bbdj.community.activity.PayforOrderFromShopingCardActivity;
import com.mt.bbdj.community.activity.SignatureActivity;
import com.mt.bbdj.community.adapter.ChangeManager2Adapter;
import com.mt.bbdj.community.adapter.ChangeManagerAdapter;
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

/**
 * Author : ZSK
 * Date : 2019/3/2
 * Description :  交接
 */
public class ChangeManagerFragmnet2 extends BaseFragment implements XRecyclerView.LoadingListener {

    private int type = 1;
    private XRecyclerView recyclerView;
    private TextView tv_title, tv_number;
    private Button buttonPanel;
    private ChangeManagerAdapter mAdapter;
    private List<HashMap<String, String>> mData = new ArrayList<>();
    private RequestQueue mRequestQueue;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private ExpressLogoDao mExpressLogoDao;
    private String user_id;
    private String express_id = "";   //快递公司id
    private boolean isFresh = true;

    private Button bt_singture;
    private String starttime;
    private String endtime;
    private WaitDialog waitDialog;


    public static ChangeManagerFragmnet2 getInstance(int type) {
        ChangeManagerFragmnet2 cmf = new ChangeManagerFragmnet2();
        cmf.type = type;
        return cmf;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_layout, container, false);
        EventBus.getDefault().register(this);
        initParams();
        initView(view);
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(TargetEvent targetEvent) {
        if (targetEvent.getTarget() == 301) {
            HashMap<String,String> data = (HashMap<String,String>)targetEvent.getObject();
            starttime = data.get("starttime");
            endtime = data.get("endtime");
            express_id = data.get("express_id");
            recyclerView.refresh();
        }

        //刷新自身
        if (targetEvent.getTarget() == TargetEvent.REFRESH_ALEADY_CHAGNE) {
           /* String typeStr = targetEvent.getData();
            type = Integer.parseInt(typeStr);*/
            type = 2;
            recyclerView.refresh();
        }
    }


    private void initParams() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();
        mExpressLogoDao = mDaoSession.getExpressLogoDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }

        //默认的是今天
        starttime = DateUtil.getTadayStartTimeZeroStamp();
        endtime = DateUtil.getTadayEndTimeLastStamp();

    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.refresh();
    }

    private void requestData() {
        Request<String> request = NoHttpRequest.getChangeManagerRequest(user_id, express_id, type,starttime,endtime);
        mRequestQueue.add(2, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "ChangeManagerFragmnet2::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (isFresh) {
                        recyclerView.refreshComplete();
                    } else {
                        recyclerView.loadMoreComplete();
                    }

                    if ("5001".equals(code)) {
                        mData.clear();
                        JSONArray list = data.getJSONArray("list");

                        String sum = data.getString("sum");
                        tv_number.setText(sum);
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject jsonObject1 = list.getJSONObject(i);
                            String express_name = jsonObject1.getString("express_name");
                            String waybill_number = jsonObject1.getString("waybill_number");
                            String send_name = jsonObject1.getString("send_name");
                            String mailing_id = jsonObject1.getString("mailing_id");
                            String collect_name = jsonObject1.getString("collect_name");
                            String send_region = jsonObject1.getString("send_region");
                            String collect_region = jsonObject1.getString("collect_region");
                            String time = jsonObject1.getString("time");
                            String goods_weight = jsonObject1.getString("goods_weight");
                            String handover_states = jsonObject1.getString("handover_states");
                            String handover_time = jsonObject1.getString("handover_time");
                            String content = jsonObject1.getString("content");
                            HashMap<String, String> map = new HashMap<>();
                            map.put("express_name", express_name);
                            map.put("waybill_number", waybill_number);
                            map.put("person", send_name + "/" + collect_name);
                            map.put("address", send_region + "/" + collect_region);
                            map.put("time", DateUtil.changeStampToStandrdTime("yyyy-MM-dd", time));
                            map.put("goods_weight", goods_weight);
                            map.put("handover_states", handover_states);
                            map.put("mailing_id", mailing_id);
                            map.put("type", type + "");
                            map.put("handover_time", DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm:ss", handover_time));
                            map.put("content",StringUtil.handleNullResultForString(content));
                            map.put("isShowAddMark","1");    //表示显示不添加按钮
                            mData.add(map);
                            map = null;
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }

            @Override
            public void onFinish(int what) {
            }
        });
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.rl_change);
        bt_singture = view.findViewById(R.id.bt_singture);
        tv_number = view.findViewById(R.id.tv_number);
        buttonPanel = view.findViewById(R.id.buttonPanel);
        tv_title = view.findViewById(R.id.tv_title);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        //设置线性布局 Creates a vertical LinearLayoutManager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        //    recyclerView.addItemDecoration(new MyDecoration(getActivity(), LinearLayoutManager.VERTICAL, Color.parseColor("#e9e9e9"), 1));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setLoadingListener(this);
        mAdapter = new ChangeManagerAdapter(mData);
        recyclerView.setAdapter(mAdapter);
        bt_singture.setVisibility(View.GONE);
        tv_title.setText("交接数量:");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
}
