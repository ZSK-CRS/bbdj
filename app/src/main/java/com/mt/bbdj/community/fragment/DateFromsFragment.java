package com.mt.bbdj.community.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseFragment;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.NumberFormatter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Author : ZSK
 * Date : 2019/1/29
 * Description :   日报
 */
public class DateFromsFragment extends BaseFragment {

    Unbinder unbinder;
    @BindView(R.id.tv_send_number)
    TextView tvSendNumber;
    @BindView(R.id.tv_patyfor_number)
    TextView tvPatyforNumber;
    @BindView(R.id.tv_out_number)
    TextView tvOutNumber;
    @BindView(R.id.tv_enter_number)
    TextView tvEnterNumber;
    @BindView(R.id.chart1)
    PieChart chart1;
    @BindView(R.id.layout_refresh)
    SwipeRefreshLayout layoutRefresh;

    private String user_id;
    private RequestQueue mRequestQueue;

    private String startTime, endTime;     //开始时间和结束时间的时间戳

    private final int ACTION_BIND_ACCOUNT = 200;
    private WaitDialog waitDialog;
    private View view;
    private PieChart mChart;

    private final int REQUEST_DATA_CENTER = 100;     //请求数据中心的数据
    private WaitDialog dialogLoading;

    public static DateFromsFragment getInstance() {
        DateFromsFragment bf = new DateFromsFragment();
        return bf;
    }

    private final int[] zidingyi = {
            Color.rgb(174, 197, 0), Color.rgb(246, 125, 0), Color.rgb(53, 139, 234),Color.rgb(174, 197, 0),
            Color.rgb(246, 125, 0)
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_froms_date, container, false);
        unbinder = ButterKnife.bind(this, view);
        initParams();
        initView(view);
        requestData();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {   //Fragment可见

        }
    }


    private void requestData() {
        startTime = "1551399010";
        endTime = "1551658210";
        Request<String> request = NoHttpRequest.getDataCenterRequest(user_id, startTime, endTime);
        mRequestQueue.add(REQUEST_DATA_CENTER, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
             //   dialogLoading = WaitDialog.show(getActivity(), "加载中...").setCanCancel(true);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "DateFromsFragment::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    layoutRefresh.setRefreshing(false);
                    if ("5001".equals(code)) {
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        String mailsum = dataObj.getString("mailsum");    //寄件数量
                        String entersum = dataObj.getString("entersum");    //入库数
                        String outsum = dataObj.getString("outsum");    //出库数
                        String paysum = dataObj.getString("paysum");   //支出
                        int maiSum = IntegerUtil.getStringChangeToNumber(mailsum);
                        int enterSum = IntegerUtil.getStringChangeToNumber(entersum);
                        int outSum = IntegerUtil.getStringChangeToNumber(outsum);
                        float paySum = IntegerUtil.getStringChangeToFloat(paysum);
                        setChartData(maiSum, outSum, enterSum);
                        tvSendNumber.setText(maiSum+"件");
                        tvPatyforNumber.setText(paySum+"元");
                        tvOutNumber.setText(outSum+"件");
                        tvEnterNumber.setText(enterSum+"件");
                    } else {
                        ToastUtil.showShort(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
               //     dialogLoading.doDismiss();
                    ToastUtil.showShort("加载失败！");
                }
              //  dialogLoading.doDismiss();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
             //   dialogLoading.doDismiss();
            }

            @Override
            public void onFinish(int what) {
              //  dialogLoading.doDismiss();
            }
        });
    }

    private void initView(View view) {
        mChart = (PieChart) view.findViewById(R.id.chart1);
        initChart();
        layoutRefresh.setClickable(true);
        layoutRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestData();
            }
        });
    }

    private void setChartData(float sendSum, float outSum, float inSum) {
        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : zidingyi)
            colors.add(c);

        List<PieEntry> pieEntries = new ArrayList<>();

        pieEntries.add(new PieEntry(sendSum, "寄件数"));
        pieEntries.add(new PieEntry(outSum, "出库"));
        pieEntries.add(new PieEntry(inSum, "入库"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "订单占比");
        pieDataSet.setDrawValues(true);
        pieDataSet.setValueFormatter(new NumberFormatter());
        pieDataSet.setValueTextSize(10f);
        pieDataSet.setValueTextColor(Color.WHITE);

        pieDataSet.setValueLinePart1OffsetPercentage(90.f);
        pieDataSet.setValueLinePart1Length(0.2f);
        pieDataSet.setValueLinePart2Length(0.4f);
        pieDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieDataSet.setColors(colors);


        mChart.setData(new PieData(pieDataSet));
        mChart.invalidate();
    }

    private void initChart() {
        mChart.setUsePercentValues(true);
        mChart.getDescription().setEnabled(false);
        mChart.setExtraOffsets(5, 5, 5, 10);

        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColor(Color.WHITE);

        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);

        mChart.setHoleRadius(0f);
        mChart.setTransparentCircleRadius(0f);

        mChart.setDrawCenterText(true);
        mChart.setCenterTextSize(20);

        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);

        mChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);

        Legend l = mChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setTextColor(Color.parseColor("#757575"));
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(0f);

        mChart.setEntryLabelColor(Color.WHITE);
        mChart.setEntryLabelTextSize(0f);
        mChart.setDrawSlicesUnderHole(true);
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

}
