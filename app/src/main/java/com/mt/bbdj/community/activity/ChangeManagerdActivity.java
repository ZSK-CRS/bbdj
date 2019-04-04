package com.mt.bbdj.community.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.flyco.tablayout.SlidingTabLayout;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.ExpressLogo;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.baseconfig.view.MyPopuwindow;
import com.mt.bbdj.community.adapter.SimpleFragmentPagerAdapter;
import com.mt.bbdj.community.adapter.SimpleStringAdapter;
import com.mt.bbdj.community.fragment.ChangeManagerFragmnet;
import com.mt.bbdj.community.fragment.ChangeManagerFragmnet2;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangeManagerdActivity extends BaseActivity {

    @BindView(R.id.slt_title)
    SlidingTabLayout sltTitle;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tv_fast_select)
    ImageView expressSelect;
    @BindView(R.id.tv_number)
    TextView tvNumber;
    @BindView(R.id.rl_select_time)
    RelativeLayout rlSelectTime;
    @BindView(R.id.express_select)
    TextView expressSelectNumber;
    @BindView(R.id.rl_select_express)
    RelativeLayout rlSelectExpress;
    @BindView(R.id.bt_select)
    Button btSelect;
    @BindView(R.id.time_select)
    TextView tvTimeSelect;


    private PopupWindow popupWindow;
    private View selectView;

    private ArrayList<Fragment> list_fragment = new ArrayList<>();       //定义要装fragment的列表
    private ArrayList<String> list_title = new ArrayList<>();            //定义标题
    private List<HashMap<String, String>> mFastData = new ArrayList<>();    //快递公司
    private ExpressLogoDao mExpressLogoDao;
    private double currentItem;
    private TimePickerView timePickerDate;
    private String startTime;
    private String endTime;
    private String express_id = "";    //快递公司id

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_managerd);
        ButterKnife.bind(this);
        initParams();
        initView();
        initSelectPop();
        initListener();
        initDialog();   //初始化时间
    }

    private void initParams() {
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        mExpressLogoDao = daoSession.getExpressLogoDao();

        //默认的是今天
        startTime = DateUtil.getTadayStartTimeZeroStamp();
        endTime = DateUtil.getTadayEndTimeLastStamp();
        String currentTime = DateUtil.getCurrentTimeFormat("yyyy-MM-dd");
        tvTimeSelect.setText(currentTime);
    }


    private void initListener() {
        expressSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //筛选快递
                showSelectPop(view);
            }
        });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentItem = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initSelectPop() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            selectView = getLayoutInflater().inflate(R.layout.fast_layout_4, null);
            RecyclerView fastList = selectView.findViewById(R.id.tl_fast_list);
            initRecycler(fastList);
            popupWindow = new PopupWindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //设置动画
            popupWindow.setAnimationStyle(R.style.popup_window_anim);
            //设置背景颜色
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
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

    private void showSelectPop(View view) {
        if (Build.VERSION.SDK_INT < 24) {
            popupWindow.showAsDropDown(view);
        } else {
            int[] location = new int[2];
            view.getLocationOnScreen(location);
            int x = location[0];
            int y = location[1];
            popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, y + view.getHeight());
        }
      //  popupWindow.showAsDropDown(view);
    }

    private void initView() {
        initFragment();
    }

    private void initRecycler(RecyclerView fastList) {
        mFastData.clear();
        //查询快递公司的信息
        List<ExpressLogo> expressLogoList = mExpressLogoDao.queryBuilder()
                .where(ExpressLogoDao.Properties.States.eq(1)).list();
        HashMap<String, String> map = new HashMap<>();
        map.put("express", "全部");
        map.put("express_id", "");
        mFastData.add(map);
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
                HashMap<String,String> map = mFastData.get(position);
                //选中的快递公司id
                express_id = map.get("express_id");
                expressSelectNumber.setText(map.get("express"));
                popupWindow.dismiss();
            }
        });

        fastList.setAdapter(goodsAdapter);
        fastList.addItemDecoration(new MarginDecoration(this));
        fastList.setLayoutManager(new LinearLayoutManager(this));
        goodsAdapter.notifyDataSetChanged();
    }

    private void sendExpressid() {
        int target = 300;
        if (currentItem == 0) {
            target = 300;
        }
        if (currentItem == 1) {
            target = 301;
        }
        HashMap<String,String> data = new HashMap<>();
        data.put("starttime",startTime);
        data.put("endtime",endTime);
        data.put("express_id",express_id);
        EventBus.getDefault().post(new TargetEvent(target, data));
    }


    private void initFragment() {
        list_fragment.clear();
        list_fragment.add(ChangeManagerFragmnet.getInstance(1));    //待交接
        list_fragment.add(ChangeManagerFragmnet2.getInstance(2));    //已交接
        list_title.clear();
        list_title.add("待交接");
        list_title.add("已交接");
        SimpleFragmentPagerAdapter pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(),
                ChangeManagerdActivity.this, list_fragment, list_title);
        viewPager.setAdapter(pagerAdapter);

        sltTitle.setViewPager(viewPager);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick(R.id.iv_back)
    public void onClick(View view) {
        finish();
    }

    @OnClick({R.id.rl_select_time, R.id.rl_select_express, R.id.bt_select})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_select_time:
                showTimeSelectDialog();     //时间筛选
                break;
            case R.id.rl_select_express:   //筛选快递公司
                showSelectPop(view);
                break;
            case R.id.bt_select:
                selectData();      //根据筛选条件筛选
                break;
        }
    }

    private void selectData() {
        sendExpressid();
    }

    private void showTimeSelectDialog() {
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

        timePickerDate = new TimePickerBuilder(this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                String current = DateUtil.dayDate(date);
                startTime = DateUtil.getSomeDayStamp(current);
                endTime = DateUtil.getSomeDayStamp(DateUtil.dayDateEnd(date));
                tvTimeSelect.setText(DateUtil.getStrDate(date, "yyyy-MM-dd"));
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
    }
}
