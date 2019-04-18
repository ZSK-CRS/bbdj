package com.mt.bbdj.community.activity;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.community.adapter.SimpleFragmentPagerAdapter;
import com.mt.bbdj.community.fragment.FinishHandleFragment;
import com.mt.bbdj.community.fragment.GlobalSearchReceiveFragment;
import com.mt.bbdj.community.fragment.GlobalSearchSendFragment;
import com.mt.bbdj.community.fragment.WaitCollectFragment;
import com.mt.bbdj.community.fragment.WaitMimeographFragment;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class GlobalSearchActivity extends BaseActivity {
    @BindView(R.id.slt_title)
    SlidingTabLayout sltTitle;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.textview_serach)
    TextView textViewSerach;
    @BindView(R.id.tv_cannel)
    TextView tvCannel;   //取消

    private ArrayList<Fragment> list_fragment = new ArrayList<>();       //定义要装fragment的列表
    private ArrayList<String> list_title = new ArrayList<>();            //定义标题
    private SimpleFragmentPagerAdapter pagerAdapter;

    private int currentItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_search);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(GlobalSearchActivity.this);
        ButterKnife.bind(this);
        initView();
        initListener();
    }

    private void initListener() {
        //搜索栏
        textViewSerach.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
                    // 先隐藏键盘
                    ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                            getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                    String keywords = textViewSerach.getText().toString().trim();

                    if (!"".equals(keywords)) {
                        EventBus.getDefault().post(new TargetEvent(TargetEvent.SEARCH_GLOBAL, keywords));
                        return true;
                    }
                }
                return false;
            }
        });

        tvCannel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewSerach.setText("");
                EventBus.getDefault().post(new TargetEvent(TargetEvent.CLEAR_SEARCH_DATA));
            }
        });
    }

    private void initView() {
        list_fragment.clear();
        list_fragment.add(GlobalSearchSendFragment.getInstance());    //寄件
        list_fragment.add(GlobalSearchReceiveFragment.getInstance());    //派件
        list_title.clear();
        list_title.add("寄件");
        list_title.add("派件");
        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(),
                GlobalSearchActivity.this, list_fragment, list_title);
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
}
