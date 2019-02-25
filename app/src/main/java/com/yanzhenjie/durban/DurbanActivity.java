/*
 * Copyright © Yan Zhenjie
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.yanzhenjie.durban;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.model.ImageCutEvent;
import com.yanzhenjie.durban.callback.BitmapCropCallback;
import com.yanzhenjie.durban.util.DurbanUtils;
import com.yanzhenjie.durban.view.CropView;
import com.yanzhenjie.durban.view.GestureCropImageView;
import com.yanzhenjie.durban.view.OverlayView;
import com.yanzhenjie.durban.view.TransformImageView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Update by Yan Zhenjie on 2017/5/23.
 */
public class DurbanActivity extends AppCompatActivity {

    private static final int PERMISSION_CODE_STORAGE = 1;

    private int mStatusColor;
    private int mNavigationColor;
    private int mToolbarColor;
    private String mTitle;

    private boolean mRotateSupport;    //是否支持裁剪区旋转
    private String mContextName;


    private int mGesture;
    private float[] mAspectRatio;
    private int[] mMaxWidthHeight;

    private Bitmap.CompressFormat mCompressFormat;
    private int mCompressQuality;

    private String mOutputDirectory;
    private ArrayList<String> mInputPathList;

    private Controller mController;

    private CropView mCropView;
    private GestureCropImageView mCropImageView;

    private ArrayList<String> mOutputPathList;

    private Intent mIntent = null;

    private Intent mRotateIntent = null;    //用来实现裁剪区域的旋转

    private final int CHANGE_ROTATE_ORITATION = 100;

    private  boolean rotateChange = false;    //表示进入的方式 true : 本界面跳转到本界面， false : 其他界面跳转到本界面

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Locale locale = Durban.getDurbanConfig().getLocale();
        DurbanUtils.applyLanguageForContext(this, locale);

