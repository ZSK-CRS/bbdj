package com.mt.bbdj.baseconfig.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Author : ZSK
 * Date : 2019/1/5
 * Description :  用于临时存储混合的省市县
 */
@Entity
public class MingleArea {
    public String id;       //id
    public String region_name;   //名称
    public String parent_id;     //上级id   0：表示的是省级
    public String region_code;   //编号
    @Generated(hash = 94304478)
    public MingleArea(String id, String region_name, String parent_id,
            String region_code) {
        this.id = id;
        this.region_name = region_name;
        this.parent_id = parent_id;
        this.region_code = region_code;
    }
    @Generated(hash = 993221961)
    public MingleArea() {
    }
    public String getId() {
        return this.id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getRegion_name() {
        return this.region_name;
    }
    public void setRegion_name(String region_name) {
        this.region_name = region_name;
    }
    public String getParent_id() {
        return this.parent_id;
    }
    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }
    public String getRegion_code() {
        return this.region_code;
    }
    public void setRegion_code(String region_code) {
        this.region_code = region_code;
    }
}
