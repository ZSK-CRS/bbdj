package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.EventLog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.GoodsMessage;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.community.adapter.GoodsAdapter;
import com.mt.bbdj.community.adapter.GoodsTypeAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GoodsDetailActivity extends BaseActivity {

    @BindView(R.id.iv_goods_detail)
    ImageView ivGoodsDetail;
    @BindView(R.id.rl_back)
    RelativeLayout rlBack;
    @BindView(R.id.tv_content)
    AppCompatTextView tvContent;
    @BindView(R.id.tv_money_title)
    TextView tvMoneyTitle;
    @BindView(R.id.tv_time_title)
    TextView tvTimeTitle;
    @BindView(R.id.tv_goods_money)
    TextView tvGoodsMoney;
    @BindView(R.id.tv_tv_goods_name)
    TextView tvTvGoodsName;

    @BindView(R.id.gv_goods)
    RecyclerView goodsRecycler;

    private RelativeLayout rl_back;
    private UserBaseMessageDao mUserMessageDao;
    private RequestQueue mRequestQueue;

    private final int REQUEST_GOODS_DETAIL = 200;    //商品详情
    private final int REQUEST_JOIN_WANT = 300;    //加入购物车
    private String product_id;
    private GoodsTypeAdapter goodsAdapter;

    private String genre_id;     //型号id

    private List<HashMap<String, String>> mGoodsData = new ArrayList<>();    //商品类型数据
    private WaitDialog waitDialog;
    private String user_id;
    private String show_images;
    private String product_name;
    private String goodsType;
    private String goods_price;
    private String genre_name;

    public static void startAction(Context context, String product_id) {
        Intent itent = new Intent(context, GoodsDetailActivity.class);
        itent.putExtra("product_id", product_id);
        context.startActivity(itent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detail);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initParams();
        initView();
        requestData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(TargetEvent targetEvent) {
        if (targetEvent.getTarget() == TargetEvent.DESTORY_GOODS_DETAIL) {
            finish();
        }
    }


    @OnClick({R.id.rl_back, R.id.bt_join_gouwu, R.id.tv_payfor})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rl_back:
                finish();
                break;
            case R.id.bt_join_gouwu:
                handleJoinGoods();   //加入购物车
                break;
            case R.id.tv_payfor:
                handlePayforAtonce();   //立刻结算
                break;
        }
    }

    private void handlePayforAtonce() {
        GoodsMessage goodsMessage = new GoodsMessage();
        GoodsMessage.Goods goods = new GoodsMessage.Goods();
        goods.setGoodsName(product_name);
        goods.setGoodsPrice(goods_price);
        goods.setGoodsTypeName(goodsType);
        goods.setGoodsPicture(show_images);
        goods.setGoodsID(product_id);
        goods.setGenre_id(genre_id);
        List<GoodsMessage.Goods> goodsList = new ArrayList<>();
        goodsList.add(goods);
        goodsMessage.setGoodsList(goodsList);
        Intent intent = new Intent(GoodsDetailActivity.this,PayforOrderActivity.class);
        intent.putExtra("goods",goodsMessage);
        startActivity(intent);
    }

    private void handleJoinGoods() {
        Request<String> request = NoHttpRequest.joinGoodsRequest(user_id, product_id, genre_id);
        mRequestQueue.add(REQUEST_JOIN_WANT, request, onResponseListener);
    }

    private void requestData() {
        Request<String> request = NoHttpRequest.getGoodsListDetailRequest(user_id, product_id);
        mRequestQueue.add(REQUEST_GOODS_DETAIL, request, onResponseListener);
    }

    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            waitDialog = WaitDialog.show(GoodsDetailActivity.this, "加载中...").setCanCancel(true);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "GoodsDetailActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.getString("code");
                JSONObject data = jsonObject.getJSONObject("data");
                handleResult(what, data);

            } catch (JSONException e) {
                e.printStackTrace();
                waitDialog.doDismiss();
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
    };

    private void handleResult(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case REQUEST_GOODS_DETAIL:     //获取商品详情
                setGoodsDetails(jsonObject);
                break;
            case REQUEST_JOIN_WANT:        //加入购物车
                joinGoods(jsonObject);
                break;
        }
    }

    private void joinGoods(JSONObject jsonObject) {
        ToastUtil.showShort("添加购物车成功！");
    }

    private void setGoodsDetails(JSONObject jsonObject) throws JSONException {
        JSONObject productinfo = jsonObject.getJSONObject("productinfo");
        product_name = productinfo.getString("product_name");
        show_images = productinfo.getString("show_images");
        String content = productinfo.getString("content");

        //设置商品的图片
        Glide.with(this).load(show_images).error(R.drawable.ic_no_picture).into(ivGoodsDetail);
        tvTvGoodsName.setText(product_name);
        tvContent.setText(content);

        //设置商品的型号
        JSONArray jsonArray = jsonObject.getJSONArray("genre");
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            String id = jsonObject1.getString("id");
            String genre_name = jsonObject1.getString("genre_name");
            String price = jsonObject1.getString("price");
            HashMap<String, String> map = new HashMap<>();
            map.put("id", id);
            map.put("genre_name", genre_name);
            map.put("price", price);
            mGoodsData.add(map);
            map = null;
        }

        if (mGoodsData.size() != 0) {
            tvGoodsMoney.setText("￥ " + mGoodsData.get(0).get("price"));
            tvMoneyTitle.setText("￥ " + mGoodsData.get(0).get("price"));
            genre_id = mGoodsData.get(0).get("id");
            goods_price = mGoodsData.get(0).get("price");
            goodsType = mGoodsData.get(0).get("genre_name");
        }
        goodsAdapter.notifyDataSetChanged();
    }

    private void initView() {
        goodsAdapter = new GoodsTypeAdapter(this, mGoodsData);
        goodsRecycler.setAdapter(goodsAdapter);
        goodsRecycler.addItemDecoration(new MarginDecoration(this));
        goodsRecycler.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        goodsAdapter.notifyDataSetChanged();
        goodsAdapter.setOnItemClickListener(new GoodsTypeAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                HashMap<String, String> map = mGoodsData.get(position);
                goodsAdapter.setCheckPosition(position);
                String price = map.get("price");
                goodsType = map.get("genre_name");
                tvGoodsMoney.setText("￥ " + price);
                tvMoneyTitle.setText("￥ " + price);
                genre_id = map.get("id");
                goods_price = map.get("price");
            }
        });
    }

    private void initParams() {
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = daoSession.getUserBaseMessageDao();
        mRequestQueue = NoHttp.newRequestQueue();

        Intent intent = getIntent();
        product_id = intent.getStringExtra("product_id");


        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mGoodsData = null;
    }
}
