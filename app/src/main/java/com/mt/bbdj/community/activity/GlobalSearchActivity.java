package com.mt.bbdj.community.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.flyco.tablayout.SlidingTabLayout;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.community.adapter.SimpleFragmentPagerAdapter;
import com.mt.bbdj.community.fragment.FinishHandleFragment;
import com.mt.bbdj.community.fragment.WaitCollectFragment;
import com.mt.bbdj.community.fragment.WaitMimeographFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GlobalSearchActivity extends BaseActivity {
    @BindView(R.id.slt_title)
    SlidingTabLayout sltTitle;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    private ArrayList<Fragment> list_fragment = new ArrayList<>();       //定义要装fragment的列表
    private ArrayList<String> list_title = new ArrayList<>();            //定义标题
    private SimpleFragmentPagerAdapter pagerAdapter;

    private int currentItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global_search);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        list_fragment.clear();
        list_fragment.add(WaitCollectFragment.getInstance());    //待收件
        list_fragment.add(FinishHandleFragment.getInstance());    //已处理
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
