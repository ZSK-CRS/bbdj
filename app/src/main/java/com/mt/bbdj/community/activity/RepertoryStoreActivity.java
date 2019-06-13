package com.mt.bbdj.community.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.SlidingTabLayout;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.kongzue.dialog.v2.SelectDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.ExpressLogo;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.PrintTagModel;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.baseconfig.view.MyPopuwindow;
import com.mt.bbdj.community.adapter.RepertoryAdapter;
import com.mt.bbdj.community.adapter.SimpleFragmentPagerAdapter;
import com.mt.bbdj.community.adapter.SimpleStringAdapter;
import com.mt.bbdj.community.fragment.RepertoryFragment;
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

public class RepertoryStoreActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.bt_receive)
    TextView btReceive;
    @BindView(R.id.rl_repertory)
    XRecyclerView recyclerView;
    @BindView(R.id.tv_fast_select)
    ImageView tvFastSelect;                   //筛选快递公司

    private List<HashMap<String, String>> mFastData = new ArrayList<>();    //快递公司

    private MyPopuwindow popupWindow;
    private View selectView;
    private ExpressLogoDao mExpressLogoDao;
    private ArrayList<HashMap<String, String>> mList;
    private ArrayList<HashMap<String, String>> mPrintList = new ArrayList<>();
    private RequestQueue mRequestQueue;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private PrintTagModel printTagModel = new PrintTagModel();
    private String user_id = "";
    private String express_id = "";   //快递公司id
    private RepertoryAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repertory_);
        ButterKnife.bind(this);
        initParams();
        initData();
        initView();
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerView.refresh();
    }


    private void initListener() {
        //筛选快递
        tvFastSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //筛选快递
                showSelectPop(view);
            }
        });

        mAdapter.setOnItemClickListener(new RepertoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onItemDelate(int position) {
                showSelectDialog(position);
            }
        });
    }

    private void showSelectDialog(int position) {
        SelectDialog.show(this, "提示", "确定删除该数据吗", "确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteRecorde(position);   //删除该信息
                        dialog.dismiss();
                    }
                }, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setCanCancel(true);
    }

    private void deleteRecorde(int position) {
        String package_id = mList.get(position).get("id");
        Request<String> request = NoHttpRequest.confirmEnterStoreRequest(user_id, package_id);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "RepertoryStoreActivity::" + response.get());
                try {

                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();

                    if ("5001".equals(code)) {

                        recyclerView.refresh();
                    }
                    ToastUtil.showShort(msg);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
            }

            @Override
            public void onFinish(int what) {
            }
        });

    }


    private void showSelectPop(View view) {
        popupWindow.showAsDropDown(view);
    }

    private void initParams() {

        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        mDaoSession = GreenDaoManager.getInstance().getSession();
        mExpressLogoDao = mDaoSession.getExpressLogoDao();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();
        mExpressLogoDao = mDaoSession.getExpressLogoDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
    }


    private void initSelectPop() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            selectView = getLayoutInflater().inflate(R.layout.fast_layout, null);
            RecyclerView fastList = selectView.findViewById(R.id.tl_fast_list);
            initRecycler(fastList);
            popupWindow = new MyPopuwindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //设置动画
            popupWindow.setAnimationStyle(R.style.popup_window_anim);
            //设置背景颜色
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));
            popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popupWindow.setTouchable(true); // 设置popupwindow可点击
            popupWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popupWindow.setFocusable(true); // 获取焦点
            selectView.findViewById(R.id.layout_left_close).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (popupWindow != null && popupWindow.isShowing()) {
                        popupWindow.dismiss();
                    }
                }
            });
        }
    }

    private void initRecycler(RecyclerView fastList) {
        mFastData.clear();
        //查询快递公司的信息
        List<ExpressLogo> expressLogoList = mExpressLogoDao.queryBuilder()
                .where(ExpressLogoDao.Properties.Property.eq(1))
                .where(ExpressLogoDao.Properties.States.eq(1)).list();
        HashMap<String, String> map = new HashMap<>();
        map.put("express", "全部");
        map.put("express_id", "");
        mFastData.add(map);
        if (expressLogoList != null && expressLogoList.size() != 0) {
            for (ExpressLogo expressLogo : expressLogoList) {
                HashMap<String, String> map1 = new HashMap<>();
                map1.put("express", expressLogo.getExpress_name());
                map1.put("express_id", expressLogo.getExpress_id());
                mFastData.add(map1);
                map1 = null;
            }
        }
        SimpleStringAdapter goodsAdapter = new SimpleStringAdapter(this, mFastData);
        goodsAdapter.setOnItemClickListener(new SimpleStringAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                //选中的快递公司id
                express_id = mFastData.get(position).get("express_id");
                recyclerView.refresh();
                popupWindow.dismiss();
            }
        });

        fastList.setAdapter(goodsAdapter);
        fastList.addItemDecoration(new MarginDecoration(this));
        fastList.setLayoutManager(new LinearLayoutManager(this));
        goodsAdapter.notifyDataSetChanged();
    }


    private void initView() {
        initSelectPop();    //初始化快递公司列表
        initDataRecycler();
    }

    private void initDataRecycler() {
        mList = new ArrayList<>();
        mAdapter = new RepertoryAdapter(this, mList,true);
        // initListData();
        recyclerView.setFocusable(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLoadingListener(this);
        recyclerView.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL, Color.parseColor("#f4f4f4"), 1));
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);

    }


    private void initData() {

    }

    @OnClick({R.id.bt_receive, R.id.iv_back})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.iv_back) {
            finish();
        } else if (view.getId() == R.id.bt_receive) {
            confirmPackage();
        }
    }

    private void confirmPackage() {
        String package_id = getPackageId();
        Request<String> request = NoHttpRequest.confirmEnterStoreRequest(user_id, package_id);
        mRequestQueue.add(2, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "RepertoryStoreActivity::" + response.get());
                try {

                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();


                    if ("5001".equals(code)) {

                        if (isFresh) {
                            mList.clear();
                            mAdapter.notifyDataSetChanged();
                        }
                        ToastUtil.showShort("揽收成功");
                        //  setPrintData(jsonObject);    //设置打印数据

                    } else {
                        ToastUtil.showShort(msg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {
            }

            @Override
            public void onFinish(int what) {
            }
        });

    }

    private void setPrintData(JSONObject jsonObject1) throws JSONException {
        JSONArray data = jsonObject1.getJSONArray("data");

        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonObject = data.getJSONObject(i);
            String code = jsonObject.getString("code");
            String qrcode = jsonObject.getString("qrcode");
            String pie_number = jsonObject.getString("pie_number");
            HashMap<String, String> map = new HashMap<>();
            map.put("code", code);
            map.put("pie_number", pie_number);
            map.put("qrcode", qrcode);
            mPrintList.add(map);
            map = null;
        }
        printTagModel.setData(mPrintList);
        //  dialogLoading.doDismiss();
        Intent intent = new Intent(RepertoryStoreActivity.this, BluetoothNumberActivity.class);
        intent.putExtra("printData", printTagModel);
        startActivity(intent);
    }

    private String getPackageId() {
        StringBuilder sb = new StringBuilder();
        for (HashMap<String, String> map : mList) {
            sb.append(map.get("id"));
            sb.append(",");
        }
        String result = sb.toString();
        String realResult = result.substring(0, result.lastIndexOf(","));
        return realResult;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mFastData.clear();
        mFastData = null;
    }

    private void requestData() {
        Request<String> request = NoHttpRequest.getEnterStoreRequest(user_id, express_id);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "RepertoryStoreActivity::" + response.get());
                try {

                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();

                    if (isFresh) {
                        recyclerView.refreshComplete();
                    } else {
                        recyclerView.loadMoreComplete();
                    }

                    if ("5001".equals(code)) {

                        if (isFresh) {
                            mList.clear();
                        }

                        JSONArray dataArray = jsonObject.getJSONArray("data");
                        setData(dataArray);
                    } else {
                        ToastUtil.showShort(msg);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(int what, Response<String> response) {

            }

            @Override
            public void onFinish(int what) {

            }
        });
    }

    private void setData(JSONArray dataArray) throws JSONException {
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject obj = dataArray.getJSONObject(i);
            HashMap<String, String> map = new HashMap<>();
            map.put("id", obj.getString("id"));
            map.put("order", obj.getString("waybill_number"));
            map.put("tag_number", obj.getString("pickup_code"));
            String time = obj.getString("create_time");
            time = DateUtil.changeStampToStandrdTime("yyyy-MM-dd HH:mm", time);
            map.put("time", time);
            map.put("express", obj.getString("express_name"));
            mList.add(map);
            map = null;
        }
        mAdapter.notifyDataSetChanged();
    }

    private boolean isFresh = true;

    @Override
    public void onRefresh() {
        isFresh = true;
        requestData();
    }

    @Override
    public void onLoadMore() {
        isFresh = false;
        recyclerView.loadMoreComplete();
    }
}
