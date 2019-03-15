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
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.CategoryBean;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.GoodsHomeAdapter;
import com.mt.bbdj.community.adapter.GoodsMenuAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;


import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MatterShopActivity extends BaseActivity {

    private RecyclerView homeList;
    private RecyclerView menuLIst;
    private RelativeLayout icBack;
    private RelativeLayout shopCart;      //购物车
    private RelativeLayout rlGoodsWants;
    private TextView mTitile;
    private List<String> menuData = new ArrayList<>();
    private List<CategoryBean.DataBean> homeData = new ArrayList<>();
    private GoodsMenuAdapter mGoodsMenuAdapter;
    private GoodsHomeAdapter mGoodsHomeAdapter;
    private ArrayList<Integer> showTitle = new ArrayList<>();
    private LinearLayoutManager mHomeLayoutManager;
    private int currentItem;
    private UserBaseMessageDao mUserMessageDao;
    private RequestQueue mRequestQueue;

    private final int REQUEST_GOODS = 100;    //获取商品列表
    private WaitDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matter_shop);
        initView();
        initParams();
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

        //购物车
        rlGoodsWants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MatterShopActivity.this,ShopCarActivity.class);
                startActivity(intent);
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
                    GoodsDetailActivity.startAction(MatterShopActivity.this,dataListBean.getId());
                }
            }
        });

    }

    private void initParams() {
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = daoSession.getUserBaseMessageDao();
        mRequestQueue = NoHttp.newRequestQueue();
    }


    private void loadData() {
        String user_id = "";
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        Request<String> request = NoHttpRequest.getGoodsListRequest(user_id);
        mRequestQueue.add(REQUEST_GOODS, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                waitDialog = WaitDialog.show(MatterShopActivity.this,"加载中...").setCanCancel(true);
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "MatterShopActivity::" + response.get());
                try {
                    org.json.JSONObject jsonObject = new org.json.JSONObject(response.get());
                    String code = jsonObject.getString("code");
                    JSONArray dataArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < dataArray.length(); i++) {
                        org.json.JSONObject jsonObject1 = dataArray.getJSONObject(i);
                        String type_Name = jsonObject1.getString("type_name");
                        menuData.add(type_Name);    //信封、面单、编织袋、

                        JSONArray productlist =  jsonObject1.getJSONArray("productlist");
                        CategoryBean.DataBean product = new CategoryBean.DataBean();
                        List<CategoryBean.DataBean.DataListBean> dataListBeans = new ArrayList<>();
                        for (int j = 0;j < productlist.length();j++) {
                            org.json.JSONObject productListObj = productlist.getJSONObject(j);
                            String product_name = productListObj.getString("product_name");
                            String id = productListObj.getString("id");
                            String thumb = productListObj.getString("thumb");
                            CategoryBean.DataBean.DataListBean productList = new CategoryBean.DataBean.DataListBean();
                            productList.setId(id);
                            productList.setImgURL(thumb);
                            productList.setTitle(product_name);
                            dataListBeans.add(productList);
                            productList = null;
                        }
                        product.setDataList(dataListBeans);
                        product.setMenuTitle(type_Name);

                        homeData.add(product);
                        showTitle.add(i);
                    }

                    //设置短信面单

                    CategoryBean.DataBean pannel = new CategoryBean.DataBean();
                    List<CategoryBean.DataBean.DataListBean> dataListBeans2 = new ArrayList<>();
                    CategoryBean.DataBean.DataListBean productList2 = new CategoryBean.DataBean.DataListBean();
                    productList2.setId("");
                    productList2.setImgURL("");
                    productList2.setTitle("面单");
                    dataListBeans2.add(productList2);
                    pannel.setDataList(dataListBeans2);
                    pannel.setMenuTitle("面单");
                    homeData.add(pannel);

                    CategoryBean.DataBean message = new CategoryBean.DataBean();
                    List<CategoryBean.DataBean.DataListBean> dataListBeans = new ArrayList<>();
                    CategoryBean.DataBean.DataListBean productList = new CategoryBean.DataBean.DataListBean();
                    productList.setId("");
                    productList.setImgURL("");
                    productList.setTitle("短信");
                    dataListBeans.add(productList);
                    message.setDataList(dataListBeans);
                    message.setMenuTitle("短信");
                    homeData.add(message);

                    menuData.add("面单");
                    menuData.add("短信");
                    int index = showTitle.size();
                    showTitle.add(index);
                    showTitle.add(index+1);
                    mGoodsMenuAdapter.setSelectItem(0);
                    mTitile.setText(homeData.get(0).getMenuTitle());
                    mGoodsHomeAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                waitDialog.doDismiss();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                waitDialog.doDismiss();
            }

            @Override
            public void onFinish(int what) {
                waitDialog.doDismiss();
            }
        });

      /*  String json = StringUtil.getJson(this, "category.json");
        CategoryBean categoryBean = JSONObject.parseObject(json, CategoryBean.class);*/

    }

    private void initView() {
        initMenuList();   //初始化左侧菜单列表
        initHomeList();   //初始胡右侧菜单列表
        initOther();
    }

    private void initOther() {
        icBack = findViewById(R.id.iv_back);
        mTitile = findViewById(R.id.tv_titile);
        rlGoodsWants = findViewById(R.id.rl_goods_want);
        shopCart = findViewById(R.id.rl_goods_want);
        icBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        shopCart = findViewById(R.id.rl_goods_want);
        shopCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MatterShopActivity.this,PayforOrderFromShopingCardActivity.class);
                startActivity(intent);
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
