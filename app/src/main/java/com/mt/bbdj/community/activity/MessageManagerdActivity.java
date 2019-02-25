package com.mt.bbdj.community.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.flyco.tablayout.SlidingTabLayout;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.community.adapter.SimpleFragmentPagerAdapter;
import com.mt.bbdj.community.fragment.FinishHandleFragment;
import com.mt.bbdj.community.fragment.MessageSendFragment;
import com.mt.bbdj.community.fragment.WaitCollectFragment;
import com.mt.bbdj.community.fragment.WaitMimeographFragment;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MessageManagerdActivity extends BaseActivity {

    @BindView(R.id.slt_title)
    SlidingTabLayout sltTitle;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private ArrayList<Fragment> list_fragment = new ArrayList<>();       //定义要装fragment的列表
    private ArrayList<String> list_title = new ArrayList<>();            //定义标题

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_managerd);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        initFragment();
    }

    private void initFragment() {
        list_fragment.clear();
        list_fragment.add(MessageSendFragment.getInstance(2));    //发送失败
        list_fragment.add(MessageSendFragment.getInstance(1));    //发送成功
        list_title.clear();
        list_title.add("发送失败");
        list_title.add("发送成功");
        SimpleFragmentPagerAdapter pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(),
                MessageManagerdActivity.this, list_fragment, list_title);
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
}
