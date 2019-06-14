package com.mt.bbdj.community.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.simonpercic.oklog3.OkLogInterceptor;
import com.jcodecraeer.xrecyclerview.ArrowRefreshHeader;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.kongzue.dialog.v2.MessageDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.activity.LoginActivity;
import com.mt.bbdj.baseconfig.application.MyApplication;
import com.mt.bbdj.baseconfig.base.BaseFragment;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.InterApi;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.internet.RetrofitApi;
import com.mt.bbdj.baseconfig.internet.RetrofitConfig;
import com.mt.bbdj.baseconfig.model.ProductModel;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.DialogUtil;
import com.mt.bbdj.baseconfig.utls.DownloadUtil;
import com.mt.bbdj.baseconfig.utls.FileUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.SystemUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.utls.TopSmoothScroller;
import com.mt.bbdj.community.activity.CannelOrderActivity;
import com.mt.bbdj.community.activity.ClearDetailActivity;
import com.mt.bbdj.community.activity.ClearStateActivity;
import com.mt.bbdj.community.activity.ConfirmReceiveActivity;
import com.mt.bbdj.community.activity.IdentificationActivity;
import com.mt.bbdj.community.activity.MailingdetailActivity;
import com.mt.bbdj.community.activity.QuotedPriceActivity;
import com.mt.bbdj.community.activity.RechargeActivity;
import com.mt.bbdj.community.activity.SendResByHandActivity;
import com.mt.bbdj.community.activity.WaterOrderDetailActivity;
import com.mt.bbdj.community.adapter.WaitHandleAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Author : ZSK
 * Date : 2018/12/26
 * Description : 社区版首页
 */
public class ComFirstFragment_new extends BaseFragment implements View.OnClickListener, XRecyclerView.LoadingListener {

    private String APP_PATH_ROOT = FileUtil.getRootPath(MyApplication.getInstance()).getAbsolutePath() + File.separator + "bbdj";
    final String fileName = "bbdj.apk";

    private WaitHandleAdapter mAdapter;

    private List<ProductModel> mList = new ArrayList<>();
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private RequestQueue mRequestQueue;
    private HkDialogLoading dialogLoading;
    private String user_id;
    private int REQUEST_IDENTIFY = 101;
    private final int REQUEST_PANNEL_MESSAGE = 102;    //获取面板信息

    private LinearLayout ll_express;    //快递
    private LinearLayout ll_water;    //桶装水
    private LinearLayout ll_clear;   //干洗

    private XRecyclerView recyclerView;
    private View mView;
    private LinearLayoutManager mLinearLayoutManager;
    private int mail_number;
    private int water_number;
    private int clean_number;
    private OkHttpClient okHttpClient;
    private String mail_id;
    private String version_url;
    private ProgressDialog mProgressBar;


