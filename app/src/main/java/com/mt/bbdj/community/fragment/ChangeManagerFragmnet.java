package com.mt.bbdj.community.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.kongzue.dialog.listener.InputDialogOkButtonClickListener;
import com.kongzue.dialog.util.InputInfo;
import com.kongzue.dialog.v2.CustomDialog;
import com.kongzue.dialog.v2.InputDialog;
import com.kongzue.dialog.v2.Notification;
import com.kongzue.dialog.v2.TipDialog;
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseFragment;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.baseconfig.view.MyInputDialog;
import com.mt.bbdj.baseconfig.view.SignView;
import com.mt.bbdj.community.activity.SignatureActivity;
import com.mt.bbdj.community.adapter.ChangeManagerAdapter;
import com.mt.bbdj.community.adapter.RechargeRecodeAdapter;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Signature;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Author : ZSK
 * Date : 2019/3/2
 * Description :  交接
 */
public class ChangeManagerFragmnet extends BaseFragment implements XRecyclerView.LoadingListener {

    private int type = 1;
    private XRecyclerView recyclerView;
    private TextView tv_title, tv_number;
    private Button buttonPanel;
    private ChangeManagerAdapter mAdapter;
    private List<HashMap<String, String>> mData = new ArrayList<>();
    private RequestQueue mRequestQueue;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private ExpressLogoDao mExpressLogoDao;
    private String user_id;
    private String express_id = "";   //快递公司id
    private boolean isFresh = true;

    private Button bt_singture;
    private SignView signView;

    private final int REQUEST_SEND_PICTURE = 100;   //上传签名文件
    private final int REQUEST_CONFIRM_CHANGE = 200;   //确认交接
    private final int REQUEST_ADD_MARK = 300;    //添加备注

    private String filePath = android.os.Environment.getExternalStorageDirectory() + "/bbdj/sign/";
    private String starttime;
    private String endtime;
    private WaitDialog waitDialog;

