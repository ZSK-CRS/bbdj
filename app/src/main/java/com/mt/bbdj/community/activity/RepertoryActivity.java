package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.flyco.tablayout.SlidingTabLayout;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.ExpressLogo;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.baseconfig.view.MyPopuwindow;
import com.mt.bbdj.community.adapter.SimpleFragmentPagerAdapter;
import com.mt.bbdj.community.adapter.SimpleStringAdapter;
import com.mt.bbdj.community.fragment.FinishHandleFragment;
import com.mt.bbdj.community.fragment.RepertoryFragment;
import com.mt.bbdj.community.fragment.WaitCollectFragment;
import com.mt.bbdj.community.fragment.WaitMimeographFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RepertoryActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.slt_title)
    SlidingTabLayout sltTitle;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tv_fast_select)
    ImageView tvFastSelect;                   //筛选快递公司

    private List<HashMap<String, String>> mFastData = new ArrayList<>();    //快递公司

    private ArrayList<Fragment> list_fragment = new ArrayList<>();       //定义要装fragment的列表
    private ArrayList<String> list_title = new ArrayList<>();            //定义标题
    private SimpleFragmentPagerAdapter pagerAdapter;
    private int currentItem;
    private MyPopuwindow popupWindow;
    private View selectView;
    private ExpressLogoDao mExpressLogoDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repertory);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initParams();
        initData();
        initListener();
        initView();
    }

    private void initListener() {
        //筛选快递
        tvFastSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //筛选快递
                showSelectPop(view);
            }
        });
    }


    private void showSelectPop(View view) {
        popupWindow.showAsDropDown(view);
    }

    private void initParams() {
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        mExpressLogoDao = daoSession.getExpressLogoDao();
    }


    private void initSelectPop() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            selectView = getLayoutInflater().inflate(R.layout.fast_layout, null);
            RecyclerView fastList = selectView.findViewById(R.id.tl_fast_list);
            initRecycler(fastList);
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

    private void initRecycler(RecyclerView fastList) {
        mFastData.clear();
        //查询快递公司的信息
        List<ExpressLogo> expressLogoList = mExpressLogoDao.queryBuilder()
                .where(ExpressLogoDao.Properties.Property.eq(1))
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
                //选中的快递公司id
                String express_id = mFastData.get(position).get("express_id");
                sendExpressid(express_id);    //向对应的界面发送快递公司消息
                popupWindow.dismiss();
            }
        });

        fastList.setAdapter(goodsAdapter);
        fastList.addItemDecoration(new MarginDecoration(this));
        fastList.setLayoutManager(new LinearLayoutManager(this));
        goodsAdapter.notifyDataSetChanged();
    }


    private void sendExpressid(String express_id) {
        int target = 300;
        if (currentItem == 0) {
            target = 300;
        }
        if (currentItem == 1) {
            target = 301;
        }
        EventBus.getDefault().post(new TargetEvent(target, express_id));
    }

    private void initView() {
        initFragment();    //初始化填充的fragment
        initSelectPop();    //初始化快递公司列表
    }

    private void initFragment() {
        list_fragment.clear();
        list_fragment.add(RepertoryFragment.getInstance(0));    //入库
        list_fragment.add(RepertoryFragment.getInstance(1));    //出库
        list_title.clear();
        list_title.add("入库");
        list_title.add("出库");
        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(),
                RepertoryActivity.this, list_fragment, list_title);
        viewPager.setAdapter(pagerAdapter);

        sltTitle.setViewPager(viewPager);

        viewPager.setCurrentItem(currentItem);

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


    private void initData() {
        Intent mIntent = getIntent();
        currentItem = mIntent.getIntExtra("currentItem", 0);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(TargetEvent targetEvent) {
        if (1 == targetEvent.getTarget()) {
            viewPager.setCurrentItem(2);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mFastData.clear();
        mFastData = null;
    }
}
