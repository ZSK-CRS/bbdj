package com.mt.bbdj.baseconfig.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.application.MyApplication;
import com.mt.bbdj.baseconfig.model.Constant;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

public class SlideActivity extends AppCompatActivity {

    private LinearLayout dotSlide;
    private ViewPager mViewPager;
    private List<View> pageViews = new ArrayList<>();
    private ImageView[] imageViews = new ImageView[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide);

        initView();
    }

    private void initView() {
        mViewPager = findViewById(R.id.vp_slide);
        dotSlide = findViewById(R.id.ll_dot_slide);
        View one = LayoutInflater.from(this).inflate(R.layout.layout_slide_one, null);
        View two = LayoutInflater.from(this).inflate(R.layout.layout_slide_two, null);
        View three = LayoutInflater.from(this).inflate(R.layout.layout_slide_three, null);
        pageViews.add(one);
        pageViews.add(two);
        pageViews.add(three);
        initClick(three);

        //设置圆点指示器
        setDotIndicator(dotSlide);
        //绑定适配器
        mViewPager.setAdapter(mPageAdapter);
        //切换监听
        mViewPager.addOnPageChangeListener(onPageChangeListener);
    }

    private void initClick(View three) {
        Button button = three.findViewById(R.id.bt_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SlideActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            View v = pageViews.get(position);
            if (position == imageViews.length - 1) {
                dotSlide.setVisibility(View.GONE);
            } else {
                dotSlide.setVisibility(View.VISIBLE);
            }
            for (int i = 0; i < imageViews.length; i++) {
                imageViews[position]
                        .setBackgroundResource(R.drawable.circle_select);
                if (position != i) {
                    imageViews[i]
                            .setBackgroundResource(R.drawable.circle_default);
                }
            }


        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    PagerAdapter mPageAdapter = new PagerAdapter() {
        @Override
        public int getCount() {
            return pageViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(pageViews.get(position));
        }

        // 返回一个对象，这个对象表明了PagerAdapter适配器选择哪个对象放在当前的ViewPager中
        public Object instantiateItem(View arg0, int arg1) {
            ((ViewPager) arg0).addView(pageViews.get(arg1));
            return pageViews.get(arg1);
        }
    };

    private void setDotIndicator(LinearLayout dotSlide) {
        for (int i = 0; i < pageViews.size(); i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(10, 0, 10, 0);
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);

            imageViews[i] = imageView;
            if (i == 0) {
                //默认选中第一张图片
                imageViews[i].setBackgroundResource(R.drawable.circle_select);
            } else {
                imageViews[i].setBackgroundResource(R.drawable.circle_default);
            }
            dotSlide.addView(imageViews[i]);
        }
    }
}
