package com.mt.bbdj.baseconfig.model;

/**
 * Author : ZSK
 * Date : 2019/3/19
 * Description :
 */

public class Area {

    private String id;
    private String region_name;
    private String parent_id;
    private String region_code;
    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setRegion_name(String region_name) {
        this.region_name = region_name;
    }
    public String getRegion_name() {
        return region_name;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }
    public String getParent_id() {
        return parent_id;
    }

    public void setRegion_code(String region_code) {
        this.region_code = region_code;
    }
    public String getRegion_code() {
        return region_code;
    }

}