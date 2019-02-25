package com.mt.bbdj.baseconfig.internet.down;

import java.io.File;

/**
 * Author : ZSK
 * Date : 2019/2/13
 * Description :  图片下载回调
 */
public interface ImageDownLoadCallBack {
    void onDownLoadSuccess(String tag,String localPath);
    void onDownLoadFailed();
}
