package com.mt.bbdj.community.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseFragment;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.GlobalHaveFinishAdapter;
import com.mt.bbdj.community.adapter.GlobalReceiveAdapter;
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
 * Date : 2019/3/25
 * Description :
 */
public class GlobalSearchReceiveFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    private XRecyclerView recyclerView;
    private TextView noAddress;
    private boolean isFresh = true;
    // private GlobalHaveFinishAdapter mAdapter;
    private GlobalReceiveAdapter mAdapter;
    private String keyWords = "";    //搜索关键字
    private final int REQUEST_GLOBAL_SEND = 100;
    private final int OUT_WAY_BILL_REQUEST = 200;

    private List<HashMap<String, String>> mList = new ArrayList<>();
    private String user_id;
    private RequestQueue mRequestQueue;
    private WaitDialog dialogLoading;

    public static GlobalSearchReceiveFragment getInstance() {
        GlobalSearchReceiveFragment gf = new GlobalSearchReceiveFragment();
        return gf;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.global_search_send_fragment, container, false);
        EventBus.getDefault().register(this);
        initParams();    //初始化参数
        initView(view);
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(TargetEvent targetEvent) {
        if (targetEvent.getTarget() == TargetEvent.SEARCH_GLOBAL) {
            keyWords = targetEvent.getData();
            recyclerView.refresh();
        }
        if (targetEvent.getTarget() == TargetEvent.CLEAR_SEARCH_DATA) {
            keyWords = "";
            mList.clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initParams() {
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = daoSession.getUserBaseMessageDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        mRequestQueue = NoHttp.newRequestQueue();
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.refresh();
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.rl_grobal_send);
        noAddress = view.findViewById(R.id.tv_no_address);

        recyclerView.setFocusable(false);
        //initTemparayData();   //模拟数据
        mAdapter = new GlobalReceiveAdapter(getActivity(), mList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLoadingListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickOutListener(new GlobalReceiveAdapter.OnItemClickOutListener() {
            @Override
            public void onItemOutClick(int position) {
                HashMap<String,String> map = mList.get(position);
                String pie_id = map.get("pie_id");
                outOfrepertory(pie_id);
            }
        });

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

    private void requestData() {
        Request<String> request = NoHttpRequest.getGloableReceiveRequest(user_id, keyWords);
        mRequestQueue.add(REQUEST_GLOBAL_SEND, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                // dialogLoading = WaitDialog.show(getActivity(), "请稍后...").setCanCancel(true);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "GlobalSearchSendFragment::" + response.get());
                try {
                    if (isFresh) {
                        mList.clear();
                        recyclerView.refreshComplete();
                    } else {
                        recyclerView.loadMoreComplete();
                    }
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        if (dataArray.length() != 0) {
                            noAddress.setVisibility(View.GONE);
                        } else {
                            noAddress.setVisibility(View.VISIBLE);
                        }
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject jsonObject1 = dataArray.getJSONObject(i);
                            HashMap<String, String> map = new HashMap<>();
                            String express_name = jsonObject1.getString("express_name");
                            String pie_id = jsonObject1.getString("pie_id");
                            String waybill_number = jsonObject1.getString("waybill_number");
                            String tagNumber = jsonObject1.getString("code");
                            String warehousing_time = jsonObject1.getString("warehousing_time");
                            String out_time = jsonObject1.getString("out_time");
                            String types = jsonObject1.getString("types");
                            map.put("express_name", StringUtil.handleNullResultForString(express_name));
                            map.put("pie_id", StringUtil.handleNullResultForString(pie_id));
                            map.put("waybill_number", StringUtil.handleNullResultForString(waybill_number));
                            map.put("tagNumber", StringUtil.handleNullResultForString(tagNumber));
                            map.put("warehousing_time", StringUtil.handleNullResultForString(warehousing_time));
                            map.put("out_time", StringUtil.handleNullResultForString(out_time));
                            map.put("types", StringUtil.handleNullResultForString(types));
                            mList.add(map);
                            map = null;
                        }
                    } else {
                        ToastUtil.showShort(msg);
                    }
                    mAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //  dialogLoading.doDismiss();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                // dialogLoading.doDismiss();
            }

            @Override
            public void onFinish(int what) {
                // dialogLoading.doDismiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void outOfrepertory(String pie_id) {
        Request<String> request = NoHttpRequest.outOfRepertoryRequest(user_id, pie_id);
        mRequestQueue.add(OUT_WAY_BILL_REQUEST, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                dialogLoading = WaitDialog.show(getActivity(), "出库中...").setCanCancel(true);

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "GlobalSearchReceiveFragment::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    outofRepertoryResult(jsonObject);    //处理结果
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dialogLoading.doDismiss();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                dialogLoading.doDismiss();
            }

            @Override
            public void onFinish(int what) {
                dialogLoading.doDismiss();
            }
        });
    }

    private void outofRepertoryResult(JSONObject jsonObject) throws JSONException {
        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();
        if ("5001".equals(code)) {
            recyclerView.refresh();
        }
        ToastUtil.showShort(msg);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
    }
}
