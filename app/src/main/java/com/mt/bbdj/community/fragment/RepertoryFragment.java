package com.mt.bbdj.community.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseFragment;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.activity.RepertoryDetailActivity;
import com.mt.bbdj.community.adapter.RepertoryAdapter;
import com.mt.bbdj.community.adapter.WaitCollectAdapter;
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
 * Date : 2019/3/12
 * Description :
 */
public class RepertoryFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    private TextView tv_no_address;
    private XRecyclerView rlRepertory;
    private boolean isFresh = true;
    private List<HashMap<String,String>> mList;
    private RepertoryAdapter mAdapter;
    private TextView tvFast,tvLast,tvCurrentTime;
    private RequestQueue mRequestQueue;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String user_id;
    private String express_id;
    private String keyword;
    private int mPage = 1;
    private String start_time;
    private String currentTime;
    private int mType = 0;

    public static RepertoryFragment getInstance(int mType) {
        RepertoryFragment rf = new RepertoryFragment();
        rf.mType = mType;
        return  rf;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_repertory,container,false);
        EventBus.getDefault().register(this);
        initData();
        initView(view);
        initListener();
        return view;
    }

    private void initListener() {
        mAdapter.setOnItemClickListener(new RepertoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getActivity(),RepertoryDetailActivity.class);
                intent.putExtra("type",mType);
                startActivity(intent);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
       // rlRepertory.refresh();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(TargetEvent targetEvent) {
        if (targetEvent.getTarget() == 300 || targetEvent.getTarget() == 301) {
           // rlRepertory.refresh();
        }
    }

    private void initData() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();

        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }

        start_time = DateUtil.getTadayStartTimeStamp();

        currentTime = DateUtil.dayDate();   //当前时间
    }

    private void initView(View view) {
        tv_no_address = view.findViewById(R.id.tv_no_address);
        tv_no_address.setVisibility(View.GONE);
        tvCurrentTime = view.findViewById(R.id.tv_current_time);
        tvFast = view.findViewById(R.id.tv_fast);
        tvLast = view.findViewById(R.id.tv_last);
        tvLast.setOnClickListener(mOnClickListener);
        tvFast.setOnClickListener(mOnClickListener);

        rlRepertory = view.findViewById(R.id.rl_repertory);
        mList = new ArrayList<>();
        mAdapter = new RepertoryAdapter(getActivity(), mList);
        initListData();
        rlRepertory.setFocusable(false);
        rlRepertory.setNestedScrollingEnabled(false);
        rlRepertory.setLoadingListener(this);
        rlRepertory.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rlRepertory.setAdapter(mAdapter);
    }

    private void initListData() {
        for (int i = 0;i<10;i++) {
            HashMap<String,String> map = new HashMap<>();
            map.put("order","74551255621232");
            map.put("express","中通快递");
            map.put("time","2019-03-12 12:33:08");
            map.put("tag_number","121526");
            mList.add(map);
        }
        mAdapter.notifyDataSetChanged();
    }

    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            int type = 0;
            if (view.getId() == R.id.tv_fast) {  //前一天
                currentTime = DateUtil.getSpecifiedDayBefore("yyyy-MM-dd HH:mm:ss", currentTime);
                start_time = DateUtil.getSomeDayStamp(currentTime);
            }

            if (view.getId() == R.id.tv_last) {  //后一天
                type = isCurrentDate();
                if (type == 1) {
                    tvCurrentTime.setText("今日");
                    return;
                }
                currentTime = DateUtil.getSpecifiedDayAfter("yyyy-MM-dd HH:mm:ss", currentTime);
                start_time = DateUtil.getSomeDayStamp(currentTime);
            }
            String time = currentTime.substring(0, currentTime.indexOf(" "));
            tvCurrentTime.setText(type == 2 ? "今日" : time);
            // rlRepertory.refresh();
        }
    };

    public void  requestData(){
        Request<String> request = NoHttpRequest.getFinishEventRequest(user_id, express_id, keyword,
                mPage + "", start_time);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

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
                        rlRepertory.refreshComplete();
                    } else {
                        rlRepertory.loadMoreComplete();
                    }
                    if ("5001".equals(code)) {

                    } else {
                        ToastUtil.showShort(msg);
                    }
                    mAdapter.notifyDataSetChanged();
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

    private int isCurrentDate() {
        //标准时间
        String date = DateUtil.dayDate();
        String standrdTime = date.substring(0, date.indexOf(" "));

        //当前选中时间的后一天
        String currentTimeLast = DateUtil.getSpecifiedDayAfter("yyyy-MM-dd HH:mm:ss", currentTime);
        String currentTime = currentTimeLast.substring(0, currentTimeLast.indexOf(" "));

        int standrdTimeNumber = IntegerUtil.getDateStringToNumber(standrdTime);
        int currentTimeLastNumber = IntegerUtil.getDateStringToNumber(currentTime);

        //如果当前选中时间的后一天大于标准时间
        if (standrdTimeNumber < currentTimeLastNumber) {
            return 1;
        }
        //等于
        if (standrdTimeNumber == currentTimeLastNumber) {
            return 2;
        }
        return 0;
    }

    @Override
    public void onRefresh() {
        isFresh = true;
        rlRepertory.refreshComplete();
    }

    @Override
    public void onLoadMore() {
        isFresh = false;
        rlRepertory.loadMoreComplete();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }
}
