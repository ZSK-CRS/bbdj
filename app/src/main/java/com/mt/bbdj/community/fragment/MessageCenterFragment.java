package com.mt.bbdj.community.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseFragment;
import com.mt.bbdj.baseconfig.db.ExpressLogo;
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
import com.mt.bbdj.baseconfig.view.BadgeView;
import com.mt.bbdj.community.activity.WebDetailActivity;
import com.mt.bbdj.community.adapter.HaveFinishAdapter;
import com.mt.bbdj.community.adapter.MessageCenterAdapter;
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
import java.util.Map;

/**
 * Author : ZSK
 * Date : 2019/2/19
 * Description :
 */
public class MessageCenterFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    private int mType = 0;
    private int page = 1;
    private RequestQueue mRequestQueue;
    private String user_id;
    private XRecyclerView recyclerView;

    private List<HashMap<String,String>> dataList = new ArrayList<>();

    private final int ACTION_REQUEST_MESSAGE = 200;
    private boolean isFresh;
    private MessageCenterAdapter mAdapter;
    private int target;
    private String unreadNumber;

    // 0 : 通知公告  1：消息中心  2：异常件
    public static MessageCenterFragment getInstance(int type) {
        MessageCenterFragment mf = new MessageCenterFragment();
        mf.mType = type;
        return mf;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_center, container, false);
        initParams();
        initList(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.refresh();
    }

    private void initList(View view) {
        recyclerView = view.findViewById(R.id.rv_message_list);
        recyclerView.setFocusable(false);
        //initTemparayData();   //模拟数据
        mAdapter = new MessageCenterAdapter(dataList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLoadingListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new MessageCenterAdapter.OnItemClickListener() {
            @Override
            public void OnClick(int position) {
                HashMap<String,String> map = dataList.get(position);
                String urllink = map.get("urllink");
                Intent intent = new Intent(getActivity(),WebDetailActivity.class);
                intent.putExtra("urllink",urllink);
                startActivity(intent);
            }
        });
    }

    private void initParams() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();

        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = daoSession.getUserBaseMessageDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }

    }

    private void requestData() {
        Request<String> request = null;
        if (mType == 0) {
            request = NoHttpRequest.getNotificationRequest(user_id, page);
            target = TargetEvent.NOTIFICATION_REFRESH;
        } else {
            request = NoHttpRequest.getSystmeMessageRequest(user_id, page);
            target = TargetEvent.SYSTEM_MESSAGE_REFRESH;
        }
        mRequestQueue.add(ACTION_REQUEST_MESSAGE, request, onResponseListener);
    }

    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "MessageCenter::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    if (page == 1) {
                        dataList.clear();
                    }
                    JSONObject dataJson = jsonObject.getJSONObject("data");
                    //未读消息
                    unreadNumber = dataJson.getString("unread");
                    JSONArray jsonArray = dataJson.getJSONArray("list");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObEntity =jsonArray.getJSONObject(i);
                        String id = jsonObEntity.getString("id");
                        String describes = jsonObEntity.getString("describes");
                        String title = jsonObEntity.getString("title");
                        String states = jsonObEntity.getString("states");
                        String time = jsonObEntity.getString("time");
                        String urllink = jsonObEntity.getString("urllink");
                        time = DateUtil.changeStampToStandrdTime("MM-dd HH:mm",time);
                        HashMap<String,String> map = new HashMap<>();
                        map.put("describes",describes);
                        map.put("title",title);
                        map.put("states",states);
                        map.put("type",mType+"");
                        map.put("time",time);
                        map.put("urllink",urllink);
                        dataList.add(map);
                        map = null;
                    }
                    unreadNumber = StringUtil.handleNullResultForNumber(unreadNumber);
                    EventBus.getDefault().post(new TargetEvent(target, unreadNumber));
                    mAdapter.notifyDataSetChanged();
                } else {
                    ToastUtil.showShort(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (isFresh) {
                recyclerView.refreshComplete();
            } else {
                recyclerView.loadMoreComplete();
            }
        }

        @Override
        public void onFailed(int what, Response<String> response) {

        }

        @Override
        public void onFinish(int what) {

        }
    };


    @Override
    public void onRefresh() {
        isFresh = true;
        page = 1;
        requestData();
    }

    @Override
    public void onLoadMore() {
        isFresh = false;
        page++;
        requestData();
    }
}
