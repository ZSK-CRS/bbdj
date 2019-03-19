package com.mt.bbdj.baseconfig.model;

import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/3/18
 * Description :  省市县
 */
public class AddressBean {

    private List<Province> province;
    private List<City> city;
    private List<Area> area;
    public void setProvince(List<Province> province) {
        this.province = province;
    }
    public List<Province> getProvince() {
        return province;
    }

    public void setCity(List<City> city) {
        this.city = city;
    }
    public List<City> getCity() {
        return city;
    }

    public void setArea(List<Area> area) {
        this.area = area;
    }
    public List<Area> getArea() {
        return area;
    }

}