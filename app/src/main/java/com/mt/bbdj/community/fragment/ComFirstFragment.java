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
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.application.MyApplication;
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
import com.mt.bbdj.baseconfig.internet.InterApi;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.AddressBean;
import com.mt.bbdj.baseconfig.model.TargetEvent;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.DialogUtil;
import com.mt.bbdj.baseconfig.utls.DownloadUtil;
import com.mt.bbdj.baseconfig.utls.FileUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.SystemUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.HorizontalProgressBar;
import com.mt.bbdj.baseconfig.view.MyGridView;
import com.mt.bbdj.community.activity.ChangeManagerdActivity;
import com.mt.bbdj.community.activity.ClientManagerActivity;
import com.mt.bbdj.community.activity.CommunityActivity;
import com.mt.bbdj.community.activity.ComplainManagerdActivity;
import com.mt.bbdj.community.activity.EnterManagerActivity;
import com.mt.bbdj.community.activity.GlobalSearchActivity;
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

import java.io.File;
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

    @BindView(R.id.tv_test_tag)
    TextView tvTestTag;

    private String APP_PATH_ROOT = FileUtil.getRootPath(MyApplication.getInstance()).getAbsolutePath() + File.separator + "bbdj";


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
    private String version_url;
    private ProgressDialog mProgressBar;

    final String fileName = "bbdj.apk";
    private boolean isGetData = false;
    private HorizontalProgressBar progressBar;

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

        if (InterApi.SERVER_ADDRESS.contains("www.81dja.com")) {
            tvTestTag.setText("待收件");
        } else {
            tvTestTag.setText("待收件(测试)");
        }
    }

    private void updataExpressState() {
        mExpressLogoList = mExpressLogoDao.queryBuilder()
                .where(ExpressLogoDao.Properties.LogoLocalPath.eq(""))
                .where(ExpressLogoDao.Properties.States.eq(1)).list();
        if (mExpressLogoList == null || mExpressLogoList.size() == 0) {
            return;
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
        Request<String> request = NoHttpRequest.updateExpressState(user_id, express_id, type);
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
        //  uploadGenealData();    //下载省市区数据

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
                ToastUtil.showShort("暂不开放！");
                //  handleEnterManagerEvent();
                break;
            case "5":       //出库管理
                ToastUtil.showShort("暂不开放！");
                // handleOutManagerEvent();
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
                //ToastUtil.showShort("该功能暂未开放！");
                showDownLoadDialog("");
                break;
        }

    }

    private void handleOutManagerEvent() {
        Intent intent = new Intent(getActivity(), OutManagerActivity.class);
        startActivity(intent);
    }

    private void handleEnterManagerEvent() {
        Intent intent = new Intent(getActivity(), EnterManagerActivity.class);
        startActivity(intent);
    }

    private void handleMoneyManagerEvent() {
        Intent intent = new Intent(getActivity(), MoneyFormatManagerActivity.class);
        startActivity(intent);
    }

    private void handleChangeManagerEvent() {
        Intent intent = new Intent(getActivity(), ChangeManagerdActivity.class);
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
        //版本地址
        version_url = dataObj.getString("version_url");
        String unread_url = dataObj.getString("unread_url");   //未读消息

        tvReceiveWait.setText(StringUtil.handleNullResultForNumber(mail_stay));
        tvReceiveHandle.setText("已处理 " + StringUtil.handleNullResultForNumber(mail_processed));
        tvAbnormalWait.setText(StringUtil.handleNullResultForNumber(abnormal_stay));
        tvAbnormalHandle.setText("出库数 " + StringUtil.handleNullResultForNumber(abnormal_processed));
        tvSmsNumber.setText("短信余额：" + StringUtil.handleNullResultForNumber(sms_number));
        tvPannelNumber.setText("面单余额：" + StringUtil.handleNullResultForNumber(face_number));
        tvAddress.setText(StringUtil.handleNullResultForString(username));
        SharedPreferences.Editor editor = SharedPreferencesUtil.getEditor();
        editor.putString("money", money);
        editor.putString("birthday", birthday);
        editor.putString("address", username);
        editor.commit();

        upLoadNewVersion(version_number, version_url);    //更新最新版本
    }

    private void upLoadNewVersion(String version_number, String version_url) {
        String version = SystemUtil.getVersion(getActivity());
        if (!version.equals(version_number)) {
            showDownLoadDialog(version_url);
        }
    }

    private void showDownLoadDialog(String version_url) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.layout_update_version,null);
        TextView updataNow = view.findViewById(R.id.id_update_now);
        updataNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                download();
            }
        });
        dialog.show();
        DisplayMetrics dm = new DisplayMetrics();   //获取屏幕的大小
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);//获取WindowManager
        windowManager.getDefaultDisplay().getMetrics(dm);   //是获取到Activity的实际屏幕信息
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (dm.widthPixels * 0.85);
        dialog.setCanceledOnTouchOutside(true);

        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setContentView(view);
      //  DialogUtil.promptDialog1(getActivity(), "更新提示", "有新版本上线，请先更新！", DetermineListener, throwListener);
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
        AlertDialog dialog = new AlertDialog.Builder(getActivity()).create();
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View view = inflater.inflate(R.layout.layout_update_version_procress,null);
        progressBar = view.findViewById(R.id.progress);
       // progressBar.setProgressWithAnimation(100);

        dialog.show();
        DisplayMetrics dm = new DisplayMetrics();   //获取屏幕的大小
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);//获取WindowManager
        windowManager.getDefaultDisplay().getMetrics(dm);   //是获取到Activity的实际屏幕信息
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.width = (int) (dm.widthPixels * 0.85);
        dialog.setCanceledOnTouchOutside(false);

        dialog.getWindow().setAttributes(lp);
        dialog.getWindow().setContentView(view);

       /* mProgressBar = new ProgressDialog(getActivity());
        mProgressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressBar.setTitle("正在下载");
        mProgressBar.setMessage("请稍后...");
        mProgressBar.setProgress(0);
        mProgressBar.setMax(100);
        mProgressBar.show();
        mProgressBar.setCancelable(false);*/

        DownloadUtil.get().download(version_url, APP_PATH_ROOT, fileName, new DownloadUtil.OnDownloadListener() {
            @Override
            public void onDownloadSuccess(File file) {
              /*  if (mProgressBar != null && mProgressBar.isShowing()) {
                    mProgressBar.dismiss();
                }*/
                //下载完成进行相关逻辑操作
                installApk(file);// 安装
            }

            @Override
            public void onDownloading(int progress) {
                //mProgressBar.setProgress(progress);
                progressBar.setCurrentProgress(progress);
            }

            @Override
            public void onDownloadFailed(Exception e) {
                //下载异常进行相关提示操作
            }
        });

    }

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

    android.content.DialogInterface.OnClickListener throwListener = new android.content.DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            dialogLoading.cancel();
        }
    };

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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.tv_address, R.id.tv_time, R.id.tv_receive_wait, R.id.tv_receive_handle,
            R.id.iv_message, R.id.tv_abnormal_wait, R.id.textview_serach})
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
                 ToastUtil.showShort("暂不开放!");
                // actionToRepertoryPannel();
                break;
            case R.id.textview_serach:
                actionToSearchPannel();    //搜索
                break;
        }
    }


    private void actionToSearchPannel() {
        Intent intent = new Intent(getActivity(), GlobalSearchActivity.class);
        startActivity(intent);
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
