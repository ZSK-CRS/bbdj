package com.mt.bbdj.community.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.community.fragment.ComDataFragment;
import com.mt.bbdj.community.fragment.DateFromsFragment;
import com.mt.bbdj.community.fragment.MonthFromsFragment;
import com.mt.bbdj.community.fragment.OrderFromsFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DataActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_date_title)
    TextView tvDateTitle;
    @BindView(R.id.tv_date_select)
    View tvDateSelect;
    @BindView(R.id.tv_month_title)
    TextView tvMonthTitle;
    @BindView(R.id.tv_month_select)
    View tvMonthSelect;
    @BindView(R.id.tv_order_title)
    TextView tvOrderTitle;
    @BindView(R.id.tv_order_select)
    View tvOrderSelect;
    private LinearLayout llDateReprot, llMonthReport, llOrderReport;    //日报,月报，周报
    private FrameLayout rootView;


    private ArrayList<Fragment> list_fragment = new ArrayList<>();       //定义要装fragment的列表
    private ArrayList<String> list_title = new ArrayList<>();            //定义标题
    private List<HashMap<String, String>> mFastData = new ArrayList<>();    //快递公司
    private ExpressLogoDao mExpressLogoDao;
    private double currentItem;

    private DateFromsFragment dateFromsFragment;
    private MonthFromsFragment monthFromsFragment;
    private OrderFromsFragment orderFromsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_com_data_fragment);
        ButterKnife.bind(this);
        initView();
        selectFragmentData();
    }

    public static ComDataFragment getInstance() {
        ComDataFragment comDataFragment = new ComDataFragment();
        return comDataFragment;
    }

    private void initView() {
        llDateReprot = findViewById(R.id.ll_date_report);
        llMonthReport = findViewById(R.id.ll_month_report);
        llOrderReport = findViewById(R.id.ll_order_report);
        rootView = findViewById(R.id.framlayout);
        llDateReprot.setOnClickListener(this);
        llMonthReport.setOnClickListener(this);
        llOrderReport.setOnClickListener(this);
    }

    private void resetSelectState() {
        tvDateTitle.setTextSize(14);
        tvMonthTitle.setTextSize(14);
        tvOrderTitle.setTextSize(14);
        tvDateSelect.setVisibility(View.GONE);
        tvMonthSelect.setVisibility(View.GONE);
        tvOrderSelect.setVisibility(View.GONE);
    }

    private void selectFragmentOrder() {
        resetSelectState();
        tvOrderSelect.setVisibility(View.VISIBLE);
        tvOrderTitle.setTextSize(18);
        FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
        if (orderFromsFragment == null) {
            orderFromsFragment = OrderFromsFragment.getInstance();
            mTransaction.add(R.id.framlayout, orderFromsFragment);
        }
        hideFragment(mTransaction);
        mTransaction.show(orderFromsFragment);
        mTransaction.commit();
    }

    private void selectFragmentMonth() {
        resetSelectState();
        tvMonthTitle.setTextSize(18);
        tvMonthSelect.setVisibility(View.VISIBLE);
        FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
        if (monthFromsFragment == null) {
            monthFromsFragment = MonthFromsFragment.getInstance();
            mTransaction.add(R.id.framlayout, monthFromsFragment);
        }
        hideFragment(mTransaction);
        mTransaction.show(monthFromsFragment);
        mTransaction.commit();
    }

    private void selectFragmentData() {
        resetSelectState();
        tvDateTitle.setTextSize(18);
        tvDateSelect.setVisibility(View.VISIBLE);
        FragmentTransaction mTransaction = getSupportFragmentManager().beginTransaction();
        if (dateFromsFragment == null) {
            dateFromsFragment = DateFromsFragment.getInstance();
            mTransaction.add(R.id.framlayout, dateFromsFragment);
        }
        hideFragment(mTransaction);
        mTransaction.show(dateFromsFragment);
        mTransaction.commit();
    }

    private void hideFragment(FragmentTransaction mTransaction) {
        if (dateFromsFragment != null) {
            mTransaction.hide(dateFromsFragment);
        }
        if (monthFromsFragment != null) {
            mTransaction.hide(monthFromsFragment);
        }
        if (orderFromsFragment != null) {
            mTransaction.hide(orderFromsFragment);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_date_report:   //日报
                selectFragmentData();
                break;
            case R.id.ll_month_report:  //月报
                selectFragmentMonth();
                break;
            case R.id.ll_order_report:   //排行榜
                selectFragmentOrder();
                break;
        }
    }
}
