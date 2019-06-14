package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.github.simonpercic.oklog3.OkLogInterceptor;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.InterApi;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.internet.RetrofitApi;
import com.mt.bbdj.baseconfig.internet.RetrofitConfig;
import com.mt.bbdj.baseconfig.model.ProductModel;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.adapter.FastmailMessageAdapter;
import com.mt.bbdj.community.adapter.WaterOrderAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WaterOrderActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    private XRecyclerView recyclerView;
    private List<ProductModel> mList = new ArrayList<>();
    private WaterOrderAdapter adapter;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String user_id;
    private OkHttpClient okHttpClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        setContentView(R.layout.activity_water_order);
        initParams();
        initView();
        initListener();
    }

    private void initListener() {
        adapter.setWaterOrderClickListener(new WaterOrderAdapter.WaterOrderClickListener() {
            @Override
            public void OnItemClick(int position) {
               ProductModel productModel = mList.get(position);
                Intent intent = new Intent(WaterOrderActivity.this,WaterServiceDetailActivity.class);
                intent.putExtra("productModel",productModel);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.refresh();
    }

    private void initParams() {
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

    private void requestData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(InterApi.BaseURL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitApi retrofitApi = retrofit.create(RetrofitApi.class);
        Map<String, String> requestMap = NoHttpRequest.setHeaderParams(user_id);
        //传入请求参数
        Call<ResponseBody> call = retrofitApi.getServiceOrder(requestMap, "1", mPage + "");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    String json = response.body().string();
                    LogUtil.d("WaterOrderActivity::", json);
                    JSONObject jsonObject = new JSONObject(json);
                    String code = jsonObject.getString("code");
                    String msg = jsonObject.getString("msg");
                    JSONArray data = jsonObject.getJSONArray("data");
                    if (isFresh) {
                        recyclerView.refreshComplete();
                    } else {
                        recyclerView.loadMoreComplete();
                    }

                    if ("5001".equals(code)) {
                        if (isFresh) {
                            mList.clear();
                        }
                        setData(data);
                        adapter.notifyDataSetChanged();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void setData(JSONArray data) throws JSONException {
        for (int i = 0; i < data.length(); i++) {
            List<HashMap<String, String>> list = new ArrayList<>();
            ProductModel productModel = new ProductModel();
            JSONObject dataObj = data.getJSONObject(i);
            productModel.setOrderNumber(dataObj.getString("order_number"));
            productModel.setProductName(dataObj.getString("commodity_name"));
            productModel.setAddress(dataObj.getString("region") + dataObj.getString("address"));
            productModel.setCreateTime(dataObj.getString("create_time"));
            productModel.setHandleTime(dataObj.getString("handle_time"));
            productModel.setPhone(dataObj.getString("user_mobile"));
            productModel.setAccountPrice(dataObj.getString("total"));
            JSONArray detailed = dataObj.getJSONArray("detailed");

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

            String states = dataObj.getString("states");
            String is_cancel = dataObj.getString("is_cancel");

            if ("4".equals(states)) {
                productModel.setOrderState(1);
            } else if ("2".equals(is_cancel)) {
                productModel.setOrderState(2);
            }
            productModel.setWaterMessageList(list);
            mList.add(productModel);
            productModel = null;
        }
    }

    private void initView() {
        recyclerView = findViewById(R.id.recycler);
        adapter = new WaterOrderAdapter(mList);
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.setLoadingListener(this);
    }

    private boolean isFresh = true;
    private int mPage = 1;

    @Override
    public void onRefresh() {
        isFresh = true;
        mPage = 1;
        requestData();
    }

    @Override
    public void onLoadMore() {
        isFresh = false;
        mPage++;
        requestData();
    }
}
