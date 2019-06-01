package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.zxing.Result;
import com.king.zxing.CaptureActivity;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.db.ExpressLogo;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.WaillMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.db.gen.WaillMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.Entermodel;
import com.mt.bbdj.baseconfig.model.PrintTagModel;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SoundHelper;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.baseconfig.view.MyPopuwindow;
import com.mt.bbdj.community.adapter.EnterManagerAdapter;
import com.mt.bbdj.community.adapter.MyOrderAdapter;
import com.mt.bbdj.community.adapter.SimpleStringAdapter;
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
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EnterManagerActivity extends CaptureActivity {

    private TextView tvPackageCode;     //提货码
    private TextView tvWailNumber;     //运单号
    private TextView expressSelect;    //快递公司选择
    private RecyclerView recyclerView;
    private RelativeLayout ivBack;    //返回
    private TextView tv_enter_number;     //入库数
    private TextView tv_enter;
    private List<HashMap<String, String>> mList = new ArrayList<>();
    private List<HashMap<String, String>> mPrintList = new ArrayList<>();
    private List<String> mTempList = new ArrayList<>();//临时数据

    private boolean isContinuousScan = true;
    private EnterManagerAdapter mAdapter;
    private String user_id;
    private RequestQueue mRequestQueue;
    private int packageCode = 1060204;

    private MyPopuwindow popupWindow;

    private List<HashMap<String, String>> mFastData = new ArrayList<>();    //快递公司
    private ExpressLogoDao mExpressLogoDao;
    private String express_id;

    private final int CHECK_WAY_BILL_STATE = 100;    //检测
    private final int ENTER_RECORDE_REQUEST = 200;    //入库
    private String resultCode;
    private String expressName;
    private PrintTagModel printTagModel = new PrintTagModel();

    private int tagNumber = 1;
    private HkDialogLoading dialogLoading;
    private WaillMessageDao mWaillMessageDao;

    @Override
    public int getLayoutId() {
        return R.layout.activity_enter_manager;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getBeepManager().setPlayBeep(true);
        // getBeepManager().setVibrate(true);
        initParams();
        initView();
        initListener();
        initSelectPop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tv_enter_number.setText("(" + mList.size() + "/30)");
        dialogLoading.dismiss();
    }

    private void initListener() {
        //删除
        mAdapter.setDeleteClickListener(new EnterManagerAdapter.onDeleteClickListener() {
            @Override
            public void onDelete(int position) {
                HashMap<String, String> map = mList.get(position);
                String resultCode = map.get("number");
                mWaillMessageDao.queryBuilder().where(WaillMessageDao.Properties.WailNumber.eq(resultCode)).buildDelete();
                mList.remove(position);
                mTempList.remove(resultCode);
                mAdapter.notifyDataSetChanged();
                tv_enter_number.setText("(" + mList.size() + "/30)");
            }
        });

        //选择对话框
        expressSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList.size() != 0) {
                    ToastUtil.showShort("请先入库后再切换快递公司！");
                    return;
                }
                selectExpressDialog(v);
            }
        });

        //返回
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //入库
        tv_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterRecorde();    //入库请求
            }
        });
    }

    private void enterRecorde() {
        if (mList.size() == 0) {
            return;
        }
        String data_json = getEnterrecordData();
        Request<String> request = NoHttpRequest.enterRecordeRequest(user_id, express_id, data_json);
        mRequestQueue.add(ENTER_RECORDE_REQUEST, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                dialogLoading.show();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "EnterManagerActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    enterRecorderResult(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialogLoading.dismiss();
                }
                dialogLoading.dismiss();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                dialogLoading.dismiss();
            }

            @Override
            public void onFinish(int what) {
                dialogLoading.dismiss();
            }
        });
    }

    private String getEnterrecordData() {
        int dataSize = mList.size();
        StringBuilder sb = new StringBuilder();
        List<Entermodel> list = new ArrayList<>();
        for (HashMap<String, String> data : mList) {
            sb.append(data.get("package_code"));
            sb.append("|");
            sb.append(data.get("mobile"));
            sb.append("|");
            sb.append(data.get("name"));
            sb.append("|");
            sb.append(data.get("wail_number"));
            sb.append(",");
           /* Entermodel entermodel = new Entermodel();
            entermodel.setCode(data.get("package_code"));
            entermodel.setMobile(data.get("mobile"));
            entermodel.setName(data.get("name"));
            entermodel.setNumber(data.get("wail_number"));
            list.add(entermodel);
            entermodel = null;*/
        }
        String sbb = sb.toString();
        String result = sbb.substring(0, sbb.length() - 1);
       /*  Gson gson = new Gson();
       // String json = gson.toJson(list.toArray(new Entermodel[list.size()]), Entermodel[].class);
        String json = gson.toJson(result);*/
        return result;
    }


    private OnResponseListener<String> mOnresponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            // dialogLoading.show();
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "EnterManagerActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                handleResultForEnter(what, jsonObject);    //处理结果
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

    private void handleResultForEnter(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case CHECK_WAY_BILL_STATE:     //检测运单号
                checkWaybillStateResult(jsonObject);
                break;
        }
    }

    private void enterRecorderResult(JSONObject jsonObject) throws JSONException {
        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();
        if ("5001".equals(code)) {
            mList.clear();
            tagNumber = 1;
            mAdapter.notifyDataSetChanged();
            JSONArray data = jsonObject.getJSONArray("data");
            printNumber(data);    //打印取件码
        }
        ToastUtil.showShort(msg);
    }


    private void printNumber(JSONArray data) throws JSONException {
        mWaillMessageDao.deleteAll();    //删除数据库中临时数据
        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            String code = jsonObject.getString("code");
            String qrcode = jsonObject.getString("qrcode");
            HashMap<String, String> map = new HashMap<>();
            map.put("code", code);
            map.put("qrcode", qrcode);
            mPrintList.add(map);
            map = null;
        }
        dialogLoading.dismiss();
        printTagModel.setData(mPrintList);
        Intent intent = new Intent(EnterManagerActivity.this, BluetoothNumberActivity.class);
        intent.putExtra("printData", printTagModel);
        startActivity(intent);
    }

    private void checkWaybillStateResult(JSONObject jsonObject) throws JSONException {
        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();
        if ("5001".equals(code)) {
            JSONObject dataArray = jsonObject.getJSONObject("data");
            String package_code = dataArray.getString("code");
            String mobile = dataArray.getString("mobile");
            String name = dataArray.getString("name");
            String resultCode = dataArray.getString("number");

            HashMap<String, String> map = new HashMap<>();
            int codeTag = IntegerUtil.getStringChangeToNumber(package_code);    //最新的数据库的提货码
            codeTag = codeTag + tagNumber;
            String currentData = DateUtil.getCurrentDay();
            String effectCode = StringUtil.getEffectCode(codeTag);
            String result = currentData + effectCode;
            LogUtil.d("tagNumber::", "codeTag::" + codeTag + "  effectCode::" + effectCode + "  tagNumber::" + tagNumber + "");

            map.put("package_code", result);
            map.put("wail_number", resultCode);
            map.put("express_name", expressName);
            map.put("mobile", mobile);
            map.put("name", name);
            mList.add(0, map);
            map = null;
            tvWailNumber.setText(resultCode);
            tvPackageCode.setText(result);
            mAdapter.notifyDataSetChanged();
            tagNumber++;
        } else {
            JSONObject dataArray = jsonObject.getJSONObject("data");
            String resultCode = dataArray.getString("number");
            mTempList.remove(resultCode);
            ToastUtil.showShort(msg);
        }

        tv_enter_number.setText("(" + mList.size() + "/30)");
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
        mWaillMessageDao = daoSession.getWaillMessageDao();
        mExpressLogoDao = daoSession.getExpressLogoDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        mRequestQueue = NoHttp.newRequestQueue();
        printTagModel = new PrintTagModel();
    }

    private void initView() {
        tvPackageCode = findViewById(R.id.tv_package_number);
        tvWailNumber = findViewById(R.id.tv_yundan);
        recyclerView = findViewById(R.id.rl_order_list);
        expressSelect = findViewById(R.id.tv_expressage_select);
        tv_enter_number = findViewById(R.id.tv_enter_number);
        tv_enter = findViewById(R.id.tv_enter);
        ivBack = findViewById(R.id.iv_back);
        initRecyclerView();    //初始化列表

        dialogLoading = new HkDialogLoading(EnterManagerActivity.this, "提交中...");
    }

    private void initRecyclerView() {
        recyclerView.setFocusable(false);
        //initTemparayData();   //模拟数据
        List<WaillMessage> dataList = mWaillMessageDao.queryBuilder().list();
        if (dataList != null && dataList.size() != 0) {
            for (WaillMessage waillMessage : dataList) {
                HashMap<String,String> map = new HashMap<>();
                map.put("package_code", waillMessage.getTagCode());
                map.put("wail_number", waillMessage.getWailNumber());
                map.put("express_name", waillMessage.getExpressName());
                map.put("mobile", waillMessage.getMobile());
                map.put("name", waillMessage.expressName);
                mList.add(map);
                map = null;
                tagNumber = waillMessage.getTagNumber();
            }
        }
        mAdapter = new EnterManagerAdapter(mList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL, Color.parseColor("#e9e9e9"), 1));
        recyclerView.setAdapter(mAdapter);
    }

    private void initRecycler(RecyclerView fastList) {
        mFastData.clear();
        //查询快递公司的信息
        List<ExpressLogo> expressLogoList = mExpressLogoDao.queryBuilder()
                .where(ExpressLogoDao.Properties.States.eq(1)).list();

        if (expressLogoList != null && expressLogoList.size() != 0) {
            for (ExpressLogo expressLogo : expressLogoList) {
                HashMap<String, String> map1 = new HashMap<>();
                map1.put("express", expressLogo.getExpress_name());
                map1.put("express_id", expressLogo.getExpress_id());
                mFastData.add(map1);
                map1 = null;
            }
        }

        SimpleStringAdapter goodsAdapter = new SimpleStringAdapter(this, mFastData);
        goodsAdapter.setOnItemClickListener(new SimpleStringAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                //选中的快递公司id
                express_id = mFastData.get(position).get("express_id");
                expressName = mFastData.get(position).get("express");
                //sendExpressid(express_id);    //向对应的界面发送快递公司消息
                expressSelect.setText(mFastData.get(position).get("express"));
                popupWindow.dismiss();
            }
        });

        if (mFastData.size() != 0) {
            expressName = mFastData.get(0).get("express");
            express_id = mFastData.get(0).get("express_id");
        }

        fastList.setAdapter(goodsAdapter);
        fastList.addItemDecoration(new MarginDecoration(this));
        fastList.setLayoutManager(new LinearLayoutManager(this));
        goodsAdapter.notifyDataSetChanged();
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

    private boolean isEffective = true;

    @Override
    public void onResult(Result result) {
        super.onResult(result);

        if (isContinuousScan) {//连续扫码时，直接弹出结果
            if (result == null || "".equals(result.getText())) {
                return;
            }

            if (mList.size() == 30) {
                ToastUtil.showShort("扫描已至30件，请先入库！");
                return;
            }
            //用于修正内部识别的bug
            String resultCode = handleResult(result);

            if (isContain(resultCode)) {   //判断是否重复
                SoundHelper.getInstance().playNotifiRepeatSound();
            } else {
                mTempList.add(resultCode);
                checkWaybillState(resultCode,"");    //检测运单号的状态
            }
        }
    }

    private void checkWaybillState(String number,String picturl) {
        if ("".equals(express_id)) {
            ToastUtil.showShort("请选择快递公司");
            return;
        }
        Request<String> request = NoHttpRequest.checkWaybillRequest(user_id, express_id, number,picturl);
        mRequestQueue.add(CHECK_WAY_BILL_STATE, request, mOnresponseListener);
    }


    private void selectExpressDialog(View view) {
        popupWindow.showAsDropDown(view);
    }

    private void initSelectPop() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            View selectView = getLayoutInflater().inflate(R.layout.fast_layout_1, null);
            RecyclerView fastList = selectView.findViewById(R.id.tl_fast_list);
            initRecycler(fastList);
            popupWindow = new MyPopuwindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            //设置动画
            popupWindow.setAnimationStyle(R.style.popup_window_anim);
            //设置背景颜色
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
            popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popupWindow.setTouchable(true); // 设置popupwindow可点击
            popupWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popupWindow.setFocusable(true); // 获取焦点
            selectView.findViewById(R.id.layout_left_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWaillMessageDao.deleteAll();
        List<WaillMessage> lists = new ArrayList<>();
        for (HashMap<String, String> map : mList) {
            String package_code = map.get("package_code");
            String wail_number = map.get("wail_number");
            String mobile = map.get("mobile");
            String express_name = map.get("express_name");
            String name = map.get("name");
            WaillMessage waillMessage = new WaillMessage();
            waillMessage.setName(name);
            waillMessage.setExpressName(express_name);
            waillMessage.setTagCode(package_code);
            waillMessage.setMobile(mobile);
            waillMessage.setWailNumber(wail_number);
            waillMessage.setTagNumber(tagNumber);
            lists.add(waillMessage);
        }
        mWaillMessageDao.saveInTx(lists);
    }
}
