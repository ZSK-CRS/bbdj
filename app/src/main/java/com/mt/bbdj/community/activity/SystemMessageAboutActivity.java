package com.mt.bbdj.community.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.view.BadgeView;
import com.mt.bbdj.community.adapter.SimpleFragmentPagerAdapter;
import com.mt.bbdj.community.fragment.MessageCenterFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;

public class SystemMessageAboutActivity extends BaseActivity {
    @BindView(R.id.slt_title)
    SlidingTabLayout sltTitle;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.bt_notification_number)
    Button btNotificationNumber;
    @BindView(R.id.bt_system_number)
    Button btSystemNumber;

    private ArrayList<Fragment> list_fragment = new ArrayList<>();       //定义要装fragment的列表
    private ArrayList<String> list_title = new ArrayList<>();            //定义标题
    private QBadgeView qBadgeView;
    private Badge mBadge;
    private Badge[] badges = new Badge[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_message_about);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        initBrage();
        initFragment();
        initListener();
    }

    private void initListener() {
        btNotificationNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return true;
            }
        });
        btSystemNumber.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                return true;
            }
        });
    }

    private void initBrage() {
        badges[0] = new QBadgeView(this).bindTarget(btSystemNumber)
                .setGravityOffset(33,5,true)
                .setBadgeBackgroundColor(getResources().getColor(R.color.yellow));
        badges[1] = new QBadgeView(this).bindTarget(btNotificationNumber)
                .setGravityOffset(33,5,true)
                .setBadgeBackgroundColor(getResources().getColor(R.color.yellow));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receivMessage(TargetEvent targetEvent) {
        if (targetEvent.getTarget() == TargetEvent.SYSTEM_MESSAGE_REFRESH) {
            if ("0".equals(targetEvent.getData())) {
                badges[0].hide(true);
                return ;
            }
            badges[0].setBadgeText(targetEvent.getData());

        } else if (targetEvent.getTarget() == TargetEvent.NOTIFICATION_REFRESH) {
            if ("0".equals(targetEvent.getData())) {
                badges[1].hide(true);
                return ;
            }
            badges[1].setBadgeText(targetEvent.getData());
        }
    }



    private void initFragment() {
        list_fragment.clear();
        list_fragment.add(MessageCenterFragment.getInstance(0));    //通知公告
        list_fragment.add(MessageCenterFragment.getInstance(1));    //消息中心
        //  list_fragment.add(MessageCenterFragment.getInstance(2));    //异常件消息
        list_title.clear();
        list_title.add("通知公告");
        list_title.add("系统消息");
        //  list_title.add("异常件消息");
        SimpleFragmentPagerAdapter pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(),
                SystemMessageAboutActivity.this, list_fragment, list_title);
        viewPager.setAdapter(pagerAdapter);
        sltTitle.setViewPager(viewPager);
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

   /* @OnClick({R.id.bt_notification_number, R.id.bt_system_number})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_notification_number:

                break;
            case R.id.bt_system_number:
                break;
        }
    }*/
}
