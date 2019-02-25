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
