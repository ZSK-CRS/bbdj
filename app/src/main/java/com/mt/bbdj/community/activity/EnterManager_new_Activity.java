package com.mt.bbdj.community.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import com.king.zxing.util.ResizeAbleSurfaceView;
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.activity.ActivityBase;
import com.mt.bbdj.baseconfig.activity.RegisterCompleteActivity;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.db.ExpressLogo;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.WaillMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.db.gen.WaillMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.Entermodel;
import com.mt.bbdj.baseconfig.model.PrintTagModel;
import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.IntegerUtil;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.RxBarTool;
import com.mt.bbdj.baseconfig.utls.RxBeepTool;
import com.mt.bbdj.baseconfig.utls.RxConstants;
import com.mt.bbdj.baseconfig.utls.RxDataTool;
import com.mt.bbdj.baseconfig.utls.RxDialogSure;
import com.mt.bbdj.baseconfig.utls.RxPhotoTool;
import com.mt.bbdj.baseconfig.utls.RxSPTool;
import com.mt.bbdj.baseconfig.utls.RxToast;
import com.mt.bbdj.baseconfig.utls.SoftKeyBoardListener;
import com.mt.bbdj.baseconfig.utls.SoundHelper;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.SystemUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MarginDecoration;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.baseconfig.view.MyPopuwindow;
import com.mt.bbdj.baseconfig.view.RxActivityTool;
import com.mt.bbdj.baseconfig.view.RxAnimationTool;
import com.mt.bbdj.community.adapter.EnterManagerAdapter;
import com.mt.bbdj.community.adapter.SimpleStringAdapter;
import com.rxfeature.activity.ActivityScanerCode;
import com.rxfeature.module.scaner.CameraManager;
import com.rxfeature.module.scaner.OnRxScanerListener;
import com.rxfeature.module.scaner.PlanarYUVLuminanceSource;
import com.rxfeature.module.scaner.decoding.InactivityTimer;
import com.rxfeature.tool.RxQrBarTool;
import com.wildma.idcardcamera.utils.ScreenUtils;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.rest.OnResponseListener;
import com.yanzhenjie.nohttp.rest.Request;
import com.yanzhenjie.nohttp.rest.RequestQueue;
import com.yanzhenjie.nohttp.rest.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static android.content.ContentValues.TAG;


public class EnterManager_new_Activity extends ActivityBase {

    private TextView tvPackageCode;     //提货码
    private TextView tvWailNumber;     //运单号
    private TextView expressSelect;    //快递公司选择
    private RecyclerView recyclerView;
    private RelativeLayout ivBack;    //返回
    private TextView tv_enter_number;     //入库数
    private TextView tv_enter;
    private List<HashMap<String, String>> mList = new ArrayList<>();
    private List<HashMap<String, String>> mPrintList = new ArrayList<>();
    private List<String> mTempList = new ArrayList<>();//临时数据

    private boolean isContinuousScan = true;
    private EnterManagerAdapter mAdapter;
    private String user_id;
    private RequestQueue mRequestQueue;
    private int packageCode = 1060204;

    private MyPopuwindow popupWindow;

    private List<HashMap<String, String>> mFastData = new ArrayList<>();    //快递公司
    private HashMap<String, String>currentMap = new HashMap<>();    //当前数据
    private ExpressLogoDao mExpressLogoDao;

    private final int CHECK_WAY_BILL_STATE = 100;    //检测
    private final int ENTER_RECORDE_REQUEST = 200;    //入库
    private final int COMMIT_PICTURE_REQUEST = 300;    //上传图片
    private String resultNumber;
    private String expressName;
    private PrintTagModel printTagModel = new PrintTagModel();
    private String rootPaht = Environment.getExternalStorageDirectory() + "/bbdj/barcode/";

    private int tagNumber = 1;

    /**
     * 扫描结果监听
     */
    private static OnRxScanerListener mScanerListener;

    private InactivityTimer inactivityTimer;

    /**
     * 扫描处理
     */
    private CaptureActivityHandler handler;

    /**
     * 整体根布局
     */
    private RelativeLayout mContainer = null;

    /**
     * 扫描框根布局
     */
    private RelativeLayout mCropLayout = null;

    /**
     * 扫描边界的宽度
     */
    private int mCropWidth = 0;

    /**
     * 扫描边界的高度
     */
    private int mCropHeight = 0;

    /**
     * 是否有预览
     */
    private boolean hasSurface;

