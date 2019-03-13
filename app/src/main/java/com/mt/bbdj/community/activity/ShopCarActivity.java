package com.mt.bbdj.community.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kongzue.dialog.v2.SelectDialog;
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
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.HaveFinishAdapter;
import com.mt.bbdj.community.adapter.ShopCardAdapter;
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

public class ShopCarActivity extends BaseActivity implements View.OnClickListener {

    private RelativeLayout ivBack;   //返回界面
    private RecyclerView rlShopGoodsName;  // 购物车列表
    private Button btCommit;   //提交订单
    private TextView tvCheck;
    private ImageView ivCheckSelect;    //选择项

    private TextView tvDeleteGoods;    //删除商品

    private TextView allMoneyTV;    //合计金额

    private boolean isCheckAll = true;     //是否全选
    private ShopCardAdapter mAdapter;

    private final int SELECT_ADDRESS = 1;     //选择收货地址

    private List<HashMap<String, String>> mList = new ArrayList<>();
    private RequestQueue mRequestQueue;
    private UserBaseMessageDao userBaseMessageDao;
    private UserBaseMessage userBaseMessage;
    private String user_id;
    private WaitDialog dialogLoading;

    private final int REQUEST_GET_GOODS_LIST = 100;     //获取购物车列表
    private final int REQUEST_DELETE_GOODS = 200;    //删除物品
    private final int REQUEST_CHANGE_GOODS_NUMBER = 300;    //修改数量
    private String cartId;
    private float allMoney;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_car);
        EventBus.getDefault().register(this);
        initView();
        initParams();
        initRecycler();    //初始化列表
        initListener();   //监听事件
        requestData();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(TargetEvent targetEvent) {
        if (targetEvent.getTarget() == TargetEvent.DESTORY_GOODS_FROM_CART) {
            finish();
        }
    }

    private void initParams() {
        mRequestQueue = NoHttp.newRequestQueue();
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        userBaseMessageDao = daoSession.getUserBaseMessageDao();
        List<UserBaseMessage> userBaseMessages = userBaseMessageDao.queryBuilder().list();
        if (userBaseMessages.size() != 0) {
            userBaseMessage = userBaseMessages.get(0);
            user_id = userBaseMessage.getUser_id();
        }
    }

    private void requestData() {
        Request<String> request = NoHttpRequest.getShopCarGoodsRequest(user_id);
        mRequestQueue.add(REQUEST_GET_GOODS_LIST, request, onResponseListener);
    }


    private OnResponseListener<String> onResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            dialogLoading = WaitDialog.show(ShopCarActivity.this, "加载中...").setCanCancel(true);
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "PayforOrderActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.getString("code");
                String msg = jsonObject.getString("msg");
                if ("5001".equals(code)) {
                    handResult(what, jsonObject);
                } else {
                    ToastUtil.showShort(msg);
                }
                accountMoney();
            } catch (JSONException e) {
                e.printStackTrace();
                dialogLoading.doDismiss();
            }
            dialogLoading.doDismiss();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            dialogLoading.doDismiss();
        }

        @Override
        public void onFinish(int what) {
            dialogLoading.doDismiss();
        }
    };

    private void handResult(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case REQUEST_GET_GOODS_LIST:   //获取购物车列表
                handleGoodsList(jsonObject);
                break;
            case REQUEST_DELETE_GOODS:    //删除商品
                handleDeleteGoods(jsonObject);
                break;
            case REQUEST_CHANGE_GOODS_NUMBER:   //修改数量

                break;
        }
    }

    private void handleDeleteGoods(JSONObject jsonObject) throws JSONException {
        String msg = jsonObject.getString("msg");
        ToastUtil.showShort(msg);
        requestData();    //刷新一下数据
    }

    private void handleGoodsList(JSONObject jsonObject) throws JSONException {
        JSONArray data = jsonObject.getJSONArray("data");
        mList.clear();
        for (int i = 0; i < data.length(); i++) {
            JSONObject goodsObj = data.getJSONObject(i);
            String cart_id = goodsObj.getString("cart_id");
            String product_id = goodsObj.getString("product_id");
            String product_name = goodsObj.getString("product_name");
            String thumb = goodsObj.getString("thumb");
            String genre_id = goodsObj.getString("genre_id");
            String genre_name = goodsObj.getString("genre_name");
            String price = goodsObj.getString("price");
            String number = goodsObj.getString("number");
            if ("0".equals(number) || "null".equals(number)) {
                number = "1";
            }
            HashMap<String, String> map = new HashMap<>();
            map.put("cart_id", cart_id);
            map.put("product_id", product_id);
            map.put("product_name", product_name);
            map.put("thumb", thumb);
            map.put("genre_id", genre_id);
            map.put("price", price);
            map.put("number", number);
            map.put("genre_name", genre_name);
            map.put("selectState", "0");
            mList.add(map);
            map = null;
        }
        mAdapter.notifyDataSetChanged();
    }


    private void initRecycler() {
        rlShopGoodsName.setFocusable(false);
        //initTemparayData();   //模拟数据
        mAdapter = new ShopCardAdapter(this, mList);
        rlShopGoodsName.setNestedScrollingEnabled(false);
        rlShopGoodsName.addItemDecoration(new MyDecoration(this,
                LinearLayoutManager.VERTICAL, Color.parseColor("#e9e9e9"), 1));
        rlShopGoodsName.setLayoutManager(new LinearLayoutManager(this));

        rlShopGoodsName.setAdapter(mAdapter);
    }

    private void initListener() {
        tvCheck.setOnClickListener(this);
        ivCheckSelect.setOnClickListener(this);
        tvDeleteGoods.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        btCommit.setOnClickListener(this);

        //选中条目
        mAdapter.setOnItemSelectListener(new ShopCardAdapter.OnItemSelectListener() {
            @Override
            public void onItemSelect(int position) {
                HashMap<String, String> map = mList.get(position);
                String selectState = map.get("selectState");
                if ("0".equals(selectState)) {
                    map.put("selectState", "1");
                } else {
                    map.put("selectState", "0");
                }

                mAdapter.notifyDataSetChanged();
                ((SimpleItemAnimator) rlShopGoodsName.getItemAnimator()).setSupportsChangeAnimations(false);
                mAdapter.notifyItemRangeChanged(0, mList.size());
                accountMoney();
            }
        });

        //数量变化
        mAdapter.setOnItemNumberChangeListener(new ShopCardAdapter.OnItemNumberChangeListener() {
            @Override
            public void onChange(int position, int value) {
                HashMap<String, String> map = mList.get(position);
                String cart_id = map.get("cart_id");
                map.put("number", value + "");
                changeGoodsNumer(user_id, cart_id, value);
                mAdapter.notifyDataSetChanged();
                accountMoney();     //计算总金额
            }
        });
    }

    private void accountMoney() {
        allMoney = 0;
        float singleMoney = 0;
        for (HashMap<String, String> map : mList) {

            String selectState = map.get("selectState");
            //表示的是选中的计算价格
            if ("1".equals(selectState)) {
                String price = map.get("price");
                String numberStr = map.get("number");
                int number = Integer.parseInt(numberStr);
                if (price == null || "".equals(price)) {
                    continue;
                }
                float money = Float.parseFloat(price);
                singleMoney = money * number;
                allMoney += singleMoney;
            }

        }
        allMoneyTV.setText(allMoney + "");
    }


    private void initView() {
        ivBack = findViewById(R.id.iv_back);
        rlShopGoodsName = findViewById(R.id.rl_goods_want);
        allMoneyTV = findViewById(R.id.tv_all_money);
        btCommit = findViewById(R.id.tv_payfor);
        tvCheck = findViewById(R.id.tv_check);
        ivCheckSelect = findViewById(R.id.iv_select_check);
        tvDeleteGoods = findViewById(R.id.tv_delete_goods);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_check:
            case R.id.iv_select_check:
                handleSelectState();      //处理选择状态   全选/取消
                break;
            case R.id.tv_delete_goods:    //删除商品
                deleteGoods();
                break;
            case R.id.tv_payfor:    //提交订单
                commitOrder();
                break;
        }
    }

    private void commitOrder() {
        GoodsMessage goodsMessage = new GoodsMessage();
        List<GoodsMessage.Goods> goodsList = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        //找到所有的选中的商品
        for (HashMap<String, String> map : mList) {
            String selectState = map.get("selectState");
            String cart_id = map.get("cart_id");
            if ("1".equals(selectState)) {
                GoodsMessage.Goods product = new GoodsMessage.Goods();
                product.setGoodsName(map.get("product_name"));
                product.setGoodsPrice(map.get("price"));
                product.setGoodsTypeName(map.get("genre_name"));
                product.setGoodsPicture(map.get("thumb"));
                product.setGoodsID(map.get("product_id"));
                String number = map.get("number");
                product.setGoodsNumber(number);
                product.setGenre_id(map.get("genre_id"));
                goodsList.add(product);
                sb.append(cart_id);
                sb.append(",");
            }
        }
        cartId = sb.toString();
        if ("".equals(cartId) || cartId == null) {
            ToastUtil.showShort("请先选择商品！");
            return;
        }
        cartId = cartId.substring(0, cartId.lastIndexOf(","));
        if (goodsList.size() == 0) {
            ToastUtil.showShort("请选择要购买的商品");
            return;
        }

        if (!isHaveAddress()) {    //判断有没有设置地址
            showAddressDialog();
        } else {
            goodsMessage.setGoodsList(goodsList);
            Intent intent = new Intent(ShopCarActivity.this, PayforOrderFromShopingCardActivity.class);
            intent.putExtra("goods", goodsMessage);
            intent.putExtra("cart_id", cartId);
            intent.putExtra("payfor", allMoneyTV.getText().toString());
            startActivity(intent);
        }
    }

    private boolean isHaveAddress() {
        SharedPreferences preferences = SharedPreferencesUtil.getSharedPreference();
        String addres = preferences.getString("myaddress_address", "");
        if ("".equals(addres)) {
            return false;
        }
        return true;
    }

    private void showAddressDialog() {
        SelectDialog.show(this, "提示", "请先添加收货地址！", "确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(ShopCarActivity.this, MyAddressActivity.class);
                        startActivityForResult(intent, SELECT_ADDRESS);
                    }
                }, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCanCancel(true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }

    }

    private void deleteGoods() {
        StringBuilder sb = new StringBuilder();
        String cartId = "";
        for (HashMap<String, String> map : mList) {
            String selectState = map.get("selectState");
            String cart_id = map.get("cart_id");
            if ("1".equals(selectState)) {
                sb.append(cart_id);
                sb.append(",");
            }
        }
        cartId = sb.toString();
        cartId = cartId.substring(0, cartId.lastIndexOf(","));
        if ("".equals(cartId)) {
            return;
        }
        Request<String> request = NoHttpRequest.deleteGoodsRequest(user_id, cartId);
        mRequestQueue.add(REQUEST_DELETE_GOODS, request, onResponseListener);
    }

    private void handleSelectState() {
        if (isCheckAll) {
            ivCheckSelect.setBackgroundResource(R.drawable.ic_check_all);
            setAllCheck();
        } else {
            setAllunCheck();
            ivCheckSelect.setBackgroundResource(R.drawable.shap_circle_grey);
        }
        accountMoney();    //设置价格
        isCheckAll = !isCheckAll;
    }

    private void setAllunCheck() {
        for (HashMap<String, String> map : mList) {
            map.put("selectState", "0");
        }
        ((SimpleItemAnimator) rlShopGoodsName.getItemAnimator()).setSupportsChangeAnimations(false);
        mAdapter.notifyItemRangeChanged(0, mList.size());
    }

    private void setAllCheck() {
        for (HashMap<String, String> map : mList) {
            map.put("selectState", "1");
        }
        ((SimpleItemAnimator) rlShopGoodsName.getItemAnimator()).setSupportsChangeAnimations(false);
        mAdapter.notifyItemRangeChanged(0, mList.size());
    }

    private void changeGoodsNumer(String user_id, String cart_id, int value) {
        Request<String> request = NoHttpRequest.changeGoodsNumberRequest(user_id, cart_id, value);
        mRequestQueue.add(REQUEST_CHANGE_GOODS_NUMBER, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {

            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mList = null;
    }
}
