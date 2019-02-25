package com.mt.bbdj.baseconfig.model;

/**
 * Author : ZSK
 * Date : 2019/2/20
 * Description :
 */
public class BindAccountModel {
    private String bank_realName;     //银行卡姓名
    private String back_number;   //卡号
    private String bank;    //开户行
    private String ali_realName;     //支付宝姓名
    private String ali_account;   //支付宝账号

    public BindAccountModel(String ali_realName, String ali_account) {
        this.ali_realName = ali_realName;
        this.ali_account = ali_account;
    }

    public BindAccountModel(String bank_realName, String back_number, String bank) {
        this.bank_realName = bank_realName;
        this.back_number = back_number;
        this.bank = bank;
    }


    public BindAccountModel(String bank_realName, String back_number, String bank,
                            String ali_realName, String ali_account) {
        this.bank_realName = bank_realName;
        this.back_number = back_number;
        this.bank = bank;
        this.ali_realName = ali_realName;
        this.ali_account = ali_account;
    }

    public String getBank_realName() {
        return bank_realName;
    }

    public void setBank_realName(String bank_realName) {
        this.bank_realName = bank_realName;
    }

    public String getBack_number() {
        return back_number;
    }

    public void setBack_number(String back_number) {
        this.back_number = back_number;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getAli_realName() {
        return ali_realName;
    }

    public void setAli_realName(String ali_realName) {
        this.ali_realName = ali_realName;
    }

    public String getAli_account() {
        return ali_account;
    }

    public void setAli_account(String ali_account) {
        this.ali_account = ali_account;
    }
}
