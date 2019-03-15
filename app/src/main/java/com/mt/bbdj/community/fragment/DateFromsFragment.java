package com.mt.bbdj.community.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
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
import com.mt.bbdj.baseconfig.view.BarChartManager;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.baseconfig.view.NumberFormatter;
import com.mt.bbdj.community.adapter.ClientDetailAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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
public class DateFromsFragment extends BaseFragment implements XRecyclerView.LoadingListener {

    Unbinder unbinder;
    @BindView(R.id.rl_date)
    XRecyclerView recyclerView;

    private String user_id;
    private RequestQueue mRequestQueue;

    private String startTime, endTime;     //开始时间和结束时间的时间戳

    private final int ACTION_BIND_ACCOUNT = 200;
    private WaitDialog waitDialog;
    private View view;

    private final int REQUEST_DATA_CENTER = 100;     //请求数据中心的数据
    private WaitDialog dialogLoading;

    private List<HashMap<String,String>> mList = new ArrayList<>();
    private HorizontalBarChart barChart;

    public static DateFromsFragment getInstance() {
        DateFromsFragment bf = new DateFromsFragment();
        return bf;
    }

    private final int[] zidingyi = {
            Color.rgb(174, 197, 0), Color.rgb(246, 125, 0), Color.rgb(53, 139, 234), Color.rgb(174, 197, 0),
            Color.rgb(246, 125, 0)
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_froms_date, container, false);
        unbinder = ButterKnife.bind(this, view);
        initParams();
        initView(view);
       // requestData();
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {   //Fragment可见

        }
    }


  /*  private void requestData() {
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
                        tvSendNumber.setText(maiSum + "件");
                        tvPatyforNumber.setText(paySum + "元");
                        tvOutNumber.setText(outSum + "件");
                        tvEnterNumber.setText(enterSum + "件");
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
    }*/

    private void initView(View view) {
        iniRecycler();
        initChart();
    }

    private void iniRecycler() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.date_account, (ViewGroup) getActivity().findViewById(android.R.id.content), false);
        recyclerView.addHeaderView(view);
        recyclerView.setLoadingListener(this);
        recyclerView.addItemDecoration(new MyDecoration(getActivity(), LinearLayoutManager.VERTICAL, Color.parseColor("#efefef"), 1));
        recyclerView.setAdapter(new ClientDetailAdapter(getActivity(),mList));

        initBarChart(view);
    }

    private void initBarChart(View view) {
        barChart = view.findViewById(R.id.chart_barchart);
        showBarChartMore();
    }


    private void initChart() {
        
    }

    private void showBarChartMore() {
        
        barChart.setDrawBarShadow(false);
        barChart.setDrawValueAboveBar(true);
        barChart.getDescription().setEnabled(false);
        barChart.setPinchZoom(true);
        barChart.setScaleEnabled(false);// 是否可以缩放
        barChart.setDrawGridBackground(false);
        barChart.getAxisLeft().setAxisMinimum(0.5f);

        YAxis yl = barChart.getAxisLeft();
        yl.setDrawAxisLine(true);
        yl.setDrawGridLines(true);
        yl.setAxisMinimum(0f);

        YAxis yr = barChart.getAxisRight();
        yr.setDrawAxisLine(true);
        yr.setDrawGridLines(true);
        yr.setAxisMinimum(0f);

        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(12f);
        l.setXEntrySpace(4f);

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
        ArrayList<BarEntry> yVals3 = new ArrayList<BarEntry>();
        final ArrayList<Float> xAxisValues = new ArrayList<Float>();

        for (int i = 0;i<7;i++) {
            HashMap<String, String> item = new HashMap<String, String>();
            item.put("sendcorp","测试"+i);
            item.put("rnumber", (i+0.5)*10+"");
            PackageOrder.add(item);
        }

        for (int i = 0; i < 7; i++) {
            HashMap<String, String> item = PackageOrder.get(i);//;
            yVals1.add(new BarEntry(i, (float) (Float.parseFloat(item.get("rnumber").toString().trim()))));
            yVals2.add(new BarEntry(i, 10f));
            yVals3.add(new BarEntry(i, 8f));
        }

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAvoidFirstLastClipping(false);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(true);
        xAxis.setLabelCount(14, false);
        xAxis.setAxisMaximum(14);
        final String[] xValues = {"2019.2.27", "2019.2.28", "2019.3.1", "2019.3.2", "2019.3.3","2019.3.4","2019.3.5","2019.3.5","2019.3.5","2019.3.5","2019.3.5"};

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                for (int i = 0;i<xAxisValues.size();i++) {
                    if (value == xAxisValues.get(i)-1) {
                        return xValues[i];
                    }
                }
                return "";

            }
        });

        BarDataSet set1;
        BarDataSet set2;
        BarDataSet set3;

        if (barChart.getData() != null &&
                barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set2 = (BarDataSet) barChart.getData().getDataSetByIndex(1);
            set3 = (BarDataSet) barChart.getData().getDataSetByIndex(2);
            set1.setValues(yVals1);
            set2.setValues(yVals2);
            set3.setValues(yVals3);
//            set1.setBarBorderWidth();
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(yVals1, "寄件数");
            set2 = new BarDataSet(yVals2, "派件书");
            set3 = new BarDataSet(yVals3, "福五鼠");
            set1.setColor(Color.rgb(57, 177, 122));
            set2.setColor(Color.rgb(19, 38, 220));
            set3.setColor(Color.rgb(197, 120, 78));
            BarData data = new BarData();
            data.addDataSet(set1);
            data.addDataSet(set2);
            data.addDataSet(set3);
            data.setBarWidth(barWidth);
            data.groupBars(0, groupSpace, barSpace);
            barChart.setData(data);

        }
        barChart.setFitBars(false);
        barChart.animateY(2500);
        barChart.getData().setHighlightEnabled(false);
    }

    private float barWidth = (float) ((1 - 0.01) / 2); // x4 DataSet
    float groupSpace = 0.3f; //柱状图组之间的间距
    float barSpace = (float) ((1 - 0.12) / 3 / 10); // x4 DataSet

    private List<HashMap<String, String>> PackageOrder = new ArrayList<HashMap<String, String>>();
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

    @Override
    public void onRefresh() {
        recyclerView.refreshComplete();
    }

    @Override
    public void onLoadMore() {
        recyclerView.loadMoreComplete();
    }
}
