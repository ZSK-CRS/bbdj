package com.mt.bbdj.community.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
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
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.GoodsOrderAdapter;
import com.mt.bbdj.community.adapter.YesterDayPayAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class YesterdayPayActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.tv_fahuofei)
    TextView tvFahuofei;
    @BindView(R.id.tv_wuliaofei)
    TextView tvWuliaofei;
    @BindView(R.id.tv_fuwufei)
    TextView tvFuwufei;
    @BindView(R.id.iv_left)
    RelativeLayout ivLeft;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.iv_right)
    RelativeLayout ivRight;
    @BindView(R.id.rl_detail)
    XRecyclerView rlDetail;

    private List<HashMap<String, String>> mList = new ArrayList<>();
    private YesterDayPayAdapter mAdapter;
    private String user_id;
    private RequestQueue mRequestQueue;
    private String startTime, endTime;
    private String currentTime;
    private TimePickerView timePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yesterday_pay);
        ButterKnife.bind(this);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(YesterdayPayActivity.this);
        initParams();
        initRecycler();
        requestData();
        initDialog();
    }

    private void initParams() {
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = daoSession.getUserBaseMessageDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        mRequestQueue = NoHttp.newRequestQueue();

        //初始化当前时间
        currentTime = DateUtil.dayDate();   //当前时间
        currentTime = DateUtil.getSpecifiedDayBefore("yyyy-MM-dd HH:mm:ss", currentTime);    //前一天
        String[] current = currentTime.split(" ");
        String[] timeArray = current[0].split("-");

        tvTime.setText(timeArray[0]+"年"+timeArray[1]+"月"+timeArray[2]+"日");

        String tempTime = currentTime;

        startTime = DateUtil.getSomeDayStamp(tempTime);
        tempTime = DateUtil.getSpecifiedDayAfter("yyyy-MM-dd HH:mm:ss", currentTime);
        endTime = DateUtil.getSomeDayStamp(tempTime);
        currentTime = tempTime;
    }


    private void initRecycler() {
        rlDetail.setFocusable(false);
        mAdapter = new YesterDayPayAdapter(mList);
        rlDetail.setNestedScrollingEnabled(false);
        rlDetail.setLayoutManager(new LinearLayoutManager(this));
        rlDetail.setAdapter(mAdapter);
        rlDetail.setLoadingListener(this);
    }

    @OnClick({R.id.iv_back, R.id.tv_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_time:
                showTimeSelect();
                break;
        }
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

        int date = DateUtil.currentDate();

        //正确设置方式 原因：注意事项有说明
        startDate.set(2010, 0, 1);
        endDate.set(year, month, date);

        timePicker = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String current = DateUtil.dayDate(date);
                startTime = DateUtil.getSomeDayStamp(current);
                current = DateUtil.dayDate(DateUtil.getSpecifiedDayAfter("yyyy-MM-dd", current) + " 00:00:00");
                endTime = DateUtil.getSomeDayStamp(current);
                rlDetail.refresh();
                tvTime.setText(DateUtil.getStrDate(date,"yyyy年MM月dd日"));
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
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("年", "月", "日", "时", "分", "秒")
                .setLineSpacingMultiplier(1.5f)
                //  .setTextXOffset(0, 0, 0, 40, 0, -40)
                .isCenterLabel(false) //是否只显示中间选中项的label文字，false则每项item全部都带有label。
                .setDividerColor(0xFF24AD9D)
                .build();

    }

    private void showTimeSelect() {
        timePicker.show();
    }


    private void requestData() {
        Request<String> request = NoHttpRequest.getYesterDayPayforRequest(user_id, startTime, endTime);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "YesterdayPayActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (isFresh) {
                        rlDetail.refreshComplete();
                    } else {
                        rlDetail.loadMoreComplete();
                    }
                    if ("5001".equals(code)) {
                        mList.clear();
                        handleResult(data);
                    } else {
                        ToastUtil.showShort("查询失败，请重试！");
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

    private void handleResult(JSONObject data) throws JSONException {
        mList.clear();
        String shipping_cost = data.getString("shipping_cost");
        String service_cost = data.getString("service_cost");
        String materiel_cost = data.getString("materiel_cost");
        tvFuwufei.setText(StringUtil.handleNullResultForNumber(service_cost));
        tvFahuofei.setText(StringUtil.handleNullResultForNumber(shipping_cost));
        tvWuliaofei.setText(StringUtil.handleNullResultForNumber(materiel_cost));

        JSONArray datArray = data.getJSONArray("data");
        int j = 0;
        for (int i = 0; i < datArray.length(); i++) {
            j = i + 1;
            JSONObject entity = datArray.getJSONObject(i);
            String serialnumber = j + "";
            String time = entity.getString("time");
            String budget = entity.getString("budget");
            String types = entity.getString("types");
            String money = entity.getString("money");
            HashMap<String, String> map = new HashMap<>();
            map.put("serialnumber", serialnumber);
            map.put("time", DateUtil.changeStampToStandrdTime("yyyy-MM-dd", time));
            map.put("budget", budget);
            map.put("types", types);
            map.put("money", StringUtil.handleNullResultForNumber(money));
            mList.add(map);
            map = null;
        }

        mAdapter.notifyDataSetChanged();
    }

    private boolean isFresh = true;

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
