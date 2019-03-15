package com.mt.bbdj.baseconfig.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
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

import com.baidu.ocr.sdk.model.IDCardParams;
import com.bumptech.glide.Glide;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.model.DestroyEvent;
import com.mt.bbdj.baseconfig.utls.HkDialogLoading;
import com.mt.bbdj.baseconfig.utls.LogUtil;
import com.mt.bbdj.baseconfig.utls.MiPictureHelper;
import com.mt.bbdj.baseconfig.utls.SharedPreferencesUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.SystemUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
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
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @BindView(R.id.et_main_register_username)
    EditText mRegisterUsername;              //用户名
    @BindView(R.id.et_main_register_idnumber)
    EditText mRegisterIdnumber;              //身份证号


    public static final int PHOTOHRAPH = 300;// 拍照
    private static final int REQUEST_CODE_SYSTEM = 200;    //系统相机
    private static final int PHOTORESOULT = 1;    //结果处理


    private PopupWindow popupWindow;
    private View selectView;

    private String picturePath = "/bbdj/picture";
    private File f = new File(Environment.getExternalStorageDirectory(), picturePath);
    private File photoFile;
    private File compressPicture;
    public static final String IMAGE_UNSPECIFIED = "image/*";
    private int clickType = 0;    //图片类型
    private String pictureType;    //图片名称

    private ImageView[] imageViews = new ImageView[3];

    private RequestQueue mRequestQueue;    //请求队列

    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharedPreferences;
    private HkDialogLoading dialogLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_complete);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
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
            R.id.ic_register_id_licence_fl, R.id.bt_register_complete})
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
        }
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

    private void takePictureByCamera() {
        Durban.with(RegisterCompleteActivity.this)
                .requestCode(PHOTORESOULT)
                .statusBarColor(ContextCompat.getColor(RegisterCompleteActivity.this, R.color.colorPrimary))
                .toolBarColor(ContextCompat.getColor(RegisterCompleteActivity.this, R.color.colorPrimary))
                .navigationBarColor(ContextCompat.getColor(RegisterCompleteActivity.this, R.color.colorPrimary))
                .maxWidthHeight(2019, 1275)
                .outputDirectory(compressPicture.getPath())
                .inputImagePaths(Uri.fromFile(photoFile).getPath())
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
                        String pictureUrl = dataObject.getString("picu rl");
                        String message = jsonObject.get("msg").toString();
                        mEditor.putString(pictureType, pictureUrl);
                        mEditor.commit();
                    } else {
                        ToastUtil.showShort("账号或者密码错误，请重试！");
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
        Request<String> request = NoHttpRequest.commitRegisterRequest(phone, password, realname, idcard, just_card, back_card, license,businessNumber);
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
        String frontIDImg = mSharedPreferences.getString("just_card", "");
        String backIDImg = mSharedPreferences.getString("back_card", "");
        if ("".equals(userName) || "".equals(frontIDImg) || "".equals(backIDImg)) {
            ToastUtil.showShort("请完善注册信息！");
            return false;
        }
        if (!StringUtil.isIDNumber(idNumber)) {
            ToastUtil.showShort("身份证不合法！");
            return false;
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
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

}
