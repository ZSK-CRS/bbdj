package com.mt.bbdj.community.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.model.OperaterUrl;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OpearteActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    RelativeLayout ivBack;
    @BindView(R.id.tv_expressage_select)
    TextView tvExpressageSelect;
    @BindView(R.id.ll_picture_teacher)
    LinearLayout llPictureTeacher;
    @BindView(R.id.tv_picture_one)
    TextView tvPictureOne;
    @BindView(R.id.tv_picture_two)
    TextView tvPictureTwo;
    @BindView(R.id.tv_picture_three)
    TextView tvPictureThree;
    @BindView(R.id.tv_picture_four)
    TextView tvPictureFour;
    @BindView(R.id.tv_picture_five)
    TextView tvPictureFive;
    @BindView(R.id.tv_picture_six)
    TextView tvPictureSix;
    @BindView(R.id.ll_video_teacher)
    LinearLayout llVideoTeacher;
    @BindView(R.id.tv_video_one)
    TextView tvVideoOne;
    @BindView(R.id.tv_video_two)
    TextView tvVideoTwo;
    @BindView(R.id.tv_video_three)
    TextView tvVideoThree;
    @BindView(R.id.tv_video_four)
    TextView tvVideoFour;
    @BindView(R.id.tv_video_five)
    TextView tvVideoFive;
    @BindView(R.id.layout_teacher)
    LinearLayout layout_teacher;
    @BindView(R.id.layout_video)
    LinearLayout layout_video;
    @BindView(R.id.rl_title_layout)
    RelativeLayout rlTitleLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opearte);
        ButterKnife.bind(this);
        initParams();
    }

    private void initParams() {
        layout_teacher.setVisibility(View.VISIBLE);
        layout_video.setVisibility(View.GONE);
    }



    private boolean isPictureShow = true;
    private boolean isVideoShow = false;

    @OnClick({R.id.iv_back, R.id.ll_picture_teacher, R.id.ll_video_teacher, R.id.tv_picture_one,
            R.id.tv_picture_two, R.id.tv_picture_three, R.id.tv_picture_four, R.id.tv_picture_five,
            R.id.tv_picture_six, R.id.tv_video_one, R.id.tv_video_two, R.id.tv_video_three, R.id.tv_video_four,
            R.id.tv_video_five})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.ll_picture_teacher:
                setPictureView();
                break;
            case R.id.ll_video_teacher:
                setVideoView();
                break;
            case R.id.tv_picture_one:
                OpearPictureActivity.actionTo(this, 1);
                break;
            case R.id.tv_picture_two:
                OpearPictureActivity.actionTo(this, 2);
                break;
            case R.id.tv_picture_three:
                OpearPictureActivity.actionTo(this, 3);
                break;
            case R.id.tv_picture_four:
                OpearPictureActivity.actionTo(this, 4);
                break;
            case R.id.tv_picture_five:
                OpearPictureActivity.actionTo(this, 5);
                break;
            case R.id.tv_picture_six:
                OpearPictureActivity.actionTo(this, 6);
                break;
            case R.id.tv_video_one:
                OperaVideoActivity.actionTo(this, OperaterUrl.VIDEO_1, "如何在兵兵到家公众号查询物流信息");
                break;
            case R.id.tv_video_two:
                OperaVideoActivity.actionTo(this, OperaterUrl.VIDEO_2, "如何在兵兵到家app查询物流信息");
                break;
            case R.id.tv_video_three:
                OperaVideoActivity.actionTo(this, OperaterUrl.VIDEO_3, "如何在门店下单寄件");
                break;
            case R.id.tv_video_four:
                OperaVideoActivity.actionTo(this, OperaterUrl.VIDEO_4, "如何在兵兵到家app下单寄件");
                break;
            case R.id.tv_video_five:
                OperaVideoActivity.actionTo(this, OperaterUrl.VIDEO_5, "如何在兵兵到家app上交接管理");
                break;
        }
    }

    private void setVideoView() {
        if (isVideoShow == true) {
            isVideoShow = false;
            layout_video.setVisibility(View.GONE);
        } else {
            isVideoShow = true;
            layout_video.setVisibility(View.VISIBLE);
        }
        layout_teacher.setVisibility(View.GONE);
    }

    private void setPictureView() {
        if (isPictureShow == true) {
            isPictureShow = false;
            layout_teacher.setVisibility(View.GONE);
        } else {
            isPictureShow = true;
            layout_teacher.setVisibility(View.VISIBLE);
        }
        layout_video.setVisibility(View.GONE);
    }

}
