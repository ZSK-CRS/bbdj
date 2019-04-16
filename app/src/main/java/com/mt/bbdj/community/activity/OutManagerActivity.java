package com.mt.bbdj.community.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.Result;
import com.king.zxing.CaptureActivity;
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.db.ExpressLogo;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SoundHelper;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.baseconfig.view.MyPopuwindow;
import com.mt.bbdj.community.adapter.EnterManagerAdapter;
import com.mt.bbdj.community.adapter.SimpleStringAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static cn.jiguang.net.HttpUtils.ENCODING_UTF_8;


public class OutManagerActivity extends CaptureActivity {

    private TextView tvPackageCode;     //提货码
    private TextView tvWailNumber;     //运单号
    private TextView expressSelect;    //快递公司选择
    private RecyclerView recyclerView;
    private RelativeLayout ivBack;    //返回
    private TextView tv_out_number;
    private TextView tvOut;    //出库
    private List<HashMap<String, String>> mList = new ArrayList<>();

    private boolean isContinuousScan = true;
    private EnterManagerAdapter mAdapter;
    private String user_id;
    private RequestQueue mRequestQueue;
    private int packageCode = 1060204;
    private WaitDialog dialogLoading;
    private List<String> mTempList = new ArrayList<>();//临时数据

    private MyPopuwindow popupWindow;

    private List<HashMap<String, String>> mFastData = new ArrayList<>();    //快递公司
    private ExpressLogoDao mExpressLogoDao;
    private String express_id;

    private final int CHECK_WAY_BILL_STATE = 300;    //检测运单号的状态
    private final int OUT_WAY_BILL_REQUEST = 400;    //出库

    @Override
    public int getLayoutId() {
        return R.layout.activity_out_manager;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBeepManager().setPlayBeep(true);
        // getBeepManager().setVibrate(true);
        initParams();
        initView();
        initListener();
    }

