package com.mt.bbdj.baseconfig.model;

/**
 * Author : ZSK
 * Date : 2018/12/27
 * Description :  用来描述销毁界面的消息
 */
public class DestroyEvent {

    private int mType;

    public DestroyEvent(int type){
        this.mType = type;
    }

    public int getType() {
        return mType;
    }
}
