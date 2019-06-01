package com.mt.bbdj.baseconfig.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.ImageViewCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.AMapLocationQualityReport;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.bumptech.glide.Glide;
import com.kongzue.dialog.v2.SelectDialog;
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.City;
import com.mt.bbdj.baseconfig.db.County;
import com.mt.bbdj.baseconfig.db.Province;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.CityDao;
import com.mt.bbdj.baseconfig.db.gen.CountyDao;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ProvinceDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.DestroyEvent;
import com.mt.bbdj.baseconfig.model.ImageCutEvent;
import com.mt.bbdj.baseconfig.model.JsonBean;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.MiPictureHelper;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.SystemUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.community.activity.GoodsDetailActivity;
import com.mt.bbdj.community.activity.MyAddressActivity;
import com.mt.bbdj.community.activity.PayforOrderFromShopingCardActivity;
import com.wildma.idcardcamera.camera.CameraActivity;
import com.yanzhenjie.durban.Durban;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionNo;
import com.yanzhenjie.permission.PermissionYes;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.wildma.idcardcamera.camera.CameraActivity.REQUEST_CODE;

public class RegisterCompleteActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    LinearLayout mbtBack;                       //返回
    @BindView(R.id.ic_register_id_front_fl)
    RelativeLayout mFrontFrameLayout;          //身份证正面
    @BindView(R.id.ic_register_id_back_fl)
    RelativeLayout mBackFrameLayout;           //身份证背面
    @BindView(R.id.ic_register_id_licence_fl)
    RelativeLayout mLicenceFrameLayout;        //营业执照


    @BindView(R.id.ic_register_id_front_add)
    ImageView icFrontAdd;
    @BindView(R.id.ic_register_id_front_tv)
    TextView icFrontTv;
    @BindView(R.id.ic_register_id_front)
    ImageView mIvRegisterIdFront;             //身份证正面
    @BindView(R.id.ic_register_id_back_add)
    ImageView icBackAdd;
    @BindView(R.id.ic_register_id_back_tv)
    TextView icBackTv;
    @BindView(R.id.ic_register_id_back)
    ImageView mIvRegisterIdBack;              //身份证背面
    @BindView(R.id.ic_register_id_licence_add)
    ImageView icLicenceAdd;
    @BindView(R.id.ic_register_id_licence)
    ImageView mIvRegisterIdLicence;           //营业执照
    @BindView(R.id.bt_register_complete)
    Button btRegisterComplete;
    @BindView(R.id.et_contact)
    EditText etContact;         //联系人
    @BindView(R.id.et_contact_phone)
    EditText etContactPhone;    //联系人电话
    @BindView(R.id.et_contact_address)
    TextView tvContactAddress;    //联系人省市县
    @BindView(R.id.et_contact_address_detail)
    EditText detailAddress;     //联系人详细地址

    @BindView(R.id.ic_mentou)
    ImageView mentouPicture;   //门头照
    @BindView(R.id.ic_neibu)
    ImageView neibuPicture;    //内部照


    @BindView(R.id.et_main_register_username)
    EditText mRegisterUsername;              //用户名
    @BindView(R.id.et_main_register_idnumber)
    EditText mRegisterIdnumber;              //身份证号
    @BindView(R.id.ll_get_current_location)
    LinearLayout currentLocation;    //获取当前的位置信息


    public static final int PHOTOHRAPH = 300;// 拍照
    private static final int REQUEST_CODE_SYSTEM = 200;    //系统相机
    private static final int PHOTORESOULT = 1;    //结果处理

    private static final int LOCATION_CODE = 400;    //定位权限

    private ArrayList<JsonBean> options1Items = new ArrayList<>();    //省
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();   //市
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();     //县/区

    private List<Province> provincesList = new ArrayList<Province>();
    private List<City> citysList = new ArrayList<City>();
    private List<County> areasList = new ArrayList<County>();

    private String mProvince;
    private String mCity;
    private String mCountry;


    private PopupWindow popupWindow;
    private View selectView;

    private String picturePath = "/bbdj/picture";

    private String IMAGE_DIR = Environment.getExternalStorageDirectory() + "/bbdj/picture";
    private File f = new File(Environment.getExternalStorageDirectory(), picturePath);
    private File photoFile;
    private File compressPicture;
    public static final String IMAGE_UNSPECIFIED = "image/*";
    private int clickType = 0;    //图片类型
    private String pictureType;    //图片名称

    private ImageView[] imageViews = new ImageView[5];

    private RequestQueue mRequestQueue;    //请求队列

    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharedPreferences;
    private HkDialogLoading dialogLoading;
    private String user_id;
    private ProvinceDao mProvinceDao;
    private CityDao mCityDao;
    private CountyDao mCountyDao;
    private AMapLocationClient locationClient;
    private AMapLocationClientOption locationOption;
    private LocationManager lm;
    private WaitDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_complete);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initParams();
        initData();
        initAreaData();
    }

    private void initParams() {
        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();
        DaoSession mDaoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = mDaoSession.getUserBaseMessageDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        mProvinceDao = mDaoSession.getProvinceDao();
        mCityDao = mDaoSession.getCityDao();
        mCountyDao = mDaoSession.getCountyDao();
        LocationByGaode();    //高德导航
    }

    private void LocationByGaode() {
        //初始化Client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    AMapLocationListener locationListener = new AMapLocationListener() {

        @Override
        public void onLocationChanged(AMapLocation location) {
            if (null != location) {
                StringBuffer sb = new StringBuffer();
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.getErrorCode() == 0) {
                    String realAddress = "";
                    String province = location.getProvince();    //省
                    String city = location.getCity();     //市
                    String area = location.getDistrict();   //区
                    String address = location.getAddress();   //地址
                    String poiName = location.getPoiName();   //兴趣点

                    String addressOne = address.replace(province, "");
                    String addressTwo = addressOne.replace(city, "");
                    String addressThress = addressTwo.replace(area, "");

                    if (province.contains("市")) {
                        province = province.replace("市", "");
                    }

                    if (addressThress.contains("靠近")) {
                        int index = addressThress.indexOf("靠近");
                        realAddress = addressThress.substring(0,index);
                    }

                    mProvince = province;
                    mCity = city;
                    mCountry = area;

                    tvContactAddress.setText(province+city+area);
                    detailAddress.setText(realAddress);

                } else {
                    ToastUtil.showShort("定位失败，请重试");
                }
            } else {
                ToastUtil.showShort("定位失败，请重试");
            }
            if (waitDialog != null) {
                waitDialog.doDismiss();
            }
        }
    };


    /**
     * 获取GPS状态的字符串
     *
     * @param statusCode GPS状态码
     * @return
     */
    private String getGPSStatusString(int statusCode) {
        String str = "";
        switch (statusCode) {
            case AMapLocationQualityReport.GPS_STATUS_OK:
                str = "GPS状态正常";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPROVIDER:
                str = "手机中没有GPS Provider，无法进行GPS定位";
                break;
            case AMapLocationQualityReport.GPS_STATUS_OFF:
                str = "GPS关闭，建议开启GPS，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_MODE_SAVING:
                str = "选择的定位模式中不包含GPS定位，建议选择包含GPS定位的模式，提高定位质量";
                break;
            case AMapLocationQualityReport.GPS_STATUS_NOGPSPERMISSION:
                str = "没有GPS定位权限，建议开启gps定位权限";
                break;
        }
        return str;
    }

    private void initData() {
        //初始化图片选择的弹出框
        initPictureSelectPop();

        initShowPictureParams();

        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();

        mEditor = SharedPreferencesUtil.getEditor();

        mSharedPreferences = SharedPreferencesUtil.getSharedPreference();
        mEditor.putString("just_card", "");
        mEditor.putString("back_card", "");
        mEditor.commit();
        dialogLoading = new HkDialogLoading(RegisterCompleteActivity.this, "提交中...");
    }

    private void initShowPictureParams() {
        imageViews[0] = mIvRegisterIdFront;
        imageViews[1] = mIvRegisterIdBack;
        imageViews[2] = mIvRegisterIdLicence;
        imageViews[3] = mentouPicture;
        imageViews[4] = neibuPicture;
    }

    private void initPictureSelectPop() {
        initPopuStyle();    //初始化popuwindow的样式
    }

    //图片来源点击事件
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.bt_take_camera:
                    takePicture();     //拍照
                    break;
                case R.id.bt_take_from_album:
                    takePictureFromAlbum();   //相册选择
                    break;
                case R.id.bt_cancle:
                    popupWindow.dismiss();
                    break;
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void imageCutListener(ImageCutEvent event) {
        if (this.getClass().getName().equals(event.getContextName())) {
            ArrayList<String> imagePath = event.getPicturePathList();
            if (imagePath.size() > 0) {
                String cutPath = imagePath.get(0);
                Glide.with(RegisterCompleteActivity.this)
                        .load(cutPath)
                        .into(mIvRegisterIdLicence);
                uploadPicture(cutPath);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case PHOTOHRAPH:
                takePictureByCamera();     //拍照
                break;
            case REQUEST_CODE_SYSTEM:   //系统相机
                takePictureBySystem(data);
                break;
            case REQUEST_CODE:    //身份证
                handleIdcardPicture(data);
                break;
            case PHOTORESOULT:    //结果处理
                handleResult(data);
                break;
        }
    }

    //拍照点击事件
    @OnClick({R.id.iv_back, R.id.ic_register_id_front_fl, R.id.ic_register_id_back_fl,
            R.id.ic_register_id_licence_fl, R.id.bt_register_complete, R.id.et_contact_address,
            R.id.rl_dianmain, R.id.ic_neibu, R.id.ll_get_current_location})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:        //返回
                finish();
                break;
            case R.id.ic_register_id_front_fl:     //拍摄图片
                clickType = 0;
                pictureType = "just_card";
                applyCameraPermission();
                break;
            case R.id.ic_register_id_back_fl:
                clickType = 1;
                pictureType = "back_card";
                applyCameraPermission();
                break;
            case R.id.ic_register_id_licence_fl:
                clickType = 2;
                pictureType = "license";
                applyCameraPermission();
                break;
            case R.id.bt_register_complete:       //完成
                handleCompleteEvent();
                break;
            case R.id.et_contact_address:    //选择省市县
                handleContactAddress();
                break;
            case R.id.rl_dianmain:     //门头照
                clickType = 3;
                pictureType = "door_photo";
                applyCameraPermission();
                break;
            case R.id.ic_neibu:     //内部照
                clickType = 4;
                pictureType = "internal_photo";
                applyCameraPermission();
                break;
            case R.id.ll_get_current_location:    //获取当前的定位
                getCurrentLocation();
                break;
        }
    }

    private void getCurrentLocation() {
        if (appLocationPersion()) {
            waitDialog = WaitDialog.show(RegisterCompleteActivity.this, "定位中...").setCanCancel(true);
            locationClient.startLocation();
        }
    }

    private boolean appLocationPersion() {
        //判断时候开启定位权限
        lm = (LocationManager) RegisterCompleteActivity.this.getSystemService(RegisterCompleteActivity.this.LOCATION_SERVICE);
        boolean ok = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (ok) {
            if (ContextCompat.checkSelfPermission(RegisterCompleteActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // 没有权限，申请权限。
                // 申请授权。
                ActivityCompat.requestPermissions(RegisterCompleteActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_CODE);
                return true;
            } else {
                return true;
            }
        } else {
            showStartGPSPermission();    //开启定位权限

        }
        return false;
    }

    private void showStartGPSPermission() {
        SelectDialog.show(this, "提示", "请先开启定位权限", "确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent, 1315);
                    }
                }, "取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).setCanCancel(true);
    }

    private void handleContactAddress() {
        if (options1Items.size() > 0 && options2Items.size() > 0 && options3Items.size() > 0) {
            showPickerView();
        }
    }

    private void showPickerView() {
        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {

            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                mProvince = options1Items.get(options1).getPickerViewText();
                mCity = options2Items.get(options1).get(options2);
                mCountry = options3Items.get(options1).get(options2).get(options3);
                tvContactAddress.setText(mProvince + mCity + mCountry);
            }
        }).setSelectOptions(0, 0, 0)  //设置默认选中项
                .setTitleText("地区选择")
                .setTitleSize(16)
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(16)
                .setLineSpacingMultiplier(2.5f)
                .setCancelColor(Color.parseColor("#0da95f"))
                .setSubmitColor(Color.parseColor("#0da95f"))
                .build();
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    private void takePictureFromAlbum() {
        //判断SD卡是否可用
        if (SystemUtil.hasSdcard()) {
            if (!f.exists()) {
                f.mkdirs();
            }
            String uuid = UUID.randomUUID().toString();
            String path2 = uuid + ".jpg";
            photoFile = new File(f, path2);
            compressPicture = new File(f, uuid);
            Intent intent = new Intent(Intent.ACTION_PICK, null);
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
            startActivityForResult(intent, REQUEST_CODE_SYSTEM);
            popupWindow.dismiss();
        }
    }


    private void handleIdcardPicture(Intent data) {
        final String filePath = CameraActivity.getImagePath(data);
        Glide.with(this)
                .load(filePath)
                .override(500, 500)
                .into(imageViews[clickType]);
        uploadPicture(filePath);    //上传图片
    }


    private void handleResult(Intent data) {
        if (data == null) {
            return;
        }
        String filePath = Durban.parseResult(data).get(0);
        // DefaultAlbumLoader.getInstance().loadImage(ivHead, filePath, 500, 500);
        Glide.with(this)
                .load(filePath)
                .override(500, 500)
                .into(imageViews[clickType]);
        if (clickType == 0) {
            icFrontAdd.setVisibility(View.GONE);
            icFrontTv.setVisibility(View.GONE);
        } else if (clickType == 1) {
            icBackAdd.setVisibility(View.GONE);
            icBackTv.setVisibility(View.GONE);
        } else {
            icLicenceAdd.setVisibility(View.GONE);
        }
        uploadPicture(filePath);    //上传图片
    }


    private void takePictureBySystem(Intent data) {
        String pickPath = MiPictureHelper.getPath(RegisterCompleteActivity.this, data.getData());

        if (clickType == 3 || clickType == 4 || clickType == 2) {
            compressFile(pickPath);
            return;
        }
        Durban.with(RegisterCompleteActivity.this)
                .requestCode(PHOTORESOULT)
                .statusBarColor(ContextCompat.getColor(RegisterCompleteActivity.this, R.color.colorPrimary))
                .toolBarColor(ContextCompat.getColor(RegisterCompleteActivity.this, R.color.colorPrimary))
                .navigationBarColor(ContextCompat.getColor(RegisterCompleteActivity.this, R.color.colorPrimary))
                .maxWidthHeight(2019, 1275)
                .outputDirectory(compressPicture.getPath())
                .inputImagePaths(pickPath)
                .aspectRatio(1346, 850)
                .start();
    }

    private void compressFile() {
        Luban.with(this)
                .load(photoFile)
                .ignoreBy(100)
                .setTargetDir(IMAGE_DIR)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                        uploadPicture(file.getAbsolutePath());    //上传图片
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }).launch();
    }

    private void compressFile(String filePath) {
        Luban.with(this)
                .load(filePath)
                .ignoreBy(100)
                .setTargetDir(IMAGE_DIR)
                .setCompressListener(new OnCompressListener() {
                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onSuccess(File file) {
                   //     Glide.with(RegisterCompleteActivity.this).load(file.getPath()).into(imageViews[clickType]);
                        uploadPicture(file.getAbsolutePath());    //上传图片
                    }

                    @Override
                    public void onError(Throwable e) {
                    }
                }).launch();
    }

    private void takePictureByCamera() {
        if (clickType == 3 || clickType == 4 || clickType == 2) {
            String picturePath = Uri.fromFile(photoFile).getPath();
            compressFile();
            return;
        }
        Durban.with(RegisterCompleteActivity.this)
                .requestCode(PHOTORESOULT)
                .statusBarColor(ContextCompat.getColor(RegisterCompleteActivity.this, R.color.colorPrimary))
                .toolBarColor(ContextCompat.getColor(RegisterCompleteActivity.this, R.color.colorPrimary))
                .navigationBarColor(ContextCompat.getColor(RegisterCompleteActivity.this, R.color.colorPrimary))
                .maxWidthHeight(2019, 1275)
                .outputDirectory(compressPicture.getPath())
                .inputImagePaths(Uri.fromFile(photoFile).getPath())
                .rotateSupport(true, this.getClass().getName())    //设置支持裁剪区域旋转
                .aspectRatio(1346, 850)
                .start();
    }

    private void takePicture() {
        //判断SD卡是否可用
        if (SystemUtil.hasSdcard()) {
            if (!f.exists()) {
                f.mkdirs();
            }

            if (clickType == 0) {
                CameraActivity.toCameraActivity(this, CameraActivity.TYPE_IDCARD_FRONT);
            } else if (clickType == 1) {
                CameraActivity.toCameraActivity(this, CameraActivity.TYPE_IDCARD_BACK);
            } else {
                String uuid = UUID.randomUUID().toString();
                String path2 = uuid + ".jpg";
                photoFile = new File(f, path2);
                compressPicture = new File(f, uuid);
                Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", photoFile);
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                startActivityForResult(intent, PHOTOHRAPH);
            }
            popupWindow.dismiss();
        }
    }

    private void applyCameraPermission() {
        if (AndPermission.hasPermission(this, Manifest.permission.CAMERA)) {
            showSelectDialog();
        } else {
            // 申请权限。
            AndPermission.with(this)
                    .requestCode(100)
                    .permission(Manifest.permission.CAMERA)
                    .callback(this)
                    .start();
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void handleMessage(DestroyEvent destroyEvent) {
        if (1 == destroyEvent.getType()) {
            finish();
        }
    }

    private void uploadPicture(String filePath) {
        if (!new File(filePath).exists()) {
            ToastUtil.showShort("文件不存在，请重拍！");
            return;
        }
        Request<String> request = NoHttpRequest.commitPictureRequest(filePath);
        mRequestQueue.add(1, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                dialogLoading.show();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "RegisterAccount::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    if ("5001".equals(code)) {
                        JSONObject dataObject = jsonObject.getJSONObject("data");
                        String pictureUrl = dataObject.getString("picurl");
                        String message = jsonObject.get("msg").toString();
                        Glide.with(RegisterCompleteActivity.this).load(pictureUrl).into(imageViews[clickType]);
                        mEditor.putString(pictureType, pictureUrl);
                        mEditor.commit();
                    } else {
                        ToastUtil.showShort("上传失败，请重试！");
                     // restorePictureState();    //还原图片位
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialogLoading.cancel();
                    ToastUtil.showShort("上传失败，请重试！");
                  //  restorePictureState();
                }
                dialogLoading.cancel();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                dialogLoading.cancel();
            }

            @Override
            public void onFinish(int what) {
                dialogLoading.cancel();
            }
        });
    }

    private void restorePictureState() {
        switch (pictureType) {
            case "just_card":     //身份证前面
                mIvRegisterIdFront.setImageBitmap(null);
                icFrontAdd.setBackgroundResource(R.drawable.ic_add);
                break;
            case "back_card":     //身份证背面
                mIvRegisterIdBack.setImageBitmap(null);
                icBackAdd.setBackgroundResource(R.drawable.ic_add);
                break;
            case "license":       //营业执照
                mIvRegisterIdLicence.setImageBitmap(null);
                icLicenceAdd.setBackgroundResource(R.drawable.ic_add);
                break;
            case "door_photo":
                mentouPicture.setImageBitmap(null);
                break;
            case "internal_photo":
                neibuPicture.setImageBitmap(null);
                break;
        }
    }

    private void handleCompleteEvent() {
        if (completeMessage()) {
            finishRegisterRequest();
        }
    }

    private void finishRegisterRequest() {
        String phone = mSharedPreferences.getString("phone", "");
        String password = mSharedPreferences.getString("password", "");
        String realname = mRegisterUsername.getText().toString();
        String idcard = mRegisterIdnumber.getText().toString();
        String just_card = mSharedPreferences.getString("just_card", "");
        String back_card = mSharedPreferences.getString("back_card", "");
        String license = mSharedPreferences.getString("license", "");
        String businessNumber = mSharedPreferences.getString("businessNumber", "");
        String door_photo = mSharedPreferences.getString("door_photo", "");
        String internal_photo = mSharedPreferences.getString("internal_photo", "");

        String contactPerson = etContact.getText().toString();
        String contactPhone = etContactPhone.getText().toString();
        String detail_Address = detailAddress.getText().toString();

        Request<String> request = NoHttpRequest.commitRegisterRequest(phone, password, realname, idcard, just_card,
                back_card, license, businessNumber, contactPerson, contactPhone, mProvince, mCity, mCountry, detail_Address, door_photo, internal_photo);
        mRequestQueue.add(2, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                dialogLoading.show();
            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "RegisterAccount::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    String code = jsonObject.get("code").toString();
                    String msg = jsonObject.get("msg").toString();
                    if ("5001".equals(code)) {
                        Intent intent = new Intent(RegisterCompleteActivity.this, RegisterFinishActivity.class);
                        startActivity(intent);
                        //注册完成之后发送消息，销毁之前的注册的界面
                        EventBus.getDefault().post(new DestroyEvent(1));
                        finish();
                    } else if ("4002".equals(code)) {
                        ToastUtil.showShort(msg);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialogLoading.cancel();
                    ToastUtil.showShort("上传失败，请重试！");
                }
                dialogLoading.cancel();
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                dialogLoading.cancel();
            }

            @Override
            public void onFinish(int what) {
                dialogLoading.cancel();
            }
        });
    }

    private boolean completeMessage() {
        String userName = mRegisterUsername.getText().toString();
        String idNumber = mRegisterIdnumber.getText().toString();

        String contactPerson = etContact.getText().toString();
        String contactPhone = etContactPhone.getText().toString();
        String contactAddress = tvContactAddress.getText().toString();
        String detail_Address = detailAddress.getText().toString();

        String frontIDImg = mSharedPreferences.getString("just_card", "");
        String backIDImg = mSharedPreferences.getString("back_card", "");
        String door_photo = mSharedPreferences.getString("door_photo", "");
        String internal_photo = mSharedPreferences.getString("internal_photo", "");

        if ("".equals(userName) || "".equals(frontIDImg) || "".equals(backIDImg) ||
                "".equals(contactPerson) || "".equals(contactAddress)
                || "".equals(detail_Address)) {
            ToastUtil.showShort("请完善注册信息！");
            return false;
        }

       /* if (!StringUtil.isID(idNumber)) {
            ToastUtil.showShort("身份证不合法！");
            return false;
        }*/

        if (!StringUtil.isMobile(contactPhone)) {
            ToastUtil.showShort("手机号码不合法！");
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        locationClient.onDestroy();
        locationClient = null;
        locationOption = null;
    }

    private void showSelectDialog() {
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(mbtBack, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    private void initPopuStyle() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            selectView = getLayoutInflater().inflate(R.layout.view_picture_select, null);
            Button takeCamera = (Button) selectView.findViewById(R.id.bt_take_camera);
            Button takeFromAlbum = (Button) selectView.findViewById(R.id.bt_take_from_album);
            Button btnCancle = (Button) selectView.findViewById(R.id.bt_cancle);
            takeCamera.setOnClickListener(mOnClickListener);
            takeFromAlbum.setOnClickListener(mOnClickListener);
            btnCancle.setOnClickListener(mOnClickListener);
            popupWindow = new PopupWindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            //设置动画
            popupWindow.setAnimationStyle(R.style.popup_window_anim);
            //设置背景颜色
            popupWindow.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#80000000")));
            popupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            popupWindow.setTouchable(true); // 设置popupwindow可点击
            popupWindow.setOutsideTouchable(true); // 设置popupwindow外部可点击
            popupWindow.setFocusable(true); // 获取焦点
            LinearLayout layout_pop_close = (LinearLayout) selectView.findViewById(R.id.layout_left_close);
            layout_pop_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
        }
    }

    // 成功回调的方法，用注解即可，里面的数字是请求时的requestCode。
    @PermissionYes(100)
    private void getMultiYes(List<String> grantedPermissions) {
        // TODO 申请权限成功。

    }

    // 失败回调的方法，用注解即可，里面的数字是请求时的requestCode。
    @PermissionNo(100)
    private void getMultiNo(List<String> deniedPermissions) {
        // 用户否勾选了不再提示并且拒绝了权限，那么提示用户到设置中授权。
        if (AndPermission.hasAlwaysDeniedPermission(this, deniedPermissions)) {
            // 第一种：用默认的提示语。
            AndPermission.defaultSettingDialog(this, 100).show();
        }
    }

    private void initAreaData() {
        ArrayList<JsonBean> jsonBean = new ArrayList<>();
        provincesList = mProvinceDao.queryBuilder().list();
        for (int i = 0; i < provincesList.size(); i++) {
            JsonBean item = new JsonBean();
            item.setName(provincesList.get(i).getRegion_name());
            item.setProvince(provincesList.get(i).getId());
            List<JsonBean.CityBean> cityBeansList = new ArrayList<>();
            citysList = mCityDao.queryBuilder().where(CityDao.Properties.Parent_id.eq(provincesList.get(i).getId())).list();
            for (int il = 0; il < citysList.size(); il++) {
                JsonBean.CityBean item1 = new JsonBean.CityBean();
                item1.setName(citysList.get(il).getRegion_name());
                item1.setCity(citysList.get(il).getId());
                List<String> area = new ArrayList<>();
                List<String> areaId = new ArrayList<>();
                areasList = mCountyDao.queryBuilder().where(CountyDao.Properties.Parent_id.eq(citysList.get(il).getId())).list();
                for (int i2 = 0; i2 < areasList.size(); i2++) {
                    area.add(areasList.get(i2).getRegion_name());
                    areaId.add(areasList.get(i2).getId());
                }
                if (area.size() < 0) {
                    area.add(citysList.get(il).getRegion_name());
                    areaId.add(citysList.get(il).getId());
                }
                item1.setArea(area);
                item1.setAreaId(areaId);
                cityBeansList.add(item1);
            }
            if (citysList.size() <= 0) {
                JsonBean.CityBean item1 = new JsonBean.CityBean();
                item1.setName(provincesList.get(i).getRegion_name());
                item1.setCity(provincesList.get(i).getId());
                List<String> area = new ArrayList<>();
                List<String> areaId = new ArrayList<>();

                area.add(provincesList.get(i).getRegion_name());
                areaId.add(provincesList.get(i).getId());


                item1.setArea(area);
                item1.setAreaId(areaId);
                cityBeansList.add(item1);
            }
            item.setCityList(cityBeansList);
            jsonBean.add(item);
        }
        options1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {   //遍历省份
            ArrayList<String> cityList = new ArrayList<>();     //该省的城市列表
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();    //该省的所有地区列表

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {   //遍历该省的所有城市
                String cityName = jsonBean.get(i).getCityList().get(c).getName();
                cityList.add(cityName);   //添加城市
                ArrayList<String> city_ArrayList = new ArrayList<>();    //该城市的所有地区列表

                //若是无地区数据，天剑空字符串，放置数据为null 导致三个选型的长度不匹配造成崩溃
                if (jsonBean.get(i).getCityList().get(c).getArea() == null ||
                        jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    city_ArrayList.add("");
                } else {
                    city_ArrayList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }
                province_AreaList.add(city_ArrayList);   //添加该省所有地区数据
            }

            //添加城市数据
            options2Items.add(cityList);

            //添加地区数据
            options3Items.add(province_AreaList);
        }
    }

}
