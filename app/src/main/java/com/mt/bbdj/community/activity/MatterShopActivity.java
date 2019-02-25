package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.EventLog;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.model.CategoryBean;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.GoodsHomeAdapter;
import com.mt.bbdj.community.adapter.GoodsMenuAdapter;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MatterShopActivity extends BaseActivity {

    private RecyclerView homeList;
    private RecyclerView menuLIst;
    private RelativeLayout icBack;
    private TextView mTitile;
    private List<String> menuData = new ArrayList<>();
    private List<CategoryBean.DataBean> homeData = new ArrayList<>();
    private GoodsMenuAdapter mGoodsMenuAdapter;
    private GoodsHomeAdapter mGoodsHomeAdapter;
    private ArrayList<Integer> showTitle;
    private LinearLayoutManager mHomeLayoutManager;
    private int currentItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matter_shop);
        initView();
        loadData();
        initListener();
    }

    private void initListener() {
        //左侧菜单的点击事件
        mGoodsMenuAdapter.setOnItemClickListener(new GoodsMenuAdapter.OnItemClickListener() {
            @Override
            public void onItenClick(int position) {
                mGoodsMenuAdapter.setSelectItem(position);
                mHomeLayoutManager.scrollToPositionWithOffset(showTitle.get(position), 0);
                mTitile.setText(menuData.get(position));
            }
        });

        //右侧物品列表的滑动
        homeList.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                //只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearLayoutManager = (LinearLayoutManager) layoutManager;
                    //获取第一个可见view的位置
                    int firstVisiablePosition = linearLayoutManager.findFirstVisibleItemPosition();
                    int lastVisiablePosition = linearLayoutManager.findLastVisibleItemPosition();
                    int current = showTitle.indexOf(firstVisiablePosition);

                    if (currentItem != current && current > 0) {
                        currentItem = current;
                        mTitile.setText(menuData.get(currentItem));
                        mGoodsMenuAdapter.setSelectItem(currentItem);
                    }

                }
                boolean isTop = recyclerView.canScrollVertically(-1);
                //  boolean isBottom = recyclerView.canScrollVertically(1);

                if (!isTop) {
                    mTitile.setText(menuData.get(0));
                    mGoodsMenuAdapter.setSelectItem(0);
                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }

        });

        mGoodsHomeAdapter.setOnItemClickListener(new GoodsHomeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position, int itemPosition) {
                List<CategoryBean.DataBean.DataListBean> dataList = homeData.get(position).getDataList();
                CategoryBean.DataBean.DataListBean dataListBean = dataList.get(itemPosition);
                int menuSize = homeData.size();
                Intent intent = new Intent();
                if (position == menuSize - 1) {
                    //跳转短信
                    intent.setClass(MatterShopActivity.this, MessageRechargePannelActivity.class);
                    startActivity(intent);
                } else if (position == menuSize - 2) {
                    //跳转面单
                    intent.setClass(MatterShopActivity.this, PannelRechargeActivity.class);
                    startActivity(intent);
                } else {
                    //商品详情
                    GoodsDetailActivity.startAction(MatterShopActivity.this);
                }

            }
        });

    }

    private void loadData() {
        String json = StringUtil.getJson(this, "category.json");
        CategoryBean categoryBean = JSONObject.parseObject(json, CategoryBean.class);
        showTitle = new ArrayList<>();
        for (int i = 0; i < categoryBean.getData().size(); i++) {
            CategoryBean.DataBean dataBean = categoryBean.getData().get(i);
            menuData.add(dataBean.getMenuTitle());
            showTitle.add(i);
            homeData.add(dataBean);
        }
        mGoodsMenuAdapter.setSelectItem(0);
        mTitile.setText(categoryBean.getData().get(0).getMenuTitle());
        mGoodsHomeAdapter.notifyDataSetChanged();
    }

    private void initView() {
        initMenuList();   //初始化左侧菜单列表
        initHomeList();   //初始胡右侧菜单列表
        initOther();
    }

    private void initOther() {
        icBack = findViewById(R.id.iv_back);
        mTitile = findViewById(R.id.tv_titile);
        icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void initMenuList() {
        menuLIst = findViewById(R.id.lv_menu);
        menuLIst.setLayoutManager(new LinearLayoutManager(this));
        mGoodsMenuAdapter = new GoodsMenuAdapter(menuData);
        menuLIst.setAdapter(mGoodsMenuAdapter);
    }

    private void initHomeList() {
        homeList = findViewById(R.id.lv_home);
        mHomeLayoutManager = new LinearLayoutManager(this);
        homeList.setLayoutManager(mHomeLayoutManager);
        mGoodsHomeAdapter = new GoodsHomeAdapter(this, homeData);
        homeList.setAdapter(mGoodsHomeAdapter);
    }

}
