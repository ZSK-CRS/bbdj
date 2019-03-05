package com.mt.bbdj.community.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.MoneyFormatManagerAdapter;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MoneyFormatManagerActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.finance_list)
    XRecyclerView financeList;
    private LinearLayout headll;

    //用于记录CustomHScrollView的初始位置
    private int leftPos;
    private int topPos;
    private MoneyFormatManagerAdapter sheetAdapter;
    private String startTime = "";
    private String endTime = "";

    private List<HashMap<String, String>> mData = new ArrayList<>();
    private String user_id;
    private RequestQueue mRequestQueue;

    private final int REQUEST_MONRY_MANAGER = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_money_format_manager);
        ButterKnife.bind(this);
        initParams();
        initList();
        requestData();
        initListener();
    }

    private void initListener() {
        ImageView tvTime = findViewById(R.id.tv_time_select);
        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.showShort("niha");
            }
        });
    }

    private void initParams() {
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = daoSession.getUserBaseMessageDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        mRequestQueue = NoHttp.newRequestQueue();
    }

    private void requestData() {
        startTime = "1551399010";
        endTime = "1551703510";
        Request<String> request = NoHttpRequest.getMoneyManagerRequest(user_id, startTime, endTime);
        mRequestQueue.add(REQUEST_MONRY_MANAGER, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                //
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "MoneyFormatManagerActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    JSONObject data = jsonObject.getJSONObject("data");

                    if ("5001".equals(code)) {
                        setListData(data);     //设置列表
                        setOtherData(data);    //设置其他数据
                    } else {
                        ToastUtil.showShort(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    ToastUtil.showShort("连接失败！");
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

    private void setOtherData(JSONObject data) throws JSONException {
        String mailingsum = data.getString("mailingsum");
        String entersum = data.getString("entersum");
        String outsum = data.getString("outsum");
        String paysum = data.getString("paysum");
        String balance = data.getString("balance");
        String available_balance = data.getString("available_balance");
        String min_balance = data.getString("min_balance");
        String message = data.getString("message");
        String face_sheet = data.getString("face_sheet");
        String servicesum = data.getString("servicesum");
        String deliversum = data.getString("deliversum");
        String mailmoney = data.getString("mailmoney");

    }

    private void setListData(JSONObject data) throws JSONException {
        JSONArray mailinglist = data.getJSONArray("mailinglist");
        for (int i = 0; i < mailinglist.length(); i++) {
            JSONObject jsonObject1 = mailinglist.getJSONObject(i);
            String express_name = jsonObject1.getString("express_name");
            String waybill_number = jsonObject1.getString("waybill_number");
            String send_name = jsonObject1.getString("send_name");
            String collect_name = jsonObject1.getString("collect_name");
            String send_region = jsonObject1.getString("send_region");
            String collect_region = jsonObject1.getString("collect_region");
            String time = jsonObject1.getString("time");
            String goods_weight = jsonObject1.getString("goods_weight");
            String service_money = jsonObject1.getString("service_money");
            String deliver_money = jsonObject1.getString("deliver_money");
            HashMap<String,String> map = new HashMap<>();
            int number = i+1;
            map.put("number",number+"");
            map.put("express_name",express_name);
            map.put("waybill_number",waybill_number);
            map.put("send_name",send_name);
            map.put("collect_name",collect_name);
            map.put("send_region",send_region);
            map.put("collect_region",collect_region);
            map.put("time",DateUtil.changeStampToStandrdTime("yy/MM/dd HH:mm",time));
            map.put("goods_weight",goods_weight);
            map.put("service_money",service_money);
            map.put("deliver_money",deliver_money);
            mData.add(map);
            map = null;
        }
        sheetAdapter.notifyDataSetChanged();
    }


    class MyTouchLinstener implements View.OnTouchListener {
        float lastX = 0;
        float lastY = 0;
        private boolean isClick = false;
        private long downTime = 0;

        @Override
        public boolean onTouch(View arg0, MotionEvent ev) {
            //判断是否是点击
            float tempX = ev.getX();
            float tempY = ev.getY();
            switch (ev.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    lastX = tempX;
                    lastY = tempY;
                    downTime = System.currentTimeMillis();
                    break;
                case MotionEvent.ACTION_MOVE:
                    isClick = false;
                    break;
                case MotionEvent.ACTION_UP:
                    if (Math.abs(lastX - tempX) > 10 || Math.abs(lastY - tempY) > 10) {
                        isClick = false;
                    } else {
                        isClick = true;
                    }
                    long timeDef = System.currentTimeMillis() - downTime;
                    if (timeDef <= 40 && isClick) {
                        isClick = false;
                    }
                    break;
            }
            if (isClick) {
                int position = sheetAdapter.getTouchPosition();
                Toast.makeText(MoneyFormatManagerActivity.this, position + "", Toast.LENGTH_SHORT).show();
            } else {
                //当在表头和listView控件上touch时，将事件分发给 ScrollView
                HorizontalScrollView headSrcrollView = (HorizontalScrollView) headll.findViewById(R.id.h_scrollView);
                headSrcrollView.onTouchEvent(ev);
            }
            return false;
        }

    }

    private void initList() {
        headll = (LinearLayout) findViewById(R.id.scrollview_head);
        headll.setFocusable(true);
        headll.setClickable(true);

        financeList.setFocusable(false);
        financeList.setNestedScrollingEnabled(false);

      /*  LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false) {
            @Override
            public boolean canScrollHorizontally() {
                return false;
            }

            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };*/

        financeList.setLayoutManager(new LinearLayoutManager(this));
        financeList.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL, Color.parseColor("#eeeeee"), 1));
        financeList.setLoadingListener(this);
        financeList.setFocusableInTouchMode(false);
        sheetAdapter = new MoneyFormatManagerAdapter(this, mData, headll, 1);
        sheetAdapter.notifyDataSetChanged();
        financeList.setAdapter(sheetAdapter);

        //设置滑动监听
        headll.setOnTouchListener(new MyTouchLinstener());
        financeList.setOnTouchListener(new MyTouchLinstener());
    }

    /**
     * 记录CustomHScrollView的初始位置
     *
     * @param l
     * @param t
     */
    public void setPosData(int l, int t) {
        this.leftPos = l;
        this.topPos = t;
    }


    @Override
    public void onRefresh() {
        financeList.refreshComplete();
    }

    @Override
    public void onLoadMore() {
        financeList.loadMoreComplete();
    }
}