    public static ComFirstFragment_new getInstance() {
        ComFirstFragment_new comFirstFragment = new ComFirstFragment_new();
        return comFirstFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.layout_com_first_fragment_new, container, false);
        initParams();
        initView();
        initListener();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.refresh();
        requestPannelMessage();
    }



    private void initListener() {

        mAdapter.setOnCheckDetailListener(new WaitHandleAdapter.ExpressInterfaceManager() {
            @Override
            public void OnCheckDetailOrderClick(int position) {
                //查看寄件详情
                ProductModel productModel = mList.get(position);
                String mail_id = productModel.getMail_id();
                Intent intent = new Intent(getActivity(), MailingdetailActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("mail_id", mail_id);
                startActivity(intent);
            }

            @Override
            public void OnReceiveExpressClick(int position) {
                ProductModel productModel = mList.get(position);
                mail_id = productModel.getMail_id();
                //验证身份寄件人是否实名
                identifyPerson(mail_id);
            }

            @Override
            public void OnConfirmWaterReceiveClick(int position) {
                ProductModel productModel = mList.get(position);
                String order_id = productModel.getOrders_id();
                //确认桶装水接单
                confirmWaterReive(order_id);
            }

            @Override
            public void OnConfirmWaterSendClick(int position) {
                //确认桶装水送达
                ProductModel productModel = mList.get(position);
                String order_id = productModel.getOrders_id();
                confirmWaterSend(order_id);
            }

            @Override
            public void OnConfirWaterCannelClick(int position) {
                String mail_id = mList.get(position).getMail_id();
                String orders_id = mList.get(position).getOrders_id();
                //桶装水取消订单
                Intent intent = new Intent(getActivity(), CannelOrderActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("mail_id", mail_id);
                intent.putExtra("orders_id", orders_id);
                intent.putExtra("cannel_type", CannelOrderActivity.STATE_CANNEL_FOR_SERVICE);
                startActivity(intent);
            }

            @Override
            public void OnConfirmClearSendClick(int position) {
                //确认干洗送达
                ProductModel productModel = mList.get(position);
                String order_id = productModel.getOrders_id();
                confirmClearSend(order_id);
            }

            @Override
            public void OnConfirmRefauseClick(int position) {
                //干洗拒绝接单
                String mail_id = mList.get(position).getMail_id();
                String orders_id = mList.get(position).getOrders_id();
                //取消订单
                Intent intent = new Intent(getActivity(), CannelOrderActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("mail_id", mail_id);
                intent.putExtra("orders_id", orders_id);
                intent.putExtra("cannel_type", CannelOrderActivity.STATE_CANNEL_FOR_SERVICE);
                startActivity(intent);
            }

            @Override
            public void OnCallClick(int position) {
                String phone = mList.get(position).getPhone();
                Intent intent = new Intent(Intent.ACTION_DIAL);
                Uri data = Uri.parse("tel:" + phone);
                intent.setData(data);
                startActivity(intent);
            }

            @Override
            public void OnPlaceOrder(int position) {
                //寄送快递
                Intent intent = new Intent();
                intent.setClass(getActivity(), SendResByHandActivity.class);
                startActivity(intent);
            }

            @Override
            public void OnCannelOrder(int position) {
                String mail_id = mList.get(position).getMail_id();
                String orders_id = mList.get(position).getOrders_id();
                //取消订单
                Intent intent = new Intent(getActivity(), CannelOrderActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("mail_id", mail_id);
                intent.putExtra("orders_id", orders_id);
                intent.putExtra("cannel_type", CannelOrderActivity.STATE_CANNEL_FOR_EXPRESS);
                startActivity(intent);
            }

            @Override
            public void OnCommitPrice(int position) {
                //干洗提交报价
                ProductModel productModel = mList.get(position);
                Intent intent = new Intent(getActivity(), QuotedPriceActivity.class);
                intent.putExtra("productModel",productModel);
                startActivity(intent);
            }

            @Override
            public void OnClearReceiveClick(int position) {
                //干洗接单
                ProductModel productModel = mList.get(position);
                String order_id = productModel.getOrders_id();
                receiveClearOrder(order_id);
            }

            @Override
            public void OnCheckClearOrderClick(int position) {
                // 有按钮的时候跳转 ClearDetailActivity  否则显示的是状态界面
                //查看干洗详情
                Intent intent = new Intent();
                ProductModel productModel = mList.get(position);
                int clearState = productModel.getClearState();
                if (clearState == 5 || clearState == 6 || clearState == 8) {    //状态为等待干洗店取件、送件、
                    intent.setClass(getActivity(),ClearStateActivity.class);
                } else {
                    intent.setClass(getActivity(),ClearDetailActivity.class);
                }
                intent.putExtra("productModel",productModel);
                startActivity(intent);
            }

            @Override
            public void OnCheckWaterOrderClick(int position) {
                ProductModel productModel = mList.get(position);
                int cannelType = productModel.getType();
                //查看桶装水的详情
                Intent intent = new Intent(getActivity(), WaterOrderDetailActivity.class);
                intent.putExtra("user_id", user_id);
                intent.putExtra("cannel_type", cannelType);
                intent.putExtra("orderDetail", productModel);
                startActivity(intent);
            }
        });
    }

    private void confirmClearSend(String order_id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InterApi.BaseURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);
        Map<String, String> requestMap = NoHttpRequest.setHeaderParams(user_id);
        //传入请求参数
        Call<ResponseBody> call = retrofitApi.confirmSendClearOrder(requestMap, order_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String json = response.body().string();
                    LogUtil.d("ComFirstFragment_new::", json);
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");

                    if ("5001".equals(code)) {
                        recyclerView.refresh();
                    }
                    ToastUtil.showShort(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtil.d("ComFirstFragment_new::", t.getMessage());
                ToastUtil.showShort(t.getMessage());
            }
        });
    }

    private void receiveClearOrder(String order_id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InterApi.BaseURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);
        Map<String, String> requestMap = NoHttpRequest.setHeaderParams(user_id);
        //传入请求参数
        Call<ResponseBody> call = retrofitApi.confirmReceiveClearOrder(requestMap, order_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String json = response.body().string();
                    LogUtil.d("ComFirstFragment_new::", json);
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");

                    if ("5001".equals(code)) {
                        recyclerView.refresh();
                    }
                    ToastUtil.showShort(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtil.d("ComFirstFragment_new::", t.getMessage());
                ToastUtil.showShort(t.getMessage());
            }
        });
    }

    private void confirmWaterSend(String order_id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InterApi.BaseURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);
        Map<String, String> requestMap = NoHttpRequest.setHeaderParams(user_id);
        //传入请求参数
        Call<ResponseBody> call = retrofitApi.confirmSendWaterOrder(requestMap, order_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String json = response.body().string();
                    LogUtil.d("ComFirstFragment_new::", json);
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");

                    if ("5001".equals(code)) {
                        recyclerView.refresh();
                    }
                    ToastUtil.showShort(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtil.d("ComFirstFragment_new::", t.getMessage());
                ToastUtil.showShort(t.getMessage());
            }
        });
    }

    private void confirmWaterReive(String order_id) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InterApi.BaseURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);
        Map<String, String> requestMap = NoHttpRequest.setHeaderParams(user_id);
        //传入请求参数
        Call<ResponseBody> call = retrofitApi.confirmReceiveWaterOrder(requestMap, order_id);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String json = response.body().string();
                    LogUtil.d("ComFirstFragment_new::", json);
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");

                    if ("5001".equals(code)) {
                        recyclerView.refresh();
                    } else {
                        // ToastUtil.showShort(msg);s
                    }
                    ToastUtil.showShort(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtil.d("ComFirstFragment_new::", t.getMessage());
                ToastUtil.showShort(t.getMessage());
            }
        });
    }



    private void requestData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InterApi.BaseURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);
        Map<String, String> requestMap = NoHttpRequest.setHeaderParams(user_id);

        //传入请求参数
        Call<ResponseBody> call = retrofitApi.getOrderList(requestMap);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    String json = response.body().string();
                    LogUtil.d("ComFirstFragment_new::", json);
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");

                    if (isFresh) {
                        recyclerView.refreshComplete();
                    } else {
                        recyclerView.loadMoreComplete();
                    }

                    if ("5001".equals(code)) {
                        mList.clear();
                        mAdapter.notifyDataSetChanged();
                        JSONObject dataObj = jsonObject.getJSONObject("data");
                        JSONObject mail_order = dataObj.getJSONObject("mail_order");
                        JSONObject water_order = dataObj.getJSONObject("water_order");
                        JSONObject cleaning_order = dataObj.getJSONObject("cleaning_order");
                        JSONArray maillist = mail_order.getJSONArray("maillist");
                        JSONArray waterlist = water_order.getJSONArray("waterlist");
                        JSONArray cleaninglist = cleaning_order.getJSONArray("cleaninglist");

                        mail_number = IntegerUtil.getStringChangeToNumber(mail_order.getString("sum"));
                        water_number = IntegerUtil.getStringChangeToNumber(water_order.getString("sum"));
                        clean_number = IntegerUtil.getStringChangeToNumber(cleaning_order.getString("sum"));


                        setMailList(maillist);     //订单
                        setWaterList(waterlist);    //桶装水
                        setClearList(cleaninglist);   //干洗
                        mAdapter.notifyDataSetChanged();
                    } else {
                        ToastUtil.showShort(msg);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                LogUtil.d("ComFirstFragment_new::", t.getMessage());
            }
        });
    }

    private void confirmReceive(String mail_id) {
        Intent intent = new Intent(getActivity(), ConfirmReceiveActivity.class);
        intent.putExtra("distributor_id", user_id);
        intent.putExtra("mail_id", mail_id);
        startActivity(intent);
    }

    private void setClearList(JSONArray cleaninglist) throws JSONException {
        int length = cleaninglist.length();
        for (int i = 0; i < cleaninglist.length() ; i++) {
            List<HashMap<String, String>> list = new ArrayList<>();
            JSONObject jsonObject = cleaninglist.getJSONObject(i);
            String id = jsonObject.getString("id");
            String user_name = jsonObject.getString("user_name");
            String user_mobile = jsonObject.getString("user_mobile");
            String region = jsonObject.getString("region");
            String address = jsonObject.getString("address");
            String time_appointment = jsonObject.getString("time_appointment");
            String pay_states = jsonObject.getString("pay_states");
            String states = jsonObject.getString("states");
            String order_number = jsonObject.getString("order_number");
            String create_time = jsonObject.getString("create_time");
            String zongjia = jsonObject.getString("zongjia");
            String overtime = jsonObject.getString("overtime");
            String juli_time = jsonObject.getString("juli_time");
            JSONArray detailed = jsonObject.getJSONArray("detailed");
            ProductModel productModel = new ProductModel();
            productModel.setContext(time_appointment);

            for (int j = 0; j < detailed.length(); j++) {
                JSONObject data = detailed.getJSONObject(j);
                HashMap<String, String> map = new HashMap<>();
                map.put("id", data.getString("id"));
                map.put("commodity_id",data.getString("commodity_id"));
                map.put("orders_id",data.getString("orders_id"));
                map.put("commodity_name", data.getString("commodity_name"));
                map.put("money", data.getString("money"));
                map.put("number",data.getString("number"));
                map.put("total",data.getString("total"));
                map.put("create_time", data.getString("create_time"));
                map.put("update_time",data.getString("update_time"));
                list.add(map);
                map = null;
            }
            productModel.setClearMessageList(list);

            int stateTag = Integer.parseInt(states);


            if (stateTag == 8 && pay_states.equals("1")) {
                productModel.setClearStateName("等待用户支付");
            } else if (stateTag == 5) {
                productModel.setClearStateName("等待干洗店取件");
            } else if (stateTag == 6) {
                productModel.setClearStateName("等待干洗店送件");
            } else if (stateTag == 7) {
                productModel.setClearStateName("等待用户取件");
            }

            if("2".equals(overtime)) {
                juli_time = "已超时"+juli_time;
            } else {
                juli_time = "剩余"+juli_time;
            }

            productModel.setShowType(i == 0);
            productModel.setAccountPrice(zongjia);
            productModel.setCreateTime(create_time);
            productModel.setJuli_time(juli_time);
            productModel.setOrderNumber(order_number);
            productModel.setClearState(stateTag);
            productModel.setOrders_id(id);
            productModel.setType(2);
            productModel.setPayfor(pay_states);
            productModel.setProductName(user_name);
            productModel.setPhone(user_mobile);
            productModel.setAddress(region + address);
            productModel.setShowBottom(i == cleaninglist.length() - 1);
            mList.add(productModel);
            productModel = null;
        }

        if (length == 0) {
            ProductModel productModel = new ProductModel();
            productModel.setType(6);
            mList.add(productModel);
        }

        ProductModel productModel = new ProductModel();
        productModel.setType(3);
        mList.add(productModel);
    }

    private void setWaterList(JSONArray waterlist) throws JSONException {
        int length = waterlist.length();
        for (int i = 0; i < length; i++) {
            List<HashMap<String, String>> list = new ArrayList<>();
            ProductModel productModel = new ProductModel();
            JSONObject jsonObject = waterlist.getJSONObject(i);
            String id = jsonObject.getString("id");
            String user_name = jsonObject.getString("user_name");
            String user_mobile = jsonObject.getString("user_mobile");
            String region = jsonObject.getString("region");
            String address = jsonObject.getString("address");
            String create_time = jsonObject.getString("create_time");
            String order_number = jsonObject.getString("order_number");
            String accountPrice = jsonObject.getString("zongjia");
            String overtime = jsonObject.getString("overtime");
            String juli_time = jsonObject.getString("juli_time");
            String states = jsonObject.getString("states");
            String time_appointment = jsonObject.getString("time_appointment");
            JSONArray detailed = jsonObject.getJSONArray("detailed");

            for (int j = 0; j < detailed.length(); j++) {
                JSONObject jsonObject1 = detailed.getJSONObject(j);
                HashMap<String, String> map = new HashMap<>();
                map.put("id", jsonObject1.getString("id"));
                map.put("commodity_id", jsonObject1.getString("commodity_id"));
                map.put("commodity_name", jsonObject1.getString("commodity_name"));
                map.put("money", jsonObject1.getString("money"));
                map.put("number", jsonObject1.getString("number"));
                map.put("total", jsonObject1.getString("total"));
                map.put("create_time", jsonObject1.getString("create_time"));
                map.put("update_time", jsonObject1.getString("update_time"));
                list.add(map);
                map = null;
            }

            if("2".equals(overtime)) {
                juli_time = "已超时"+juli_time;
            } else {
                juli_time = "剩余"+juli_time;
            }

            productModel.setWaterMessageList(list);
            productModel.setShowType(i == 0);
            productModel.setJuli_time(juli_time);
            productModel.setType(1);
            productModel.setStates(states);
            productModel.setProductName(user_name);
            productModel.setContext(time_appointment);
            productModel.setOrders_id(id);
            productModel.setPhone(user_mobile);
            productModel.setCreateTime(create_time);
            productModel.setAccountPrice(accountPrice);
            productModel.setOrderNumber(order_number);
            productModel.setAddress(region + address);
            productModel.setShowBottom(i == waterlist.length() - 1);
            mList.add(productModel);
            productModel = null;
        }
        if (length == 0) {
            ProductModel productModel = new ProductModel();
            productModel.setType(5);
            mList.add(productModel);
        }

    }

    private void setMailList(JSONArray maillist) throws JSONException {
        int length = maillist.length();
        for (int i = 0; i < length; i++) {
            JSONObject jsonObject = maillist.getJSONObject(i);
            String id = jsonObject.getString("id");
            String send_name = jsonObject.getString("send_name");
            String send_phone = jsonObject.getString("send_phone");
            String send_region = jsonObject.getString("send_region");
            String send_address = jsonObject.getString("send_address");

            ProductModel productModel = new ProductModel();
            productModel.setShowType(i == 0);
            productModel.setMail_id(id);
            productModel.setType(0);
            productModel.setProductName(send_name);
            productModel.setPhone(send_phone);
            productModel.setAddress("收件地址：" + send_region + send_address);
            productModel.setShowBottom(i == maillist.length() - 1);
            mList.add(productModel);
            productModel = null;
        }

        if (length == 0) {
            ProductModel productModel = new ProductModel();
            productModel.setType(4);
            mList.add(productModel);
        }
    }

    private void identifyPerson(String mail_id) {
        Request<String> request = NoHttpRequest.identifySealRequest(user_id, mail_id);
        mRequestQueue.add(REQUEST_IDENTIFY, request, mresponseListener);
    }

    private OnResponseListener<String> mresponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            //  dialogLoading.show();
        }

        @Override
        public void onSucceed(int what, com.yanzhenjie.nohttp.rest.Response<String> response) {
            LogUtil.i("photoFile", "WaitCollectFragment::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                if ("5001".equals(code)) {
                    confirmReceive(mail_id);
                } else {
                    if (what == REQUEST_IDENTIFY) {
                        //提示身份验证
                        showPromitDialog();
                    } else {
                        ToastUtil.showShort("查询失败，请重试！");
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //   dialogLoading.cancel();
        }

        @Override
        public void onFailed(int what, com.yanzhenjie.nohttp.rest.Response<String> response) {
            // dialogLoading.cancel();
        }

        @Override
        public void onFinish(int what) {
            //   dialogLoading.cancel();
        }
    };

    private void showPromitDialog() {
        MessageDialog.show(getActivity(), "提示", "身份未验证！", "知道了",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), IdentificationActivity.class);
                        intent.putExtra("come_type", false);
                        intent.putExtra("mail_id", mail_id);
                       // intent.putExtra("send_name", send_name);
                        startActivity(intent);
                        dialog.dismiss();
                    }
                });
    }

    private void initView() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mAdapter = new WaitHandleAdapter(getActivity(), mList);
        recyclerView = mView.findViewById(R.id.rl_product);
        View head = LayoutInflater.from(getActivity()).inflate(R.layout.head, (ViewGroup) getActivity().findViewById(android.R.id.content), false);

        ll_express = head.findViewById(R.id.ll_express);
        ll_water = head.findViewById(R.id.ll_water);
        ll_clear = head.findViewById(R.id.ll_clear);

        ll_express.setOnClickListener(this);
        ll_water.setOnClickListener(this);
        ll_clear.setOnClickListener(this);
        recyclerView.addHeaderView(head);
        recyclerView.setRefreshHeader(new ArrowRefreshHeader(getActivity()));
        recyclerView.setLoadingListener(this);
        recyclerView.setLayoutManager(mLinearLayoutManager);
        recyclerView.setAdapter(mAdapter);
    }

    private void initParams() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        dialogLoading = new HkDialogLoading(getActivity(), "请稍后...");

        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }

        OkLogInterceptor okLogInterceptor = OkLogInterceptor.builder().build();
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();
        okHttpBuilder.addInterceptor(okLogInterceptor);
        okHttpClient = okHttpBuilder.build();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_express:
                LinearSmoothScroller s1 = new TopSmoothScroller(getActivity());
                s1.setTargetPosition(2);
                mLinearLayoutManager.startSmoothScroll(s1);
                break;
            case R.id.ll_water:
                LinearSmoothScroller s2 = new TopSmoothScroller(getActivity());
                s2.setTargetPosition(2 + mail_number);
                mLinearLayoutManager.startSmoothScroll(s2);
                break;
            case R.id.ll_clear:
                LinearSmoothScroller s3 = new TopSmoothScroller(getActivity());
                s3.setTargetPosition(2 + mail_number + (water_number == 0 ? 1 : water_number));
                mLinearLayoutManager.startSmoothScroll(s3);
                break;
        }
    }

    private void requestPannelMessage() {
        Request<String> request = NoHttpRequest.getPannelmessageRequest(user_id);
        mRequestQueue.add(REQUEST_PANNEL_MESSAGE, request, mResponseListener);
    }

    private OnResponseListener<String> mResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            //   dialogLoading.show();
        }

        @Override
        public void onSucceed(int what, com.yanzhenjie.nohttp.rest.Response<String> response) {
            LogUtil.i("photoFile", "ComFirstFragment_new::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    handleEvent(what, jsonObject);
                } else {
                    ToastUtil.showShort(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                // dialogLoading.cancel();
                ToastUtil.showShort("更新失败！");
            }
            //  dialogLoading.cancel();
        }

        @Override
        public void onFailed(int what, com.yanzhenjie.nohttp.rest.Response<String> response) {
            //  dialogLoading.cancel();
            ToastUtil.showShort("连接服务器失败！");
        }

        @Override
        public void onFinish(int what) {
            //  dialogLoading.cancel();
        }
    };

    private void handleEvent(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case REQUEST_PANNEL_MESSAGE:      //更新主界面的信息
                chnagePannelMessage(jsonObject);
                break;
        }
    }

    private void chnagePannelMessage(JSONObject jsonObject) throws JSONException {
        JSONObject dataObj = jsonObject.getJSONObject("data");
        String money = dataObj.getString("money");   //账户余额
        String prohibit = dataObj.getString("prohibit");   //用户状态
        String version_number = dataObj.getString("version_number");   //版本号
        //版本地址
        version_url = dataObj.getString("version_url");

        money = StringUtil.handleNullResultForNumber(money);
        float moneyNumber = Float.parseFloat(money);

        if ("2".equals(prohibit)) {
            SharedPreferences.Editor editor = SharedPreferencesUtil.getEditor();
            editor.remove("userName");
            editor.remove("password");
            editor.commit();
            ToastUtil.showShort("您的驿站已禁止登录!");
            Intent intent = new Intent(getActivity(),LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        } else if (moneyNumber == 0) {
            showRechargeDiaolog();   //提示充值
        } else {
            upLoadNewVersion(version_number, version_url);    //更新最新版本
        }
    }

    private void showRechargeDiaolog() {
        MessageDialog.show(getActivity(), "提示", "余额不足，请充值", "去充值", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getActivity(),RechargeActivity.class);
                startActivity(intent);
                dialog.dismiss();
            }
        });
    }

    private void upLoadNewVersion(String version_number, String version_url) {
        String version = SystemUtil.getVersion(getActivity());
        if (!version.equals(version_number)) {
            showDownLoadDialog(version_url);
        }
    }

    private void showDownLoadDialog(String version_url) {

        DialogUtil.promptDialog1(getActivity(), "更新提示", "有新版本上线，请先更新！", DetermineListener, throwListener);
    }


    android.content.DialogInterface.OnClickListener DetermineListener = new android.content.DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            SharedPreferences.Editor editor = SharedPreferencesUtil.getEditor();
           /* editor.putString("userName","");
            editor.putString("password","");*/
            editor.remove("userName");
            editor.remove("password");
            editor.commit();
            download();
        }
    };
    private void download() {
        mProgressBar = new ProgressDialog(getActivity());
        mProgressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressBar.setTitle("正在下载");
        mProgressBar.setMessage("请稍后...");
        mProgressBar.setProgress(0);
        mProgressBar.setMax(100);
        mProgressBar.show();
        mProgressBar.setCancelable(false);

        DownloadUtil.get().download(version_url, APP_PATH_ROOT, fileName, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
                if (mProgressBar != null && mProgressBar.isShowing()) {
                    mProgressBar.dismiss();
                }
                //下载完成进行相关逻辑操作
                installApk(file);// 安装
            }

            @Override
            public void onDownloading(int progress) {
                mProgressBar.setProgress(progress);
            }

            @Override
            public void onDownloadFailed(Exception e) {
                //下载异常进行相关提示操作
            }
        });

    }



    android.content.DialogInterface.OnClickListener throwListener = new android.content.DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            dialogLoading.cancel();
        }
    };

    private void installApk(File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //判断是否是AndroidN以及更高的版本
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            Uri photoURI = FileProvider.getUriForFile(getActivity(), MyApplication.getInstance().getPackageName() + ".provider", file);
            intent.setDataAndType(photoURI, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    private boolean isFresh = true;

    @Override
    public void onRefresh() {
        isFresh = true;
        requestData();
    }

    @Override
    public void onLoadMore() {
        recyclerView.loadMoreComplete();
    }
       /* isFresh = false;
        requestData();
    }*/
}
