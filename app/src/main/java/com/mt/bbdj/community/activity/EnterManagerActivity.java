package com.mt.bbdj.community.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.king.zxing.CaptureActivity;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.ExpressLogo;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SoundHelper;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.baseconfig.view.MyDecoration;
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
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EnterManagerActivity extends CaptureActivity {

    private TextView tvPackageCode;     //提货码
    private TextView tvWailNumber;     //运单号
    private TextView expressSelect;    //快递公司选择
    private RecyclerView recyclerView;
    private List<HashMap<String, String>> mList = new ArrayList<>();

    private boolean isContinuousScan = true;
    private EnterManagerAdapter mAdapter;
    private String user_id;
    private RequestQueue mRequestQueue;
    private int packageCode = 1060204;

    private PopupWindow popupWindow;

    private List<HashMap<String, String>> mFastData = new ArrayList<>();    //快递公司
    private ExpressLogoDao mExpressLogoDao;

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

    private void initListener() {
        //删除
        mAdapter.setDeleteClickListener(new EnterManagerAdapter.onDeleteClickListener() {
            @Override
            public void onDelete(int position) {
                mList.remove(position);
                mAdapter.notifyDataSetChanged();
            }
        });

        expressSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectExpressDialog(v);
            }
        });
    }

    private void selectExpressDialog(View view) {
        if (Build.VERSION.SDK_INT < 24) {
            popupWindow.showAsDropDown(view);
        } else {
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            popupWindow.showAtLocation(view, Gravity.RIGHT | Gravity.TOP, 0, y + view.getHeight());
        }
    }

    private void initSelectPop() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            View selectView = getLayoutInflater().inflate(R.layout.fast_layout_1, null);
            RecyclerView fastList = selectView.findViewById(R.id.tl_fast_list);
            initRecycler(fastList);
            popupWindow = new PopupWindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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
                String express_id = mFastData.get(position).get("express_id");
                // sendExpressid(express_id);    //向对应的界面发送快递公司消息
                expressSelect.setText(mFastData.get(position).get("express"));
                popupWindow.dismiss();
            }
        });

        fastList.setAdapter(goodsAdapter);
        fastList.addItemDecoration(new MarginDecoration(this));
        fastList.setLayoutManager(new LinearLayoutManager(this));
        goodsAdapter.notifyDataSetChanged();
    }

    private void requestEnter() {
        Request<String> request = NoHttpRequest.getMyOrderList(user_id, 1);
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
                    if ("5001".equals(code)) {

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
            // TODO: 2019/3/7 请求接口
            // requestEnter();
            if (result == null || "".equals(result.getText())) {
                return ;
            }
            String resultCode = handleResult(result);    //用于修正内部识别的bug

            if (isContain(resultCode)) {   //判断是否重复
                SoundHelper.getInstance().playNotifiRepeatSound();
                return;
            } else {   //入库成功
                SoundHelper.getInstance().playNotifiSuccessSound();
            }

            packageCode++;
            HashMap<String, String> map = new HashMap<>();
            map.put("package_code", packageCode + "");
            map.put("wail_number",resultCode);
            map.put("express_name","中通快递");
            mList.add(0,map);
            map = null;

            tvWailNumber.setText(resultCode);
            tvPackageCode.setText(packageCode+"");
            mAdapter.notifyDataSetChanged();
        }
    }

    private boolean isContain(String resultCode) {
        for (HashMap<String,String> map : mList) {
            String wail_number = map.get("wail_number");
            if (resultCode.equals(wail_number)) {
                return true;
            }
        }
        return false;
    }

    private String handleResult(Result result) {
        String resultStr = result.getText();
        int beganIndex = resultStr.lastIndexOf("-");
        String effectiveResult = resultStr.substring(beganIndex+1);
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
