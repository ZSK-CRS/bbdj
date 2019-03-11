package com.mt.bbdj.community.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.ExpressageEvent;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
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

import java.util.HashMap;
import java.util.List;

public class SearchPackageActivity extends BaseActivity {

    private EditText etDanhao;
    private TextView tvExpress;
    private Button btSearch;
    private LinearLayout llSelectExpress;
    private RelativeLayout rlSelectPiture;
    private RelativeLayout iv_back;

    private String expressId = "";    //快递公司id

    private final int SELECT_EXPRESS = 100;    //选择快递公司
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String user_id;
    private RequestQueue mRequestQueue;

    private int REQUEST_SEARCH_PACKAGE = 100;
    private WaitDialog waitDialog;
    private String expressName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_package);
        EventBus.getDefault().register(this);
        initParams();
        initView();
        initListener();
    }

    private void initParams() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receMessage(ExpressageEvent expressageEvent) {
        if (expressageEvent != null) {
            tvExpress.setText(expressageEvent.getExpress_name());
            expressId = expressageEvent.getExpress_id();
        }
    }

    private void initListener() {
        llSelectExpress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SearchPackageActivity.this,ExpressageListActivity.class);
                startActivity(intent);
            }
        });

        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //查询物流轨迹
                searchPackageMessage();
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void searchPackageMessage() {
        String yundanhao = etDanhao.getText().toString();
        expressName = tvExpress.getText().toString();
        if ("".equals(yundanhao)) {
            ToastUtil.showShort("请输入运单号");
            return ;
        }
        if ("".equals(expressName)) {
            ToastUtil.showShort("请选择快递公司");
            return;
        }

        Request<String> request = NoHttpRequest.getSearchPackRequest(user_id,yundanhao,expressId);
        mRequestQueue.add(REQUEST_SEARCH_PACKAGE, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                waitDialog = WaitDialog.show(SearchPackageActivity.this,"搜索中...");
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "SearchPackageActivity::" + response.get());
                try {
                    String result = response.get();
                    JSONObject jsonObject = new JSONObject(result);
                    String code = jsonObject.get("code").toString();
                    if ("5001".equals(code)) {
                        Intent intent = new Intent(SearchPackageActivity.this,ShowPackageMessageActivity.class);
                        intent.putExtra("express_id",expressId);
                        intent.putExtra("express",expressName);
                        intent.putExtra("result",result);
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    waitDialog.doDismiss();
                }
                waitDialog.doDismiss();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                waitDialog.doDismiss();
            }

            @Override
            public void onFinish(int what) {
                waitDialog.doDismiss();
            }
        });
    }

    private void initView() {
        etDanhao = findViewById(R.id.et_yundan);
        tvExpress = findViewById(R.id.et_kuiadi);
        btSearch = findViewById(R.id.tv_search);
        iv_back = findViewById(R.id.iv_back);
        llSelectExpress = findViewById(R.id.ll_select_express);
        tvExpress.setCursorVisible(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
