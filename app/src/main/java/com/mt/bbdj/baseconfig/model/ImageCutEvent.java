package com.mt.bbdj.baseconfig.model;

import java.util.ArrayList;

/**
 * Author : ZSK
 * Date : 2018/12/28
 * Description : 图片裁剪消息
 */
public class ImageCutEvent {
    private ArrayList<String> picturePathList;

    private String contextName;

    public ImageCutEvent(ArrayList<String> picturePathList, String contextName) {
        this.picturePathList = picturePathList;
        this.contextName = contextName;
    }

    public ArrayList<String> getPicturePathList() {
        return picturePathList;
    }

    public void setPicturePathList(ArrayList<String> picturePathList) {
        this.picturePathList = picturePathList;
    }

    public String getContextName() {
        return contextName;
    }

    public void setContextName(String contextName) {
        this.contextName = contextName;
    }
}