    /**
     * 扫描成功后是否震动
     */
    private boolean vibrate = false;

    /**
     * 闪光灯开启状态
     */
    private boolean mFlashing = true;

    /**
     * 生成二维码 & 条形码 布局
     */
    private LinearLayout mLlScanHelp;

    /**
     * 闪光灯 按钮
     */
    private ImageView mIvLight;

    /**
     * 扫描结果显示框
     */
    private RxDialogSure rxDialogSure;

    private WaillMessageDao mWaillMessageDao;
    private WaitDialog dialogLoading;
    private String wayNumber = "";

    private String station_id = "";    //驿站id
    private String courier_id = "";    //快递员id
    private String express_id = "";    //快递公司id
    private RelativeLayout rl_scan;
    private TextView tv_phone;
    private TextView tv_yundan;
    private boolean isAdd;


    /**
     * 设置扫描信息回调
     */
    public static void setScanerListener(OnRxScanerListener scanerListener) {
        mScanerListener = scanerListener;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_manager_new);
        //界面控件初始化
        initDecode();
        initParams();
        initView();
        initListener();
        initSelectPop();
        //权限初始化
        initPermission();
        //扫描动画初始化
        initScanerAnimation();
        //初始化 CameraManager
        CameraManager.init(mContext);
        hasSurface = false;
        //  inactivityTimer = new InactivityTimer(this);
    }



    private void initListener() {
        //删除
        mAdapter.setDeleteClickListener(new EnterManagerAdapter.onDeleteClickListener() {
            @Override
            public void onDelete(int position) {
                HashMap<String, String> map = mList.get(position);
                String resultCode = map.get("wail_number");
                mWaillMessageDao.queryBuilder().where(WaillMessageDao.Properties.WailNumber.eq(resultCode)).buildDelete();
                mList.remove(position);
                mTempList.remove(resultCode);
                mAdapter.notifyDataSetChanged();
                tv_enter_number.setText("(" + mList.size() + "/30)");
                if (handler != null) {
                    // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                    handler.sendEmptyMessage(R.id.restart_preview);
                }
            }
        });

        //选择对话框
        expressSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList.size() != 0) {
                    ToastUtil.showShort("请先入库后再切换快递公司！");
                    return;
                }
                selectExpressDialog(v);
            }
        });

        //返回
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //入库
        tv_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enterRecorde();    //入库请求
            }
        });

        //手动输入号码的监听
        tv_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int legth = tv_phone.getText().toString().length();
                if (legth == 11 && isAdd) {
                    String yundan = tv_yundan.getText().toString();
                    if (yundan.length() == 0) {
                        ToastUtil.showShort("运单号不可为空！");
                        return;
                    }

                    isAdd = false;

                    SystemUtil.hideKeyBoard(EnterManager_new_Activity.this, tv_yundan);
                    //设置数据
                    currentMap.put("express_name", s.toString());
                    mList.add(currentMap);

                    tv_enter_number.setText("(" + mList.size() + "/30)");

                    tv_phone.setText("");
                    tvPackageCode.setText("");
                    tv_yundan.setText("");
                }
            }
        });

        //监听软键盘的弹出隐藏
        SoftKeyBoardListener.setListener(this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                rl_scan.setVisibility(View.GONE);
            }

            @Override
            public void keyBoardHide(int height) {
                rl_scan.setVisibility(View.VISIBLE);
                if (handler != null) {
                    // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                    handler.sendEmptyMessage(R.id.restart_preview);
                }
            }
        });
    }

    private void enterRecorde() {
        if (mList.size() == 0) {
            ToastUtil.showShort("无可提交数据");
            return;
        }
        tv_enter.setEnabled(false);
        dialogLoading = WaitDialog.show(EnterManager_new_Activity.this, "入库中...").setCanCancel(false);
        String data_json = getEnterrecordData();
        Request<String> request = NoHttpRequest.enterRecordeRequest(user_id, express_id, data_json);
        mRequestQueue.add(ENTER_RECORDE_REQUEST, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "EnterManagerActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    enterRecorderResult(jsonObject);
                } catch (JSONException e) {
                    e.printStackTrace();
                    dialogLoading.doDismiss();
                    tv_enter.setEnabled(true);
                }
                tv_enter.setEnabled(true);
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                dialogLoading.doDismiss();
                tv_enter.setEnabled(true);
            }

            @Override
            public void onFinish(int what) {
                dialogLoading.doDismiss();
                tv_enter.setEnabled(true);
            }
        });
    }

    private void enterRecorderResult(JSONObject jsonObject) throws JSONException {
        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();
        if ("5001".equals(code)) {
            mList.clear();
            tv_enter_number.setText("(" + 0 + "/30)");
            tagNumber = 1;
            mAdapter.notifyDataSetChanged();
            JSONArray data = jsonObject.getJSONArray("data");
            printNumber(data);    //打印取件码
        }
        ToastUtil.showShort(msg);
    }

    private void printNumber(JSONArray data) throws JSONException {
        mWaillMessageDao.deleteAll();    //删除数据库中临时数据
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
        Intent intent = new Intent(EnterManager_new_Activity.this, BluetoothNumberActivity.class);
        intent.putExtra("printData", printTagModel);
        startActivity(intent);
    }


    private String getEnterrecordData() {
        StringBuilder sb = new StringBuilder();
        for (HashMap<String, String> data : mList) {
            sb.append(data.get("package_code"));
            sb.append("|");
            sb.append(data.get("express_name"));
            sb.append("|");
            sb.append(data.get("name"));
            sb.append("|");
            sb.append(data.get("wail_number"));
            sb.append(",");
        }
        String sbb = sb.toString();
        String result = sbb.substring(0, sbb.length() - 1);
        return result;
    }

    private void selectExpressDialog(View view) {
        popupWindow.showAsDropDown(view);
    }

    private void initSelectPop() {
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        } else {
            View selectView = getLayoutInflater().inflate(R.layout.fast_layout_1, null);
            RecyclerView fastList = selectView.findViewById(R.id.tl_fast_list);
            initRecycler(fastList);
            popupWindow = new MyPopuwindow(selectView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

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

    private void initRecyclerView() {
        recyclerView.setFocusable(false);
        //initTemparayData();   //模拟数据
        List<WaillMessage> dataList = mWaillMessageDao.queryBuilder().list();
        if (dataList != null && dataList.size() != 0) {
            for (WaillMessage waillMessage : dataList) {
                HashMap<String, String> map = new HashMap<>();
                map.put("package_code", waillMessage.getTagCode());
                map.put("wail_number", waillMessage.getWailNumber());
                map.put("express_name", waillMessage.getExpressName());
                map.put("mobile", waillMessage.getMobile());
                map.put("name", waillMessage.expressName);
                mList.add(map);
                map = null;
                tagNumber = waillMessage.getTagNumber();
            }
        }

        mAdapter = new EnterManagerAdapter(mList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL, Color.parseColor("#e9e9e9"), 1));
        recyclerView.setAdapter(mAdapter);
    }

    private void initRecycler(RecyclerView fastList) {
        mFastData.clear();
        //查询快递公司的信息
        List<ExpressLogo> expressLogoList = mExpressLogoDao.queryBuilder()
                .where(ExpressLogoDao.Properties.States.eq(1)).list();

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
                expressName = mFastData.get(position).get("express");
                //sendExpressid(express_id);    //向对应的界面发送快递公司消息
                expressSelect.setText(mFastData.get(position).get("express"));
                popupWindow.dismiss();
            }
        });

        if (mFastData.size() != 0) {
            expressName = mFastData.get(0).get("express");
            express_id = mFastData.get(0).get("express_id");
        }

        fastList.setAdapter(goodsAdapter);
        fastList.addItemDecoration(new MarginDecoration(this));
        fastList.setLayoutManager(new LinearLayoutManager(this));
        goodsAdapter.notifyDataSetChanged();
    }

    private void initParams() {

        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = daoSession.getUserBaseMessageDao();
        mWaillMessageDao = daoSession.getWaillMessageDao();
        mExpressLogoDao = daoSession.getExpressLogoDao();

        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        mRequestQueue = NoHttp.newRequestQueue();
        printTagModel = new PrintTagModel();
    }


    private void initDecode() {
        multiFormatReader = new MultiFormatReader();

        // 解码的参数
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(2);
        // 可以解析的编码类型
        Vector<BarcodeFormat> decodeFormats = new Vector<BarcodeFormat>();
        if (decodeFormats == null || decodeFormats.isEmpty()) {
            decodeFormats = new Vector<BarcodeFormat>();

            Vector<BarcodeFormat> PRODUCT_FORMATS = new Vector<BarcodeFormat>(5);
            PRODUCT_FORMATS.add(BarcodeFormat.UPC_A);
            PRODUCT_FORMATS.add(BarcodeFormat.UPC_E);
            PRODUCT_FORMATS.add(BarcodeFormat.EAN_13);
            PRODUCT_FORMATS.add(BarcodeFormat.EAN_8);
            // PRODUCT_FORMATS.add(BarcodeFormat.RSS14);
            Vector<BarcodeFormat> ONE_D_FORMATS = new Vector<BarcodeFormat>(PRODUCT_FORMATS.size() + 4);
            ONE_D_FORMATS.addAll(PRODUCT_FORMATS);
            ONE_D_FORMATS.add(BarcodeFormat.CODE_39);
            ONE_D_FORMATS.add(BarcodeFormat.CODE_93);
            ONE_D_FORMATS.add(BarcodeFormat.CODE_128);
            ONE_D_FORMATS.add(BarcodeFormat.ITF);
            Vector<BarcodeFormat> QR_CODE_FORMATS = new Vector<BarcodeFormat>(1);
            QR_CODE_FORMATS.add(BarcodeFormat.QR_CODE);
            Vector<BarcodeFormat> DATA_MATRIX_FORMATS = new Vector<BarcodeFormat>(1);
            DATA_MATRIX_FORMATS.add(BarcodeFormat.DATA_MATRIX);

            // 这里设置可扫描的类型，我这里选择了都支持
            decodeFormats.addAll(ONE_D_FORMATS);
            decodeFormats.addAll(QR_CODE_FORMATS);
            decodeFormats.addAll(DATA_MATRIX_FORMATS);
        }
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);

        multiFormatReader.setHints(hints);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        super.onResume();
        mPrintList.clear();
        ResizeAbleSurfaceView surfaceView = findViewById(R.id.capture_preview);
        int width = ScreenUtils.getScreenWidth(this);
        int height = ScreenUtils.dip2px(this, 280);
        surfaceView.resize(width, height);
        SurfaceHolder surfaceHolder = surfaceView.getHolder();
        if (hasSurface) {
            //Camera初始化
            initCamera(surfaceHolder);
        } else {
            surfaceHolder.addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

                }

                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    if (!hasSurface) {
                        hasSurface = true;
                        initCamera(holder);
                    }
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    hasSurface = false;

                }
            });
            surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (handler != null) {
            handler.quitSynchronously();
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
        CameraManager.get().closeDriver();
    }

    @Override
    protected void onDestroy() {
        //  inactivityTimer.shutdown();
        mScanerListener = null;
        super.onDestroy();
        mWaillMessageDao.deleteAll();
        List<WaillMessage> lists = new ArrayList<>();
        for (HashMap<String, String> map : mList) {
            String package_code = map.get("package_code");
            String wail_number = map.get("wail_number");
            String mobile = map.get("mobile");
            String express_name = map.get("express_name");
            String name = map.get("name");
            WaillMessage waillMessage = new WaillMessage();
            waillMessage.setName(name);
            waillMessage.setExpressName(express_name);
            waillMessage.setTagCode(package_code);
            waillMessage.setMobile(mobile);
            waillMessage.setWailNumber(wail_number);
            waillMessage.setTagNumber(tagNumber);
            lists.add(waillMessage);
        }
        mWaillMessageDao.saveInTx(lists);
    }

    private void initView() {
        mIvLight = findViewById(R.id.top_mask);
        mContainer = findViewById(R.id.capture_containter);
        mCropLayout = findViewById(R.id.capture_crop_layout);
        rl_scan = findViewById(R.id.rl_scan);
        rl_scan.setVisibility(View.VISIBLE);
        tv_phone = findViewById(R.id.tv_phone);
        tv_yundan = findViewById(R.id.tv_yundan);
        tv_enter = findViewById(R.id.tv_enter);
        tvPackageCode = findViewById(R.id.tv_package_number);
        recyclerView = findViewById(R.id.rl_order_list);
        expressSelect = findViewById(R.id.tv_expressage_select);
        tv_enter_number = findViewById(R.id.tv_enter_number);
        tv_enter = findViewById(R.id.tv_enter);
        ivBack = findViewById(R.id.iv_back);
        initRecyclerView();    //初始化列表

    }

    private void initPermission() {
        //请求Camera权限 与 文件读写 权限
        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }

    private void initScanerAnimation() {
        ImageView mQrLineView = findViewById(R.id.capture_scan_line);
        RxAnimationTool.ScaleUpDowm(mQrLineView);
    }

    public int getCropWidth() {
        return mCropWidth;
    }

    public void setCropWidth(int cropWidth) {
        mCropWidth = cropWidth;
        CameraManager.FRAME_WIDTH = mCropWidth;
    }

    public int getCropHeight() {
        return mCropHeight;
    }

    public void setCropHeight(int cropHeight) {
        this.mCropHeight = cropHeight;
        CameraManager.FRAME_HEIGHT = mCropHeight;
    }


    private void initCamera(SurfaceHolder surfaceHolder) {
        try {
            CameraManager.get().openDriver(surfaceHolder);
            Point point = CameraManager.get().getCameraResolution();
            AtomicInteger width = new AtomicInteger(point.y);
            AtomicInteger height = new AtomicInteger(point.x);
            int cropWidth = mCropLayout.getWidth() * width.get() / mContainer.getWidth();
            int cropHeight = mCropLayout.getHeight() * height.get() / mContainer.getHeight();
            setCropWidth(cropWidth);
            setCropHeight(cropHeight);
        } catch (IOException | RuntimeException ioe) {
            return;
        }
        if (handler == null) {
            handler = new CaptureActivityHandler();
        }
    }


    //--------------------------------------打开本地图片识别二维码 start---------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            ContentResolver resolver = getContentResolver();
            // 照片的原始资源地址
            Uri originalUri = data.getData();
            try {
                // 使用ContentProvider通过URI获取原始图片
                Bitmap photo = MediaStore.Images.Media.getBitmap(resolver, originalUri);

                // 开始对图像资源解码
                Result rawResult = RxQrBarTool.decodeFromPhoto(photo);
                if (rawResult != null) {
                    if (mScanerListener == null) {
                        initDialogResult(rawResult);
                    } else {
                        mScanerListener.onSuccess("From to Picture", rawResult);
                    }
                } else {
                    if (mScanerListener == null) {
                        RxToast.error("图片识别失败.");
                    } else {
                        mScanerListener.onFail("From to Picture", "图片识别失败");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    //========================================打开本地图片识别二维码 end=================================


    private void initDialogResult(Result result) {
        BarcodeFormat type = result.getBarcodeFormat();
        String realContent = result.getText();

        if (rxDialogSure == null) {
            //提示弹窗
            rxDialogSure = new RxDialogSure(mContext);
        }

        if (BarcodeFormat.QR_CODE.equals(type)) {
            rxDialogSure.setTitle("二维码扫描结果");
        } else if (BarcodeFormat.EAN_13.equals(type)) {
            rxDialogSure.setTitle("条形码扫描结果");
        } else {
            rxDialogSure.setTitle("扫描结果");
        }

        rxDialogSure.setContent(realContent);
        rxDialogSure.setSureListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rxDialogSure.cancel();
            }
        });

        rxDialogSure.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (handler != null) {
                    // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                    handler.sendEmptyMessage(R.id.restart_preview);
                }
            }
        });

        if (!rxDialogSure.isShowing()) {
            rxDialogSure.show();
        }

        RxSPTool.putContent(mContext, RxConstants.SP_SCAN_CODE, RxDataTool.stringToInt(RxSPTool.getContent(mContext, RxConstants.SP_SCAN_CODE)) + 1 + "");
    }

    public void handleDecode(Result result, String imagePath) {
        // inactivityTimer.onActivity();
        //扫描成功之后的振动与声音提示
     //   SoundHelper.getInstance().playNotifiSound();
        String result1 = result.getText();

        if (result1.length() < 7) {
            if (handler != null) {
                // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                handler.sendEmptyMessage(R.id.restart_preview);
            }
            return;
        }

        if (mList.size() == 30) {
            ToastUtil.showShort("超出数量限制！");
            if (handler != null) {
                // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                handler.sendEmptyMessage(R.id.restart_preview);
            }
            return;
        }

        if (isContain(result1)) {   //判断是否重复
            SoundHelper.getInstance().playNotifiRepeatSound();

            if (handler != null) {
                // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                handler.sendEmptyMessage(R.id.restart_preview);
            }
        } else {
            mTempList.add(result1);
            //上传图片
           // uploadImage(imagePath);
            checkWaybillState(wayNumber, "");
        }
    }

    private boolean isContain(String resultCode) {
        for (String data : mTempList) {
            if (resultCode.equals(data)) {
                return true;
            }
        }
        return false;
    }

    private void checkWaybillState(String number, String picturl) {
        if ("".equals(express_id)) {
            ToastUtil.showShort("请选择快递公司");
            return;
        }
        Request<String> request = NoHttpRequest.checkWaybillRequest(user_id, express_id, number, picturl);
        mRequestQueue.add(CHECK_WAY_BILL_STATE, request, mOnresponseListener);
    }


    private void uploadImage(String imagePath) {
        Request<String> request = NoHttpRequest.commitScanPictureRequest(imagePath);
        mRequestQueue.add(COMMIT_PICTURE_REQUEST, request, mOnresponseListener);
    }

    private OnResponseListener<String> mOnresponseListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {

        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "EnterManagerActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                handleResultForEnter(what, jsonObject);    //处理结果
            } catch (JSONException e) {
                e.printStackTrace();
                if (handler != null) {
                    // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                    handler.sendEmptyMessage(R.id.restart_preview);
                }
            }
        }

        @Override
        public void onFailed(int what, Response<String> response) {
            if (handler != null) {
                // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                handler.sendEmptyMessage(R.id.restart_preview);
            }
        }

        @Override
        public void onFinish(int what) {

        }
    };


    private void handleResultForEnter(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case CHECK_WAY_BILL_STATE:     //检测运单号
                checkWaybillStateResult(jsonObject);
                break;
            case COMMIT_PICTURE_REQUEST:    //上传图片
                commitPictureResult(jsonObject);
                break;
        }
    }

    private void commitPictureResult(JSONObject jsonObject) throws JSONException {
        JSONObject data = jsonObject.getJSONObject("data");
        String picurl = data.getString("picurl");
        //检测运单号
        checkWaybillState(wayNumber, picurl);
    }

    private void checkWaybillStateResult(JSONObject jsonObject) throws JSONException {
        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();
        if ("5001".equals(code)) {
            setListData(jsonObject);
            tagNumber++;
        } else if ("4004".equals(code)) {
            SoundHelper.getInstance().playEnterSound();
            resetScan(jsonObject, msg);
        }else if ("4003".equals(code)){
            SoundHelper.getInstance().playChangeSound();
            resetScan(jsonObject, msg);
        } else {
            resetScan(jsonObject, msg);
        }

        tv_enter_number.setText("(" + mList.size() + "/30)");
    }

    private void resetScan(JSONObject jsonObject, String msg) throws JSONException {
        JSONObject dataArray = jsonObject.getJSONObject("data");
        String resultCode = dataArray.getString("number");
        mTempList.remove(resultCode);
        ToastUtil.showShort(msg);

        if (handler != null) {
            // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
            handler.sendEmptyMessage(R.id.restart_preview);
        }
    }

    private void setListData(JSONObject jsonObject) throws JSONException {
        JSONObject dataArray = jsonObject.getJSONObject("data");
        String package_code = dataArray.getString("code");
        String mobile = dataArray.getString("mobile");
        String type = dataArray.getString("type");
        String name = dataArray.getString("name");
        String number = dataArray.getString("number");

        HashMap<String, String> map = new HashMap<>();
        int codeTag = IntegerUtil.getStringChangeToNumber(package_code);    //最新的数据库的提货码
        codeTag = codeTag + tagNumber;
        String currentData = DateUtil.getCurrentDay();
        String effectCode = StringUtil.getEffectCode(codeTag);
        String result = currentData +"0"+ effectCode;

        map.put("package_code", result);
        map.put("wail_number", number);
        map.put("express_name", mobile);
        map.put("type", type);
        map.put("name", name);

        tv_phone.setText(mobile);
        tvPackageCode.setText(result);
        tv_yundan.setText(number);
        currentMap = map;

        if ("".equals(mobile)) {
            rl_scan.setVisibility(View.GONE);
            SystemUtil.showKeyBoard(EnterManager_new_Activity.this, tv_phone);
            isAdd = true;
        } else {
            tv_phone.setText("");
            tvPackageCode.setText("");
            tv_yundan.setText("");
            rl_scan.setVisibility(View.VISIBLE);
            mList.add(0, map);
            mAdapter.notifyDataSetChanged();

            if (handler != null) {
                // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                handler.sendEmptyMessage(R.id.restart_preview);
            }
        }
    }

    //==============================================================================================解析结果 及 后续处理 end

    final class CaptureActivityHandler extends Handler {

        DecodeThread decodeThread = null;
        private State state;

        public CaptureActivityHandler() {
            decodeThread = new DecodeThread();
            decodeThread.start();
            state = State.SUCCESS;
            CameraManager.get().startPreview();
            restartPreviewAndDecode();
        }

        @Override
        public void handleMessage(Message message) {
            if (message.what == R.id.auto_focus) {
                if (state == State.PREVIEW) {
                    CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
                }
            } else if (message.what == R.id.restart_preview) {
                //显示扫描框
                rl_scan.setVisibility(View.VISIBLE);
                restartPreviewAndDecode();
            } else if (message.what == R.id.decode_succeeded) {
                state = State.SUCCESS;
                Result result = (Result) message.obj;
                Bitmap barcode_bitmap = (Bitmap) message.getData().get("barcode_bitmap");
                String imagePath = saveBitmap(result.getText(), barcode_bitmap);
                wayNumber = result.getText();

                handleDecode((Result) message.obj, imagePath);// 解析成功，回调
            } else if (message.what == R.id.decode_failed) {
                state = State.PREVIEW;
                CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
            }
        }

        public void quitSynchronously() {
            state = State.DONE;
            decodeThread.interrupt();
            CameraManager.get().stopPreview();
            removeMessages(R.id.decode_succeeded);
            removeMessages(R.id.decode_failed);
            removeMessages(R.id.decode);
            removeMessages(R.id.auto_focus);
        }

        private void restartPreviewAndDecode() {
            if (state == State.SUCCESS) {
                state = State.PREVIEW;
                CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            }
        }
    }

    final class DecodeThread extends Thread {

        private final CountDownLatch handlerInitLatch;
        private Handler handler;

        DecodeThread() {
            handlerInitLatch = new CountDownLatch(1);
        }

        Handler getHandler() {
            try {
                handlerInitLatch.await();
            } catch (InterruptedException ie) {
                // continue?
            }
            return handler;
        }

        @Override
        public void run() {
            Looper.prepare();
            handler = new DecodeHandler();
            handlerInitLatch.countDown();
            Looper.loop();
        }
    }

    final class DecodeHandler extends Handler {
        DecodeHandler() {
        }

        @Override
        public void handleMessage(Message message) {
            if (message.what == R.id.decode) {
                decode((byte[]) message.obj, message.arg1, message.arg2);
            } else if (message.what == R.id.quit) {
                Looper.myLooper().quit();
            }
        }
    }

    private MultiFormatReader multiFormatReader;

    private void decode(byte[] data, int width, int height) {
        long start = System.currentTimeMillis();
        Result rawResult = null;

        //modify here
        byte[] rotatedData = new byte[data.length];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                rotatedData[x * height + height - y - 1] = data[x + y * width];
            }
        }
        // Here we are swapping, that's the difference to #11
        int tmp = width;
        width = height;
        height = tmp;

        //    Bitmap phoneBitmap = getBitmap(data);


        PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(rotatedData, width, height);
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
        try {
            rawResult = multiFormatReader.decodeWithState(bitmap);
        } catch (ReaderException e) {
            // continue
        } finally {
            multiFormatReader.reset();
        }

        if (rawResult != null) {
            long end = System.currentTimeMillis();
            Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());
            Message message = Message.obtain(handler, R.id.decode_succeeded, rawResult);
            Bundle bundle = new Bundle();
            bundle.putParcelable("barcode_bitmap", source.renderCroppedGreyscaleBitmap());
            message.setData(bundle);
            //Log.d(TAG, "Sending decode succeeded message...");
            message.sendToTarget();
        } else {
            if (handler != null) {
                Message message = Message.obtain(handler, R.id.decode_failed);
                message.sendToTarget();
            }
        }
    }

    private String saveBitmap(String imageName, Bitmap bitmap) {
        File rootPath = new File(rootPaht);
        if (!rootPath.exists()) {
            rootPath.mkdirs();
        }
        String filePaht = "";
        File saveFile = new File(rootPath, imageName + ".jpg");
        FileOutputStream os = null;
        try {
            Log.d("FileCache", "Saving File To Cache " + saveFile.getPath());
            os = new FileOutputStream(saveFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            filePaht = saveFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return filePaht;
    }


    private enum State {
        //预览
        PREVIEW,
        //成功
        SUCCESS,
        //完成
        DONE
    }

}
