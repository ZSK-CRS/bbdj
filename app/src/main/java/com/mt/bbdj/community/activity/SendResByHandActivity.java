package com.mt.bbdj.community.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kongzue.dialog.v2.InputDialog;
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
import com.mt.bbdj.baseconfig.view.MyInputDialog;
import com.mt.bbdj.baseconfig.view.MyMessageDialog;
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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

//手动寄件
public class SendResByHandActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;     //返回键
    @BindView(R.id.ll_send_message)
    LinearLayout llSendMessage;    //选择寄件人
    @BindView(R.id.ll_receive_message)
    LinearLayout llReceiveMessage;   //选择收件人
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
    LinearLayout flSelectExpressage;       //选择快递
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

    @BindView(R.id.et_mark)
    EditText etMark;
    @BindView(R.id.ll_select_layout)
    LinearLayout llselectLayout;
    @BindView(R.id.rl_add_send_address)
    RelativeLayout rlSendAddress;   //寄件人地址
    @BindView(R.id.rl_add_receive_address)
    RelativeLayout rlReceiveAddress;   //收件人地址
    @BindView(R.id.tv_send_detail_address)
    TextView tvSendDetailAddress;
    @BindView(R.id.tv_receive_detail_address)
    TextView tvReceiveDetailAddress;
    @BindView(R.id.tv_identify_state)
    TextView tvIdentifyState;   //未认证状态
    @BindView(R.id.tv_identify_state_have)
    TextView tvIdentifyStateHave;    //已认证
    @BindView(R.id.ll_identification_layout)
    LinearLayout identificationLayout;    //认证标题
    @BindView(R.id.tv_cannel_title)
    RelativeLayout cannelTitle;    //取消标题

    @BindView(R.id.tv_money)
    TextView tvMoney;       //预估价格
    @BindView(R.id.tv_time)
    TextView tvTime;   //花费时间

    private boolean isIdentifyState = false;   //认证状态

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
    private int mGoodsPosition = -1;
    private String selectGoodsName = "";
    private String selectWeight = "";


    private final int SELECT_SEND_MESSAGE = 1;      //寄件人
    private final int SELECT_RECEIVE_MESSAGE = 2;    //收件人
    private final int SELECT_IDENTIFICATION = 3;    //身份认证
    private final int ADD_SEND_ADDRESS = 4;     //添加寄件人
    private final int ADD_RECEIVE_ADDRESS = 5;   //添加收件人
    private final int CHANGE_SEND_ADDRESS = 6;    //修改寄件人
    private final int CHANEE_RECEIVE_ADDRESS = 7;   //修改收件人

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
    private String mCountry;
    private TextView etWeightOther;
    private TextView weithOne;
    private TextView weithTwo;
    private TextView weithThree;
    private TextView weightOther;
    private TextView[] weights;
    private TextView weihtTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_send_res_by_hand);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
        initListener();
        initRequest();    //初始化网络请求
    }

    private void initListener() {
        cannelTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                identificationLayout.setVisibility(View.GONE);
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


    @OnClick({R.id.iv_back, R.id.ll_send_message, R.id.ll_receive_message,
            R.id.ll_pressage_layot, R.id.ll_select_goods, R.id.fl_select_expressage,
            R.id.bt_commit, R.id.rl_add_send_address, R.id.rl_add_receive_address, R.id.tv_send_res,
            R.id.tv_receive_res, R.id.ll_receive_layout, R.id.ll_send_layout, R.id.tv_identify_one,
            R.id.tv_identify_state})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_add_send_address:     //选择寄件人地址
                selectMessageAbout(SELECT_SEND_MESSAGE);
                break;
            case R.id.rl_add_receive_address:  //选择收件人地址
                selectMessageAbout(SELECT_RECEIVE_MESSAGE);
                break;
            case R.id.ll_pressage_layot:  //选择快递公司列表
            case R.id.fl_select_expressage:
                selectExpressage();
                break;
            case R.id.ll_select_goods:     //选择商品类型
                showSelectTypeDialog();
                break;
            case R.id.tv_identify_one:    //身份认证
            case R.id.tv_identify_state:    //身份认证
                identificationID();
                break;
            case R.id.bt_commit:
                commitAllData();
                break;
            case R.id.tv_send_res:    //添加寄件人地址
                addSendAddress();
                break;
            case R.id.ll_send_layout:    //修改寄件人地址
                changeSendAddress();
                break;
            case R.id.tv_receive_res:    //添加收件人地址
                addReceiveAddress();
                break;
            case R.id.ll_receive_layout:   //修改收件人地址
                changeReceiveAddress();
                break;

        }
    }


    private void changeReceiveAddress() {
        Intent intent = new Intent(this, ChangeMessageActivity.class);
        intent.putExtra("book_id", book_id);
        intent.putExtra("book_name", tvReceiveName.getText().toString());
        intent.putExtra("book_telephone", tvReceivePhone.getText().toString());
        intent.putExtra("book_region", tvReceiveAddress.getText().toString());
        intent.putExtra("book_address", tvReceiveDetailAddress.getText().toString());
        intent.putExtra("book_province", start_province);
        intent.putExtra("book_city", start_city);
        intent.putExtra("book_area", mCountry);
        intent.putExtra("type", 2);
        startActivityForResult(intent, CHANEE_RECEIVE_ADDRESS);
    }

    private void addReceiveAddress() {
        Intent intent = new Intent(this, ChangeMessageActivity.class);
        intent.putExtra("type", 2);
        startActivityForResult(intent, ADD_RECEIVE_ADDRESS);
    }

    private void changeSendAddress() {
        Intent intent = new Intent(this, ChangeMessageActivity.class);
        intent.putExtra("book_id", book_id);
        intent.putExtra("book_name", tvSendName.getText().toString());
        intent.putExtra("book_telephone", tvSendPhone.getText().toString());
        intent.putExtra("book_region", tvSendAddress.getText().toString());
        intent.putExtra("book_address", tvSendDetailAddress.getText().toString());
        intent.putExtra("book_province", end_province);
        intent.putExtra("book_city", end_city);
        intent.putExtra("book_area", mCountry);
        startActivityForResult(intent, CHANGE_SEND_ADDRESS);
    }

    private void addSendAddress() {
        Intent intent = new Intent(this, ChangeMessageActivity.class);
        startActivityForResult(intent, ADD_SEND_ADDRESS);
    }


    private void commitAllData() {
        String user_id = "";
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        String mark = etMark.getText().toString();

        if (!isRightAboutOrderMessage()) {
            return;
        }

        Request<String> request = NoHttpRequest.commitOrderRequest(user_id, express_id,
                send_id, receive_id, type_id, mWeight, mark);
        mRequestQueue.add(REQUEST_COMMIT_ORDER, request, mOnresponseListener);
    }

    private boolean isRightAboutOrderMessage() {
        if ("".equals(book_id) || "".equals(express_id) ||
                "".equals(receive_id) || "".equals(send_id) || "".equals(type_id)) {
            ToastUtil.showShort("请完善信息！");
            return false;
        }
        if (!isIdentifyState) {
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

        if (isIdentifyState) {
            return;
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
            case SELECT_SEND_MESSAGE:     //选择寄件人地址
                handleSendMessage(data);
                break;
            case SELECT_RECEIVE_MESSAGE:   //选择收件人地址
                handleReceiveMessage(data);
                break;
            case SELECT_IDENTIFICATION:    //身份认证
                handleIdentification(data);
                break;
            case ADD_SEND_ADDRESS:    //添加寄件人地址
                handleSendMessage(data);
                break;
            case ADD_RECEIVE_ADDRESS:   //添加收件人地址
                handleReceiveMessage(data);
                break;
            case CHANEE_RECEIVE_ADDRESS:    //修改收件人地址
                handleReceiveMessage(data);
                break;
            case CHANGE_SEND_ADDRESS:    //修改收件人地址
                handleSendMessage(data);
                break;
        }
    }


    private void handleIdentification(Intent data) {
        requestIdentification();
    }

    //选择快递公司
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(ExpressageEvent expressageEvent) {
        flSelectExpressage.setVisibility(View.GONE);
        llselectExpressage.setVisibility(View.VISIBLE);
        ivSelectLogo.setVisibility(View.VISIBLE);
        tvExpressageName.setText(expressageEvent.getExpress_name());
        llselectLayout.setVisibility(View.GONE);
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
        tvReceiveAddress.setText(data.getStringExtra("book_region"));
        tvReceiveDetailAddress.setText(data.getStringExtra("book_address"));
        tvReceivePhone.setText(data.getStringExtra("book_telephone"));
        requestPredictMoney();   //获取预估价格
    }

    private void handleSendMessage(Intent data) {
        if (data == null) {
            return;
        }
        start_province = data.getStringExtra("book_province");
        start_city = data.getStringExtra("book_city");
        mCountry = data.getStringExtra("book_area");
        book_id = data.getStringExtra("book_id");
        send_id = data.getStringExtra("book_id");
        tvSendRes.setVisibility(View.GONE);
        llSendLayout.setVisibility(View.VISIBLE);
        tvSendName.setText(data.getStringExtra("book_name"));
        tvSendAddress.setText(data.getStringExtra("book_region"));
        tvSendDetailAddress.setText(data.getStringExtra("book_address"));
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
        tvSendRes.setVisibility(View.VISIBLE);
        llSendLayout.setVisibility(View.GONE);

        tvReceiveRes.setVisibility(View.VISIBLE);
        llReceiveLayout.setVisibility(View.GONE);

        initPopuStyle();    //初始化popuwindow的样式
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
                        tvIdentifyState.setVisibility(View.VISIBLE);
                        tvIdentifyStateHave.setVisibility(View.GONE);
                        identificationLayout.setVisibility(View.VISIBLE);
                        isIdentifyState = false;
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
        tvIdentifyState.setVisibility(View.GONE);
        tvIdentifyStateHave.setVisibility(View.VISIBLE);
        identificationLayout.setVisibility(View.GONE);
        isIdentifyState = true;
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

        int account = expressArray.length() > 5 ? 5 : expressArray.length();

        for (int j = 0; j < account; j++) {
            JSONObject expressObj = expressArray.getJSONObject(j);
            String expressPaht = expressObj.getString("express_logo");
            String expressId = expressObj.getString("express_id");
            List<ExpressLogo> expressLogos = mExpressLogoDao.queryBuilder()
                    .where(ExpressLogoDao.Properties.Express_id.eq(expressId)).list();
            if (expressLogos == null || expressLogos.size() == 0) {
                return;
            }
            ExpressLogo expressLogo = expressLogos.get(0);
            if (expressLogo == null) {
                logoPaths[j] = expressPaht;
            } else {
                logoPaths[j] = expressLogo.getLogoLocalPath();
            }
        }
        goodsAdapter.notifyDataSetChanged();

        if (mGoodsData.size() != 0) {
            type_id = mGoodsData.get(0).get("goods_id");
            String name = mGoodsData.get(0).get("name");
            mWeight = "1";
            tvGoodsSelect.setText(name+" "+mWeight+"kg");
        }

    }

    private void initPopuStyle() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            selectView = getLayoutInflater().inflate(R.layout.pop_goods_type_layout, null);
            myGridView = selectView.findViewById(R.id.gv_goods);
            TextView tvSure = selectView.findViewById(R.id.sure);
            TextView tvcannel = selectView.findViewById(R.id.cannel);
            initWeightListenre(selectView);   //重量选择

            tvcannel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });


            tvSure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ("".equals(mWeight)) {
                        mWeight = etWeightOther.getText().toString();
                        if ("".equals(mWeight)) {
                            ToastUtil.showShort("请选择重量！");
                            return;
                        }
                    }
                    if (mGoodsPosition == -1) {
                        ToastUtil.showShort("请选择物品类型！");
                        return;
                    }

                    tvGoodsSelect.setText(selectGoodsName + " " + mWeight + " kg");
                    requestPredictMoney();
                    popupWindow.dismiss();
                }
            });

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
                    //  popupWindow.dismiss();
                }
            });
        }
    }

    private void initWeightListenre(View selectView) {
        weithOne = selectView.findViewById(R.id.tv_weight_one);
        weithTwo = selectView.findViewById(R.id.tv_weight_two);
        weithThree = selectView.findViewById(R.id.tv_weight_three);
        weightOther = selectView.findViewById(R.id.tv_other);
        etWeightOther = selectView.findViewById(R.id.id_other_weight);
        weights = new TextView[]{weithOne, weithTwo, weithThree, weightOther};
        weihtTag = selectView.findViewById(R.id.tv_weiht_tag);

        weithOne.setOnClickListener(mWeightClickListenr);
        weithTwo.setOnClickListener(mWeightClickListenr);
        weithThree.setOnClickListener(mWeightClickListenr);
        weightOther.setOnClickListener(mWeightClickListenr);
    }

    private View.OnClickListener mWeightClickListenr = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tv_weight_one:
                    mWeight = "1";
                    setWightState(0);
                    break;
                case R.id.tv_weight_two:
                    mWeight = "2";
                    setWightState(1);
                    break;
                case R.id.tv_weight_three:
                    mWeight = "3";
                    setWightState(2);
                    break;
                case R.id.tv_other:
                    setWightState(3);
                    mWeight = "";
                    break;
            }
        }
    };


    //设置重量选择按钮的状态
    public void setWightState(int position) {
        restoreWeithState();    //重置选择状态
        TextView weightTextView = weights[position];
        weightTextView.setBackgroundResource(R.drawable.bg_green_circle);
        weightTextView.setTextColor(Color.parseColor("#ffffff"));
        if (position == 3) {
            etWeightOther.setVisibility(View.VISIBLE);
            weihtTag.setVisibility(View.VISIBLE);
        }
    }

    private void restoreWeithState() {
        for (TextView weitht : weights) {
            weitht.setBackgroundResource(R.drawable.tv_bg_grey_circle);
            weitht.setTextColor(Color.parseColor("#353535"));
        }
        etWeightOther.setVisibility(View.INVISIBLE);
        weihtTag.setVisibility(View.INVISIBLE);
    }

    private void initRecycler() {
        goodsAdapter = new GoodsAdapter(this, mGoodsData);
        myGridView.setAdapter(goodsAdapter);
        myGridView.addItemDecoration(new MarginDecoration(this));
        myGridView.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        goodsAdapter.setOnItemClickListener(new GoodsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mGoodsPosition = position;
                HashMap<String, String> map = mGoodsData.get(position);
                String goodsName = map.get("name");
                selectGoodsName = goodsName;
                type_id = map.get("goods_id");
                goodsAdapter.setPosition(position);
                // popupWindow.dismiss();
                goodsAdapter.notifyDataSetChanged();
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
