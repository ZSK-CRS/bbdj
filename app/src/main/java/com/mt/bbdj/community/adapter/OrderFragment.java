package com.mt.bbdj.community.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseFragment;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.activity.MyOrderDetailActivity;
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
 * Date : 2019/2/26
 * Description :
 */
public class OrderFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    private XRecyclerView recyclerView;
    private MyOrderAdapter mAdapter;
    private List<HashMap<String,String>> mList = new ArrayList<>();
    private String user_id;
    private RequestQueue mRequestQueue;
    private int type = 1;    //1 ： 未处理  2：已处理
    private boolean isFresh;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order,container,false);
        initView(view);
        initParams();
        requestData();
        return view;
    }

    public static OrderFragment getInstance(int type) {
        OrderFragment of = new OrderFragment();
        of.type = type;
        return of;
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

    }

    private void requestData() {
        Request<String> request = NoHttpRequest.getMyOrderList(user_id,type);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                // dialogLoading.show();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "OrderFragment::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (isFresh) {
                        mList.clear();
                        recyclerView.refreshComplete();
                    } else {
                        recyclerView.loadMoreComplete();
                    }
                    if ("5001".equals(code)) {
                        for (int i = 0;i<jsonArray.length();i++) {
                            JSONObject jsonArray1 = jsonArray.getJSONObject(i);
                            String orders_id = jsonArray1.getString("orders_id");
                            String thumb = jsonArray1.getString("thumb");
                            String product_name = jsonArray1.getString("product_name");
                            String genre_name = jsonArray1.getString("genre_name");
                            String number = jsonArray1.getString("number");
                            String money = jsonArray1.getString("money");
                            HashMap<String,String> map = new HashMap<>();
                            map.put("orders_id",orders_id);
                            map.put("thumb",thumb);
                            map.put("product_name",product_name);
                            map.put("genre_name",genre_name);
                            map.put("number",number);
                            map.put("money",money);
                            map.put("state",type == 1?"未处理":"已处理");
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
                //  dialogLoading.cancel();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                // dialogLoading.cancel();
            }

            @Override
            public void onFinish(int what) {
                //   dialogLoading.cancel();
            }
        });
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.rl_order);
        initRecycler();
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

        mAdapter.setOnItemClickListener(new MyOrderAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                HashMap<String,String> map = mList.get(position);
                Intent intent = new Intent(getActivity(),MyOrderDetailActivity.class);
                intent.putExtra("orders_id",map.get("orders_id"));
                intent.putExtra("type",type);
                startActivity(intent);
            }
        });
    }

    private void initRecycler() {
        recyclerView.setFocusable(false);
        //initTemparayData();   //模拟数据
        mAdapter = new MyOrderAdapter(getActivity(),mList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLoadingListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
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
}
