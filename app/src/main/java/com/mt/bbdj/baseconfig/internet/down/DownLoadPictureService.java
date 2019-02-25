package com.mt.bbdj.baseconfig.internet.down;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

/**
 * Author : ZSK
 * Date : 2019/2/13
 * Description :  图片下载线程
 */
public class DownLoadPictureService implements Runnable {

    private String pictureUrl;
    private String path;
    private String pictureTag;
    private ImageDownLoadCallBack callBack;

    public DownLoadPictureService(String path, String pictureUrl, String pictureTag,ImageDownLoadCallBack callBack) {
        this.pictureUrl = pictureUrl;
        this.callBack = callBack;
        this.path = path;
        this.pictureTag = pictureTag;
    }


    @Override
    public void run() {
        URL url = null;
        try {
            url = new URL(pictureUrl);
            DataInputStream dataInputStream = new DataInputStream(url.openStream());

            FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int length;

            while ((length = dataInputStream.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            fileOutputStream.write(output.toByteArray());
            dataInputStream.close();
            fileOutputStream.close();
            callBack.onDownLoadSuccess(pictureTag,path);
        } catch (Exception e) {
            e.printStackTrace();
            callBack.onDownLoadFailed();
        }
    }
}
