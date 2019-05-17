package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.simonpercic.oklog3.OkLogInterceptor;
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.internet.RetrofitApi;
import com.mt.bbdj.baseconfig.internet.RetrofitConfig;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.community.adapter.GoodsAdapter;
import com.mt.bbdj.community.adapter.Goods_New_Adapter;
import com.yanzhenjie.nohttp.rest.Request;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ConfirmReceiveActivity extends BaseActivity {

    @BindView(R.id.rl_goods)
    RecyclerView rlGoods;
    @BindView(R.id.tv_weight_one)
    TextView tvWeightOne;
    @BindView(R.id.tv_weight_two)
    TextView tvWeightTwo;
    @BindView(R.id.tv_weight_other)
    EditText tvWeightOther;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_express_name)
    TextView tvExpressName;
    @BindView(R.id.et_money)
    EditText etMoney;
    @BindView(R.id.tv_receive_address)
    AppCompatTextView tvReceiveAddress;
    @BindView(R.id.tv_receive_person)
    TextView tvReceivePerson;
    @BindView(R.id.tv_send_address)
    AppCompatTextView tvSendAddress;
    @BindView(R.id.tv_send_message)
    TextView tvSendMessage;
    @BindView(R.id.bt_confirm)
    Button btConfirm;
    private String user_id;
    private OkHttpClient okHttpClient;

    private List<HashMap<String, String>> mGoodsData = new ArrayList<>();    //商品类型数据
    private List<String> mGoods = new ArrayList<>();
    private int mGoodsPosition;
    private String type_id;
    private String selectGoodsName = "";
    private Goods_New_Adapter goodsAdapter;
    private RetrofitApi retrofitApi;
    private String mail_id;
    private SharedPreferences.Editor mEditor;
    private float goodsWeight = 1;
    private float goodsMoney = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        setContentView(R.layout.activity_confirm_receive);
        ButterKnife.bind(this);
        initParams();
        initListener();
        initGoodsList();
        requestGoodsData();    //获取商品类型
        requestSendMessage();    //获取快递信息
    }

    private void requestSendMessage() {
        WaitDialog dialogLoading = WaitDialog.show(this, "加载中...").setCanCancel(true);
        Map<String, String> requestMap = NoHttpRequest.setHeaderParams(user_id);
        Call<ResponseBody> call = retrofitApi.getExpressMessage(requestMap, mail_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String json = response.body().string();
                    LogUtil.d("ConfirmReceiveActivity::快递", json);
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");
                    if ("5001".equals(code)) {
                        setResultData(jsonObject);
                    } else {
                        ToastUtil.showShort(msg);
                    }
                    dialogLoading.doDismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                    dialogLoading.doDismiss();
                    LogUtil.d("ConfirmReceiveActivity::Exception快递", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtil.d("ConfirmReceiveActivity::", t.getMessage());
                dialogLoading.doDismiss();
            }
        });
    }

    private void setResultData(JSONObject jsonObject) throws JSONException {
        JSONObject dataObj = jsonObject.getJSONObject("data");
        String mail_id = dataObj.getString("mail_id");
        String express_id = dataObj.getString("express_id");
        String express_name = dataObj.getString("express_name");
        String express_logo = dataObj.getString("express_logo");
        String send_name = dataObj.getString("send_name");
        String send_phone = dataObj.getString("send_phone");
        String send_region = dataObj.getString("send_region");
        String send_address = dataObj.getString("send_address");
        String collect_name = dataObj.getString("collect_name");
        String collect_phone = dataObj.getString("collect_phone");
        String collect_region = dataObj.getString("collect_region");
        String collect_address = dataObj.getString("collect_address");
        String goods_name = dataObj.getString("goods_name");
        String weight = dataObj.getString("weight");
        String momey = dataObj.getString("momey");
        selectGoodsName = goods_name;
        int position = mGoods.indexOf(goods_name);
        goodsAdapter.setPosition(position);
        goodsAdapter.notifyDataSetChanged();

        Glide.with(ConfirmReceiveActivity.this)
                .load(express_logo)
                .into(ivLogo);
        tvReceiveAddress.setText(collect_region + collect_address);
        tvReceivePerson.setText(collect_name + "   " + collect_phone);
        tvSendAddress.setText(send_region + send_address);
        tvSendMessage.setText(collect_name + "   " + collect_phone);
    }


    private void requestGoodsData() {
        Map<String, String> requestMap = NoHttpRequest.setHeaderParams(user_id);
        //传入请求参数
        Call<ResponseBody> call = retrofitApi.getGoodsType(requestMap, "getItemType300", user_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String json = response.body().string();
                    LogUtil.d("ConfirmReceiveActivity::", json);
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");
                    if ("5001".equals(code)) {
                        JSONObject goodaArray = jsonObject.getJSONObject("data");
                        //商品类型
                        JSONArray goodsArray = goodaArray.getJSONArray("goods");
                        mGoodsData.clear();
                        mGoods.clear();
                        for (int i = 0; i < goodsArray.length(); i++) {
                            JSONObject jsonObject1 = goodsArray.getJSONObject(i);
                            String goods_id = jsonObject1.getString("goods_id");
                            String name = jsonObject1.getString("name");
                            String states = jsonObject1.getString("states");
                            HashMap<String, String> map = new HashMap<>();
                            map.put("goods_id", goods_id);
                            map.put("name", name);
                            mGoodsData.add(map);
                            mGoods.add(name);
                        }

                        goodsAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showShort(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.d("ConfirmReceiveActivity::Exception", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtil.d("ConfirmReceiveActivity::", t.getMessage());
            }
        });

    }

    @OnClick({R.id.tv_weight_one, R.id.tv_weight_two, R.id.bt_confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_weight_one:
                setSelectWeight(1);
                break;
            case R.id.tv_weight_two:
                setSelectWeight(2);
                break;
            case R.id.bt_confirm:
                confirmReceive();
                break;
        }
    }

    private void confirmReceive() {
       if (isRight()) {
           getWaybillNumber();
       }
    }

    private void getWaybillNumber() {
        WaitDialog dialogLoading = WaitDialog.show(this, "提交中...").setCanCancel(true);
        Map<String, String> requestMap = NoHttpRequest.setHeaderParams(user_id);
        Map<String,String> params = new HashMap<>();
        params.put("method","getWaybillNumber600");
        params.put("user_id",user_id);
        params.put("mail_id",mail_id);
        params.put("goods_name",selectGoodsName);
        params.put("weight",goodsWeight+"");
        params.put("money",goodsMoney+"");
        //传入请求参数
        Call<ResponseBody> call = retrofitApi.getWaybillNumber(requestMap, params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    LogUtil.i("photoFile--", "ConfirmReceiveActivity::" + jsonObject.toString());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                            ToastUtil.showShort("提交成功！");
                            savePannelMessage(jsonObject);
                            Intent intent = new Intent(ConfirmReceiveActivity.this,ReceiveExpressActivity.class);
                            startActivity(intent);
                    } else {
                        ToastUtil.showShort(msg);
                    }
                    dialogLoading.doDismiss();

                } catch (Exception e) {
                    e.printStackTrace();
                    dialogLoading.doDismiss();
                    ToastUtil.showShort(e.getMessage());

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogLoading.doDismiss();
                ToastUtil.showShort(t.getMessage());
            }
        });

    }

    private boolean isRight() {
        if ("".equals(selectGoodsName)) {
            ToastUtil.showShort("请选择物品类型!");
            return false;
        }
        String weightOther = tvWeightOther.getText().toString();
        if (!"".equals(weightOther)) {
            goodsWeight = Float.parseFloat(weightOther);
        }
        if (goodsWeight == 0) {
            ToastUtil.showShort("请输入物品重量!");
            return false;
        }

        String money = etMoney.getText().toString();
        if (!"".equals(money)) {
            goodsMoney = Float.parseFloat(money);
        } else {
            ToastUtil.showShort("请输入实际收取金额");
            return false;
        }

        if (goodsMoney == 0) {
            ToastUtil.showShort("实际金额不可为0!");
            return false;
        }
        return true;
    }


    private void setSelectWeight(int target) {
        if (target == 1) {
            tvWeightOne.setBackgroundResource(R.drawable.tv_bg_green_2);
            tvWeightOne.setTextColor(Color.parseColor("#0da95f"));
            tvWeightTwo.setBackgroundResource(R.drawable.shap_tv_bg);
            tvWeightTwo.setTextColor(Color.parseColor("#777777"));
            goodsWeight = 1;
        } else {
            tvWeightTwo.setBackgroundResource(R.drawable.tv_bg_green_2);
            tvWeightTwo.setTextColor(Color.parseColor("#0da95f"));
            tvWeightOne.setBackgroundResource(R.drawable.shap_tv_bg);
            tvWeightOne.setTextColor(Color.parseColor("#777777"));
            goodsWeight = 2;
        }
        tvWeightOther.clearFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tvWeightOther.getWindowToken(), 0);
    }


    private void initListener() {
        tvWeightOther.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    tvWeightTwo.setBackgroundResource(R.drawable.shap_tv_bg);
                    tvWeightTwo.setTextColor(Color.parseColor("#777777"));
                    tvWeightOne.setBackgroundResource(R.drawable.shap_tv_bg);
                    tvWeightOne.setTextColor(Color.parseColor("#777777"));
                }
            }
        });
    }

    private void initGoodsList() {
        goodsAdapter = new Goods_New_Adapter(this, mGoodsData);
        rlGoods.setAdapter(goodsAdapter);
        rlGoods.addItemDecoration(new MarginDecoration(this, 5, 10, 5, 10));
        rlGoods.setLayoutManager(new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
        goodsAdapter.setOnItemClickListener(new Goods_New_Adapter.OnItemClickListener() {
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

    private void initParams() {

        Intent intent = getIntent();
        mail_id = intent.getStringExtra("mail_id");

        mEditor = SharedPreferencesUtil.getEditor();

        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = daoSession.getUserBaseMessageDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        OkLogInterceptor okLogInterceptor = OkLogInterceptor.builder().build();
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.addInterceptor(okLogInterceptor);
        okHttpClient = okHttpBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(RetrofitConfig.BaseURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        retrofitApi = retrofit.create(RetrofitApi.class);
    }

    private void savePannelMessage(JSONObject jsonObject) throws JSONException {
        JSONObject dataObj = jsonObject.getJSONObject("data");
        String mail_id = dataObj.getString("mail_id");       //订单id
        String dingdanhao = dataObj.getString("dingdanhao");       //订单id
        String express_id = dataObj.getString("express_id");     //快递公司id
        String express_name = dataObj.getString("express_name");     //快递公司名称
        String number = dataObj.getString("number");      //驿站代码
        String yundanhao = dataObj.getString("yundanhao");    //运单号
        String code = dataObj.getString("code");   //标识码
        String place = dataObj.getString("place");      //中转地
        String transit = dataObj.getString("transit");     //中转地标识码和时间
        String send_name = dataObj.getString("send_name");    //寄送名称
        String send_phone = dataObj.getString("send_phone");    //寄送电话
        String send_region = dataObj.getString("send_region");    //寄送区域
        String send_address = dataObj.getString("send_address");  //寄送地址
        String collect_name = dataObj.getString("collect_name");   //收件人
        String collect_phone = dataObj.getString("collect_phone"); //收件电话
        String collect_region = dataObj.getString("collect_region");  //收件区域
        String collect_address = dataObj.getString("collect_address");  //收件地址
        String goods_name = dataObj.getString("goods_name");  //商品名称
        String weight = dataObj.getString("weight"); //计费重量

        SharedPreferences.Editor editor = SharedPreferencesUtil.getEditor();
        editor.putString("mail_id",StringUtil.handleNullResultForString(mail_id));
        editor.putString("dingdanhao",StringUtil.handleNullResultForString(dingdanhao));
        editor.putString("express_id",StringUtil.handleNullResultForString(express_id));
        editor.putString("express_name",StringUtil.handleNullResultForString(express_name));
        editor.putString("number",StringUtil.handleNullResultForString(number));
        editor.putString("yundanhao",StringUtil.handleNullResultForString(yundanhao));
        editor.putString("code",StringUtil.handleNullResultForString(code));
        editor.putString("place",StringUtil.handleNullResultForString(place));
        editor.putString("transit",StringUtil.handleNullResultForString(transit));
        editor.putString("send_name",StringUtil.handleNullResultForString(send_name));
        editor.putString("send_phone",StringUtil.handleNullResultForString(send_phone));
        editor.putString("send_region",StringUtil.handleNullResultForString(send_region));
        editor.putString("send_address",StringUtil.handleNullResultForString(send_address));
        editor.putString("collect_name",StringUtil.handleNullResultForString(collect_name));
        editor.putString("collect_phone",StringUtil.handleNullResultForString(collect_phone));
        editor.putString("collect_region",StringUtil.handleNullResultForString(collect_region));
        editor.putString("collect_address",StringUtil.handleNullResultForString(collect_address));
        editor.putString("goods_name",StringUtil.handleNullResultForString(goods_name));
        editor.putString("weight",StringUtil.handleNullResultForString(weight));
        editor.putString("content","");
        editor.putString("money",goodsMoney+"");
        editor.commit();
    }
}
