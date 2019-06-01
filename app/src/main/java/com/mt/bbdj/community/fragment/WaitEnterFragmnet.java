package com.mt.bbdj.community.fragment;

import android.app.Dialog;
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
import com.kongzue.dialog.listener.InputDialogOkButtonClickListener;
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
import com.mt.bbdj.baseconfig.view.MyInputDialog;
import com.mt.bbdj.baseconfig.view.SignView;
import com.mt.bbdj.community.activity.SignatureActivity;
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
public class WaitEnterFragmnet extends BaseFragment implements XRecyclerView.LoadingListener {

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
    private SignView signView;
    
    private final int REQUEST_CONFIRM_CHANGE = 200;   //请求数据

    private String starttime;
    private String endtime;
    private WaitDialog waitDialog;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_layout, container, false);
        EventBus.getDefault().register(this);
        initParams();
        initView(view);
        initListener();
        return view;
    }

    private void initListener() {
        //签名
        bt_singture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectDialog();
            }
        });

    }

    
    private void showSelectDialog() {
        if (mData.size() == 0) {
            ToastUtil.showShort("无入库数据");
            return;
        }

        // TODO: 2019/5/25 打印标签
        Intent intent = new Intent(getActivity(), SignatureActivity.class);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(TargetEvent targetEvent) {
        if (targetEvent.getTarget() == 300) {
            HashMap<String,String> data = (HashMap<String,String>)targetEvent.getObject();
            starttime = data.get("starttime");
            endtime = data.get("endtime");
            express_id = data.get("express_id");
            recyclerView.refresh();
        }

        //刷新自身
        if (targetEvent.getTarget() == TargetEvent.REFRESH_ALEADY_CHAGNE) {
            String typeStr = targetEvent.getData();

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
        Request<String> request = NoHttpRequest.getEnterStoreRequest(user_id, express_id);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "WaitEnterFragmnet::" + response.get());
                try {

                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();

                    if (isFresh) {
                        recyclerView.refreshComplete();
                    } else {
                        recyclerView.loadMoreComplete();
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
