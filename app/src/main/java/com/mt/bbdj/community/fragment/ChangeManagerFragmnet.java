package com.mt.bbdj.community.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
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
import com.mt.bbdj.baseconfig.view.SignView;
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
    private TextView tv_title,tv_number;
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
    private PopupWindow popupWindow;
    private View selectView;
    private SignView signView;

    private final int REQUEST_SEND_PICTURE = 100;   //上传签名文件
    private final int REQUEST_CONFIRM_CHANGE = 200;   //确认交接

    private String filePath = android.os.Environment.getExternalStorageDirectory() + "/bbdj/sign/";

    public static ChangeManagerFragmnet getInstance(int type) {
        ChangeManagerFragmnet cmf = new ChangeManagerFragmnet();
        cmf.type = type;
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
        initPopuStyle();
        return view;
    }

    private void initListener() {
        bt_singture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSelectDialog();
            }
        });
    }

    private void showSelectDialog() {
        if (mData.size() == 0) {
            ToastUtil.showShort("无交接数据！");
            return;
        }
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(buttonPanel, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(TargetEvent targetEvent) {
        if (targetEvent.getTarget() == 300 || targetEvent.getTarget() == 301) {
            express_id = targetEvent.getData();
            recyclerView.refresh();
        }
    }

    private void initPopuStyle() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            selectView = getLayoutInflater().inflate(R.layout.view_signutre, null);
            signView = (SignView) selectView.findViewById(R.id.et_signview);
            TextView cannel = selectView.findViewById(R.id.tv_singture);
            TextView tv_signture_yes = selectView.findViewById(R.id.tv_signture_yes);

            popupWindow = new PopupWindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //设置动画
            popupWindow.setAnimationStyle(R.style.popup_window_anim);
            //设置背景颜色
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
            popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popupWindow.setTouchable(true); // 设置popupwindow可点击
            popupWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popupWindow.setFocusable(true); // 获取焦点
           /* LinearLayout layout_pop_close = (LinearLayout) selectView.findViewById(R.id.layout_left_close);
            layout_pop_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });*/

            cannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });


            tv_signture_yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commitData();      //交接
                }
            });

        }
    }

    private void commitData() {
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        //保存图片
        String filePath = saveSign(signView.getCachebBitmap());
        uploadPicture(filePath);
    }

    private String saveSign(Bitmap cachebBitmap) {
        ByteArrayOutputStream baos = null;
        String _path = null;
        String randomStr = UUID.randomUUID().toString();
        try {
            _path = filePath + randomStr + ".png";
            baos = new ByteArrayOutputStream();
            cachebBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] photoBytes = baos.toByteArray();
            if (photoBytes != null) {
                new FileOutputStream(new File(_path)).write(photoBytes);
            }
        } catch (IOException e) {
            Log.e("handSignPicturePath_e", e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return _path;
    }

    private void uploadPicture(String filePath) {
        if (!new File(filePath).exists()) {
            ToastUtil.showShort("文件不存在，请重拍！");
            return;
        }
        Request<String> request = NoHttpRequest.commitPictureRequest(filePath);
        mRequestQueue.add(REQUEST_SEND_PICTURE, request, onResponseListener);
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
                        case REQUEST_SEND_PICTURE:   //签名文件
                            handleSingntureResult(jsonObject);
                            break;
                        case REQUEST_CONFIRM_CHANGE:   //确认交接
                            handldChangeResult(jsonObject);
                            break;
                    }

                } else {
                    ToastUtil.showShort("账号或者密码错误，请重试！");
                }
            } catch (JSONException e) {
                e.printStackTrace();
                ToastUtil.showShort("上传失败，请重试！");
            }

        }

        @Override
        public void onFailed(int what, Response<String> response) {

        }

        @Override
        public void onFinish(int what) {

        }
    };

    private void handldChangeResult(JSONObject jsonObject) {
        popupWindow.dismiss();
        recyclerView.refresh();
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
    }

    @Override
    public void onResume() {
        super.onResume();
        requestData();
    }

    private void requestData() {

        Request<String> request = NoHttpRequest.getChangeManagerRequest(user_id, express_id, type);
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
        tv_title.setText(type == 2?"交接数量:":"待交接数量:");

     /*   LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(getActivity()) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
        recyclerView.setLayoutManager(mLinearLayoutManager);*/
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
