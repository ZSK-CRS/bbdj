package com.mt.bbdj.baseconfig.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/4/12
 * Description :
 */
public class PrintTagModel implements Serializable {

    public List<HashMap<String,String>> data;

    public List<HashMap<String, String>> getData() {
        return data;
    }

    public void setData(List<HashMap<String, String>> data) {
        this.data = data;
    }
}
