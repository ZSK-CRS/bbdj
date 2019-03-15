package com.mt.bbdj.community.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseFragment;
import com.mt.bbdj.baseconfig.db.City;
import com.mt.bbdj.baseconfig.db.County;
import com.mt.bbdj.baseconfig.db.ExpressLogo;
import com.mt.bbdj.baseconfig.db.MingleArea;
import com.mt.bbdj.baseconfig.db.Province;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.CityDao;
import com.mt.bbdj.baseconfig.db.gen.CountyDao;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.db.gen.MingleAreaDao;
import com.mt.bbdj.baseconfig.db.gen.ProvinceDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyGridView;
import com.mt.bbdj.community.activity.ChangeManagerdActivity;
import com.mt.bbdj.community.activity.ClientManagerActivity;
import com.mt.bbdj.community.activity.CommunityActivity;
import com.mt.bbdj.community.activity.ComplainManagerdActivity;
import com.mt.bbdj.community.activity.EnterManagerActivity;
import com.mt.bbdj.community.activity.MatterShopActivity;
import com.mt.bbdj.community.activity.MessageAboutActivity;
import com.mt.bbdj.community.activity.MessageManagerdActivity;
import com.mt.bbdj.community.activity.MoneyFormatManagerActivity;
import com.mt.bbdj.community.activity.OutManagerActivity;
import com.mt.bbdj.community.activity.RepertoryActivity;
import com.mt.bbdj.community.activity.SearchPackageActivity;
import com.mt.bbdj.community.activity.SendManagerActivity;
import com.mt.bbdj.community.activity.SendResByHandActivity;
import com.mt.bbdj.community.activity.SystemMessageAboutActivity;
import com.mt.bbdj.community.adapter.MyGridViewAdapter;
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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Author : ZSK
 * Date : 2018/12/26
 * Description : 社区版首页
 */
public class ComFirstFragment extends BaseFragment {

    @BindView(R.id.gv_com_first)
    MyGridView mComGridView;
    @BindView(R.id.com_ll_message_about)
    LinearLayout messageAbout;    //短信信息
    @BindView(R.id.com_ll_pannel_about)
    LinearLayout pannelAbout;     //面单信息

    Unbinder unbinder;
    @BindView(R.id.tv_address)
    TextView tvAddress;      //地址
    @BindView(R.id.tv_time)
    TextView tvTime;         //时间
    @BindView(R.id.tv_receive_wait)
    TextView tvReceiveWait;    //待收件
    @BindView(R.id.tv_receive_handle)
    TextView tvReceiveHandle;  //待收件已处理
    @BindView(R.id.tv_sms_number)
    TextView tvSmsNumber;    //短信余额
    @BindView(R.id.tv_abnormal_wait)
    TextView tvAbnormalWait;   //入库数
    @BindView(R.id.tv_abnormal_handle)
    TextView tvAbnormalHandle;  //异常件已处理的消息
    @BindView(R.id.tv_pannel_number)
    TextView tvPannelNumber;     //面单余额
    @BindView(R.id.iv_message)
    ImageView ivMessage;     //消息
    View mView;
    @BindView(R.id.textview_serach)
    TextView tvSearch;     //搜索


    private List<HashMap<String, Object>> mList = new ArrayList<>();
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private RequestQueue mRequestQueue;
    private HkDialogLoading dialogLoading;
    private String express_id = "";   //快递公司id

    private ProvinceDao mProvinceDao;     //省
    private CityDao mCityDao;     //市
    private CountyDao mCountyDao;   //县
    private MingleAreaDao mMingleAreaDao;    //混合地区

    private final int REQUEST_UPLOAD_AREA = 2;    //下载省市区

    private final int REQUEST_UPLOAD_LOGO = 3;    //下载没有图片的logo

    private final int REQUEST_PANNEL_MESSAGE = 101;    //获取面板信息
    private String user_id;
    private ExpressLogoDao mExpressLogoDao;
    private List<ExpressLogo> mExpressLogoList;

