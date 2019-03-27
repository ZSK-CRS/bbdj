package com.mt.bbdj.community.activity;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.City;
import com.mt.bbdj.baseconfig.db.County;
import com.mt.bbdj.baseconfig.db.MingleArea;
import com.mt.bbdj.baseconfig.db.Province;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.CityDao;
import com.mt.bbdj.baseconfig.db.gen.CountyDao;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.ExpressLogo;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.db.gen.ProvinceDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.internet.down.DownLoadPictureService;
import com.mt.bbdj.baseconfig.internet.down.ImageDownLoadCallBack;
import com.mt.bbdj.baseconfig.model.AddressBean;
import com.mt.bbdj.baseconfig.model.Area;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

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
    private ProvinceDao mProvinceDao;     //省
    private CityDao mCityDao;     //市
    private CountyDao mCountyDao;   //县

    private String user_id = "";     //用户id
    private String express_id = "";    //快递公司id
    private ExpressLogoDao mExpressLogoDao;
    private Thread upLoadThread;

    private ExecutorService executorService;   //用于下载图片的线程池


    // 定义一个变量，来标识是否退出
    private static boolean isExit = false;


    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_community);

        ButterKnife.bind(this);
        initView();
        initParams();
        loadAddressData();
        //下载快递logo
        upLoadexpressLogo();
    }

    private void loadAddressData() {

        mProvinceDao.deleteAll();
        mCountyDao.deleteAll();
        mCityDao.deleteAll();

        String json = StringUtil.getJson(this, "adress.json");
        AddressBean addressBean = com.alibaba.fastjson.JSONObject.parseObject(json, AddressBean.class);
        List<com.mt.bbdj.baseconfig.model.Province> provinceList = addressBean.getProvince();
        List<com.mt.bbdj.baseconfig.model.City> cityList = addressBean.getCity();
        List<Area> areaList = addressBean.getArea();

        List<Province> db_Province = new ArrayList<>();
        List<City> db_City = new ArrayList<>();
        List<County> db_County = new ArrayList<>();

        for (int i = 0;i<provinceList.size();i++) {
            com.mt.bbdj.baseconfig.model.Province entity = provinceList.get(i);
            Province province = new Province(entity.getId(),entity.getRegion_name(),entity.getParent_id(),entity.getRegion_code());
            db_Province.add(province);
            province = null;
        }

        for (int i = 0;i<cityList.size();i++) {
            com.mt.bbdj.baseconfig.model.City entity = cityList.get(i);
            City city = new City(entity.getId(),entity.getRegion_name(),entity.getParent_id(),entity.getRegion_code());
            db_City.add(city);
            city = null;
        }

        for (int i = 0;i<areaList.size();i++) {
            Area entity = areaList.get(i);
            County county = new County(entity.getId(),entity.getRegion_name(),entity.getParent_id(),entity.getRegion_code());
            db_County.add(county);
            county = null;
        }


        mProvinceDao.saveInTx(db_Province);
        mCountyDao.saveInTx(db_County);
        mCityDao.saveInTx(db_City);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exit() {
        if (!isExit) {
            isExit = true;
            Toast.makeText(getApplicationContext(), "再按一次退出程序",
                    Toast.LENGTH_SHORT).show();
            // 利用handler延迟发送更改状态信息
            mHandler.sendEmptyMessageDelayed(0, 500);
        } else {
            finish();
            System.exit(0);
        }
    }

    private void initParams() {
        DaoSession mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();
        mExpressLogoDao = mDaoSession.getExpressLogoDao();
        mProvinceDao = mDaoSession.getProvinceDao();
        mCityDao = mDaoSession.getCityDao();
        mCountyDao = mDaoSession.getCountyDao();
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
            upLogo(expressLogo.getExpress_id(), expressLogo.getLogoInterPath());
        }
    }


    private void upLogo(final String express_id, String logoInterPath) {
        if (logoInterPath == null || "".equals(logoInterPath)) {
            return;
        }
        String uuid = UUID.randomUUID().toString();
        String path2 = uuid + ".jpg";
        File logo = new File(f, path2);

        DownLoadPictureService downLoadPictureService = new DownLoadPictureService(logo.getPath(),
                logoInterPath, express_id, new ImageDownLoadCallBack() {
            @Override
            public void onDownLoadSuccess(String tag, String localPath) {
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
            return;
        }
        List<ExpressLogo> expressLogoList = mExpressLogoDao.queryBuilder()
                .where(ExpressLogoDao.Properties.Express_id.eq(express_id)).list();
        if (expressLogoList == null || expressLogoList.size() == 0) {
            return;
        }

        for (ExpressLogo expressLogo : expressLogoList) {
            expressLogo.setLogoLocalPath(filePath);
            expressLogo.setId(expressLogo.getId());
            mExpressLogoDao.update(expressLogo);
        }
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

        JSONArray dataArray = jsonObject.getJSONArray("data");
        mExpressLogoDao.deleteAll();
        for (int i = 0; i < dataArray.length(); i++) {
            JSONObject data = dataArray.getJSONObject(i);
            String express_id = data.getString("express_id");
            String express_logo = data.getString("express_logo");
            String express_name = data.getString("express_name");
            String states = data.getString("states");
            String flag = data.getString("flag");
            String category = data.getString("category");

            ExpressLogo expressLogo = new ExpressLogo();
            expressLogo.setExpress_id(express_id);
            expressLogo.setFlag(flag);
            expressLogo.setStates(states);
            expressLogo.setLogoInterPath(express_logo);
            expressLogo.setExpress_name(express_name);
            expressLogo.setLogoLocalPath("");
            expressLogo.setProperty(category);
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
        initExpressLogo();
        //表示软件第一次运行，直接下载图标
      /*  if (isUpdate) {
            //初始化快递公司logo
            initExpressLogo();
            editor.putBoolean("update", false);
            editor.commit();
        } else {
            updateLogo();
        }*/
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
                ToastUtil.showShort("该功能暂未开放！");
               // selectFragmentData();     //选中数据界面
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
       /* YCAppBar.setStatusBarColor(CommunityActivity.this,
                ContextCompat.getColor(CommunityActivity.this,
                        R.color.mainColor));*/
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
      /*  YCAppBar.setStatusBarColor(CommunityActivity.this,
                ContextCompat.getColor(CommunityActivity.this,
                        R.color.mainColor));*/
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
       /* YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(CommunityActivity.this);*/
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
       /* YCAppBar.setStatusBarColor(CommunityActivity.this,
                ContextCompat.getColor(CommunityActivity.this,
                        R.color.mainColor));*/
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

       // StatusBarUtils.StatusBarLightMode(CommunityActivity.this);
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
