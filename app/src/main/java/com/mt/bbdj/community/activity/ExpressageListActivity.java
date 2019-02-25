package com.mt.bbdj.community.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RelativeLayout;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.community.adapter.SimpleFragmentPagerAdapter;
import com.mt.bbdj.community.fragment.ExpressageFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//快递公司列表
public class ExpressageListActivity extends BaseActivity implements OnTabSelectListener {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.slt_title)
    SlidingTabLayout sltTitle;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    private ArrayList<Fragment> list_fragment = new ArrayList<>();       //定义要装fragment的列表
    private ArrayList<String> list_title = new ArrayList<>();            //定义要装fragment的列表
    private SimpleFragmentPagerAdapter pagerAdapter;
    private View decorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expressage_list);
        ButterKnife.bind(this);

        initView();
    }

    private void initView() {
        initFragment();    //初始化填充的fragment
    }

    private void initFragment() {
        list_fragment.clear();
        list_fragment.add(ExpressageFragment.getInstance(1));    //快递公司
        list_fragment.add(ExpressageFragment.getInstance(2));    //物流公司
        list_title.clear();
        list_title.add("快递公司");
        list_title.add("物流公司");
        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(),
                ExpressageListActivity.this, list_fragment, list_title);
        viewPager.setAdapter(pagerAdapter);

        /**自定义部分属性*/
        decorView = getWindow().getDecorView();
        sltTitle.setViewPager(viewPager);
        sltTitle.setOnTabSelectListener(this);
    }

    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }

    @OnClick(R.id.iv_back)
    public void viewOnClick(){
        finish();
    }

}
