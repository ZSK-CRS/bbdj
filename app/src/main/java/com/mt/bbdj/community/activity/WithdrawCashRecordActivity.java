package com.mt.bbdj.community.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.configure.PickerOptions;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.WithdrawCashRecordAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WithdrawCashRecordActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;     //返回
    @BindView(R.id.tv_fast_select)
    ImageView tvFastSelect;     //筛选
    @BindView(R.id.rl_record)
    XRecyclerView rlRecord;    //提现记录
    @BindView(R.id.ll_title)
    LinearLayout llTitle;

    private List<HashMap<String, String>> mList;
    private WithdrawCashRecordAdapter mAdapter;
    private String user_id;
    private RequestQueue mRequestQueue;
    private int mPage = 1;
    private String startTime, endTime;
    private boolean isFresh = true;


    private final int REQUEST_GET_MONEY_RECORD = 300;
    private int mYear;
    private int mMonth;
    private int mDay;
    private TimePickerView timePicker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_cash_record);
        ButterKnife.bind(this);
        initParams();
        initRecycler();     //初始化列表
        initDialog();
    }

    private void initDialog() {
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        //startDate.set(2013,1,1);
        Calendar endDate = Calendar.getInstance();
        //endDate.set(2020,1,1);

        String yearStr = DateUtil.yearDate();
        int year = Integer.parseInt(yearStr);

        int month = DateUtil.monthDate();

        //正确设置方式 原因：注意事项有说明
        startDate.set(2010,0,1);
        endDate.set(year,month,31);

        timePicker = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                startTime = DateUtil.getTimeForYearAndMonth(date);
                endTime = DateUtil.getNextMonthForSpecial(date);
                rlRecord.refresh();
                //ToastUtil.showShort(startTime+"    "+endTime);
            }
        })
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        TextView ivCancel = (TextView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                timePicker.returnData();
                                timePicker.dismiss();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                timePicker.dismiss();
                            }
                        });
                    }
                })
                .setContentTextSize(18)
                .setType(new boolean[]{true, true, false, false, false, false})
                .setLabel("年", "月", "日", "时", "分", "秒")
                .setLineSpacingMultiplier(1.5f)
                //  .setTextXOffset(0, 0, 0, 40, 0, -40)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(0xFF24AD9D)
                .build();

    }

    private void initParams() {
        DaoSession mDaoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = mDaoSession.getUserBaseMessageDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        rlRecord.refresh();
    }

    private void requestData() {

        Request<String> request = NoHttpRequest.getMoneyRecordRequest(user_id, mPage, startTime, endTime);
        mRequestQueue.add(REQUEST_GET_MONEY_RECORD, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "WithDrawCashgRecordActivity::" + response.get());
                if (isFresh) {
                    rlRecord.refreshComplete();
                } else {
                    rlRecord.loadMoreComplete();
                }
                if (mPage == 1) {
                    mList.clear();
                }
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    JSONObject dataObj = jsonObject.getJSONObject("data");
                    if ("5001".equals(code)) {
                        setData(dataObj);
                    } else {
                        ToastUtil.showShort(msg);
                    }
                } catch (JSONException e) {
                    ToastUtil.showShort(e.getMessage());
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

    private void setData(JSONObject dataObj) throws JSONException {
        JSONArray jsonArray = dataObj.getJSONArray("list");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            String type = jsonObject1.getString("type");
            String card_number = jsonObject1.getString("card_number");
            String pay_account = jsonObject1.getString("pay_account");
            String money = jsonObject1.getString("money");
            String Flag = jsonObject1.getString("flag");
            String Time = jsonObject1.getString("time");
            HashMap<String,String> map = new HashMap<>();
            map.put("type",type);
            map.put("card_number",StringUtil.handleNullResultForString(card_number));
            map.put("pay_account",StringUtil.handleNullResultForString(pay_account));
            map.put("money",StringUtil.handleNullResultForString(money));
            map.put("Flag",Flag);
            map.put("Time",Time);
            mList.add(map);
            map = null;
        }
        mAdapter.notifyDataSetChanged();
    }

    private void initRecycler() {
        mList = new ArrayList<>();
        rlRecord.setFocusable(false);
        rlRecord.setNestedScrollingEnabled(false);
        //设置线性布局 Creates a vertical LinearLayoutManager
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rlRecord.setLayoutManager(mLayoutManager);
        rlRecord.setLoadingListener(this);
        mAdapter = new WithdrawCashRecordAdapter(this, mList);
        rlRecord.setAdapter(mAdapter);
    }

    @OnClick({R.id.iv_back, R.id.tv_fast_select})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_fast_select:
                showTimeSelectDialog();
                break;
        }
    }

    private void showTimeSelectDialog() {
        timePicker.show();
    }

    @Override
    public void onRefresh() {
        isFresh = true;
        mPage = 1;
        requestData();
    }

    @Override
    public void onLoadMore() {
        isFresh = false;
        mPage++;
        requestData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mList = null;
    }

}