    private void initListener() {
        //删除
        mAdapter.setDeleteClickListener(new EnterManagerAdapter.onDeleteClickListener() {
            @Override
            public void onDelete(int position) {
                HashMap<String,String> map = mList.get(position);
                String resultCode = map.get("wail_number");
                mList.remove(position);
                mTempList.remove(resultCode);
                tv_out_number.setText("(" + mList.size() + ")");
                mAdapter.notifyDataSetChanged();
            }
        });

        //返回
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList.size() == 0) {
                    ToastUtil.showShort("没有出库数据！");
                    return;
                }
                outOfrepertory();
            }
        });

    }

    private void outOfrepertory() {
        StringBuilder sb = new StringBuilder();
        for (HashMap<String,String> map : mList) {
            String pie_id = map.get("pie_id");
            sb.append(pie_id);
            sb.append(",");
        }
        String result = sb.toString();
        String effectionResult = result.substring(0,result.length()-1);
        Request<String> request = NoHttpRequest.outOfRepertoryRequest(user_id, effectionResult);
        mRequestQueue.add(OUT_WAY_BILL_REQUEST, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
               // dialogLoading = WaitDialog.show(OutManagerActivity.this, "提交中...").setCanCancel(true);

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "OutManagerActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    outofRepertoryResult(jsonObject);    //处理结果
                } catch (JSONException e) {
                    e.printStackTrace();
                }
               // dialogLoading.doDismiss();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
              //  dialogLoading.doDismiss();
            }

            @Override
            public void onFinish(int what) {
              //  dialogLoading.doDismiss();
            }
        });
    }


    /**
     * 是否连续扫码，如果想支持连续扫码，则将此方法返回{@code true}
     *
     * @return 默认返回 false
     */
    @Override
    public boolean isContinuousScan() {
        return isContinuousScan;
    }

    /**
     * 接收扫码结果，想支持连扫时，可将{@link #isContinuousScan()}返回为{@code true}并重写此方法
     * 如果{@link #isContinuousScan()}支持连扫，则默认重启扫码和解码器；当连扫逻辑太复杂时，
     * 请将{@link #isAutoRestartPreviewAndDecode()}返回为{@code false}，并手动调用{@link #restartPreviewAndDecode()}
     *
     * @param result 扫码结果
     */
    @Override
    public void onResult(Result result) {
        super.onResult(result);

        if (isContinuousScan) {//连续扫码时，直接弹出结果

            if (result == null || "".equals(result.getText())) {
                return;
            }

            String resultCode = handleResult(result);    //用于修正内部识别的bug

            if (isContain(resultCode)) {   //判断是否重复
                SoundHelper.getInstance().playNotifiRepeatSound();
            } else {
                mTempList.add(resultCode);
                checkWaybillState(resultCode);    //检测运单号的状态
            }
        }
    }

    private void checkWaybillState(String number) {
        Request<String> request = NoHttpRequest.checkOutWailnumberStateRequest(user_id, number);
        mRequestQueue.add(CHECK_WAY_BILL_STATE, request, mOnresponseListener);
    }


    private OnResponseListener<String> mOnresponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "EnterManagerActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                handleResultForOut(what, jsonObject);    //处理结果
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
    };

    private void handleResultForOut(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case CHECK_WAY_BILL_STATE:    //运单状态
                CheckWaybillResult(jsonObject);
                break;

        }
    }

    private void outofRepertoryResult(JSONObject jsonObject) throws JSONException {
        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();

        if ("5001".equals(code)) {
            mList.clear();
            mAdapter.notifyDataSetChanged();
        }
        ToastUtil.showShort(msg);
        tv_out_number.setText("(" + mList.size() + ")");
    }

    private void CheckWaybillResult(JSONObject jsonObject) throws JSONException {

        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();

        if ("5001".equals(code)) {
            JSONObject dataObj = jsonObject.getJSONObject("data");
            String package_code = dataObj.getString("code");
            String express = dataObj.getString("express");
            String pie_id = dataObj.getString("pie_id");
            String number = dataObj.getString("number");

            HashMap<String, String> map = new HashMap<>();
            map.put("package_code", package_code);
            map.put("wail_number", number);
            map.put("express_name", express);
            map.put("pie_id", pie_id);
            mList.add(0, map);
            map = null;
            tvWailNumber.setText(number);
            tvPackageCode.setText(package_code);
            mAdapter.notifyDataSetChanged();
        } else {
            JSONObject dataArray = jsonObject.getJSONObject("data");
            String resultCode = dataArray.getString("number");
            mTempList.remove(resultCode);
            ToastUtil.showShort(msg);
        }
        tv_out_number.setText("(" + mList.size() + ")");
    }


    private synchronized boolean isContain(String resultCode) {
        for (String data : mTempList) {
            if (resultCode.equals(data)) {
                return true;
            }
        }
        return false;
    }

    private String handleResult(Result result) {
        String resultStr = result.getText();
        int beganIndex = resultStr.lastIndexOf("-");
        String effectiveResult = resultStr.substring(beganIndex + 1);
        return effectiveResult;
    }


    /**
     * 是否自动重启扫码和解码器，当支持连扫时才起作用。
     *
     * @return 默认返回 true
     */
    @Override
    public boolean isAutoRestartPreviewAndDecode() {
        return super.isAutoRestartPreviewAndDecode();
    }

    private void initParams() {
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = daoSession.getUserBaseMessageDao();
        mExpressLogoDao = daoSession.getExpressLogoDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        mRequestQueue = NoHttp.newRequestQueue();
    }

    private void initView() {
        tvPackageCode = findViewById(R.id.tv_package_number);
        tvWailNumber = findViewById(R.id.tv_yundan);
        recyclerView = findViewById(R.id.rl_order_list);
        expressSelect = findViewById(R.id.tv_expressage_select);
        ivBack = findViewById(R.id.iv_back);
        tv_out_number = findViewById(R.id.tv_out_number);
        tvOut = findViewById(R.id.tv_out);
        initRecyclerView();    //初始化列表
    }

    private void initRecyclerView() {
        recyclerView.setFocusable(false);
        //initTemparayData();   //模拟数据
        mAdapter = new EnterManagerAdapter(mList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL, Color.parseColor("#e9e9e9"), 1));
        recyclerView.setAdapter(mAdapter);
    }

}