    public static ComFirstFragment getInstance() {
        ComFirstFragment comFirstFragment = new ComFirstFragment();
        return comFirstFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.layout_com_first_fragment, container, false);
        unbinder = ButterKnife.bind(this, mView);
        EventBus.getDefault().register(this);
        initParams();
        initData();
        initView();
        initClick();
        requestAreaData();    //下载省市县
        updataExpressState();   //更新快递公司状态
        return mView;
    }

    private void initParams() {

        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        dialogLoading = new HkDialogLoading(getActivity(), "请稍后...");

        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();
        mProvinceDao = mDaoSession.getProvinceDao();
        mExpressLogoDao = mDaoSession.getExpressLogoDao();
        mCityDao = mDaoSession.getCityDao();
        mCountyDao = mDaoSession.getCountyDao();
        mMingleAreaDao = mDaoSession.getMingleAreaDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
    }

    private void updataExpressState() {
        mExpressLogoList = mExpressLogoDao.queryBuilder()
                .where(ExpressLogoDao.Properties.LogoLocalPath.eq(""))
                .where(ExpressLogoDao.Properties.States.eq(1)).list();
        if (mExpressLogoList == null || mExpressLogoList.size() == 0) {
            return ;
        }
        for (ExpressLogo expressLogo : mExpressLogoList) {
            String localPath = expressLogo.getLogoLocalPath();
            if ("".equals(localPath) || null == localPath) {
                express_id = expressLogo.getExpress_id();
                String type = expressLogo.getProperty();
                uploadLogoPicture(type);
            }
        }

    }

    private void uploadLogoPicture(String type) {
        Request<String> request = NoHttpRequest.updateExpressState(user_id, express_id,type);
        mRequestQueue.add(REQUEST_UPLOAD_LOGO, request, mResponseListener);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void receiveMessage(TargetEvent targetEvent) {
        //收到推送消息
        if (TargetEvent.COMMIT_FIRST_REFRESH == targetEvent.getTarget()) {
            //更新界面的信息
            requestPannelMessage();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        //更新界面的信息
        requestPannelMessage();
    }

    private void requestPannelMessage() {
        Request<String> request = NoHttpRequest.getPannelmessageRequest(user_id);
        mRequestQueue.add(REQUEST_PANNEL_MESSAGE, request, mResponseListener);
    }

    private void initData() {

        tvTime.setText(DateUtil.getCurrentTimeFormat("yyyy-MM-dd"));
    }

    private void requestAreaData() {
        uploadGenealData();    //下载省市区数据
    }

    private void uploadGenealData() {
        Request<String> request = NoHttpRequest.getAreaRequest(user_id, express_id);
        mRequestQueue.add(REQUEST_UPLOAD_AREA, request, mResponseListener);
    }

    private void initClick() {
        mComGridView.setOnItemClickListener(mGrideClickListener);
        messageAbout.setOnClickListener(mOnClickListenre);
        pannelAbout.setOnClickListener(mOnClickListenre);
    }


    private View.OnClickListener mOnClickListenre = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.com_ll_message_about:
                    handleMessageAbouitEvent();     //跳转短息相关的界面
                    break;
                case R.id.com_ll_pannel_about:
                    handlePannelAboutEvent();       //跳转短信相关的界面
                    break;
            }
        }
    };

    private void handlePannelAboutEvent() {
        MessageAboutActivity.startAction(getActivity(), 2);
    }

    private void handleMessageAbouitEvent() {
        MessageAboutActivity.startAction(getActivity(), 1);
    }


    //功能列表的点击事件
    private AdapterView.OnItemClickListener mGrideClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            HashMap<String, Object> item = mList.get(position);
            //处理点击实事件
            handleItemClick(item);
        }
    };

    private void handleItemClick(HashMap<String, Object> item) {
        String id = item.get("id").toString();
        switch (id) {
            case "0":       //寄件管理
                handleSendManagerEvent();
                break;
            case "1":       //手动寄件
                handleSendByhandEvent();
                break;
            case "2":       //物流查询
                handleSearchPackageEvent();
                break;
            case "3":       //交接管理
                handleChangeManagerEvent();
                break;
            case "4":       //入库管理
                handleEnterManagerEvent();
                break;
            case "5":       //出库管理
                handleOutManagerEvent();
                break;
            case "6":       //财务管理
                handleMoneyManagerEvent();
                break;
            case "7":       //客户管理
                handleClientManagerEvent();
                break;
            case "8":       //短信管理
                handleMessageEvent();
                break;
            case "9":       //物料商城
                handleShopEvent();
                break;
            case "10":      //投诉
                handleComplainEvent();
                break;
            case "11":      //操作手册
                break;
        }

    }

    private void handleOutManagerEvent() {
        Intent intent = new Intent(getActivity(),OutManagerActivity.class);
        startActivity(intent);
    }

    private void handleEnterManagerEvent() {
        Intent intent = new Intent(getActivity(),EnterManagerActivity.class);
        startActivity(intent);
    }

    private void handleMoneyManagerEvent() {
        Intent intent = new Intent(getActivity(),MoneyFormatManagerActivity.class);
        startActivity(intent);
    }

    private void handleChangeManagerEvent() {
        Intent intent = new Intent(getActivity(),ChangeManagerdActivity.class);
        startActivity(intent);
    }

    private void handleClientManagerEvent() {
        Intent intent = new Intent(getActivity(), ClientManagerActivity.class);
        startActivity(intent);
    }

    private void handleSearchPackageEvent() {
        Intent intent = new Intent(getActivity(), SearchPackageActivity.class);
        startActivity(intent);
    }

    private void handleComplainEvent() {
        Intent intent = new Intent(getActivity(), ComplainManagerdActivity.class);
        startActivity(intent);
    }

    private void handleMessageEvent() {
        Intent intent = new Intent(getActivity(), MessageManagerdActivity.class);
        startActivity(intent);
    }

    private void handleSendManagerEvent() {
        Intent intent = new Intent(getActivity(), SendManagerActivity.class);
        startActivity(intent);
    }

    private void handleSendByhandEvent() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), SendResByHandActivity.class);
        startActivity(intent);
    }

    //跳转物料商城界面
    private void handleShopEvent() {
        Intent intent = new Intent(getActivity(), MatterShopActivity.class);
        startActivity(intent);
    }

    private void initView() {
        for (int i = 0; i < 12; i++) {
            HashMap<String, Object> item = new HashMap<>();
            if (i == 0) {
                item.put("id", "0");
                item.put("name", "寄件管理");
                item.put("ic", R.drawable.ic_main_jijianguanli);
            }
                
            if (i == 1) {
                item.put("id", "1");
                item.put("name", "手动寄件");
                item.put("ic", R.drawable.ic_main_shoudongjijian);
            }

            if (i == 2) {
                item.put("id", "2");
                item.put("name", "物流查询");
                item.put("ic", R.drawable.ic_main_wuliuchaxun);
            }

            if (i == 3) {
                item.put("id", "3");
                item.put("name", "交接管理");
                item.put("ic", R.drawable.ic_jiaojieguanli);
            }
            if (i == 4) {
                item.put("id", "4");
                item.put("name", "入库管理");
                item.put("ic", R.drawable.ic_main_ruku);
            }
            if (i == 5) {
                item.put("id", "5");
                item.put("name", "出库管理");
                item.put("ic", R.drawable.ic_main_chuku);
            }
            if (i == 6) {
                item.put("id", "6");
                item.put("name", "财务管理");
                item.put("ic", R.drawable.ic_money_manager);
            }
            if (i == 7) {
                item.put("id", "7");
                item.put("name", "客户管理");
                item.put("ic", R.drawable.ic_main_kehuguanli);
            }
            if (i == 8) {
                item.put("id", "8");
                item.put("name", "短信管理");
                item.put("ic", R.drawable.ic_main_duanxin);
            }
            if (i == 9) {
                item.put("id", "9");
                item.put("name", "物料商城");
                item.put("ic", R.drawable.ic_main_wuliao);
            }
            if (i == 10) {
                item.put("id", "10");
                item.put("name", "投诉管理");
                item.put("ic", R.drawable.ic_main_tousu);
            }
            if (i == 11) {
                item.put("id", "11");
                item.put("name", "操作手册");
                item.put("ic", R.drawable.ic_main_caozuo);
            }

            mList.add(item);
        }
        MyGridViewAdapter myGridViewAdapter = new MyGridViewAdapter(mList);
        mComGridView.setAdapter(myGridViewAdapter);
    }

    private OnResponseListener<String> mResponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            //   dialogLoading.show();
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "ComFirstFragment::" + response.get());
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
        public void onFailed(int what, Response<String> response) {
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
            case REQUEST_UPLOAD_AREA:    //下载省市县
                handleUploadArea(jsonObject);
                break;
            case REQUEST_PANNEL_MESSAGE:      //更新主界面的信息
                chnagePannelMessage(jsonObject);
                break;
            case REQUEST_UPLOAD_LOGO:    //更新logo图片
                break;
        }
    }

    private void chnagePannelMessage(JSONObject jsonObject) throws JSONException {
        JSONObject dataObj = jsonObject.getJSONObject("data");
        String mail_stay = dataObj.getString("mail_stay");    //待收件未处理
        String mail_processed = dataObj.getString("mail_processed");  //待收件已处理
        String abnormal_stay = dataObj.getString("abnormal_stay");   //异常件未处理
        String abnormal_processed = dataObj.getString("abnormal_processed");  //异常件已处理
        String sms_number = dataObj.getString("sms_number");   //短信余额
        String face_number = dataObj.getString("face_number");   //面单余额
        String username = dataObj.getString("username");   //位置
        String money = dataObj.getString("money");   //账户余额
        String birthday = dataObj.getString("birthday");   //入驻天数
        String version_number = dataObj.getString("version_number");   //版本号
        String version_url = dataObj.getString("version_url");   //版本地址
        String unread_url = dataObj.getString("unread_url");   //未读消息
        tvReceiveWait.setText(StringUtil.handleNullResultForNumber(mail_stay));
        tvReceiveHandle.setText("已处理 " + StringUtil.handleNullResultForNumber(mail_processed));
        tvAbnormalWait.setText(StringUtil.handleNullResultForNumber(abnormal_stay));
        tvAbnormalHandle.setText("入库数 " + StringUtil.handleNullResultForNumber(abnormal_processed));
        tvSmsNumber.setText("短信余额：" + StringUtil.handleNullResultForNumber(sms_number));
        tvPannelNumber.setText("面单余额：" + StringUtil.handleNullResultForNumber(face_number));
        tvAddress.setText(StringUtil.handleNullResultForString(username));
        SharedPreferences.Editor editor = SharedPreferencesUtil.getEditor();
        editor.putString("money", money);
        editor.putString("birthday", birthday);
        editor.putString("address", username);
        editor.commit();
    }


    private void handleUploadArea(JSONObject jsonObject) throws JSONException {    //下载省市县
        mProvinceDao.deleteAll();
        mCountyDao.deleteAll();
        mCityDao.deleteAll();
        mMingleAreaDao.deleteAll();
        JSONArray jsonArray = jsonObject.getJSONArray("data");
        List<MingleArea> mingleAreaList = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonProvince = jsonArray.getJSONObject(i);
            String provinceId = jsonProvince.getString("id");
            String region_name = jsonProvince.getString("region_name");
            String parent_id = jsonProvince.getString("parent_id");
            String region_code = jsonProvince.getString("region_code");

            MingleArea mingleArea = new MingleArea(provinceId, region_name, parent_id, region_code);
            mingleAreaList.add(mingleArea);
        }
        mMingleAreaDao.saveInTx(mingleAreaList);

        //分离不同的地区
        new Thread(new Runnable() {
            @Override
            public void run() {
                spliteDifferenceArea();
            }
        }).start();
    }

    private void spliteDifferenceArea() {
        List<Province> provinceList = new ArrayList<>();    //省
        List<City> cityList = new ArrayList<>();   //市
        List<County> countyList = new ArrayList<>();   //县

        List<MingleArea> mingleAreaList = mMingleAreaDao.queryBuilder().list();
        //查找省
        Iterator<MingleArea> mingleAreaIterator = mingleAreaList.iterator();
        while (mingleAreaIterator.hasNext()) {
            MingleArea mingleArea = mingleAreaIterator.next();
            if ("0".equals(mingleArea.getParent_id())) {
                Province province = new Province(mingleArea.getId(), mingleArea.getRegion_name(), mingleArea.getParent_id(), mingleArea.getRegion_code());
                provinceList.add(province);
                province = null;
                mingleAreaIterator.remove();
            }
        }

        //查找市
        for (int i = 0; i < provinceList.size(); i++) {
            Province province = provinceList.get(i);
            for (int j = 0; j < mingleAreaList.size(); j++) {
                MingleArea mingleArea = mingleAreaList.get(j);
                if (province.getId().equals(mingleArea.getParent_id())) {
                    City city = new City(mingleArea.getId(), mingleArea.getRegion_name(), mingleArea.getParent_id(), mingleArea.getRegion_code());
                    cityList.add(city);
                    mingleAreaList.remove(j);
                    city = null;
                }
            }
        }

        //县
        for (MingleArea mingleArea : mingleAreaList) {
            County county = new County(mingleArea.getId(), mingleArea.getRegion_name(), mingleArea.getParent_id(), mingleArea.getRegion_code());
            countyList.add(county);
            county = null;
        }
        mProvinceDao.saveInTx(provinceList);
        mCountyDao.saveInTx(countyList);
        mCityDao.saveInTx(cityList);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_address, R.id.tv_time, R.id.tv_receive_wait, R.id.tv_receive_handle,
            R.id.iv_message,R.id.tv_abnormal_wait,R.id.textview_serach})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_address:
                break;
            case R.id.tv_time:
                break;
            case R.id.tv_receive_wait:       //待收件
                actionToWaitPannel();
                break;
            case R.id.tv_receive_handle:     //已处理
                actionToFinishPannel();
                break;
            case R.id.iv_message:
                actionToMessagePannel();      //跳转到消息界面
                break;
            case R.id.tv_abnormal_wait:     //仓库
                actionToRepertoryPannel();
                break;
            case R.id.textview_serach:
                actionToSearchPannel();    //搜索
                break;
        }
    }

    private void actionToSearchPannel() {

    }

    private void actionToRepertoryPannel() {
        Intent intent = new Intent(getActivity(), RepertoryActivity.class);
        startActivity(intent);
    }

    private void actionToMessagePannel() {
        Intent intent = new Intent(getActivity(), SystemMessageAboutActivity.class);
        startActivity(intent);
    }

    private void actionToFinishPannel() {
        Intent intent = new Intent(getActivity(), SendManagerActivity.class);
        intent.putExtra("currentItem", 1);
        startActivity(intent);
    }

    private void actionToWaitPannel() {
        Intent intent = new Intent(getActivity(), SendManagerActivity.class);
        intent.putExtra("currentItem", 0);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
