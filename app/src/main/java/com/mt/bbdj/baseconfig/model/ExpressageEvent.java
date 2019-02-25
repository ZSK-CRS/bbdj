package com.mt.bbdj.baseconfig.model;

import android.widget.TextView;

/**
 * Author : ZSK
 * Date : 2019/1/7
 * Description :   快递信息
 */
public class ExpressageEvent {
    private String express_id;   //快递id
    private String express_name;   //快递公司
    private String express_logo;    //快递logo


    public String getExpress_id() {
        return express_id;

    }

    public void setExpress_id(String express_id) {
        this.express_id = express_id;
    }

    public String getExpress_name() {
        return express_name;
    }

    public void setExpress_name(String express_name) {
        this.express_name = express_name;
    }

    public String getExpress_logo() {
        return express_logo;
    }

    public void setExpress_logo(String express_logo) {
        this.express_logo = express_logo;
    }

    public String getStates() {
        return states;
    }

    public void setStates(String states) {
        this.states = states;
    }

    private String states;    //状态

    public ExpressageEvent(String express_id, String express_name, String express_logo, String states) {
        this.express_id = express_id;
        this.express_name = express_name;
        this.express_logo = express_logo;
        this.states = states;
    }
}
