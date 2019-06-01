package com.mt.bbdj.community.activity;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.base.BaseActivity;

public class OpearVideoActivity extends BaseActivity {

    private String url;
    private VideoView videoView;


    public static void actionTo(Context context, String url) {
        Intent intent = new Intent(context, OpearPictureActivity.class);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opear_video);

        initParams();

        loadVideoView();
    }

    private void loadVideoView() {
        //加载指定的视频文件

        videoView.setVideoPath(url);
      //创建MediaController对象
        MediaController mediaController = new MediaController(this);
        //VideoView与MediaController建立关联
        videoView.setMediaController(mediaController);
        //让VideoView获取焦点
        videoView.requestFocus();


    }

    private void initParams() {
        Intent intent = getIntent();
        url = intent.getStringExtra("url");

        videoView = findViewById(R.id.videoView);
    }
}
