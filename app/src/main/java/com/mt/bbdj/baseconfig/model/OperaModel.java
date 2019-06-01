package com.mt.bbdj.baseconfig.model;

import java.io.Serializable;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/5/31
 * Description :
 */
public class OperaModel implements Serializable {

    private int type;    // 1:图文  2：视频

    private List<String> data;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<String> getData() {
        return data;
    }

    public void setData(List<String> data) {
        this.data = data;
    }
}
