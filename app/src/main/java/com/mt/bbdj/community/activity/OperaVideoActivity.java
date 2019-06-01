package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.bumptech.glide.Glide;
import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;
import com.mt.bbdj.baseconfig.model.OperaterUrl;
import com.xiao.nicevideoplayer.Clarity;
import com.xiao.nicevideoplayer.NiceVideoPlayer;
import com.xiao.nicevideoplayer.NiceVideoPlayerManager;
import com.xiao.nicevideoplayer.TxVideoPlayerController;

import java.util.ArrayList;
import java.util.List;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class OperaVideoActivity extends BaseActivity {

    private JzvdStd myJzvdStd;
    private String videoUrl;

    public static void actionTo(Context context, String videoUrl,String title) {

        Intent intent = new Intent(context, OperaVideoActivity.class);
        intent.putExtra("videoUrl", videoUrl);
        intent.putExtra("title", title);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opera_video);

        initParams();

        initVideoView();

    }

    private void initVideoView() {
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initParams() {

        Intent intent = getIntent();
        videoUrl = intent.getStringExtra("videoUrl");
        String title = intent.getStringExtra("title");
        // videoUrl = "http://play.g3proxy.lecloud.com/vod/v2/MjUxLzE2LzgvbGV0di11dHMvMTQvdmVyXzAwXzIyLTExMDc2NDEzODctYXZjLTE5OTgxOS1hYWMtNDgwMDAtNTI2MTEwLTE3MDg3NjEzLWY1OGY2YzM1NjkwZTA2ZGFmYjg2MTVlYzc5MjEyZjU4LTE0OTg1NTc2ODY4MjMubXA0?b=259&mmsid=65565355&tm=1499247143&key=f0eadb4f30c404d49ff8ebad673d3742&platid=3&splatid=345&playid=0&tss=no&vtype=21&cvid=2026135183914&payff=0&pip=08cc52f8b09acd3eff8bf31688ddeced&format=0&sign=mb&dname=mobile&expect=1&tag=mobile&xformat=super";
        JzvdStd jzvdStd = (JzvdStd) findViewById(R.id.videoplayer);
        jzvdStd.setUp(videoUrl, title);
        Glide.with(this).load(OperaterUrl.PICTURE_1).into(jzvdStd.thumbImageView);
        jzvdStd.startVideo();

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public void onBackPressed() {
        if (JzvdStd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JzvdStd.resetAllVideos();
    }
}