        setContentView(R.layout.durban_activity_photobox);
        mIntent = getIntent();
        initArgument(mIntent);
        initFrameViews();
        initContentViews();
        initControllerViews();
        cropNextImage();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mCropImageView != null) mCropImageView.cancelAllAnimations();
    }

    private void initArgument(Intent intent) {
        rotateChange = intent.getBooleanExtra(Durban.KEY_INTO_TYPE,false);
        mRotateIntent = new Intent(this, DurbanActivity.class);
        mStatusColor = ContextCompat.getColor(this, R.color.durban_ColorPrimaryDark);
        mToolbarColor = ContextCompat.getColor(this, R.color.durban_ColorPrimary);
        mNavigationColor = ContextCompat.getColor(this, R.color.durban_ColorPrimaryBlack);

        mRotateSupport = intent.getBooleanExtra(Durban.KEY_ROTATE_SUPPORT, false);
        mContextName = intent.getStringExtra(Durban.KEY_ROTATE_CONTEXT_NAME);

        mStatusColor = intent.getIntExtra(Durban.KEY_INPUT_STATUS_COLOR, mStatusColor);
        mToolbarColor = intent.getIntExtra(Durban.KEY_INPUT_TOOLBAR_COLOR, mToolbarColor);
        mNavigationColor = intent.getIntExtra(Durban.KEY_INPUT_NAVIGATION_COLOR, mNavigationColor);
        mTitle = intent.getStringExtra(Durban.KEY_INPUT_TITLE);
        if (TextUtils.isEmpty(mTitle)) mTitle = getString(R.string.durban_title_crop);

        mGesture = intent.getIntExtra(Durban.KEY_INPUT_GESTURE, Durban.GESTURE_ALL);
        mAspectRatio = intent.getFloatArrayExtra(Durban.KEY_INPUT_ASPECT_RATIO);
        if (mAspectRatio == null) mAspectRatio = new float[]{0, 0};
        mMaxWidthHeight = intent.getIntArrayExtra(Durban.KEY_INPUT_MAX_WIDTH_HEIGHT);
        if (mMaxWidthHeight == null) mMaxWidthHeight = new int[]{500, 500};

        //noinspection JavacQuirks
        int compressFormat = intent.getIntExtra(Durban.KEY_INPUT_COMPRESS_FORMAT, 0);
        mCompressFormat = compressFormat == Durban.COMPRESS_PNG ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG;
        mCompressQuality = intent.getIntExtra(Durban.KEY_INPUT_COMPRESS_QUALITY, 90);

        mOutputDirectory = intent.getStringExtra(Durban.KEY_INPUT_DIRECTORY);
        if (TextUtils.isEmpty(mOutputDirectory)) mOutputDirectory = getFilesDir().getAbsolutePath();
        mInputPathList = intent.getStringArrayListExtra(Durban.KEY_INPUT_PATH_ARRAY);

        mController = intent.getParcelableExtra(Durban.KEY_INPUT_CONTROLLER);
        if (mController == null) mController = Controller.newBuilder().build();

        mOutputPathList = new ArrayList<>();

        initRotateIntentParams();    //旋转裁剪区域，设置参数
    }

    private void initRotateIntentParams() {
        mRotateIntent.putExtra(Durban.KEY_ROTATE_SUPPORT, mRotateSupport);
        mRotateIntent.putExtra(Durban.KEY_ROTATE_CONTEXT_NAME, mContextName);
        mRotateIntent.putExtra(Durban.KEY_INPUT_STATUS_COLOR, mStatusColor);
        mRotateIntent.putExtra(Durban.KEY_INPUT_TOOLBAR_COLOR, mToolbarColor);
        mRotateIntent.putExtra(Durban.KEY_INPUT_NAVIGATION_COLOR, mNavigationColor);
        mRotateIntent.putExtra(Durban.KEY_INPUT_TITLE, mTitle);
        mRotateIntent.putExtra(Durban.KEY_INPUT_GESTURE, mGesture);
        mRotateIntent.putExtra(Durban.KEY_INPUT_ASPECT_RATIO, mAspectRatio);
        mRotateIntent.putExtra(Durban.KEY_INPUT_MAX_WIDTH_HEIGHT, mMaxWidthHeight);
        mRotateIntent.putExtra(Durban.KEY_INPUT_COMPRESS_FORMAT, mCompressFormat);
        mRotateIntent.putExtra(Durban.KEY_INPUT_COMPRESS_QUALITY, mCompressQuality);
        mRotateIntent.putExtra(Durban.KEY_INPUT_DIRECTORY, mOutputDirectory);
        ArrayList<String> mRotateInputList = new ArrayList<>();
        mRotateInputList.addAll(mInputPathList);
        mRotateIntent.putExtra(Durban.KEY_INPUT_PATH_ARRAY, mRotateInputList);
        mRotateIntent.putExtra(Durban.KEY_INPUT_CONTROLLER, mController);
    }

    private void initFrameViews() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            final Window window = getWindow();
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(mStatusColor);
                window.setNavigationBarColor(mNavigationColor);
            }
        }

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(mToolbarColor);
        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(mTitle);

    }

    private void initContentViews() {
        mCropView = (CropView) findViewById(R.id.crop_view);
        mCropImageView = mCropView.getCropImageView();
        mCropImageView.setOutputDirectory(mOutputDirectory);
        mCropImageView.setTransformImageListener(mImageListener);
        mCropImageView.setScaleEnabled(mGesture == Durban.GESTURE_ALL || mGesture == Durban.GESTURE_SCALE);
        mCropImageView.setRotateEnabled(mGesture == Durban.GESTURE_ALL || mGesture == Durban.GESTURE_ROTATE);
        // Durban image view options
        mCropImageView.setMaxBitmapSize(GestureCropImageView.DEFAULT_MAX_BITMAP_SIZE);
        mCropImageView.setMaxScaleMultiplier(GestureCropImageView.DEFAULT_MAX_SCALE_MULTIPLIER);
        mCropImageView.setImageToWrapCropBoundsAnimDuration(GestureCropImageView.DEFAULT_IMAGE_TO_CROP_BOUNDS_ANIM_DURATION);

        // Overlay view options
        OverlayView overlayView = mCropView.getOverlayView();
        overlayView.setFreestyleCropMode(OverlayView.FREESTYLE_CROP_MODE_DISABLE);
        overlayView.setDimmedColor(ContextCompat.getColor(this, R.color.durban_CropDimmed));
        overlayView.setCircleDimmedLayer(false);
        overlayView.setShowCropFrame(true);
        overlayView.setCropFrameColor(ContextCompat.getColor(this, R.color.durban_CropFrameLine));
        overlayView.setCropFrameStrokeWidth(getResources().getDimensionPixelSize(R.dimen.durban_dp_1));
        overlayView.setShowCropGrid(true);
        overlayView.setCropGridRowCount(2);
        overlayView.setCropGridColumnCount(2);
        overlayView.setCropGridColor(ContextCompat.getColor(this, R.color.durban_CropGridLine));
        overlayView.setCropGridStrokeWidth(getResources().getDimensionPixelSize(R.dimen.durban_dp_1));

        // Aspect ratio options
        if (mAspectRatio[0] > 0 && mAspectRatio[1] > 0)
            mCropImageView.setTargetAspectRatio(mAspectRatio[0] / mAspectRatio[1]);
        else mCropImageView.setTargetAspectRatio(GestureCropImageView.SOURCE_IMAGE_ASPECT_RATIO);

        // Result exception max size options
        if (mMaxWidthHeight[0] > 0 && mMaxWidthHeight[1] > 0) {
            mCropImageView.setMaxResultImageSizeX(mMaxWidthHeight[0]);
            mCropImageView.setMaxResultImageSizeY(mMaxWidthHeight[1]);
        }

    }

    private TransformImageView.TransformImageListener mImageListener = new TransformImageView.TransformImageListener() {
        @Override
        public void onRotate(float currentAngle) {
        }

        @Override
        public void onScale(float currentScale) {
        }

        @Override
        public void onLoadComplete() {
            ViewCompat.animate(mCropView)
                    .alpha(1)
                    .setDuration(300)
                    .setInterpolator(new AccelerateInterpolator());
        }

        @Override
        public void onLoadFailure() {
            cropNextImage();
        }
    };

    private void initControllerViews() {
        View controllerRoot = findViewById(R.id.iv_controller_root);

        View rotationTitle = findViewById(R.id.tv_controller_title_rotation);
        View rotationLeft = findViewById(R.id.layout_controller_rotation_left);
        View rotationRight = findViewById(R.id.layout_controller_rotation_right);
        View scaleTitle = findViewById(R.id.tv_controller_title_scale);
        View scaleBig = findViewById(R.id.layout_controller_scale_big);
        View scaleSmall = findViewById(R.id.layout_controller_scale_small);
        View rotateChange = findViewById(R.id.iv_controller_rotate_change);   //裁剪界面旋转

        controllerRoot.setVisibility(mController.isEnable() ? View.VISIBLE : View.GONE);

        // rotationTitle.setVisibility(mController.isRotationTitle() ? View.VISIBLE : View.INVISIBLE);
        rotationLeft.setVisibility(mController.isRotation() ? View.VISIBLE : View.GONE);
        rotationRight.setVisibility(mController.isRotation() ? View.VISIBLE : View.GONE);
        // scaleTitle.setVisibility(mController.isScaleTitle() ? View.VISIBLE : View.INVISIBLE);

        scaleBig.setVisibility(mController.isScale() ? View.VISIBLE : View.GONE);
        scaleSmall.setVisibility(mController.isScale() ? View.VISIBLE : View.GONE);

        //裁剪区域旋转
        rotateChange.setVisibility(mRotateSupport ? View.VISIBLE : View.GONE);
        rotateChange.setOnClickListener(mControllerClick);

        if (!mController.isRotationTitle() && !mController.isScaleTitle())
            findViewById(R.id.layout_controller_title_root).setVisibility(View.GONE);
        if (!mController.isRotation())
            rotationTitle.setVisibility(View.GONE);
        if (!mController.isScale())
            scaleTitle.setVisibility(View.GONE);

        rotationLeft.setOnClickListener(mControllerClick);
        rotationRight.setOnClickListener(mControllerClick);
        scaleBig.setOnClickListener(mControllerClick);
        scaleSmall.setOnClickListener(mControllerClick);
    }

    private View.OnClickListener mControllerClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = v.getId();
            if (id == R.id.layout_controller_rotation_left) {
                mCropImageView.postRotate(-90);
                mCropImageView.setImageToWrapCropBounds();
            } else if (id == R.id.layout_controller_rotation_right) {
                mCropImageView.postRotate(90);
                mCropImageView.setImageToWrapCropBounds();
            } else if (id == R.id.layout_controller_scale_big) {
                mCropImageView.zoomOutImage(mCropImageView.getCurrentScale()
                        + ((mCropImageView.getMaxScale() - mCropImageView.getMinScale()) / 10));
                mCropImageView.setImageToWrapCropBounds();
            } else if (id == R.id.layout_controller_scale_small) {
                mCropImageView.zoomInImage(mCropImageView.getCurrentScale()
                        - ((mCropImageView.getMaxScale() - mCropImageView.getMinScale()) / 10));
                mCropImageView.setImageToWrapCropBounds();
            } else if (id == R.id.iv_controller_rotate_change){
                int[] wh = {mMaxWidthHeight[1], mMaxWidthHeight[0]};
                float[] ar = {mAspectRatio[1],mAspectRatio[0]};
                mRotateIntent.putExtra(Durban.KEY_INPUT_ASPECT_RATIO, ar);
                mRotateIntent.putExtra(Durban.KEY_INPUT_MAX_WIDTH_HEIGHT, wh);
                mRotateIntent.putExtra(Durban.KEY_INTO_TYPE,true);
                startActivity(mRotateIntent);
                finish();
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHANGE_ROTATE_ORITATION) {
            setResult(resultCode, data);
        }
        DurbanActivity.this.finish();
    }

    /**
     * Start cropping and request permission if there is no permission.
     */
    private void cropNextImage() {
        resetRotation();
        requestStoragePermission(PERMISSION_CODE_STORAGE);
    }

    /**
     * Restore the rotation angle.
     */
    private void resetRotation() {
        mCropImageView.postRotate(-mCropImageView.getCurrentAngle());
        mCropImageView.setImageToWrapCropBounds();
    }

    private void requestStoragePermission(int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permissionResult = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (permissionResult == PackageManager.PERMISSION_GRANTED) {
                onRequestPermissionsResult(
                        requestCode,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        new int[]{PackageManager.PERMISSION_GRANTED});
            } else {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        requestCode);
            }
        } else {
            onRequestPermissionsResult(
                    requestCode,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    new int[]{PackageManager.PERMISSION_GRANTED});
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    cropNextImageWithPermission();
                } else {
                    Log.e("Durban", "Storage device permission is denied.");
                    setResultFailure();
                }
                break;
            }
        }
    }

    private void cropNextImageWithPermission() {
        if (mInputPathList != null) {
            if (mInputPathList.size() > 0) {
                String currentPath = mInputPathList.remove(0);
                try {
                    mCropImageView.setImagePath(currentPath);
                } catch (Exception e) {
                    cropNextImage();
                }
            } else if (mOutputPathList.size() > 0) setResultSuccessful();
            else setResultFailure();
        } else {
            Log.e("Durban", "The file list is empty.");
            setResultFailure();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(final Menu menu) {
        getMenuInflater().inflate(R.menu.durban_menu_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_action_ok) {
            cropAndSaveImage();
        } else if (item.getItemId() == android.R.id.home) {
            setResultFailure();
        }
        return true;
    }

    private void cropAndSaveImage() {
        mCropImageView.cropAndSaveImage(mCompressFormat, mCompressQuality, cropCallback);
    }

    private BitmapCropCallback cropCallback = new BitmapCropCallback() {
        @Override
        public void onBitmapCropped(@NonNull String imagePath, int imageWidth, int imageHeight) {
            mOutputPathList.add(imagePath);
            cropNextImage();
        }

        @Override
        public void onCropFailure(@NonNull Throwable t) {
            cropNextImage();
        }
    };

    private void setResultSuccessful() {
        if (rotateChange) {
            EventBus.getDefault().post(new ImageCutEvent(mOutputPathList,mContextName));
        } else {
            Intent intent = new Intent();
            intent.putStringArrayListExtra(Durban.KEY_OUTPUT_IMAGE_LIST, mOutputPathList);
            setResult(RESULT_OK, intent);
        }
        finish();
    }

    private void setResultFailure() {
        Intent intent = new Intent();
        intent.putStringArrayListExtra(Durban.KEY_OUTPUT_IMAGE_LIST, mOutputPathList);
        setResult(RESULT_CANCELED, intent);
        finish();
    }

}