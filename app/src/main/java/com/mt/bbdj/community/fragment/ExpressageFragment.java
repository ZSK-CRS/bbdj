package com.mt.bbdj.community.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseFragment;
import com.mt.bbdj.baseconfig.db.ExpressLogo;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.ExpressageEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.ExpressageAdapter;
import com.mt.bbdj.community.adapter.RechargeRecodeAdapter;
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
import butterknife.Unbinder;

/**
 * Author : ZSK
 * Date : 2019/1/5
 * Description :   快递列表
 */
public class ExpressageFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    @BindView(R.id.rl_expressage)
    XRecyclerView rlExpressage;
    Unbinder unbinder;

    private int mType = 1;

    private RequestQueue mRequestQueue;
    private HkDialogLoading dialogLoading;

    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private ExpressLogoDao mExpressLogoDao;

    private ExpressageAdapter mAdapter;

    private List<HashMap<String, String>> mData = new ArrayList<>();
    private String user_id = "";
    private boolean isFresh = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_expressage, container, false);
        unbinder = ButterKnife.bind(this, view);
        initParams();
        initList();
        initListener();       //点击事件
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        rlExpressage.refresh();
    }

    private void initListener() {
        mAdapter.setOnItemClickLister(new ExpressageAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                HashMap<String,String> map = mData.get(position);
                String express_id = map.get("express_id");
                String express_name = map.get("express_name");
                String express_logo = map.get("express_logo");
                String states = map.get("states");
                ExpressageEvent expressageEvent = new ExpressageEvent(express_id,express_name,express_logo,states);
                EventBus.getDefault().post(expressageEvent);
                getActivity().finish();
            }
        });
    }

    private void initList() {
        rlExpressage.setFocusable(false);
        rlExpressage.setNestedScrollingEnabled(false);
        //设置线性布局 Creates a vertical LinearLayoutManager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rlExpressage.addItemDecoration(new MyDecoration(getActivity(), LinearLayoutManager.VERTICAL, Color.parseColor("#e9e9e9"), 1));
        rlExpressage.setLayoutManager(mLayoutManager);
        rlExpressage.setLoadingListener(this);
        mAdapter = new ExpressageAdapter(getActivity(), mData);
        rlExpressage.setAdapter(mAdapter);
    }


    private void initParams() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        dialogLoading = new HkDialogLoading(getActivity(), "请稍后...");

        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();
        mExpressLogoDao = mDaoSession.getExpressLogoDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
    }

    /**
     * 获取fragment
     *
     * @param type 1: 表示快递公司   2:物流公司
     * @return
     */
    public static ExpressageFragment getInstance(int type) {
        ExpressageFragment fragment = new ExpressageFragment();
        fragment.mType = type;
        return fragment;
    }


    private void requestExpressage() {

        Request<String> request = NoHttpRequest.getExpressageRequest(user_id, mType + "");
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
               // dialogLoading.show();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "ExpressageFragment::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    if ("5001".equals(code)) {
                        mData.clear();
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i = 0; i < data.length(); i++) {
                            JSONObject jsonObject1 = data.getJSONObject(i);
                            String express_id = jsonObject1.getString("express_id");
                            String express_name = jsonObject1.getString("express_name");
                            String states = jsonObject1.getString("states");
                            String express_logo = jsonObject1.getString("express_logo");
                            HashMap<String,String> map = new HashMap<>();
                            ExpressLogo expressLogo = mExpressLogoDao.queryBuilder()
                                    .where(ExpressLogoDao.Properties.Express_id.eq(express_id)).unique();
                            if (expressLogo == null) {
                                map.put("express_logo",express_logo);
                            } else {
                                map.put("express_logo",expressLogo.getLogoLocalPath());
                            }
                            map.put("express_id",express_id);
                            map.put("express_name",express_name);
                            map.put("states",states);
                            mData.add(map);
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                } catch (JSONException e) {
                    //   dialogLoading.cancel();
                    e.printStackTrace();
                }
                //  dialogLoading.cancel();
                if (isFresh) {
                    rlExpressage.refreshComplete();
                } else {
                    rlExpressage.loadMoreComplete();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                // dialogLoading.cancel();
                if (isFresh) {
                    rlExpressage.refreshComplete();
                } else {
                    rlExpressage.loadMoreComplete();
                }
            }

            @Override
            public void onFinish(int what) {
                // dialogLoading.cancel();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
    }

    @Override
    public void onRefresh() {

      /*  new Handler().postDelayed(new Runnable() {
            public void run() {

                requestExpressage();
                rlExpressage.refreshComplete();
            }
        }, 100);*/
        isFresh = true;
        requestExpressage();
    }

    @Override
    public void onLoadMore() {
        /*new Handler().postDelayed(new Runnable() {
            public void run() {
                requestExpressage();
                rlExpressage.loadMoreComplete();
            }
        }, 100);*/
        isFresh = false;
        requestExpressage();
    }
}
