package com.mt.bbdj.community.activity;

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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.bumptech.glide.Glide;
import com.mt.bbdj.R;

import com.mt.bbdj.baseconfig.db.UserBaseMessage;
import com.mt.bbdj.baseconfig.db.gen.DaoSession;
import com.mt.bbdj.baseconfig.db.gen.UserBaseMessageDao;
import com.mt.bbdj.baseconfig.internet.NoHttpRequest;
import com.mt.bbdj.baseconfig.utls.GreenDaoManager;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.wildma.idcardcamera.camera.CameraActivity.REQUEST_CODE;
import static com.wildma.idcardcamera.camera.CameraActivity.RESULT_CODE;


public class IdentificationActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;    //返回
    @BindView(R.id.ic_id_front)
    ImageView icIdFront;    //正面照
    @BindView(R.id.iv_add_front)
    ImageView ivAddFront;
    @BindView(R.id.ic_id_back)
    ImageView icIdBack;    //反面照
    @BindView(R.id.iv_add_back)
    ImageView ivAddBack;
    @BindView(R.id.bt_commit_next)
    Button btCommitNext;   //提交
    @BindView(R.id.rl_address_title)
    RelativeLayout rlAddressTitle;
    @BindView(R.id.tv_front_title)
    TextView tvFrontTitle;
    @BindView(R.id.tv_back_title)
    TextView tvBackTitle;
    @BindView(R.id.tv_person_name)
    EditText tvPersonName;
    @BindView(R.id.tv_person_number)
    EditText tvPersonNumber;
    private RequestQueue mRequestQueue;    //请求队列

    private SharedPreferences.Editor mEditor;
    private SharedPreferences mSharedPreferences;
    private HkDialogLoading dialogLoading;

    private PopupWindow popupWindow;
    private View selectView;

    private String picturePath = "/bbdj/picture";
    private File f = new File(Environment.getExternalStorageDirectory(), picturePath);
    private File photoFile;
    private File compressPicture;
    public static final String IMAGE_UNSPECIFIED = "image/*";
    private int clickType = 0;    //图片类型
    private String pictureType;    //图片名称

    public static final int PHOTOHRAPH = 1;// 拍照
    private static final int REQUEST_CODE_SYSTEM = 200;    //系统相机
    private static final int PHOTORESOULT = 1;    //结果处理

    private static final int REQUEST_COMMIT_PICTURE = 101;    //上传图片
    private static final int REQUEST_COMMIT_IDMESSAGE = 102;   //上传所有认证信息

    private ImageView[] imageViews = new ImageView[2];

    private AlertDialog.Builder alertDialog;
    private DaoSession mDaoSession;
    private UserBaseMessageDao mUserMessageDao;
    private String book_id;     //地址id
    private String mail_id;     //订单id

    private boolean isApp = false;    //身份验证两种来源 false : 寄件管理  true : 手动寄件

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_identification);
        ButterKnife.bind(this);

        initData();
    }

    private void initData() {
        //初始化图片选择的弹出框
        initPictureSelectPop();

        initShowPictureParams();

        Intent intent = getIntent();
        book_id = intent.getStringExtra("book_id");

        isApp = intent.getBooleanExtra("come_type", false);
        mail_id = intent.getStringExtra("mail_id");

        if (!isApp) {
            String send_name = intent.getStringExtra("send_name");
            tvPersonName.setText(send_name);
            tvPersonName.setFocusableInTouchMode(false);
        }


        //初始化请求队列
        mRequestQueue = NoHttp.newRequestQueue();

        mEditor = SharedPreferencesUtil.getEditor();

        mSharedPreferences = SharedPreferencesUtil.getSharedPreference();

        dialogLoading = new HkDialogLoading(IdentificationActivity.this, "提交中...");

        alertDialog = new AlertDialog.Builder(this);

        mDaoSession = GreenDaoManager.getInstance().getSession();
        mUserMessageDao = mDaoSession.getUserBaseMessageDao();
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
          /*  case PHOTOHRAPH:
                takePictureByCamera();     //拍照
                break;*/
            case REQUEST_CODE_SYSTEM:   //系统相机
                takePictureBySystem(data);
                break;
            case PHOTORESOULT:    //结果处理
                handleResult(data);
                break;
            case REQUEST_CODE:      //身份证
                handleIdcardPicture(data);
                break;
        }
    }

    private void handleIdcardPicture(Intent data) {
        final String filePath = CameraActivity.getImagePath(data);
        if (clickType == 0) {    //正面照
            if (!TextUtils.isEmpty(filePath)) {
                setPictureIntoView(filePath, 0);
                recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
            }
        } else {   //反面照
            if (!TextUtils.isEmpty(filePath)) {
                setPictureIntoView(filePath, 1);
                recIDCard(IDCardParams.ID_CARD_SIDE_BACK, filePath);
            }
        }
        uploadPicture(filePath);    //上传图片
    }

    private void handleResult(Intent data) {
        if (data == null) {
            return;
        }
        String filePath = Durban.parseResult(data).get(0);
        if (clickType == 0) {    //正面
            setPictureIntoView(filePath, 0);   
            recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
        } else {    //反面
            setPictureIntoView(filePath, 1);
            recIDCard(IDCardParams.ID_CARD_SIDE_BACK, filePath);
        }
        uploadPicture(filePath);    //上传图片
    }

    private void uploadPicture(String filePath) {
        if (!new File(filePath).exists()) {
            ToastUtil.showShort("文件不存在，请重拍！");
            return;
        }
        Request<String> request = NoHttpRequest.commitPictureRequest(filePath);
        mRequestQueue.add(REQUEST_COMMIT_PICTURE, request, onResultListener);
    }

    private OnResponseListener<String> onResultListener = new OnResponseListener<String>() {
        @Override
        public void onStart(int what) {
            dialogLoading.show();
        }

        @Override
        public void onSucceed(int what, Response<String> response) {
            LogUtil.i("photoFile", "IdentificationActivity::" + response.get());
            try {
                JSONObject jsonObject = new JSONObject(response.get());
                String code = jsonObject.get("code").toString();
                if ("5001".equals(code)) {
                    switch (what) {
                        case REQUEST_COMMIT_PICTURE:
                            handleRequestData(jsonObject);
                            break;
                        case REQUEST_COMMIT_IDMESSAGE:
                            ToastUtil.showShort("提交成功！");
                            setResult(RESULT_OK);
                            finish();
                            break;
                    }

                } else {
                    ToastUtil.showShort("认证失败，请重试！");
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
    };

    private void handleRequestData(JSONObject jsonObject) throws JSONException {
        JSONObject dataObject = jsonObject.getJSONObject("data");
        String pictureUrl = dataObject.getString("picurl");
        String message = jsonObject.get("msg").toString();
        mEditor.putString(pictureType, pictureUrl);
        mEditor.commit();
    }

    private void setPictureIntoView(String filePath, int type) {
        Glide.with(this)
                .load(filePath)
                .fitCenter()
                .into(type == 1 ? icIdBack : icIdFront);
        if (1 == type) {
            ivAddBack.setVisibility(View.GONE);
            tvBackTitle.setVisibility(View.GONE);
        } else {
            ivAddFront.setVisibility(View.GONE);
            tvFrontTitle.setVisibility(View.GONE);
        }
    }

    private void takePictureByCamera() {
        Durban.with(IdentificationActivity.this)
                .requestCode(PHOTORESOULT)
                .statusBarColor(ContextCompat.getColor(IdentificationActivity.this, R.color.colorPrimary))
                .toolBarColor(ContextCompat.getColor(IdentificationActivity.this, R.color.colorPrimary))
                .navigationBarColor(ContextCompat.getColor(IdentificationActivity.this, R.color.colorPrimary))
                .maxWidthHeight(2019, 1275)
                .outputDirectory(compressPicture.getPath())
                .inputImagePaths(Uri.fromFile(photoFile).getPath())
                .aspectRatio(1346, 850)
                .start();
    }

    private void takePictureBySystem(Intent data) {
        String pickPath = MiPictureHelper.getPath(IdentificationActivity.this, data.getData());
        Durban.with(IdentificationActivity.this)
                .requestCode(PHOTORESOULT)
                .statusBarColor(ContextCompat.getColor(IdentificationActivity.this, R.color.colorPrimary))
                .toolBarColor(ContextCompat.getColor(IdentificationActivity.this, R.color.colorPrimary))
                .navigationBarColor(ContextCompat.getColor(IdentificationActivity.this, R.color.colorPrimary))
                .maxWidthHeight(2019, 1275)
                .outputDirectory(compressPicture.getPath())
                .inputImagePaths(pickPath)
                .aspectRatio(1346, 850)
                .start();
    }


    private void recIDCard(final String idCardSide, String filePath) {
        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        // 设置身份证正反面
        param.setIdCardSide(idCardSide);
        // 设置方向检测
        param.setDetectDirection(true);
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.setImageQuality(20);

        OCR.getInstance(this).recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                if (result != null) {
                    if (IDCardParams.ID_CARD_SIDE_FRONT.equals(idCardSide)) {
                        String personName = result.getName().getWords();
                        String personNumber = result.getIdNumber().getWords();
                        if ("".equals(personName) || "".equals(personNumber)) {
                            ToastUtil.showShort("请拍摄清晰的身份证正面照！");
                            ivAddFront.setVisibility(View.VISIBLE);
                            tvFrontTitle.setVisibility(View.VISIBLE);
                            icIdFront.setImageBitmap(null);
                            mEditor.putString(pictureType, "");
                            mEditor.commit();
                            return;
                        } else {
                            tvPersonName.setText(personName);
                            tvPersonNumber.setText(personNumber);
                        }
                    } else {
                        String signDate = result.getIssueAuthority().getWords();
                       /* if ("".equals(signDate)) {
                            ToastUtil.showShort("请拍摄清晰的身份证背面照！");
                            ivAddBack.setVisibility(View.VISIBLE);
                            tvBackTitle.setVisibility(View.VISIBLE);
                            icIdBack.setImageBitmap(null);
                            mEditor.putString(pictureType, "");
                            mEditor.commit();
                            return;
                        }*/
                    }
                }
            }

            @Override
            public void onError(OCRError error) {
                alertText("", error.getMessage());
            }
        });
    }

    private void alertText(final String title, final String message) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                alertDialog.setTitle(title)
                        .setMessage(message)
                        .setPositiveButton("确定", null)
                        .show();
            }
        });
    }

    private void takePicture() {
        //判断SD卡是否可用
      /*  if (SystemUtil.hasSdcard()) {
            if (!f.exists()) {
                f.mkdirs();
            }
           *//* String uuid = UUID.randomUUID().toString();
            String path2 = uuid + ".jpg";
            photoFile = new File(f, path2);
            compressPicture = new File(f, uuid);
            Uri photoURI = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", photoFile);
            Intent intent = new Intent();

            intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);

            startActivityForResult(intent, PHOTOHRAPH);*//*
            if (clickType == 0) {
                CameraActivity.toCameraActivity(this, TYPE_IDCARD_FRONT);
            } else {
                CameraActivity.toCameraActivity(this, CameraActivity.TYPE_IDCARD_BACK);
            }
            popupWindow.dismiss();
        }*/
        if (clickType == 0) {
            CameraActivity.toCameraActivity(this, CameraActivity.TYPE_IDCARD_FRONT);
        } else {
            CameraActivity.toCameraActivity(this, CameraActivity.TYPE_IDCARD_BACK);
        }
        popupWindow.dismiss();
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


    private void initPictureSelectPop() {
        initPopuStyle();    //初始化popuwindow的样式
    }

    private void initShowPictureParams() {
        imageViews[0] = icIdFront;
        imageViews[1] = icIdBack;
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


    @OnClick({R.id.iv_back, R.id.ic_id_front, R.id.ic_id_back, R.id.bt_commit_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ic_id_front:
                clickType = 0;
                pictureType = "just_card";
                applyCameraPermission();
                break;
            case R.id.ic_id_back:
                clickType = 1;
                pictureType = "back_card";
                applyCameraPermission();
                break;
            case R.id.bt_commit_next:
                commitData();
                break;
        }
    }

    private void commitData() {
        String user_id = "";
        List<UserBaseMessage> list = mUserMessageDao.queryBuilder().list();
        if (list != null && list.size() != 0) {
            user_id = list.get(0).getUser_id();
        }
        String personName = tvPersonName.getText().toString();
        String personNumber = tvPersonNumber.getText().toString();
        String just_card = mSharedPreferences.getString("just_card", "");
        String back_card = mSharedPreferences.getString("back_card", "");

        if (!isRightAboutMessage(personName, personNumber)) {
            return;
        }
        Request<String> request = null;
        if (isApp) { //手动寄件
            request = NoHttpRequest.commitIdentification(user_id, book_id, personName, personNumber,
                    just_card, back_card);
        } else {   //寄件管理
            request = NoHttpRequest.commitIdentificationForManager(user_id,mail_id, personName, personNumber,
                    just_card, back_card);
        }
        mRequestQueue.add(REQUEST_COMMIT_IDMESSAGE, request, onResultListener);
    }

    private boolean isRightAboutMessage(String personName, String personNumber) {
        if ("".equals(personName) || "".equals(personNumber)) {
            ToastUtil.showShort("请完善认证信息！");
            return false;
        }
        if (!StringUtil.isID(personNumber)) {
            ToastUtil.showShort("身份证号不合法！");
            return false;
        }
        return true;
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

    private void showSelectDialog() {
        if (popupWindow != null && !popupWindow.isShowing()) {
            popupWindow.showAtLocation(ivBack, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
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
