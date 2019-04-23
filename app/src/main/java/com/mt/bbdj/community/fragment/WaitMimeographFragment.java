package com.mt.bbdj.community.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.EventLog;
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
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.activity.IdentificationActivity;
import com.mt.bbdj.community.activity.RecordSheetActivity;
import com.mt.bbdj.community.adapter.HaveFinishAdapter;
import com.mt.bbdj.community.adapter.WaitPrintAdapter;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/1/8
 * Description :  待打印
 */
public class WaitMimeographFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    private XRecyclerView recyclerView;

    private TextView tv_no_address;

    private WaitPrintAdapter mAdapter;
    private RequestQueue mRequestQueue;
    private HkDialogLoading dialogLoading;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String express_id = "";      //快递公司id
    private String keyword = "";    //关键字
    private int page = 1;     //分页
    private String user_id = "";       //用户id
    private String start_time;    //开始时间

    private List<HashMap<String, String>> mList = new ArrayList<>();


    private final int REQUEST_IDENTIFY = 101;     //验证身份是否认证

    private final int REQUEST_WAIT_PRINT = 102;     //待打印数据列表

    private String mail_id;     //订单id
    private String send_name;   //寄件人
    private String goods_name;   //选中的商品价格
    private String goods_weight;   //选中的商品重量
    private String mailing_momey;   //选中的商品运费
    private String content;    //选中的商品备注

    public static WaitMimeographFragment getInstance() {
        WaitMimeographFragment sf = new WaitMimeographFragment();
        return sf;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wait_mimeograph, container, false);
        EventBus.getDefault().register(this);
        initView(view);
        initData();
        initClickListener();
        requestWaitPrint();    //请求待打印数据
        return view;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveEvent(TargetEvent targetEvent) {
        if (targetEvent.getTarget() == 2) {
            recyclerView.refresh();
        }

        if (targetEvent.getTarget() == 202) {
            //表示顶部搜索栏内容
            keyword = targetEvent.getData();
            recyclerView.refresh();
        }

        if (targetEvent.getTarget() == 302) {
            //表示快递公司id
            express_id = targetEvent.getData();
            recyclerView.refresh();
        }
    }

    private void initClickListener() {
        //立刻打印
        mAdapter.setOnPrintatOnceListner(new WaitPrintAdapter.OnPrintatOnceListner() {
            @Override
            public void onPrint(int positon) {
                HashMap<String, String> map = mList.get(positon);
                mail_id = map.get("mail_id");
                send_name = map.get("send_name");
                goods_name = map.get("goods_name");
                goods_weight = map.get("goods_weight");
                mailing_momey = map.get("mailing_momey");
                content = map.get("content");

                //用来和“待收件”界面中的“立刻打印”作区别
                SharedPreferencesUtil.getEditor()
                        .putString("printType","2")
                        .commit();

                //验证身份寄件人是否实名
                identifyPerson(mail_id);
            }
        });
    }

    private void identifyPerson(String mail_id) {
        Request<String> request = NoHttpRequest.identifySealRequest(user_id, mail_id);
        mRequestQueue.add(REQUEST_IDENTIFY, request, onResponseListener);
    }

    private void requestWaitPrint() {
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }

        Request<String> request = NoHttpRequest.getWaitPrintRequest(user_id, express_id, keyword,
                page + "", start_time);

        mRequestQueue.add(REQUEST_WAIT_PRINT, request, onResponseListener);
    }


    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
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
                if ("5001".equals(code)) {
                    handleResult(what, jsonObject);
                } else {
                    if (what == REQUEST_IDENTIFY) {
                        //提示身份验证
                        showPromitDialog();
                    } else {
                        ToastUtil.showShort("查询失败，请重试！");
                    }
                }
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
          //  dialogLoading.cancel();
        }
    };

    private void handleResult(int what, JSONObject jsonObject) throws JSONException {
        if (what == REQUEST_WAIT_PRINT) {
            setData(jsonObject);
        }
        if (what == REQUEST_IDENTIFY) {
            startRecord();    //验证通过，跳转录单
        }
    }

    private void startRecord() {
        Intent intent = new Intent(getActivity(), RecordSheetActivity.class);
        intent.putExtra("waitPrint",true);
        intent.putExtra("mail_id", mail_id);
        intent.putExtra("user_id", user_id);
        intent.putExtra("goods_name", goods_name);
        intent.putExtra("goods_weight", goods_weight);
        intent.putExtra("mailing_momey", mailing_momey);
        intent.putExtra("content", content);
        startActivity(intent);
    }

    private void showPromitDialog() {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle("提示")
                .setMessage("身份未验证！")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), IdentificationActivity.class);
                        intent.putExtra("come_type", false);
                        intent.putExtra("mail_id", mail_id);
                        intent.putExtra("send_name",send_name);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
        try {
            Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
            mAlert.setAccessible(true);
            Object mAlertController = mAlert.get(dialog);
            Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
            mMessage.setAccessible(true);
            TextView mMessageView = (TextView) mMessage.get(mAlertController);
            mMessageView.setTextSize(20);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setData(JSONObject jsonObject) throws JSONException {
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        if (page == 1) {
            mList.clear();
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
            String mail_id = jsonObject1.getString("number");
            String express_id = jsonObject1.getString("express_id");
            String send_name = jsonObject1.getString("send_name");
            String collect_name = jsonObject1.getString("collect_name");
            String create_time = jsonObject1.getString("create_time");
            String express_logo = jsonObject1.getString("express_logo");

            String goods_name = jsonObject1.getString("goods_name");
            String goods_weight = jsonObject1.getString("goods_weight");
            String mailing_momey = jsonObject1.getString("mailing_momey");
            String content = jsonObject1.getString("content");

            HashMap<String, String> map = new HashMap<>();
            map.put("mail_id", mail_id);
            map.put("express_id", express_id);
            map.put("send_name", send_name);
            map.put("create_time", create_time);
            map.put("express_logo", express_logo);
            map.put("collect_name", collect_name);

            map.put("goods_name", goods_name);
            map.put("goods_weight", goods_weight);
            map.put("mailing_momey", mailing_momey);
            map.put("content", content);

            mList.add(map);
        }
        if (mList.size() == 0) {
            tv_no_address.setVisibility(View.VISIBLE);
        } else {
            tv_no_address.setVisibility(View.GONE);
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.rl_wait_print);
        tv_no_address = view.findViewById(R.id.tv_no_address);
        mAdapter = new WaitPrintAdapter(getActivity(), mList);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLoadingListener(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);
    }


    private void initData() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        dialogLoading = new HkDialogLoading(getActivity(), "请稍后...");

        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        start_time = DateUtil.getTadayStartTimeStamp();    //默认当天0点整的时间戳
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                page = 1;
                requestWaitPrint();
                recyclerView.refreshComplete();
            }
        }, 100);
    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                page++;
                requestWaitPrint();
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