    public static ChangeManagerFragmnet getInstance(int type) {
        ChangeManagerFragmnet cmf = new ChangeManagerFragmnet();
        cmf.type     = type;
        return cmf;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_layout, container, false);
        EventBus.getDefault().register(this);
        initParams();
        initView(view);
        initListener();
        return view;
    }

    private void initListener() {
        //签名
        bt_singture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectDialog();
            }
        });

        //添加备注
        mAdapter.setOnItemAddmarkClick(new ChangeManagerAdapter.OnItemAddmarkClick() {
            @Override
            public void addMarkClick(int position) {
                showAddMarkDialog(position);
            }
        });
    }

    private void showAddMarkDialog(int position) {
        HashMap<String,String> map = mData.get(position);
        MyInputDialog.show(getActivity(), "提示", "请输入备注信息", new InputDialogOkButtonClickListener() {
            @Override
            public void onClick(Dialog dialog, String inputText) {
                map.put("content",inputText);
                String mailing_id =  map.get("mailing_id");
                // TODO: 2019/4/4 提交之后再显示
                addMarkRequest(inputText,mailing_id);
                dialog.dismiss();
            }
        });

    }

    private void addMarkRequest(String inputText,String mailing_id) {
        if ("".equals(inputText)) {
            return ;
        }
        Request<String> request = NoHttpRequest.addMarkRequest(user_id,mailing_id,inputText);
        mRequestQueue.add(REQUEST_ADD_MARK,request,onResponseListener);
    }

    private void showSelectDialog() {
        if (mData.size() == 0) {
            ToastUtil.showShort("无交接数据！");
            return;
        }
        Intent intent = new Intent(getActivity(), SignatureActivity.class);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(TargetEvent targetEvent) {
        if (targetEvent.getTarget() == 300) {
            HashMap<String,String> data = (HashMap<String,String>)targetEvent.getObject();
            starttime = data.get("starttime");
            endtime = data.get("endtime");
            express_id = data.get("express_id");
            recyclerView.refresh();
        }

        //刷新自身
        if (targetEvent.getTarget() == TargetEvent.REFRESH_ALEADY_CHAGNE) {
            String typeStr = targetEvent.getData();
            type = Integer.parseInt(typeStr);
            recyclerView.refresh();
        }

        //签名图片的路径 发送
        if (targetEvent.getTarget() == TargetEvent.SEND_SIGN_PICTURE) {
            String signPicture = targetEvent.getData();
            //直接上传
            sendChangeRequest(signPicture);
        }
    }
    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "ChangeManagerFragmnet::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String message = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    switch (what) {
                        case REQUEST_CONFIRM_CHANGE:   //确认交接
                            handldChangeResult(jsonObject);
                            break;
                        case REQUEST_ADD_MARK:    //添加备注
                            handleAddmark(jsonObject);
                            break;
                    }
                } else {
                    ToastUtil.showShort(message);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ToastUtil.showShort("当前网络不佳，请重试！");
            }

        }

        @Override
        public void onFailed(int what, Response<String> response) {

        }

        @Override
        public void onFinish(int what) {

        }
    };

    private void handleAddmark(JSONObject jsonObject) {
        mAdapter.notifyDataSetChanged();
    }

    private void handldChangeResult(JSONObject jsonObject) {
        EventBus.getDefault().post(new TargetEvent(TargetEvent.REFRESH_ALEADY_CHAGNE,type+""));
        ToastUtil.showShort("交接成功！");
    }

    private void handleSingntureResult(JSONObject jsonObject) throws JSONException {
        JSONObject dataObject = jsonObject.getJSONObject("data");
        String pictureUrl = dataObject.getString("picurl");
        sendChangeRequest(pictureUrl);  //交接请求
    }

    private void sendChangeRequest(String pictureUrl) {
        StringBuilder sb = new StringBuilder();
        for (HashMap<String, String> map : mData) {
            String mailing_id = map.get("mailing_id");
            sb.append(mailing_id);
            sb.append(",");
        }
        String cartId = sb.toString();
        cartId = cartId.substring(0, cartId.lastIndexOf(","));
        Request<String> request = NoHttpRequest.sendChangeRequest(user_id, pictureUrl, cartId);
        mRequestQueue.add(REQUEST_CONFIRM_CHANGE, request, onResponseListener);
    }

    private void initParams() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();
        mExpressLogoDao = mDaoSession.getExpressLogoDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }

        //默认的是今天
        starttime = DateUtil.getTadayStartTimeZeroStamp();
        endtime = DateUtil.getTadayEndTimeLastStamp();

    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.refresh();
    }

    private void requestData() {
        Request<String> request = NoHttpRequest.getChangeManagerRequest(user_id, express_id, type,starttime,endtime);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "ChangeManagerFragmnet::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (isFresh) {
                        recyclerView.refreshComplete();
                    } else {
                        recyclerView.loadMoreComplete();
                    }

                    if ("5001".equals(code)) {
                        mData.clear();
                        JSONArray list = data.getJSONArray("list");

                        String sum = data.getString("sum");
                        tv_number.setText(sum);
                        for (int i = 0; i < list.length(); i++) {
                            JSONObject jsonObject1 = list.getJSONObject(i);
                            String express_name = jsonObject1.getString("express_name");
                            String waybill_number = jsonObject1.getString("waybill_number");
                            String send_name = jsonObject1.getString("send_name");
                            String mailing_id = jsonObject1.getString("mailing_id");
                            String collect_name = jsonObject1.getString("collect_name");
                            String send_region = jsonObject1.getString("send_region");
                            String collect_region = jsonObject1.getString("collect_region");
                            String time = jsonObject1.getString("time");
                            String goods_weight = jsonObject1.getString("goods_weight");
                            String handover_states = jsonObject1.getString("handover_states");
                            String handover_time = jsonObject1.getString("handover_time");
                            String content = jsonObject1.getString("content");
                            HashMap<String, String> map = new HashMap<>();
                            map.put("express_name", express_name);
                            map.put("waybill_number", waybill_number);
                            map.put("person", send_name + "/" + collect_name);
                            map.put("address", send_region + "/" + collect_region);
                            map.put("time", DateUtil.changeStampToStandrdTime("yyyy-MM-dd", time));
                            map.put("goods_weight", goods_weight);
                            map.put("handover_states", handover_states);
                            map.put("mailing_id", mailing_id);
                            map.put("type", type + "");
                            map.put("handover_time", DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm:ss", handover_time));
                            map.put("content",StringUtil.handleNullResultForString(content));
                            map.put("isShowAddMark","0");    //表示显示添加按钮
                            mData.add(map);
                            map = null;
                        }
                        mAdapter.notifyDataSetChanged();
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

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.rl_change);
        bt_singture = view.findViewById(R.id.bt_singture);
        tv_number = view.findViewById(R.id.tv_number);
        buttonPanel = view.findViewById(R.id.buttonPanel);
        tv_title = view.findViewById(R.id.tv_title);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        //设置线性布局 Creates a vertical LinearLayoutManager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        //    recyclerView.addItemDecoration(new MyDecoration(getActivity(), LinearLayoutManager.VERTICAL, Color.parseColor("#e9e9e9"), 1));
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setLoadingListener(this);
        mAdapter = new ChangeManagerAdapter(mData);
        recyclerView.setAdapter(mAdapter);

        bt_singture.setVisibility(type == 2 ? View.GONE : View.VISIBLE);
        tv_title.setText(type == 2 ? "交接数量:" : "待交接数量:");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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
