package com.mt.bbdj.community.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.kongzue.dialog.v2.MessageDialog;
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
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.community.activity.CauseForcannelOrderActivity;
import com.mt.bbdj.community.activity.ChangeMessageActivity;
import com.mt.bbdj.community.activity.IdentificationActivity;
import com.mt.bbdj.community.activity.MailingdetailActivity;
import com.mt.bbdj.community.activity.RecordSheetActivity;
import com.mt.bbdj.community.adapter.MessagePannelAdapter;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author : ZSK
 * Date : 2019/1/8
 * Description :  待收件界面
 */
public class WaitCollectFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    @BindView(R.id.rl_wait_collect)
    XRecyclerView rlWaitCollect;
    Unbinder unbinder;

    private TextView tv_no_address;


    private List<HashMap<String, String>> mList = new ArrayList<>();
    private WaitCollectAdapter mAdapter;
    private RequestQueue mRequestQueue;
    private HkDialogLoading dialogLoading;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;

    private String express_id = "";      //快递公司id
    private String keyword = "";    //关键字
    private int page = 1;     //分页
    private String user_id = "";       //用户id
    private String mail_id;   //订单id
    private String send_name;   //寄件人姓名

    private String goods_name;   //选中的商品价格
    private String goods_weight;   //选中的商品重量
    private String mailing_momey;   //选中的商品运费
    private String content;    //选中的商品备注

    private boolean waitPrint = false;

    private final int REQUEST_GET_WAITCOLLECT = 1;    //获取待收件的数据
    //private final int REQUEST_GET_WAITCOLLECT = 1;    //获取待收件的数据

    private final int REQUEST_IDENTIFY = 2;    //验证是否实名

    private boolean isGetData = false;


    public static WaitCollectFragment getInstance() {
        WaitCollectFragment sf = new WaitCollectFragment();
        return sf;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_wait_collect, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        initData();
        initView(view);   //初始化视图
        initClickListener();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        rlWaitCollect.refresh();
    }

    private void initClickListener() {
        //点击事件
        mAdapter.setOnItemClickListener(new WaitCollectAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                HashMap<String, String> map = mList.get(position);
                mail_id = map.get("mail_id");
                Intent intent = new Intent(getActivity(), MailingdetailActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("mail_id", mail_id);
                startActivity(intent);
            }
        });

        //取消订单
        mAdapter.setOnCannelOrderClickListener(new WaitCollectAdapter.OnCannelOrderClickListener() {
            @Override
            public void OnCannelOrderClick(int position) {
                HashMap<String, String> map = mList.get(position);
                mail_id = map.get("mail_id");
                Intent intent = new Intent(getActivity(), CauseForcannelOrderActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("mail_id", mail_id);
                startActivity(intent);
            }
        });

        //先存后打
        mAdapter.setOnSaveFirstClickListener(new WaitCollectAdapter.OnSaveFirstClickListener() {
            @Override
            public void OnClick(int position) {
                HashMap<String, String> map = mList.get(position);
                waitPrint = false;
                mail_id = map.get("mail_id");
                send_name = map.get("send_name");
                goods_name = map.get("goods_name");
                goods_weight = map.get("goods_weight");
                mailing_momey = map.get("mailing_momey");
                content = map.get("content");
                //验证身份寄件人是否实名
                identifyPerson(mail_id);
            }
        });

        //立刻打印
        mAdapter.setOnPrintatOnceClickListener(new WaitCollectAdapter.OnPrintatOnceClickListener() {
            @Override
            public void OnPrint(int position) {
                waitPrint = true;
                HashMap<String, String> map = mList.get(position);
                mail_id = map.get("mail_id");
                send_name = map.get("send_name");
                goods_name = map.get("goods_name");
                goods_weight = map.get("goods_weight");
                mailing_momey = map.get("mailing_momey");
                content = map.get("content");

                //用来和“待打印”界面作区别
                SharedPreferencesUtil.getEditor()
                        .putString("printType", "1")
                        .commit();

                //验证身份寄件人是否实名
                identifyPerson(mail_id);
            }
        });
    }

    private void identifyPerson(String mail_id) {
        Request<String> request = NoHttpRequest.identifySealRequest(user_id, mail_id);
        mRequestQueue.add(REQUEST_IDENTIFY, request, mresponseListener);
    }

    private void requestWaitCollectData() {
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        Request<String> request = NoHttpRequest.getWaitCollecRequest(user_id, express_id, keyword, page + "");
        mRequestQueue.add(REQUEST_GET_WAITCOLLECT, request, mresponseListener);
    }

    private OnResponseListener<String> mresponseListener = new OnResponseListener<String>() {
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
            //   dialogLoading.cancel();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            // dialogLoading.cancel();
        }

        @Override
        public void onFinish(int what) {
            //   dialogLoading.cancel();
        }
    };


    private void handleResult(int what, JSONObject jsonArray) throws JSONException {
        switch (what) {
            case REQUEST_GET_WAITCOLLECT:   //获取待收件数据
                setData(jsonArray);
                break;
            case REQUEST_IDENTIFY:
                startRecord();    //验证通过，跳转录单
                break;
        }
    }

    private void startRecord() {
        Intent intent = new Intent(getActivity(), RecordSheetActivity.class);
        intent.putExtra("mail_id", mail_id);
        intent.putExtra("user_id", user_id);

        intent.putExtra("goods_name", goods_name);
        intent.putExtra("goods_weight", goods_weight);
        intent.putExtra("mailing_momey", mailing_momey);
        intent.putExtra("content", content);
        intent.putExtra("waitPrint", waitPrint);
        startActivity(intent);
    }

    private void setData(JSONObject jsonObject) throws JSONException {
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        if (isFresh) {
            rlWaitCollect.refreshComplete();
        } else {
            rlWaitCollect.loadMoreComplete();
        }

        if (page == 1) {
            mList.clear();
        }
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject1 = (JSONObject) jsonArray.get(i);
            String mail_id = jsonObject1.getString("mail_id");
            String express_id = jsonObject1.getString("express_id");
            String send_name = jsonObject1.getString("send_name");
            String send_phone = jsonObject1.getString("send_phone");
            String send_region = jsonObject1.getString("send_region");
            String send_address = jsonObject1.getString("send_address");
            String collect_name = jsonObject1.getString("collect_name");
            String collect_phone = jsonObject1.getString("collect_phone");
            String collect_region = jsonObject1.getString("collect_region");
            String collect_address = jsonObject1.getString("collect_address");
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
            map.put("send_phone", send_phone);
            map.put("send_raddress", send_region + send_address);
            map.put("collect_name", collect_name);
            map.put("collect_phone", collect_phone);
            map.put("collect_address", collect_region + collect_address);
            map.put("create_time", create_time);
            map.put("express_logo", express_logo);

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

    private void initData() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        dialogLoading = new HkDialogLoading(getActivity(), "请稍后...");

        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();
    }

    private void initView(View view) {
        tv_no_address = view.findViewById(R.id.tv_no_address);
        mList = new ArrayList<>();
        mAdapter = new WaitCollectAdapter(getActivity(), mList);
        rlWaitCollect.setFocusable(false);
        rlWaitCollect.setNestedScrollingEnabled(false);
        rlWaitCollect.setLoadingListener(this);
        rlWaitCollect.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        rlWaitCollect.setAdapter(mAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveEvent(TargetEvent targetEvent) {
        //打印完成、取消订单、时候刷新一下
        if (targetEvent.getTarget() == 0) {
           // rlWaitCollect.refresh();
        }

        if (targetEvent.getTarget() == 200) {
            //表示的是顶部的传值
            keyword = targetEvent.getData();
            rlWaitCollect.refresh();
        }

        //表示的是快递公司的筛选
        if (targetEvent.getTarget() == 300) {
            express_id = targetEvent.getData();
            rlWaitCollect.refresh();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
    }

    private void showPromitDialog() {
        MessageDialog.show(getActivity(), "提示", "身份未验证！", "知道了",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), IdentificationActivity.class);
                        intent.putExtra("come_type", false);
                        intent.putExtra("mail_id", mail_id);
                        intent.putExtra("send_name", send_name);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
    }

    @Override
    public void onRefresh() {
        isFresh = true;
        page = 1;
        requestWaitCollectData();
    }

    private boolean isFresh = true;    //

    @Override
    public void onLoadMore() {
        isFresh = false;
        page++;
        requestWaitCollectData();
    }

}
