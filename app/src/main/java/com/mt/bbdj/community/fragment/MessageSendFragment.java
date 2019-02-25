package com.mt.bbdj.community.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.kongzue.dialog.v2.MessageDialog;
import com.kongzue.dialog.v2.TipDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseFragment;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.MessageSendAdapter;
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
import java.util.Map;

/**
 * Author : ZSK
 * Date : 2019/2/15
 * Description :   短信发送情况的界面展示
 */
public class MessageSendFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    private int type = 0;    // 0： 表示显示的是发送失败  1：表示发送成功

    private XRecyclerView recyclerView;
    private Button btSendAll;

    private MessageSendAdapter messageSendAdapter;
    private RequestQueue mRequestQueue;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;


    private List<HashMap<String, String>> mData = new ArrayList<>();
    private String user_id;
    private int page = 0;
    private final int REQUEST_GET_MESSAGE = 100;   //获取短信消息
    private final int REQUEST_SEND_MESSAGE = 200;   //发送消息

    /**
     * @param type 0 : 发送失败  1： 发送成功
     * @return
     */
    public static MessageSendFragment getInstance(int type) {
        MessageSendFragment mf = new MessageSendFragment();
        mf.type = type;
        return mf;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_send, container, false);
        EventBus.getDefault().register(this);
        initParams();
        initFragment(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(TargetEvent targetEvent) {
        if (TargetEvent.MESSAGE_MANAGE_REFRESH == targetEvent.getTarget()) {
            recyclerView.refresh();
        }
    }


    private void initParams() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();

        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
    }

    private void RequestData() {
        Request<String> request = NoHttpRequest.getMessageManagerRequest(user_id, type, page);
        mRequestQueue.add(REQUEST_GET_MESSAGE, request, mresponseListener);
    }

    private void sendMessageAgain(int type, String message_id) {
        Request<String> request = NoHttpRequest.sendMessageRequest(user_id, type, message_id);
        mRequestQueue.add(REQUEST_SEND_MESSAGE, request, mresponseListener);
    }

    private void initFragment(View view) {
        btSendAll = view.findViewById(R.id.tv_message_send_all);
        btSendAll.setVisibility(type == 1 ? View.GONE : View.VISIBLE);
        mData.clear();
        recyclerView = view.findViewById(R.id.rl_message_send_list);

        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        //设置线性布局 Creates a vertical LinearLayoutManager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setLoadingListener(this);
        messageSendAdapter = new MessageSendAdapter(mData, type);
        recyclerView.setAdapter(messageSendAdapter);

        //单个发送消息
        messageSendAdapter.setSendMessageListener(new MessageSendAdapter.OnSendMessageListener() {
            @Override
            public void onSendMessage(int position) {
                Map<String, String> messageMap = mData.get(position);
                String message_id = messageMap.get("id");
                sendMessageAgain(1, message_id);   //重新发送
            }
        });

        //一键全发
        btSendAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessageAgain(2, "");
            }
        });
    }


    private OnResponseListener<String> mresponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "MessageSendFragment::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                if ("5001".equals(code)) {
                    handleResult(what, jsonObject);
                } else if ("4002".equals(code)) {
                    showToast();
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
    };

    private void showToast() {
        MessageDialog.show(getActivity(), "提示", "短信余额不足！",
                "知道了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }

    private void handleResult(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case REQUEST_GET_MESSAGE:
                setMessageList(jsonObject);
                break;
            case REQUEST_SEND_MESSAGE:     //消息发送成功
                TipDialog.show(getActivity(), "发送成功", TipDialog.SHOW_TIME_SHORT, TipDialog.TYPE_FINISH);
                EventBus.getDefault().post(new TargetEvent(TargetEvent.MESSAGE_MANAGE_REFRESH));
                break;
        }
    }

    private void setMessageList(JSONObject dataObj) throws JSONException {
        JSONObject data = dataObj.getJSONObject("data");
        JSONArray list = data.getJSONArray("list");
        if (page == 1) {
            mData.clear();
        }

        String sendState = type == 1 ? "发送成功" : "发送失败";

        for (int i = 0; i < list.length(); i++) {
            JSONObject jsonObject1 = list.getJSONObject(i);
            HashMap<String, String> map = new HashMap<>();
            map.put("id", jsonObject1.getString("id"));
            map.put("dingdan", jsonObject1.getString("order_number"));
            map.put("yundan", jsonObject1.getString("waybill_number"));
            map.put("phone", jsonObject1.getString("send_phone"));
            map.put("name", jsonObject1.getString("send_name"));
            map.put("sendstate", sendState);
            map.put("content", jsonObject1.getString("content"));
            mData.add(map);
            map = null;
        }

        messageSendAdapter.notifyDataSetChanged();
    }


    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                page = 1;
                RequestData();
                recyclerView.refreshComplete();
            }
        }, 100);
    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                page++;
                RequestData();
                recyclerView.loadMoreComplete();
            }
        }, 100);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
    }
}
