package com.mt.bbdj.community.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.king.zxing.CaptureActivity;
import com.king.zxing.util.ResizeAbleSurfaceView;
import com.kongzue.dialog.v2.WaitDialog;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.activity.ActivityBase;
import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.ExpressLogoDao;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.db.gen.WaillMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.RxBeepTool;
import com.mt.bbdj.baseconfig.utls.RxConstants;
import com.mt.bbdj.baseconfig.utls.RxDataTool;
import com.mt.bbdj.baseconfig.utls.RxDialogSure;
import com.mt.bbdj.baseconfig.utls.RxSPTool;
import com.mt.bbdj.baseconfig.utls.RxToast;
import com.mt.bbdj.baseconfig.utls.SoundHelper;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.mt.bbdj.baseconfig.view.MyDecoration;
import com.mt.bbdj.baseconfig.view.MyPopuwindow;
import com.mt.bbdj.baseconfig.view.RxAnimationTool;
import com.mt.bbdj.community.adapter.EnterManagerAdapter;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static android.content.ContentValues.TAG;


public class OutManager_new_Activity extends ActivityBase {

    private TextView tvPackageCode;     //提货码
    private TextView tvWailNumber;     //运单号
    private TextView expressSelect;    //快递公司选择
    private RecyclerView recyclerView;
    private RelativeLayout ivBack;    //返回
    private TextView tv_out_number;
    private TextView tvOut;    //出库
    private List<HashMap<String, String>> mList = new ArrayList<>();

    private boolean isContinuousScan = true;
    private EnterManagerAdapter mAdapter;
    private String user_id;
    private RequestQueue mRequestQueue;
    private int packageCode = 1060204;
    private WaitDialog dialogLoading;
    private List<String> mTempList = new ArrayList<>();//临时数据

    private MyPopuwindow popupWindow;

    private List<HashMap<String, String>> mFastData = new ArrayList<>();    //快递公司
    private ExpressLogoDao mExpressLogoDao;
    private String express_id;

    private final int CHECK_WAY_BILL_STATE = 300;    //检测运单号的状态
    private final int OUT_WAY_BILL_REQUEST = 400;    //出库

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



    /**
     * 设置扫描信息回调
     */
    public static void setScanerListener(OnRxScanerListener scanerListener) {
        mScanerListener = scanerListener;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_out_manager_new);
        initParams();

