package com.mt.bbdj.baseconfig.model;

import java.io.Serializable;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/4/10
 * Description :
 */
public class PrintNumberModel implements Serializable {

    private List<EnterModel> dataList;

    public List<EnterModel> getDataList() {
        return dataList;
    }

    public void setDataList(List<EnterModel> dataList) {
        this.dataList = dataList;
    }

    public static class EnterModel {
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getQrcode() {
            return qrcode;
        }

        public void setQrcode(String qrcode) {
            this.qrcode = qrcode;
        }

        private String code;   //取件码
        private String qrcode;   //二维码数据
    }


}
