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
import com.mt.bbdj.community.adapter.GlobalHaveFinishAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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
    private GlobalHaveFinishAdapter mAdapter;
    private String keyWords = "";    //搜索关键字
    private final int REQUEST_GLOBAL_SEND = 100;

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
        if (targetEvent.getTarget() == TargetEvent.SEARCH_GLOBAL_PAI) {
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
        mAdapter = new GlobalHaveFinishAdapter(getActivity(), mList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLoadingListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(mAdapter);

        mAdapter.notifyDataSetChanged();


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    Glide.with(getActivity()).resumeRequests();//恢复Glide加载图片
                } else {
                    Glide.with(getActivity()).pauseRequests();//禁止Glide加载图片
                }
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
        Request<String> request = NoHttpRequest.getGlobalSendRequest(user_id, keyWords);
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
                        recyclerView.refreshComplete();
                    } else {
                        recyclerView.loadMoreComplete();
                    }
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {

                    }
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
}
