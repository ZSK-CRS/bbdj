package com.mt.bbdj.baseconfig.utls;

import android.content.Context;
import android.media.MediaPlayer;

import com.mt.bbdj.R;
import com.mt.bbdj.baseconfig.application.MyApplication;

import java.util.IllegalFormatCodePointException;

/**
 * Author : ZSK
 * Date : 2019/1/26
 * Description :  音频播放
 */
public class MediaPlayHelper {

    private static MediaPlayHelper sInstance;

    private MediaPlayer mediaPlayer;


    public static MediaPlayHelper getInstance() {
        if (sInstance == null) {
            sInstance = new MediaPlayHelper();
        }
        return sInstance;
    }

    private MediaPlayHelper() {
        mediaPlayer = MediaPlayer.create(MyApplication.getInstance(), R.raw.mp_promit);
    }

    private void startSound() {
        mediaPlayer.start();
    }
}
