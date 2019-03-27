package com.mt.bbdj.baseconfig.model;

/**
 * Author : ZSK
 * Date : 2019/1/10
 * Description : 发送临时消息
 */
public class TargetEvent {

    public static int PRINT_AGAIN = 400;      //原单重打
    public static int DESTORY = 404;      //销毁
    public static int COMMIT_FIRST_REFRESH = 405;    //社区版首页刷新
    public static int MESSAGE_MANAGE_REFRESH = 406;     //刷新短信管理界面
    public static int SYSTEM_MESSAGE_REFRESH = 407;     //系统消息
    public static int NOTIFICATION_REFRESH = 408;   //通知公告
    public static int BIND_ACCOUNT_BUTTON = 409;    //表示两者都绑定
    public static int BIND_ALI_ACCOUNT = 410;     //表示只绑定了支付宝
    public static int BIND_BANK_ACCOUNT = 411;    //绑定了银行卡号
    public static int BIND_ACCOUNT_NONE = 412;   //表示没有绑定一个
    public static int DESTORY_GOODS_DETAIL = 413;   //销毁商品详情界面
    public static int DESTORY_GOODS_FROM_CART = 414;    //销毁购物车界面
    public static int REFRESH_ALEADY_CHAGNE = 415;     //刷新已交接界面
    public static int DESTORY_RECHAR = 416;      //销毁充值界面
    public static int SEARCH_GLOBAL_SEND = 417;     //首页搜索内寄件
    public static int SEARCH_GLOBAL_PAI = 418;    //首页搜索派件
    public static int CLEAR_SEARCH_DATA = 419;    //清空首页搜索信息

    private int target;

    private String data;

    private Object object;

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return this.data;
    }

    public TargetEvent(int target,String data) {
        this.target = target;
        this.data = data;
    }

    public TargetEvent(int target,Object object){
        this.target = target;
        this.object = object;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public TargetEvent(int target) {
        this.target = target;

    }
}
