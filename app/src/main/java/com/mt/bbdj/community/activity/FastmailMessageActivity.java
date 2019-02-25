package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.community.adapter.FastmailMessageAdapter;
import com.mt.bbdj.community.adapter.MessagePannelAdapter;
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
import butterknife.OnClick;

//快递信息，收件人，寄件人信息
public class FastmailMessageActivity extends BaseActivity implements XRecyclerView.LoadingListener {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;         //返回
    @BindView(R.id.rl_address_list)
    XRecyclerView rlAddressList;     //列表
    @BindView(R.id.tv_add_address)
    TextView tvAddAddress;         //添加地址
    @BindView(R.id.tv_no_address)
    TextView tvNoAddress;     //提示语

    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private FastmailMessageAdapter messageAdapter;

    private RequestQueue mRequestQueue;
    private HkDialogLoading dialogLoading;
    private List<HashMap<String,String>> mList;
    private int mType;   // 1 : 寄件地址   2： 收件地址

    private final int TYPE_CHANGE_ADDRESS = 1;    //修改地址
    private final int TYPE_ADD_ADDRESS = 2;    //新添地址
    private final int TYPE_DELETE_ADDRESS = 3;   //删除地址
    private final int TYPE_GET_ADDRESS = 4;   //获取地址列表


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fastmail_message);
        ButterKnife.bind(this);

        initData();

        requestData();
    }

    private void requestData() {
        String user_id = "";
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        Request<String> request = NoHttpRequest.getStageAddressRequest(user_id,mType+"");
        mRequestQueue.add(TYPE_GET_ADDRESS, request, mResponseListener);
    }


    private void initClickListener() {
        //点击删除
        messageAdapter.setDeleteClickListener(new FastmailMessageAdapter.OnDeleteClickListener() {
            @Override
            public void onClick(int position) {
                deleteAddress(mList.get(position));
            }
        });

        //点击编辑
        messageAdapter.setEditClickListener(new FastmailMessageAdapter.OnEditClickListener() {
            @Override
            public void onClick(int position) {
                //跳转编辑
                changeAddress(position);
            }
        });

        //选中
        messageAdapter.setItemClickListener(new FastmailMessageAdapter.OnItemSelectClickListener() {
            @Override
            public void onItemSelectClick(int position) {
                Intent intent = new Intent();
                HashMap<String,String> item = mList.get(position);
                intent.putExtra("book_id",item.get("book_id"));
                intent.putExtra("book_name",item.get("book_name"));
                intent.putExtra("book_telephone",item.get("book_telephone"));
                intent.putExtra("book_region",item.get("book_region"));
                intent.putExtra("book_address",item.get("book_address"));
                intent.putExtra("book_province",item.get("book_province"));
                intent.putExtra("book_city",item.get("book_city"));
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    private void handleResult(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case TYPE_GET_ADDRESS:     //获取地址列表
                setMessageData(jsonObject);
                break;
            case TYPE_DELETE_ADDRESS:   //删除地址
                ToastUtil.showShort("删除成功！");
                rlAddressList.refresh();
                break;
        }
    }

    private void deleteAddress(HashMap<String, String> addressMap) {
        String user_id = "";
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        Request<String> request = NoHttpRequest.deleteAddressRequest(user_id,addressMap.get("book_id"));
        mRequestQueue.add(TYPE_DELETE_ADDRESS,request,mResponseListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (RESULT_OK == resultCode) {
            rlAddressList.refresh();
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_add_address})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_add_address:
                addAddressEvent();
                break;
        }
    }

    private OnResponseListener<String> mResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
          //  dialogLoading.show();
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "FastmailMessageActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    handleResult(what,jsonObject);
                } else {
                    //这里是以为处理获取失败的一点小容错
                    if (what == TYPE_DELETE_ADDRESS) {
                        ToastUtil.showShort(msg);
                    }
                    if (what == TYPE_GET_ADDRESS) {
                        handleNullData();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
          //  dialogLoading.cancel();
        }

        @Override
        public void onFailed(int what, Response<String> response) {
         //   dialogLoading.cancel();
        }

        @Override
        public void onFinish(int what) {
         //   dialogLoading.cancel();
        }
    };

    private void handleNullData() {
        mList.clear();
        messageAdapter.notifyDataSetChanged();
        tvNoAddress.setVisibility(View.VISIBLE);
        rlAddressList.setVisibility(View.GONE);
    }



    private void setMessageData(JSONObject jsonObject) throws JSONException {
        mList.clear();
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        if (jsonArray.length() == 0) {
            tvNoAddress.setVisibility(View.VISIBLE);
            rlAddressList.setVisibility(View.GONE);
        } else {
            tvNoAddress.setVisibility(View.GONE);
            rlAddressList.setVisibility(View.VISIBLE);
        }
        for (int i = 0;i < jsonArray.length();i++) {
            HashMap<String,String> map = new HashMap<>();
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            String book_id = jsonObject1.getString("book_id");
            String book_name = jsonObject1.getString("book_name");
            String book_telephone = jsonObject1.getString("book_telephone");
            String book_region = jsonObject1.getString("book_region");
            String book_address = jsonObject1.getString("book_address");

            String book_province = jsonObject1.getString("book_province");
            String book_city = jsonObject1.getString("book_city");
            String book_area = jsonObject1.getString("book_area");


            if ("".equals(book_region) || "null".equals(book_region)
                    || "".equals(book_address) || "null".equals(book_address)) {
                book_region = "无";
                book_address = "";
            }
            map.put("book_id",book_id);
            map.put("book_name",book_name);
            map.put("book_telephone",book_telephone);
            map.put("book_region",book_region);
            map.put("book_address",book_address);

            map.put("book_province",book_province);
            map.put("book_city",book_city);
            map.put("book_area",book_area);
            mList.add(map);
            map = null;
        }
        messageAdapter.notifyDataSetChanged();
    }

    private void initData() {

        Intent intent = getIntent();
        mType = intent.getIntExtra("type",1);

        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        dialogLoading = new HkDialogLoading(FastmailMessageActivity.this, "请稍后...");

        initList();    //初始化列表
    }

    private void initList() {
        mList = new ArrayList<>();
        messageAdapter = new FastmailMessageAdapter(mList);
        rlAddressList.setFocusable(false);
        rlAddressList.setNestedScrollingEnabled(false);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        rlAddressList.setLayoutManager(mLayoutManager);
        rlAddressList.setAdapter(messageAdapter);
        rlAddressList.setLoadingListener(this);
        initClickListener();
    }


    private void addAddressEvent() {
        Intent intent = new Intent(FastmailMessageActivity.this,ChangeMessageActivity.class);
        intent.putExtra("type",mType);
        startActivityForResult(intent,TYPE_ADD_ADDRESS);
    }

    private void changeAddress(int position) {
        HashMap<String,String> item = mList.get(position);
        Intent intent = new Intent(FastmailMessageActivity.this,ChangeMessageActivity.class);
        intent.putExtra("book_id",item.get("book_id"));
        intent.putExtra("book_name",item.get("book_name"));
        intent.putExtra("book_telephone",item.get("book_telephone"));
        intent.putExtra("book_region",item.get("book_region"));
        intent.putExtra("book_address",item.get("book_address"));
        intent.putExtra("book_province",item.get("book_province"));
        intent.putExtra("book_city",item.get("book_city"));
        intent.putExtra("book_area",item.get("book_area"));
        intent.putExtra("type",mType);
        startActivityForResult(intent,TYPE_CHANGE_ADDRESS);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRequestQueue.cancelAll();
        mRequestQueue.stop();
        mList = null;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                requestData();
                rlAddressList.refreshComplete();
            }
        }, 100);
    }

    @Override
    public void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                requestData();
                rlAddressList.loadMoreComplete();
            }
        }, 100);
    }
}
