package com.mt.bbdj.community.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.ExpressLogo;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.internet.down.DownLoadPictureService;
import com.mt.bbdj.baseconfig.internet.down.ImageDownLoadCallBack;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.fragment.ComDataFragment;
import com.mt.bbdj.community.fragment.ComFirstFragment;
import com.mt.bbdj.community.fragment.ComMymessageFragment;
import com.mt.bbdj.community.fragment.ComOrderFragment;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CommunityActivity extends BaseActivity {

    @BindView(R.id.main_fl_parent)
    FrameLayout mainFlParent;
    @BindView(R.id.main_tab_imgbt_first)
    ImageButton imgbtn_first;
    @BindView(R.id.main_tab_tv_first)
    TextView mtv_first;
    @BindView(R.id.main_tab_ll_first)
    LinearLayout mainTabLlFirst;
    @BindView(R.id.main_tab_imgbt_order)
    ImageButton imgbtn_order;
    @BindView(R.id.main_tab_tv_order)
    TextView mtv_order;
    @BindView(R.id.main_tab_ll_order)
    LinearLayout mainTabLlOrder;
    @BindView(R.id.main_tab_imgbt_data)
    ImageButton imgbtn_data;
    @BindView(R.id.main_tab_tv_data)
    TextView mtv_data;
    @BindView(R.id.main_tab_ll_data)
    LinearLayout mainTabLlData;
    @BindView(R.id.main_tab_imgbt_my)
    ImageButton imgbtn_my;
    @BindView(R.id.main_tab_tv_my)
    TextView mtv_my;
    @BindView(R.id.main_tab_ll_my)
    LinearLayout mainTabLlMy;

    private final int REQUEST_UPLOAD_LOGO = 100;    //获取图标的信息
    private final int REQUEST_UPLOAD_PICTURE = 200;   //下载logo

    private String picturePath = "/bbdj/logo";
    private File f = new File(Environment.getExternalStorageDirectory(), picturePath);

    private ComFirstFragment mComFirstFragment;     //社区版首页
    private ComOrderFragment mComOrderFragment;     //社区版订单
    private ComDataFragment mComDataFragment;     //社区版数据
    private ComMymessageFragment mComMymessageFragment;   //社区版我的
    private List<ExpressLogo> mExpressLogoList;

    private UserBaseMessageDao mUserMessageDao;
    private RequestQueue mRequestQueue;

    private String user_id = "";     //用户id
    private String express_id = "";    //快递公司id
    private ExpressLogoDao mExpressLogoDao;
    private Thread upLoadThread;

    private ExecutorService executorService;   //用于下载图片的线程池

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);
        ButterKnife.bind(this);
        initView();
        initParams();
        //下载快递logo
        upLoadexpressLogo();
    }

    private void initParams() {
        DaoSession mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();
        mExpressLogoDao = mDaoSession.getExpressLogoDao();
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }

        executorService = Executors.newCachedThreadPool();
    }

    private void initExpressLogo() {
        Request<String> request = NoHttpRequest.getExpressLogoRequest(user_id);
        mRequestQueue.add(REQUEST_UPLOAD_LOGO, request, mResponseListener);
    }

    private void updateLogo() {
        mExpressLogoList = mExpressLogoDao.queryBuilder()
                .where(ExpressLogoDao.Properties.LogoLocalPath.eq(""))
                .where(ExpressLogoDao.Properties.States.eq(1)).list();
        if (mExpressLogoList.size() == 0) {
            return;
        }

        for (ExpressLogo expressLogo : mExpressLogoList) {
            //下载logo
            upLogo(expressLogo.getExpress_id(),expressLogo.getLogoInterPath());
        }
    }



    private void upLogo(final String express_id, String logoInterPath) {
        if (logoInterPath == null || "".equals(logoInterPath)) {
            return ;
        }
        String uuid = UUID.randomUUID().toString();
        String path2 = uuid + ".jpg";
        File logo = new File(f, path2);

        DownLoadPictureService downLoadPictureService = new DownLoadPictureService(logo.getPath(),
                logoInterPath, express_id, new ImageDownLoadCallBack() {
            @Override
            public void onDownLoadSuccess(String tag,String localPath) {
                //更新本地图片地址和状态
                updateLogoAfterLoad(tag, localPath);
            }

            @Override
            public void onDownLoadFailed() {

            }
        });
        executorService.execute(downLoadPictureService);
    }

    private void updateLogoAfterLoad(String express_id, String filePath) {
        if ("".equals(filePath)) {
            return ;
        }
        List<ExpressLogo> expressLogoList = mExpressLogoDao.queryBuilder()
                .where(ExpressLogoDao.Properties.Express_id.eq(express_id)).list();
        if (expressLogoList == null ||expressLogoList.size() == 0) {
            return ;
        }
        ExpressLogo expressLogo = expressLogoList.get(0);
        expressLogo.setLogoLocalPath(filePath);
        expressLogo.setId(expressLogo.getId());
        mExpressLogoDao.update(expressLogo);
    }

    private void initView() {

        if (!f.exists()) {
            f.mkdirs();
        }

        //默认选中首页
        selectFragmentFirst();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(TargetEvent event) {
        if (111 == event.getTarget()) {
            finish();
        }
    }

    private OnResponseListener<String> mResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "CommunityActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                String msg = jsonObject.get("msg").toString();
                if ("5001".equals(code)) {
                    handleResult(what, jsonObject);
                } else {
                    ToastUtil.showShort(msg);
                }
            } catch (JSONException e) {
                e.printStackTrace();

            }
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            ToastUtil.showShort("连接服务器失败！");
        }

        @Override
        public void onFinish(int what) {

        }
    };

    private void handleResult(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case REQUEST_UPLOAD_LOGO:
                setExpressLogoMessage(jsonObject);
                break;
        }
    }

    private void setExpressLogoMessage(JSONObject jsonObject) throws JSONException {
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        mExpressLogoDao.deleteAll();
        List<ExpressLogo> expressLogos =  mExpressLogoDao.queryBuilder().list();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
            ExpressLogo expressLogo = new ExpressLogo();
            expressLogo.setExpress_id(jsonObject1.getString("express_id"));
            expressLogo.setFlag(jsonObject1.getString("flag"));
            expressLogo.setStates(jsonObject1.getString("states"));
            expressLogo.setLogoInterPath(jsonObject1.getString("express_logo"));
            expressLogo.setExpress_name(jsonObject1.getString("express_name"));
            expressLogo.setLogoLocalPath("");
            mExpressLogoDao.save(expressLogo);
        }
        //保存logo信息
        updateLogo();
    }

    private void upLoadexpressLogo() {
        //在滑动界面SlideActivity做分别
        SharedPreferences sharedPreferences = SharedPreferencesUtil.getSharedPreference();
        SharedPreferences.Editor editor = SharedPreferencesUtil.getEditor();
        boolean isUpdate = sharedPreferences.getBoolean("update", false);
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        //表示软件第一次运行，直接下载图标
        if (isUpdate) {
            //初始化快递公司logo
            initExpressLogo();
            editor.putBoolean("update", false);
            editor.commit();
        } else {
            updateLogo();
        }
    }

    @OnClick({R.id.main_tab_imgbt_first, R.id.main_tab_tv_first, R.id.main_tab_ll_first,
            R.id.main_tab_imgbt_order, R.id.main_tab_tv_order, R.id.main_tab_ll_order,
            R.id.main_tab_imgbt_data, R.id.main_tab_tv_data, R.id.main_tab_ll_data,
            R.id.main_tab_imgbt_my, R.id.main_tab_tv_my, R.id.main_tab_ll_my})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.main_tab_imgbt_first:
            case R.id.main_tab_tv_first:
            case R.id.main_tab_ll_first:
                selectFragmentFirst();    //选中首页界面
                break;
            case R.id.main_tab_imgbt_order:
            case R.id.main_tab_tv_order:
            case R.id.main_tab_ll_order:
                selectFragmentOrder();    //选中订单界面
                break;
            case R.id.main_tab_imgbt_data:
            case R.id.main_tab_tv_data:
            case R.id.main_tab_ll_data:
                selectFragmentData();     //选中数据界面
                break;
            case R.id.main_tab_imgbt_my:
            case R.id.main_tab_tv_my:
            case R.id.main_tab_ll_my:
                selectFragmentMy();        //选中我的界面
                break;
        }
    }

    private void selectFragmentMy() {
        resetSelectState();
        mtv_my.setSelected(true);
        imgbtn_my.setSelected(true);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mComMymessageFragment == null) {
            mComMymessageFragment = ComMymessageFragment.getInstance();
            transaction.add(R.id.main_fl_parent, mComMymessageFragment);
        }
        hideFragment(transaction);
        transaction.show(mComMymessageFragment);
        transaction.commit();
    }

    private void selectFragmentData() {
        resetSelectState();
        mtv_data.setSelected(true);
        imgbtn_data.setSelected(true);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mComDataFragment == null) {
            mComDataFragment = ComDataFragment.getInstance();
            transaction.add(R.id.main_fl_parent, mComDataFragment);
        }
        hideFragment(transaction);
        transaction.show(mComDataFragment);
        transaction.commit();
    }

    private void selectFragmentOrder() {
        resetSelectState();
        mtv_order.setSelected(true);
        imgbtn_order.setSelected(true);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mComOrderFragment == null) {
            mComOrderFragment = ComOrderFragment.getInstance();
            transaction.add(R.id.main_fl_parent, mComOrderFragment);
        }
        hideFragment(transaction);
        transaction.show(mComOrderFragment);
        transaction.commit();
    }

    private void selectFragmentFirst() {
        //重置状态
        resetSelectState();
        mtv_first.setSelected(true);
        imgbtn_first.setSelected(true);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mComFirstFragment == null) {
            mComFirstFragment = ComFirstFragment.getInstance();
            transaction.add(R.id.main_fl_parent, mComFirstFragment);
        }
        //隐藏所有的界面
        hideFragment(transaction);
        //显示需要的界面
        transaction.show(mComFirstFragment);
        transaction.commit();
    }

    private void resetSelectState() {
        mtv_data.setSelected(false);
        mtv_first.setSelected(false);
        mtv_my.setSelected(false);
        mtv_order.setSelected(false);
        imgbtn_data.setSelected(false);
        imgbtn_first.setSelected(false);
        imgbtn_order.setSelected(false);
        imgbtn_my.setSelected(false);
    }

    //隐藏Fragment
    private void hideFragment(FragmentTransaction transaction) {
        if (mComFirstFragment != null) {
            transaction.hide(mComFirstFragment);
        }
        if (mComDataFragment != null) {
            transaction.hide(mComDataFragment);
        }
        if (mComOrderFragment != null) {
            transaction.hide(mComOrderFragment);
        }
        if (mComMymessageFragment != null) {
            transaction.hide(mComMymessageFragment);
        }
    }
}
