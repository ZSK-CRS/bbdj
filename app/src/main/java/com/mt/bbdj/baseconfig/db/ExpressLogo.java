package com.mt.bbdj.baseconfig.db;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * Author : ZSK
 * Date : 2019/2/13
 * Description :   快递公司logo
 */
@Entity
public class ExpressLogo {
    @Id
    private Long id;
    private String express_id;    //图标id
    private String logoInterPath;   //网络路径
    private String logoLocalPath;   //本地路径
    private String express_name;    //快递公司名称
    private String flag;   // 1 : 快递  2：物流
    private String states;    // 1：使用  2：禁用
    private String property;    //1：寄件、派件  2：寄件   3：派件
    @Generated(hash = 1975175317)
    public ExpressLogo(Long id, String express_id, String logoInterPath,
            String logoLocalPath, String express_name, String flag, String states,
            String property) {
        this.id = id;
        this.express_id = express_id;
        this.logoInterPath = logoInterPath;
        this.logoLocalPath = logoLocalPath;
        this.express_name = express_name;
        this.flag = flag;
        this.states = states;
        this.property = property;
    }
    @Generated(hash = 943793390)
    public ExpressLogo() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getExpress_id() {
        return this.express_id;
    }
    public void setExpress_id(String express_id) {
        this.express_id = express_id;
    }
    public String getLogoInterPath() {
        return this.logoInterPath;
    }
    public void setLogoInterPath(String logoInterPath) {
        this.logoInterPath = logoInterPath;
    }
    public String getLogoLocalPath() {
        return this.logoLocalPath;
    }
    public void setLogoLocalPath(String logoLocalPath) {
        this.logoLocalPath = logoLocalPath;
    }
    public String getExpress_name() {
        return this.express_name;
    }
    public void setExpress_name(String express_name) {
        this.express_name = express_name;
    }
    public String getFlag() {
        return this.flag;
    }
    public void setFlag(String flag) {
        this.flag = flag;
    }
    public String getStates() {
        return this.states;
    }
    public void setStates(String states) {
        this.states = states;
    }
    public String getProperty() {
        return this.property;
    }
    public void setProperty(String property) {
        this.property = property;
    }

}