        //界面控件初始化
        initDecode();
        initView();
        initListener();
        //权限初始化
        initPermission();
        //扫描动画初始化
        initScanerAnimation();
        //初始化 CameraManager
        CameraManager.init(mContext);
        hasSurface = false;
     //   inactivityTimer = new InactivityTimer(this);

    }

    private void initParams() {
        DaoSession daoSession = GreenDaoManager.getInstance().getSession();
        UserBaseMessageDao mUserMessageDao = daoSession.getUserBaseMessageDao();
        mExpressLogoDao = daoSession.getExpressLogoDao();
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        mRequestQueue = NoHttp.newRequestQueue();
    }

    private void initView() {
        tvPackageCode = findViewById(R.id.tv_package_number);
        tvWailNumber = findViewById(R.id.tv_yundan);
        recyclerView = findViewById(R.id.rl_order_list);
        expressSelect = findViewById(R.id.tv_expressage_select);
        ivBack = findViewById(R.id.iv_back);
        tv_out_number = findViewById(R.id.tv_out_number);
        tvOut = findViewById(R.id.tv_out);
        initRecyclerView();    //初始化列表

        mIvLight = findViewById(R.id.top_mask);
        mContainer = findViewById(R.id.capture_containter);
        mCropLayout = findViewById(R.id.capture_crop_layout);

        tvPackageCode = findViewById(R.id.tv_package_number);
        tvWailNumber = findViewById(R.id.tv_yundan);
        recyclerView = findViewById(R.id.rl_order_list);
        expressSelect = findViewById(R.id.tv_expressage_select);


    }

    private void initRecyclerView() {
        recyclerView.setFocusable(false);
        //initTemparayData();   //模拟数据
        mAdapter = new EnterManagerAdapter(mList);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new MyDecoration(this, LinearLayoutManager.VERTICAL, Color.parseColor("#e9e9e9"), 1));
        recyclerView.setAdapter(mAdapter);
    }
    private void initListener() {
        //删除
        mAdapter.setDeleteClickListener(new EnterManagerAdapter.onDeleteClickListener() {
            @Override
            public void onDelete(int position) {
                HashMap<String,String> map = mList.get(position);
                String resultCode = map.get("wail_number");
                mList.remove(position);
                mTempList.remove(resultCode);
                tv_out_number.setText("(" + mList.size() + ")");
                mAdapter.notifyDataSetChanged();
                if (handler != null) {
                    // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                    handler.sendEmptyMessage(R.id.restart_preview);
                }
            }
        });

        //返回
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tvOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList.size() == 0) {
                    ToastUtil.showShort("没有出库数据！");
                    return;
                }
                tvOut.setEnabled(false);
                outOfrepertory();
            }
        });

    }


    private void outOfrepertory() {
        StringBuilder sb = new StringBuilder();
        for (HashMap<String,String> map : mList) {
            String pie_id = map.get("pie_id");
            sb.append(pie_id);
            sb.append(",");
        }
        String result = sb.toString();
        String effectionResult = result.substring(0,result.length()-1);
        Request<String> request = NoHttpRequest.outOfRepertoryRequest(user_id, effectionResult);
        mRequestQueue.add(OUT_WAY_BILL_REQUEST, request, new OnResponseListener<String>() {
            @Override
            public void onStart(int what) {
                dialogLoading = WaitDialog.show(OutManager_new_Activity.this, "出库中...").setCanCancel(true);

            }

            @Override
            public void onSucceed(int what, Response<String> response) {
                LogUtil.i("photoFile", "OutManagerActivity::" + response.get());
                try {
                    JSONObject jsonObject = new JSONObject(response.get());
                    outofRepertoryResult(jsonObject);    //处理结果
                } catch (JSONException e) {
                    e.printStackTrace();
                    tvOut.setEnabled(false);
                }
                 dialogLoading.doDismiss();
                tvOut.setEnabled(false);
            }

            @Override
            public void onFailed(int what, Response<String> response) {
                dialogLoading.doDismiss();
                tvOut.setEnabled(false);
            }

            @Override
            public void onFinish(int what) {
                dialogLoading.doDismiss();
                tvOut.setEnabled(false);
            }
        });
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
        ResizeAbleSurfaceView surfaceView = findViewById(R.id.capture_preview);
        int width = ScreenUtils.getScreenWidth(this);
        int height = ScreenUtils.dip2px(this,280);
        surfaceView.resize(width,height);
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
                restartPreviewAndDecode();
            } else if (message.what == R.id.decode_succeeded) {
                state = State.SUCCESS;
                handleDecode((Result) message.obj);// 解析成功，回调
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

    public void handleDecode(Result result) {
      //  inactivityTimer.onActivity();
        //扫描成功之后的振动与声音提示
        RxBeepTool.playBeep(mContext, vibrate);

        String result1 = result.getText();
        Log.v("二维码/条形码 扫描结果", result1);
        if (mScanerListener == null) {
            /*RxToast.success(result1);
            initDialogResult(result);*/

            if (isContain(result1)) {   //判断是否重复
                SoundHelper.getInstance().playNotifiRepeatSound();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (handler != null) {
                    // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
                    handler.sendEmptyMessage(R.id.restart_preview);
                }
            } else {
                mTempList.add(result1);
                checkWaybillState(result1);    //检测运单号的状态
            }
        } else {
            mScanerListener.onSuccess("From to Camera", result);
        }
    }

    private  boolean isContain(String resultCode) {
        for (String data : mTempList) {
            if (resultCode.equals(data)) {
                return true;
            }
        }
        return false;
    }

    private MultiFormatReader multiFormatReader;

    private enum State {
        //预览
        PREVIEW,
        //成功
        SUCCESS,
        //完成
        DONE
    }

    private void checkWaybillState(String number) {
        Request<String> request = NoHttpRequest.checkOutWailnumberStateRequest(user_id, number);
        mRequestQueue.add(CHECK_WAY_BILL_STATE, request, mOnresponseListener);
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
                handleResultForOut(what, jsonObject);    //处理结果
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
    };

    private void handleResultForOut(int what, JSONObject jsonObject) throws JSONException {
        switch (what) {
            case CHECK_WAY_BILL_STATE:    //运单状态
                CheckWaybillResult(jsonObject);
                break;

        }
    }

    private void outofRepertoryResult(JSONObject jsonObject) throws JSONException {
        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();

        if ("5001".equals(code)) {
            mList.clear();
            mAdapter.notifyDataSetChanged();
        }
        ToastUtil.showShort(msg);
        tv_out_number.setText("(" + mList.size() + ")");
    }

    private void CheckWaybillResult(JSONObject jsonObject) throws JSONException {

        String code = jsonObject.get("code").toString();
        String msg = jsonObject.get("msg").toString();

        if ("5001".equals(code)) {
            JSONObject dataObj = jsonObject.getJSONObject("data");
            String package_code = dataObj.getString("code");
            String express = dataObj.getString("express");
            String pie_id = dataObj.getString("pie_id");
            String number = dataObj.getString("number");

            HashMap<String, String> map = new HashMap<>();
            map.put("package_code", package_code);
            map.put("wail_number", number);
            map.put("express_name", express);
            map.put("pie_id", pie_id);
            mList.add(0, map);
            map = null;
            tvWailNumber.setText(number);
            tvPackageCode.setText(package_code);
            mAdapter.notifyDataSetChanged();
        } else {
            JSONObject dataArray = jsonObject.getJSONObject("data");
            String resultCode = dataArray.getString("number");
            mTempList.remove(resultCode);
            ToastUtil.showShort(msg);
        }
        if (handler != null) {
            // 连续扫描，不发送此消息扫描一次结束后就不能再次扫描
            handler.sendEmptyMessage(R.id.restart_preview);
        }
        tv_out_number.setText("(" + mList.size() + ")");
    }


}
