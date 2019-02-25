package com.mt.bbdj.community.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.EventLog;
import android.view.View;
import android.widget.RelativeLayout;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.BindAccountModel;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.community.adapter.SimpleFragmentPagerAdapter;
import com.mt.bbdj.community.fragment.BindAliFragment;
import com.mt.bbdj.community.fragment.BindBankcardFragment;
import com.mt.bbdj.community.fragment.FinishHandleFragment;
import com.mt.bbdj.community.fragment.WaitCollectFragment;
import com.mt.bbdj.community.fragment.WaitMimeographFragment;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class BindAccountActivity extends BaseActivity implements OnTabSelectListener {

    private SlidingTabLayout sltTitle;
    private ViewPager viewPager;
    private RelativeLayout iv_back;

    private ArrayList<Fragment> list_fragment = new ArrayList<>();       //定义要装fragment的列表
    private ArrayList<String> list_title = new ArrayList<>();            //定义要装fragment的列表
    private SimpleFragmentPagerAdapter pagerAdapter;
    private UserBaseMessageDao mUserMessageDao;
    private RequestQueue mRequestQueue;

    private final int REQUEST_BIND_MESSAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_account);
        initView();
        requestAccountMessage();
    }

    private void requestAccountMessage() {
        String user_id = "";
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        Request<String> request = NoHttpRequest.checkisBindAccountRequest(user_id);
        mRequestQueue.add(REQUEST_BIND_MESSAGE, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "BindAccountActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.getString("code");
                    if ("4001".equals(code)) {
                        // 表示没有绑定任何账号
                        EventBus.getDefault().post(new TargetEvent(TargetEvent.BIND_ACCOUNT_NONE));
                    } else if ("5001".equals(code)) {
                        // 都已经绑定
                        handleBindAll(jsonObject);
                    } else if ("5002".equals(code)) {
                        // 银行卡绑定
                        handleBindBank(jsonObject);
                    } else if ("5003".equals(code)) {
                        // 支付宝绑定
                        handleBindAli(jsonObject);
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

    private void handleBindAli(JSONObject jsonObject) throws JSONException {
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject payObj = data.getJSONObject("pay");
        String realname = payObj.getString("realname");
        String account = payObj.getString("account");
        BindAccountModel bindAccountModel = new BindAccountModel(realname,account);
        EventBus.getDefault().post(new TargetEvent(TargetEvent.BIND_ALI_ACCOUNT,bindAccountModel));
    }

    private void handleBindBank(JSONObject jsonObject) throws JSONException {
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject cardObj = data.getJSONObject("card");
        String bankName = cardObj.getString("realname");
        String bankNumber = cardObj.getString("number");
        String bank = cardObj.getString("bank");
        BindAccountModel bindAccountModel = new BindAccountModel(bankName,bankNumber,bank);
        EventBus.getDefault().post(new TargetEvent(TargetEvent.BIND_BANK_ACCOUNT,bindAccountModel));
    }

    private void handleBindAll(JSONObject jsonObject) throws JSONException {
        JSONObject data = jsonObject.getJSONObject("data");
        JSONObject cardObj = data.getJSONObject("card");
        String bankName = cardObj.getString("realname");
        String bankNumber = cardObj.getString("number");
        String bank = cardObj.getString("bank");

        JSONObject payObj = data.getJSONObject("pay");
        String realname = payObj.getString("realname");
        String account = payObj.getString("account");
        BindAccountModel bindAccountModel = new BindAccountModel(bankName,bankNumber,bank,realname,account);
        EventBus.getDefault().post(new TargetEvent(TargetEvent.BIND_ACCOUNT_BUTTON,bindAccountModel));
    }

    private void initView() {
        initParams();
        sltTitle = findViewById(R.id.slt_title);
        viewPager = findViewById(R.id.viewpager);
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        initFragment();
    }

    private void initParams() {
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = daoSession.getUserBaseMessageDao();
        mRequestQueue = NoHttp.newRequestQueue();
    }

    private void initFragment() {
        list_fragment.clear();
        list_fragment.add(BindBankcardFragment.getInstance());    //绑定银行卡
        list_fragment.add(BindAliFragment.getInstance());    //绑定支付宝
        list_title.clear();
        list_title.add("绑定银行卡");
        list_title.add("绑定支付宝");
        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(),
                BindAccountActivity.this, list_fragment, list_title);
        viewPager.setAdapter(pagerAdapter);

        sltTitle.setViewPager(viewPager);
        sltTitle.setOnTabSelectListener(this);
    }

    @Override
    public void onTabSelect(int position) {

    }

    @Override
    public void onTabReselect(int position) {

    }
}
