package com.mt.bbdj.community.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.activity.MailingdetailActivity;
import com.mt.bbdj.community.activity.OutManager_new_Activity;
import com.mt.bbdj.community.adapter.HaveFinishAdapter;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author : ZSK
 * Date : 2019/1/8
 * Description :  已处理界面
 */
public class FinishHandleFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    private XRecyclerView recyclerView;

    private TextView tv_no_address;

    private RequestQueue mRequestQueue;
    private WaitDialog dialogLoading;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String express_id = "";      //快递公司id
    private String keyword = "";    //关键字
    private int page = 1;     //分页
    private String user_id = "";       //用户id
    private String start_time = "";    //开始时间

    private TextView tvCurrentTime;   //标题显示时间

    private List<HashMap<String, String>> mList = new ArrayList<>();
    private HaveFinishAdapter mAdapter;
    private TextView tvFast;
    private TextView tvLast;
    private String currentTime;
    private String currentStamp;

    public static FinishHandleFragment getInstance() {
        FinishHandleFragment sf = new FinishHandleFragment();
        return sf;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveEvent(TargetEvent targetEvent) {
        //刷新
        if (targetEvent.getTarget() == 1 || targetEvent.getTarget() == 0 || targetEvent.getTarget() == 2) {
            recyclerView.refresh();
        }

        if (targetEvent.getTarget() == 201) {
            //这个表示的是顶部搜索传值
            keyword = targetEvent.getData();
            recyclerView.refresh();
        }

        if (targetEvent.getTarget() == 301) {
            //这个表示的是快递公司id
            express_id = targetEvent.getData();
            recyclerView.refresh();
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_finish_handle, container, false);
        EventBus.getDefault().register(this);
        initView(view);
        initData();
        initClickListener();
        requestFinishHandleData();    //获取已处理的数据
        return view;
    }

    private void initClickListener() {

        mAdapter.setOnItemClickListener(new HaveFinishAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                HashMap<String, String> map = mList.get(position);
                String mail_id = map.get("mail_id");
                Intent intent = new Intent(getActivity(), MailingdetailActivity.class                                                                                                                                            );
                intent.putExtra("user_id", user_id);
                intent.putExtra("mail_id", mail_id);
                intent.putExtra("type",1);
                SharedPreferencesUtil.getEditor().putString("printType","3").commit();
                startActivity(intent);
            }

            //催单
            @Override
            public void onHandleNow(int position) {
                HashMap<String, String> map = mList.get(position);
                String mail_id = map.get("mail_id");
                requestHandleNow(mail_id);
            }
        });
    }

    private void requestHandleNow(String mail_id) {

        Request<String> request = NoHttpRequest.getHandleEventRequest(user_id,mail_id);

        mRequestQueue.add(2, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                dialogLoading = WaitDialog.show(getActivity(), "催单中...").setCanCancel(true);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "WaitCollectFragment::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();

                    if ("5001".equals(code)) {
                        recyclerView.refresh();
                    } else {
                        ToastUtil.showShort(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialogLoading.doDismiss();
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
        recyclerView = view.findViewById(R.id.rl_hava_finish);
        tv_no_address = view.findViewById(R.id.tv_no_address);
        tvCurrentTime = view.findViewById(R.id.tv_current_time);
        tvFast = view.findViewById(R.id.tv_fast);
        tvLast = view.findViewById(R.id.tv_last);
        tvLast.setOnClickListener(mOnClickListener);
        tvFast.setOnClickListener(mOnClickListener);

        recyclerView.setFocusable(false);
        //initTemparayData();   //模拟数据
        mAdapter = new HaveFinishAdapter(getActivity(), mList);
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


    private void requestFinishHandleData() {
        Request<String> request = NoHttpRequest.getFinishEventRequest(user_id, express_id, keyword,
                page + "", start_time);

        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                //  dialogLoading.show();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "WaitCollectFragment::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    JSONArray jsonArray = jsonObject.getJSONArray("data");

                    if (isFresh) {
                        recyclerView.refreshComplete();
                    } else {
                        recyclerView.loadMoreComplete();
                    }
                    if ("5001".equals(code)) {
                        setData(jsonArray);
                    } else {
                        ToastUtil.showShort("查询失败，请重试！");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // dialogLoading.cancel();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                //  dialogLoading.cancel();
            }

            @Override
            public void onFinish(int what) {
                // dialogLoading.cancel();
            }
        });
    }

    private void setData(JSONArray jsonArray) throws JSONException {
        if (jsonArray.length() == 0) {
            tv_no_address.setVisibility(View.VISIBLE);
        } else {
            tv_no_address.setVisibility(View.GONE);
        }
        if (page == 1) {
            mList.clear();
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
            String waybill_number = jsonObject1.getString("waybill_number");
            String express_id = jsonObject1.getString("express_id");
            String send_name = jsonObject1.getString("send_name");
            String collect_name = jsonObject1.getString("collect_name");
            String create_time = jsonObject1.getString("create_time");
            String express_logo = jsonObject1.getString("express_logo");
            String is_reminder = jsonObject1.getString("is_reminder");
            String mail_id = jsonObject1.getString("mail_id");
            HashMap<String, String> map = new HashMap<>();
            map.put("mail_id", mail_id);
            map.put("waybill_number", StringUtil.handleNullResultForString(waybill_number));
            map.put("express_id", express_id);
            map.put("send_name", send_name);
            map.put("create_time", create_time);
            map.put("express_logo", express_logo);
            map.put("collect_name", collect_name);
            map.put("is_reminder", is_reminder);
            mList.add(map);
        }
        if (mList.size() == 0) {
            tv_no_address.setVisibility(View.VISIBLE);
        } else {
            tv_no_address.setVisibility(View.GONE);
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
            recyclerView.refresh();
        }
    };

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

    private boolean isFresh = true;
    @Override
    public void onRefresh() {
        isFresh = true;
        page = 1;
        requestFinishHandleData();
    }

    @Override
    public void onLoadMore() {
        isFresh = false;
        page++;
        requestFinishHandleData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
    }
}
