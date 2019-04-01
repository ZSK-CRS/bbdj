package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ScreenStateManager;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.MoneyFormatManagerAdapter;
import com.mt.bbdj.community.adapter.MoneyManagerAdapter;
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
import butterknife.OnClick;

public class MoneyFormatManagerActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.finance_list)
    XRecyclerView financeList;
    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.rl_title)
    RelativeLayout rlTitle;
    @BindView(R.id.tv_dianpu)
    TextView tvDianpu;
    @BindView(R.id.tv_money)
    TextView tvMoney;
    @BindView(R.id.tv_jinajie)
    TextView tvJinajie;
    @BindView(R.id.tv_jifen)
    TextView tvJifen;
    @BindView(R.id.tv_rongyu)
    TextView tvRongyu;
    @BindView(R.id.ll_chongzhi)
    LinearLayout llChongzhi;
    @BindView(R.id.tv_tixian)
    LinearLayout llTixian;
    @BindView(R.id.tv_yestday_pay_number)
    TextView tvYestdayPayNumber;
    @BindView(R.id.tv_yestday_pay)
    TextView tvYestdayPay;
    @BindView(R.id.tv_yestday_send_number)
    TextView tvYestdaySendNumber;
    @BindView(R.id.tv_yestday_send)
    TextView tvYestdaySend;
    @BindView(R.id.tv_yestday_pai_number)
    TextView tvYestdayPaiNumber;
    @BindView(R.id.tv_yestday_pai)
    TextView tvYestdayPai;
    @BindView(R.id.tv_yestday_service_number)
    TextView tvYestdayServiceNumber;
    @BindView(R.id.tv_yestday_service)
    TextView tvYestdayService;
    private LinearLayout headll;

    //用于记录CustomHScrollView的初始位置
    private int leftPos;
    private int topPos;
    private MoneyFormatManagerAdapter sheetAdapter;
    private MoneyManagerAdapter managerAdapter;
    private String startTime = "";
    private String endTime = "";

    private List<HashMap<String, String>> mData = new ArrayList<>();
    private String user_id;
    private RequestQueue mRequestQueue;

    private final int REQUEST_MONRY_MANAGER = 100;
    private final int REQUEST_MONEY_MANAGER_DATA = 200;   //搜索财务首页数据
    private WaitDialog dialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_format_manager);
     /*   if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            window.setNavigationBarColor(Color.TRANSPARENT);

        }*/
      /*  View view1 = findViewById(R.id.view);
        ViewGroup.LayoutParams layoutParams = view1.getLayoutParams();
        layoutParams.height = ScreenStateManager.getStatusBarHeight(this);
        view1.setLayoutParams(layoutParams);
        view1.setBackgroundColor(Color.TRANSPARENT);*/


     /*   YCAppBar.setStatusBarLightMode(this, R.color.mainColor_3);
        StatusBarUtils.StatusBarLightMode(MoneyFormatManagerActivity.this);*/
        ButterKnife.bind(this);
        initParams();
        initList();
        //  requestData();
        requestMoneyManagerData();
        initListener();
    }

    private void initListener() {
        //在线充值
        llChongzhi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoneyFormatManagerActivity.this,RechargeActivity.class);
                startActivity(intent);
            }
        });

        //提现
        llTixian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MoneyFormatManagerActivity.this, WithdrawCashActivity.class);
                startActivity(intent);
            }
        });


    }



    private void requestMoneyManagerData() {
        Request<String> request = NoHttpRequest.getMoneyManagerRequest(user_id);
        mRequestQueue.add(REQUEST_MONEY_MANAGER_DATA, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                dialogLoading = WaitDialog.show(MoneyFormatManagerActivity.this, "请稍后...").setCanCancel(true);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "MoneyFormatManagerActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        handleResult(what, jsonObject);
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

    private void handleResult(int what, JSONObject jsonObject) throws JSONException {
        JSONObject data = jsonObject.getJSONObject("data");
        String username = data.getString("username");
        String balance = data.getString("balance");
        String min_balance = data.getString("min_balance");
        String Integral = data.getString("integral");
        String credit = data.getString("credit");
        String consum = data.getString("consum");
        String mailingsum = data.getString("mailingsum");
        String piesum = data.getString("piesum");
        String servicesum = data.getString("servicesum");
        tvDianpu.setText(StringUtil.handleNullResultForString(username));   //店铺名称
        tvMoney.setText(StringUtil.handleNullResultForNumber(balance));   //余额
        tvJinajie.setText(StringUtil.handleNullResultForNumber(min_balance));   //警戒余额
        tvJifen.setText(StringUtil.handleNullResultForNumber(Integral));   //积分
        tvRongyu.setText(StringUtil.handleNullResultForNumber(credit));    //荣誉值
        tvYestdayPayNumber.setText(StringUtil.handleNullResultForNumber(consum) + "(" + "元" + ")");   //支出
        tvYestdaySendNumber.setText(StringUtil.handleNullResultForNumber(mailingsum) + "(" + "件" + ")");   //寄件
        tvYestdayPaiNumber.setText(StringUtil.handleNullResultForNumber(piesum) + "(" + "件" + ")");   //派件
        tvYestdayServiceNumber.setText(StringUtil.handleNullResultForNumber(servicesum) + "(" + "单" + ")");   //服务数
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


    @OnClick({R.id.iv_back, R.id.tv_yestday_pay, R.id.tv_yestday_send, R.id.tv_yestday_pai, R.id.tv_yestday_service})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_yestday_pay:
                handleCheckPay();
                break;
            case R.id.tv_yestday_send:
                handleCheckSend();
                break;
            case R.id.tv_yestday_pai:
                ToastUtil.showShort("暂不可查看");
                break;
            case R.id.tv_yestday_service:
                ToastUtil.showShort("暂不可查看");
                break;
        }
    }

    private void handleCheckSend() {
        Intent intent = new Intent(this,YesterdSendPayActivity.class);
        startActivity(intent);
    }

    private void handleCheckPay() {
        Intent intent = new Intent(this,YesterdayPayActivity.class);
        startActivity(intent);
    }


    class MyTouchLinstener implements View.OnTouchListener {
        float lastX = 0;
        float lastY = 0;
        private boolean isClick = false;
        private long downTime = 0;

        @Override
        public boolean onTouch(View arg0, MotionEvent ev) {
            //判断是否是点击
            float tempX = ev.getX();
            float tempY = ev.getY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = tempX;
                    lastY = tempY;
                    downTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    isClick = false;
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(lastX - tempX) > 10 || Math.abs(lastY - tempY) > 10) {
                        isClick = false;
                    } else {
                        isClick = true;
                    }
                    long timeDef = System.currentTimeMillis() - downTime;
                    if (timeDef <= 40 && isClick) {
                        isClick = false;
                    }
                    break;
            }
            if (isClick) {
                int position = sheetAdapter.getTouchPosition();
                Toast.makeText(MoneyFormatManagerActivity.this, position + "", Toast.LENGTH_SHORT).show();
            } else {
                //当在表头和listView控件上touch时，将事件分发给 ScrollView
                HorizontalScrollView headSrcrollView = (HorizontalScrollView) headll.findViewById(R.id.h_scrollView);
                headSrcrollView.onTouchEvent(ev);
            }
            return false;
        }

    }

    private void initList() {

        financeList.setFocusable(false);
        financeList.setNestedScrollingEnabled(false);

      /*  LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };*/

        financeList.setLayoutManager(new LinearLayoutManager(this));
        financeList.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL, Color.parseColor("#eeeeee"), 1));
        financeList.setLoadingListener(this);
        financeList.setFocusableInTouchMode(false);
        sheetAdapter = new MoneyFormatManagerAdapter(this, mData, headll, 1);
        sheetAdapter.notifyDataSetChanged();
        financeList.setAdapter(sheetAdapter);

    }

    /**
     * 记录CustomHScrollView的初始位置
     *
     * @param l
     * @param t
     */
    public void setPosData(int l, int t) {
        this.leftPos = l;
        this.topPos = t;
    }


    @Override
    public void onRefresh() {
        financeList.refreshComplete();
    }

    @Override
    public void onLoadMore() {
        financeList.loadMoreComplete();
    }
}
