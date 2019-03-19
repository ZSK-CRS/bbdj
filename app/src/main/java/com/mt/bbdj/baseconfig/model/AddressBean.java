package com.mt.bbdj.baseconfig.model;

import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/3/18
 * Description :  省市县
 */
public class AddressBean {

    private int code;
    private String msg;
    private List<AddressEntity> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<AddressEntity> getData() {
        return data;
    }

    public void setData(List<AddressEntity> data) {
        this.data = data;
    }

    public static class AddressEntity{
        private String id;
        private String region_name;
        private String parent_id;
        private String region_code;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getRegion_name() {
            return region_name;
        }

        public void setRegion_name(String region_name) {
            this.region_name = region_name;
        }

        public String getParent_id() {
            return parent_id;
        }

        public void setParent_id(String parent_id) {
            this.parent_id = parent_id;
        }

        public String getRegion_code() {
            return region_code;
        }

        public void setRegion_code(String region_code) {
            this.region_code = region_code;
        }
    }
}
