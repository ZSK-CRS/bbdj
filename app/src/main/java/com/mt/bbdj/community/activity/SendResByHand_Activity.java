package com.mt.bbdj.community.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kongzue.dialog.v2.MessageDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.ExpressLogo;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.ExpressageEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.community.adapter.GoodsAdapter;
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

//手动寄件
public class SendResByHand_Activity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;     //返回键
    @BindView(R.id.ll_send_message)
    LinearLayout llSendMessage;    //选择寄件人
    @BindView(R.id.ll_receive_message)
    LinearLayout llReceiveMessage;   //选择收件人
    @BindView(R.id.iv_send_one)
    ImageView ivSendOne;
    @BindView(R.id.iv_send_two)
    ImageView ivSendTwo;
    @BindView(R.id.iv_send_three)
    ImageView ivSendThree;
    @BindView(R.id.iv_send_four)
    ImageView ivSendFour;
    @BindView(R.id.iv_send_five)
    ImageView ivSendFive;
    @BindView(R.id.tv_send_res)
    TextView tvSendRes;             //寄件人提示
    @BindView(R.id.tv_send_name)
    TextView tvSendName;        //寄件人姓名
    @BindView(R.id.tv_send_phone)
    TextView tvSendPhone;        //寄件人电话
    @BindView(R.id.tv_send_address)
    TextView tvSendAddress;      //寄件人地址
    @BindView(R.id.tv_receive_res)
    TextView tvReceiveRes;     //收件人提示
    @BindView(R.id.tv_receive_name)
    TextView tvReceiveName;      //收件人姓名
    @BindView(R.id.tv_receive_phone)
    TextView tvReceivePhone;     //收件人电话
    @BindView(R.id.tv_receive_address)
    TextView tvReceiveAddress;    //收件人地址
    @BindView(R.id.ll_send_layout)
    LinearLayout llSendLayout;
    @BindView(R.id.ll_receive_layout)
    LinearLayout llReceiveLayout;

    @BindView(R.id.fl_select_expressage)
    FrameLayout flSelectExpressage;       //选择快递
    @BindView(R.id.ll_expressage_title)
    LinearLayout flexpressTitle;
    @BindView(R.id.ll_pressage_layot)
    LinearLayout llselectExpressage;      //显示快递
    @BindView(R.id.iv_select_ic)
    ImageView ivSelectLogo;          //快递图标
    @BindView(R.id.tv_expressage_name)
    TextView tvExpressageName;      //快递名称

    @BindView(R.id.tv_goods)
    TextView tvGoods;           //商品名称
    @BindView(R.id.tv_goods_select)
    TextView tvGoodsSelect;

    @BindView(R.id.tv_is_identification)
    TextView isIdentification;    //认证标识
    @BindView(R.id.rl_ic_layout)
    RelativeLayout rlIcLayout;
    @BindView(R.id.et_weight)
    EditText etWeight;
    @BindView(R.id.et_mark)
    EditText etMark;

    @BindView(R.id.tv_money)
    TextView tvMoney;       //预估价格
    @BindView(R.id.tv_time)
    TextView tvTime;   //花费时间


    private RequestQueue mRequestQueue;
    private HkDialogLoading dialogLoading;

    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private ExpressLogoDao mExpressLogoDao;

    private List<HashMap<String, String>> mGoodsData = new ArrayList<>();    //商品类型数据

    private String[] logoPaths = new String[5];
    private ImageView[] logoImages;
    private PopupWindow popupWindow;

    private View selectView;

    private GoodsAdapter goodsAdapter;


    private final int SELECT_SEND_MESSAGE = 1;      //寄件人
    private final int SELECT_RECEIVE_MESSAGE = 2;    //收件人
    private final int SELECT_IDENTIFICATION = 3;    //身份认证

    private final int REQUEST_GOODS_REQUEST = 101;   //请求物品类型的请求
    private final int REQUEST_IS_IDENTIFY_REQUEST = 102;    //是否实名
    private final int REQUEST_COMMIT_ORDER = 103;   //提交订单
    private final int REQUEST_ESTIMATE = 104;    //预估价格

    private RecyclerView myGridView;
    private String book_id = "";    //寄件人地址id
    private String express_id = "";   //快递公司id
    private String receive_id = "";   //收件人地址id
    private String send_id = "";   //寄件人地址id
    private String type_id = "";   //物品类型id

    private String start_province = "";    //开始省

    private String start_city = "";   //开始城市

    private String end_province = "";  //目的地 省

    private String end_city = "";   //目的地 市

    private String mWeight = "";   //重量
    private String user_id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_send_res_by_hand_);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
        initListener();
        initRequest();    //初始化网络请求
    }

    private void initListener() {
        etWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mWeight = s.toString();
                if ("".equals(mWeight) || "0".equals(mWeight)) {
                    return;
                }
                mWeight = Integer.parseInt(mWeight) + "";
                requestPredictMoney();
            }
        });
    }

    //获取预估价格
    private void requestPredictMoney() {
        if ("".equals(user_id) || "".equals(express_id) || "".equals(start_province)
                || "".equals(start_city) || "".equals(end_province) || "".equals(end_city) || "".equals(mWeight)) {
            return;
        }
        Request<String> request = NoHttpRequest.getPredictMoneyRequest(user_id, express_id, start_province, start_city, end_province, end_city, mWeight);
        mRequestQueue.add(REQUEST_ESTIMATE, request, mOnresponseListener);
    }


    private void initRequest() {
        initRequestParams();    //初始化请求参数
        requestGoodsType();    //请求物品类型
    }

    private void requestIdentification() {
        Request<String> request = NoHttpRequest.isIdentifyRequest(user_id, book_id);
        mRequestQueue.add(REQUEST_IS_IDENTIFY_REQUEST, request, mOnresponseListener);
    }

    private void requestGoodsType() {
        String user_id = "";
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        Request<String> request = NoHttpRequest.getGoodsTypeRequest(user_id);
        mRequestQueue.add(REQUEST_GOODS_REQUEST, request, mOnresponseListener);
    }


    @OnClick({R.id.iv_back, R.id.ll_send_message, R.id.ll_receive_message, R.id.fl_select_expressage,
            R.id.ll_pressage_layot, R.id.ll_expressage_title, R.id.ll_select_goods, R.id.ll_identification,
            R.id.bt_commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_send_message:     //选择寄件人地址
                selectMessageAbout(SELECT_SEND_MESSAGE);
                break;
            case R.id.ll_receive_message:  //选择收件人地址
                selectMessageAbout(SELECT_RECEIVE_MESSAGE);
                break;
            case R.id.fl_select_expressage:   //选择快递公司列表
            case R.id.ll_pressage_layot:
            case R.id.ll_expressage_title:
                selectExpressage();
                break;
            case R.id.ll_select_goods:     //选择商品类型
                showSelectTypeDialog();
                break;
            case R.id.ll_identification:    //身份认证
                identificationID();
                break;
            case R.id.bt_commit:
                commitAllData();
                break;
        }
    }


    private void commitAllData() {
        String user_id = "";
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        String weight = etWeight.getText().toString();
        String mark = etMark.getText().toString();

        if (!isRightAboutOrderMessage()) {
            return;
        }
        if ("".equals(weight)) {
            ToastUtil.showShort("请填写重量！");
            return;
        }


        Request<String> request = NoHttpRequest.commitOrderRequest(user_id, express_id,
                send_id, receive_id, type_id, weight, mark);
        mRequestQueue.add(REQUEST_COMMIT_ORDER, request, mOnresponseListener);
    }

    private boolean isRightAboutOrderMessage() {
        if ("".equals(book_id) || "".equals(express_id) ||
                "".equals(receive_id) || "".equals(send_id) || "".equals(type_id)) {
            ToastUtil.showShort("请完善信息！");
            return false;
        }
        if (!"已认证".equals(isIdentification.getText().toString())) {
            ToastUtil.showShort("请先实名认证！");
            return false;
        }
        return true;
    }

    private void identificationID() {
        if ("".equals(tvSendName.getText().toString())) {
            ToastUtil.showShort("请先填写寄件人信息！");
            return;
        }

        if ("已认证".equals(isIdentification.getText().toString())) {
            return ;
        }
        Intent intent = new Intent(this, IdentificationActivity.class);
        intent.putExtra("come_type", true);
        intent.putExtra("book_id", book_id);
        startActivityForResult(intent, SELECT_IDENTIFICATION);
    }

    private void showSelectTypeDialog() {
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(ivBack, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case SELECT_SEND_MESSAGE:     //添加寄件人地址
                handleSendMessage(data);
                break;
            case SELECT_RECEIVE_MESSAGE:   //添加收件人地址
                handleReceiveMessage(data);
                break;
            case SELECT_IDENTIFICATION:    //身份认证
                handleIdentification(data);
                break;
        }
    }

    private void handleIdentification(Intent data) {
        isIdentification.setText("已认证");
    }

    //选择快递公司
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(ExpressageEvent expressageEvent) {
        flSelectExpressage.setVisibility(View.GONE);
        flexpressTitle.setVisibility(View.GONE);
        llselectExpressage.setVisibility(View.VISIBLE);
        ivSelectLogo.setVisibility(View.VISIBLE);
        tvExpressageName.setText(expressageEvent.getExpress_name());
        express_id = expressageEvent.getExpress_id();
        Glide.with(this)
                .load(expressageEvent.getExpress_logo())
                .into(ivSelectLogo);
        requestPredictMoney();   //获取预估价格
    }

    private void handleReceiveMessage(Intent data) {
        if (data == null) {
            return;
        }

        end_province = data.getStringExtra("book_province");
        end_city = data.getStringExtra("book_city");

        receive_id = data.getStringExtra("book_id");
        tvReceiveRes.setVisibility(View.GONE);
        llReceiveLayout.setVisibility(View.VISIBLE);
        tvReceiveName.setText(data.getStringExtra("book_name"));
        tvReceiveAddress.setText(data.getStringExtra("book_region") + data.getStringExtra("book_address"));
        tvReceivePhone.setText(data.getStringExtra("book_telephone"));
        requestPredictMoney();   //获取预估价格
    }

    private void handleSendMessage(Intent data) {
        if (data == null) {
            return;
        }
        start_province = data.getStringExtra("book_province");
        start_city = data.getStringExtra("book_city");
        book_id = data.getStringExtra("book_id");
        send_id = data.getStringExtra("book_id");
        tvSendRes.setVisibility(View.GONE);
        llSendLayout.setVisibility(View.VISIBLE);
        tvSendName.setText(data.getStringExtra("book_name"));
        tvSendAddress.setText(data.getStringExtra("book_region") + data.getStringExtra("book_address"));
        tvSendPhone.setText(data.getStringExtra("book_telephone"));
        requestIdentification();   //是否认证过
        requestPredictMoney();   //获取预估价格
    }

    private void selectMessageAbout(int type) {
        Intent intent = new Intent(this, FastmailMessageActivity.class);
        intent.putExtra("type", type);
        startActivityForResult(intent, type);
    }

    private void selectExpressage() {
        Intent intent = new Intent(this, ExpressageListActivity.class);
        startActivity(intent);
    }

    private void initRequestParams() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        dialogLoading = new HkDialogLoading(this, "请稍后...");

        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();
        mExpressLogoDao = mDaoSession.getExpressLogoDao();

        //获取用户id
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
    }


    private void initView() {
        initLogoImage();
        tvSendRes.setVisibility(View.VISIBLE);
        llSendLayout.setVisibility(View.GONE);

        tvReceiveRes.setVisibility(View.VISIBLE);
        llReceiveLayout.setVisibility(View.GONE);

        initPopuStyle();    //初始化popuwindow的样式
    }

    private void initLogoImage() {
        logoImages = new ImageView[]{ivSendOne, ivSendTwo, ivSendThree, ivSendFour, ivSendFive};
    }


    private OnResponseListener<String> mOnresponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            // dialogLoading.show();
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "ExpressageFragment::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    handleRequestData(what, jsonObject);
                } else {
                    if (what == REQUEST_IS_IDENTIFY_REQUEST) {
                        isIdentification.setText("未认证");
                    }else if (what == REQUEST_COMMIT_ORDER){
                        ToastUtil.showShort(msg);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            // dialogLoading.cancel();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            // dialogLoading.cancel();
        }

        @Override
        public void onFinish(int what) {
            //   dialogLoading.cancel();
        }
    };

    private void handleRequestData(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case REQUEST_GOODS_REQUEST:
                setGoodsData(jsonObject);     //设置商品数据
                break;
            case REQUEST_IS_IDENTIFY_REQUEST:
                isIdentify(jsonObject);     //是否实名
                break;
            case REQUEST_COMMIT_ORDER:   //提交订单
                showPromptDialog();
                break;
            case REQUEST_ESTIMATE:     //预估价格
                handleEstimate(jsonObject);
                break;
        }
    }

    private void handleEstimate(JSONObject jsonObject) throws JSONException {
        JSONObject dataObj = jsonObject.getJSONObject("data");
        String price = dataObj.getString("price");
        String costTime = dataObj.getString("estimated_time");
        tvTime.setText(StringUtil.handleNullResultForString(costTime) + "天");
        tvMoney.setText(StringUtil.handleNullResultForString(price) + "元");
    }

    private void showPromptDialog() {
        MessageDialog.show(this, "提示", "下单成功！", "知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
    }

    private void isIdentify(JSONObject jsonObject) throws JSONException {
        isIdentification.setText("已认证");
    }

    private void setGoodsData(JSONObject jsonObject) throws JSONException {
        JSONObject goodaArray = jsonObject.getJSONObject("data");
        //商品类型
        JSONArray goodsArray = goodaArray.getJSONArray("goods");
        //五个快递公司logo
        JSONArray expressArray = goodaArray.getJSONArray("express");
        mGoodsData.clear();
        for (int i = 0; i < goodsArray.length(); i++) {
            JSONObject jsonObject1 = goodsArray.getJSONObject(i);
            String goods_id = jsonObject1.getString("goods_id");
            String name = jsonObject1.getString("name");
            String states = jsonObject1.getString("states");
            HashMap<String, String> map = new HashMap<>();
            map.put("goods_id", goods_id);
            map.put("name", name);
            mGoodsData.add(map);
        }

        int account  = expressArray.length() > 5?5:expressArray.length();

        for (int j = 0; j < account; j++) {
            JSONObject expressObj = expressArray.getJSONObject(j);
            String expressPaht = expressObj.getString("express_logo");
            String expressId = expressObj.getString("express_id");
            List<ExpressLogo> expressLogos = mExpressLogoDao.queryBuilder()
                    .where(ExpressLogoDao.Properties.Express_id.eq(expressId)).list();
            if (expressLogos == null || expressLogos.size() == 0) {
                return ;
            }
            ExpressLogo expressLogo = expressLogos.get(0);
            if (expressLogo == null) {
                logoPaths[j] = expressPaht;
            } else {
                logoPaths[j] = expressLogo.getLogoLocalPath();
            }
        }

        setLogo();   //设置logo
        goodsAdapter.notifyDataSetChanged();
    }

    private void setLogo() {
        for (int i = 0; i < 5; i++) {
            Glide.with(this)
                    .load(logoPaths[i])
                    .into(logoImages[i]);
        }
    }

    private void initPopuStyle() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            selectView = getLayoutInflater().inflate(R.layout.pop_goods_type_layout, null);
            myGridView = selectView.findViewById(R.id.gv_goods);
            initRecycler();
            popupWindow = new PopupWindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //设置动画
            popupWindow.setAnimationStyle(R.style.popup_window_anim);
            //设置背景颜色
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
            popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popupWindow.setTouchable(true); // 设置popupwindow可点击
            popupWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popupWindow.setFocusable(true); // 获取焦点
            LinearLayout layout_pop_close = (LinearLayout) selectView.findViewById(R.id.layout_left_close);
            layout_pop_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
        }
    }

    private void initRecycler() {
        goodsAdapter = new GoodsAdapter(this, mGoodsData);
        myGridView.setAdapter(goodsAdapter);
        myGridView.addItemDecoration(new MarginDecoration(this));
        myGridView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        goodsAdapter.notifyDataSetChanged();
        goodsAdapter.setOnItemClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                HashMap<String, String> map = mGoodsData.get(position);
                String goodsName = map.get("name");
                type_id = map.get("goods_id");
                tvGoodsSelect.setText(goodsName);
                popupWindow.dismiss();
            }
        });
    }

    //设置对话框内容返回键不消失
    DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                return true;
            } else {
                return false;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
    }
}
