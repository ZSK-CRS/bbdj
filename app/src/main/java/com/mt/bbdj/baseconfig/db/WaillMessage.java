package com.mt.bbdj.baseconfig.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Author : ZSK
 * Date : 2019/4/22
 * Description :
 */
@Entity
public class WaillMessage {
    public String id;       //
    public String tagCode;   //提货码
    public String wailNumber;     //运单号
    public String expressName;   //快递公司
    public String name;
    public String mobile;
    public int tagNumber;    //当前的位数
    @Generated(hash = 1472683431)
    public WaillMessage(String id, String tagCode, String wailNumber,
            String expressName, String name, String mobile, int tagNumber) {
        this.id = id;
        this.tagCode = tagCode;
        this.wailNumber = wailNumber;
        this.expressName = expressName;
        this.name = name;
        this.mobile = mobile;
        this.tagNumber = tagNumber;
    }
    @Generated(hash = 920651765)
    public WaillMessage() {
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getTagCode() {
        return this.tagCode;
    }
    public void setTagCode(String tagCode) {
        this.tagCode = tagCode;
    }
    public String getWailNumber() {
        return this.wailNumber;
    }
    public void setWailNumber(String wailNumber) {
        this.wailNumber = wailNumber;
    }
    public String getExpressName() {
        return this.expressName;
    }
    public void setExpressName(String expressName) {
        this.expressName = expressName;
    }
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getMobile() {
        return this.mobile;
    }
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
    public int getTagNumber() {
        return this.tagNumber;
    }
    public void setTagNumber(int tagNumber) {
        this.tagNumber = tagNumber;
    }


}
