package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.model.OperaModel;
import com.mt.bbdj.baseconfig.model.OperaterUrl;
import com.sunfusheng.GlideImageLoader;
import com.sunfusheng.GlideImageView;
import com.sunfusheng.progress.CircleProgressView;
import com.sunfusheng.progress.OnProgressListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.ycbjie.ycstatusbarlib.StatusBarUtils;
import cn.ycbjie.ycstatusbarlib.bar.YCAppBar;

public class OpearPictureActivity extends BaseActivity {

    @BindView(R.id.imageView1)
    GlideImageView imageView1;
    @BindView(R.id.progressView1)
    CircleProgressView progressView1;
    @BindView(R.id.imageView2)
    GlideImageView imageView2;
    @BindView(R.id.progressView2)
    CircleProgressView progressView2;
    @BindView(R.id.imageView3)
    GlideImageView imageView3;
    @BindView(R.id.progressView3)
    CircleProgressView progressView3;
    @BindView(R.id.imageView4)
    GlideImageView imageView4;
    @BindView(R.id.progressView4)
    CircleProgressView progressView4;
    @BindView(R.id.rl_one)
    RelativeLayout rl_onel;
    @BindView(R.id.rl_two)
    RelativeLayout rl_two;
    @BindView(R.id.rl_three)
    RelativeLayout rl_three;
    @BindView(R.id.rl_four)
    RelativeLayout rl_four;
    @BindView(R.id.iv_back)
    RelativeLayout iv_back;

    private int index;

    private String url1 = "";
    private String url2 = "";
    private String url3 = "";
    private String url4 = "";


    public static void actionTo(Context context, int index) {
        Intent intent = new Intent(context, OpearPictureActivity.class);
        intent.putExtra("index", index);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* YCAppBar.setStatusBarLightMode(this, Color.WHITE);
        StatusBarUtils.StatusBarLightMode(OpearPictureActivity.this);*/
        setContentView(R.layout.activity_opear_picture);
        ButterKnife.bind(this);
        initParams();
        setPictrure();
    }

    private void setPictrure() {
        imageView1.fitCenter().error(R.mipmap.image_load_err).diskCacheStrategy(DiskCacheStrategy.NONE).load(url1, R.color.placeholder, (isComplete, percentage, bytesRead, totalBytes) -> {
            if (isComplete) {
                progressView1.setVisibility(View.GONE);
            } else {
                progressView1.setVisibility(View.VISIBLE);
                progressView1.setProgress(percentage);
            }
        });

        imageView2.fitCenter().error(R.mipmap.image_load_err).diskCacheStrategy(DiskCacheStrategy.NONE).load(url2, R.color.placeholder, (isComplete, percentage, bytesRead, totalBytes) -> {
            if (isComplete) {
                progressView2.setVisibility(View.GONE);
            } else {
                progressView2.setVisibility(View.VISIBLE);
                progressView2.setProgress(percentage);

            }
        });

        imageView3.fitCenter().error(R.mipmap.image_load_err).diskCacheStrategy(DiskCacheStrategy.NONE).load(url3, R.color.placeholder, (isComplete, percentage, bytesRead, totalBytes) -> {
            if (isComplete) {
                progressView3.setVisibility(View.GONE);
            } else {
                progressView3.setVisibility(View.VISIBLE);
                progressView3.setProgress(percentage);
            }
        });

        imageView4.fitCenter().error(R.mipmap.image_load_err).diskCacheStrategy(DiskCacheStrategy.NONE).load(url4, R.color.placeholder, (isComplete, percentage, bytesRead, totalBytes) -> {
            if (isComplete) {
                progressView4.setVisibility(View.GONE);
            } else {
                progressView4.setVisibility(View.VISIBLE);
                progressView4.setProgress(percentage);
            }
        });

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initParams() {
        Intent intent = getIntent();
        index = intent.getIntExtra("index",0);

        if (index == 1) {
            url1 = OperaterUrl.PICTURE_2;
            url2 = OperaterUrl.PICTURE_3;
            url3 = OperaterUrl.PICTURE_4;
            url4 = OperaterUrl.PICTURE_5;
        } else if (index == 2) {
            rl_four.setVisibility(View.GONE);
            url1 = OperaterUrl.PICTURE_6;
            url2 = OperaterUrl.PICTURE_7;
            url3 = OperaterUrl.PICTURE_8;
        } else if (index == 3) {
            url1 = OperaterUrl.PICTURE_9;
            rl_two.setVisibility(View.GONE);
            rl_three.setVisibility(View.GONE);
            rl_four.setVisibility(View.GONE);
        } else if (index == 4) {
            url1 = OperaterUrl.PICTURE_10;
            url2 = OperaterUrl.PICTURE_11;
            url3 = OperaterUrl.PICTURE_12;
            rl_four.setVisibility(View.GONE);
        } else if (index == 5) {
            url1 = OperaterUrl.PICTURE_13;
            url2 = OperaterUrl.PICTURE_14;
            rl_three.setVisibility(View.GONE);
            rl_four.setVisibility(View.GONE);
        } else if (index == 6) {
            url1 = OperaterUrl.PICTURE_15;
            url2 = OperaterUrl.PICTURE_16;
            url3 = OperaterUrl.PICTURE_17;
            rl_four.setVisibility(View.GONE);
        }
    }
}
