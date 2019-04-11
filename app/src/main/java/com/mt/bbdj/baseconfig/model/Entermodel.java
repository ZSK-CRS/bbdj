package com.mt.bbdj.baseconfig.model;

/**
 * Author : ZSK
 * Date : 2019/4/10
 * Description :  入库模型
 */
public class Entermodel {
    private String number;       //运单号
    private String mobile;     //手机号码
    private String name;    //姓名
    private String code;    //取货码

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
