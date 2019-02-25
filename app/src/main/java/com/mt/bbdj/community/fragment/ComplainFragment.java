package com.mt.bbdj.community.fragment;

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
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseFragment;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.community.adapter.ComplainAdapter;
import com.mt.bbdj.community.adapter.MessageSendAdapter;
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

/**
 * Author : ZSK
 * Date : 2019/2/15
 * Description :   投诉管理
 */
public class ComplainFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    private int type = 0;    // 0： 未处理  1：已处理

    private XRecyclerView recyclerView;

    private ComplainAdapter messageSendAdapter;
    private RequestQueue mRequestQueue;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;

    private final int REQUEST_COMPLAIN_MANAGER = 100;


    private List<HashMap<String, String>> mData = new ArrayList<>();
    private String user_id;
    private int page = 1;

    /**
     * @param type 0 : 发送失败  1： 发送成功
     * @return
     */
    public static ComplainFragment getInstance(int type) {
        ComplainFragment mf = new ComplainFragment();
        mf.type = type;
        return mf;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_complain, container, false);
        initParams();
        initFragment(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        RequestData();
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
        Request<String> request = NoHttpRequest.getComplainManagerRequest(user_id, type);
        mRequestQueue.add(REQUEST_COMPLAIN_MANAGER, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "ComplainFragment::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    if ("5001".equals(code)) {
                        JSONObject data = jsonObject.getJSONObject("data");
                        JSONArray listData = data.getJSONArray("list");
                        if (page == 1) {
                            mData.clear();
                        }

                        for (int i = 0; i < listData.length(); i++) {
                            JSONObject jsonObject1 = listData.getJSONObject(i);
                            jsonObject1.getString("id");
                            HashMap<String,String> map = new HashMap<>();
                            map.put("phone",jsonObject1.getString("phone"));
                            map.put("name",jsonObject1.getString("username"));
                            map.put("complain_type",jsonObject1.getString("complaint_name"));
                            map.put("describe",jsonObject1.getString("content"));
                            map.put("result",jsonObject1.getString("feedback"));
                            map.put("type",type+"");
                            mData.add(map);
                            map = null;
                        }
                        messageSendAdapter.notifyDataSetChanged();
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

    private void initFragment(View view) {
        mData.clear();
        recyclerView = view.findViewById(R.id.rl_message_complain_list);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        //设置线性布局 Creates a vertical LinearLayoutManager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setLoadingListener(this);
        messageSendAdapter = new ComplainAdapter(mData);
        recyclerView.setAdapter(messageSendAdapter);
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
}
