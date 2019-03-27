package com.mt.bbdj.community.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseFragment;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyPopuwindow;
import com.mt.bbdj.community.adapter.BluetoothSearchAdapter;
import com.mt.bbdj.community.adapter.SortOrderAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Author : ZSK
 * Date : 2019/3/20
 * Description :
 */
public class OrderFromsFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.ll_sort)
    LinearLayout llSort;
    @BindView(R.id.ll_type)
    LinearLayout llType;
    @BindView(R.id.ll_time)
    LinearLayout llTime;
    @BindView(R.id.tv_order_number)
    TextView tvOrderNumber;
    @BindView(R.id.tv_send_number)
    TextView tvSendNumber;
    @BindView(R.id.tv_pai_number)
    TextView tvPaiNumber;
    @BindView(R.id.tv_service_number)
    TextView tvServiceNumber;
    @BindView(R.id.rl_sort)
    RecyclerView rlSort;
    @BindView(R.id.tv_sort_describe)
    TextView tvSortDescribe;    //描述

    @BindView(R.id.tv_time)
    TextView tvTime;

    private String user_id;
    private RequestQueue mRequestQueue;
    private MyPopuwindow popupWindow;

    private String startTime, endTime;     //开始时间和结束时间的时间戳
    private String mType = "1";    // 1：全部  2：寄件  3：派件  4：服务

    private List<HashMap<String, String>> mList = new ArrayList<>();

    private WaitDialog waitDialog;
    private View view;

    private final int REQUEST_SORT_DATA = 100;    //排序
    private WaitDialog dialogLoading;

    private SortOrderAdapter mAdapter;
    private TimePickerView timePickerYue;
    private TimePickerView timePickerDate;
    private View selectView;


    public static OrderFromsFragment getInstance() {
        OrderFromsFragment bf = new OrderFromsFragment();
        return bf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_froms_order, container, false);
        unbinder = ButterKnife.bind(this, view);
        initParams();
        initView(view);
        requestData();
        initDialog();
        return view;
    }

    private void requestData() {
        Request<String> request = NoHttpRequest.getSortRequest(user_id, startTime, endTime, mType);
        mRequestQueue.add(REQUEST_SORT_DATA, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "OrderFromsFragment::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        handleResult(jsonObject);
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

    private void handleResult(JSONObject jsonObject) throws JSONException {

        JSONObject dataObject = jsonObject.getJSONObject("data");
        JSONArray entitys = dataObject.getJSONArray("ranking");

        JSONObject own = dataObject.getJSONObject("own");
        String ranking = own.getString("ranking");
        String mail = own.getString("mail");
        String pie = own.getString("pie");
        String service = own.getString("service");
        String totalall = own.getString("total");

        tvSortDescribe.setText("您的当日排行：" + StringUtil.handleNullResultForString(ranking));
        tvOrderNumber.setText(StringUtil.handleNullResultForNumber(totalall));
        tvSendNumber.setText(StringUtil.handleNullResultForNumber(mail));
        tvPaiNumber.setText(StringUtil.handleNullResultForNumber(pie));
        tvServiceNumber.setText(StringUtil.handleNullResultForNumber(service));

        mList.clear();
        String j = "0";
        for (int i = 0; i < entitys.length(); i++) {
            j = i + 1 + "";
            JSONObject entity = entitys.getJSONObject(i);
            String userName = entity.getString("username");    //店名
            String total = entity.getString("total");    //总订单
            String mailsum = entity.getString("mailsum");    //寄件数
            String piesum = entity.getString("piesum");    //派件数
            String servicesum = entity.getString("servicesum");    //服务数
            HashMap<String, String> map = new HashMap<>();
            map.put("userName", StringUtil.handleNullResultForString(userName));
            map.put("total", StringUtil.handleNullResultForNumber(total));
            map.put("mailsum", StringUtil.handleNullResultForNumber(mailsum));
            map.put("piesum", StringUtil.handleNullResultForNumber(piesum));
            map.put("servicesum", StringUtil.handleNullResultForNumber(servicesum));
            mList.add(map);
            map = null;
        }
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {   //Fragment可见

        }
    }

    private void initView(View view) {
        rlSort.setLayoutManager(new LinearLayoutManager(getActivity()));
        rlSort.setFocusable(false);
        rlSort.setNestedScrollingEnabled(false);
        mAdapter = new SortOrderAdapter(mList);
        rlSort.setAdapter(mAdapter);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.ll_sort, R.id.ll_type, R.id.ll_time})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_sort:
                break;
            case R.id.ll_type:
                showTypeSelect();
                break;
            case R.id.ll_time:
                shouTimeSelect();
                break;
        }
    }

    private void showTypeSelect() {

    }

    private void shouTimeSelect() {
        timePickerDate.show();
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

        timePickerDate = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String current = DateUtil.dayDate(date);
                startTime = DateUtil.getSomeDayStamp(current);
                current = DateUtil.dayDate(DateUtil.getSpecifiedDayAfter("yyyy-MM-dd", current) + " 00:00:00");
                endTime = DateUtil.getSomeDayStamp(current);
                requestData();
                tvTime.setText(DateUtil.getStrDate(date, "yyyy年MM月dd日"));
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
                                timePickerDate.returnData();
                                timePickerDate.dismiss();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                timePickerDate.dismiss();
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

        timePickerYue = new TimePickerBuilder(getActivity(), new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String current = DateUtil.dayDate(date);
                startTime = DateUtil.getSomeDayStamp(current);
                current = DateUtil.dayDate(DateUtil.getSpecifiedDayAfter("yyyy-MM-dd", current) + " 00:00:00");
                endTime = DateUtil.getSomeDayStamp(current);
                requestData();
                tvTime.setText(DateUtil.getStrDate(date, "yyyy年MM月dd日"));
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
                                timePickerYue.returnData();
                                timePickerYue.dismiss();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                timePickerYue.dismiss();
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

    private void initSelectPop() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            selectView = getLayoutInflater().inflate(R.layout.fast_layout, null);
            popupWindow = new MyPopuwindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
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

}

