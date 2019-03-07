package com.mt.bbdj.baseconfig.internet;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.mt.bbdj.baseconfig.utls.DateUtil;
import com.mt.bbdj.baseconfig.utls.StringUtil;
import com.mt.bbdj.baseconfig.utls.ToastUtil;
import com.yanzhenjie.nohttp.BasicBinary;
import com.yanzhenjie.nohttp.FileBinary;
import com.yanzhenjie.nohttp.NoHttp;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.Request;

import java.io.File;

/**
 * Author : ZSK
 * Date : 2018/12/27
 * Description :  网络请求的封装
 */
public class NoHttpRequest {
    /**
     * 获取验证码的请求
     *
     * @param phoneNumber : 手机号码
     * @param type        : 1 注册  2 忘记密码  3 提现
     */
    public static Request<String> getIdentifyCodeRequest(String phoneNumber, String type) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_IDENTIFY_CODE, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_IDENTIFY_CODE);
        request.add("signature", signature);
        request.add("phone", phoneNumber);
        request.add("type", type);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        return request;
    }

    /**
     * 上传图片请求
     *
     * @param filePath 图片的路径
     */
    public static Request<String> commitPictureRequest(@NonNull String filePath) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        BasicBinary fileBinary = new FileBinary(new File(filePath));
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_COMMIT_PICTURE, RequestMethod.POST);
        request.add("method", InterApi.ACTION_COMMIT_PICTURE);
        request.add("file", fileBinary);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        return request;
    }

    /**
     * 上传注册信息的请求
     *
     * @return
     */
    public static Request<String> commitRegisterRequest(String phone, String password, String realname, String idcard,
                                                        String just_card, String back_card, String license,String number) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_COMMIT_REGISTER_MESSAGE, RequestMethod.GET);
        request.add("method", InterApi.ACTION_COMMIT_REGISTER_MESSAGE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("phone", phone);        //注册电话
        request.add("number", number);        //工号
        request.add("password", password);    //密码
        request.add("realname", realname);    //真实姓名
        request.add("idcard", idcard);       //身份证号
        request.add("just_card", just_card);    //正面照片
        request.add("back_card", back_card);   //背面照片
        request.add("license", license);      //营业执照
        return request;
    }

    /**
     * 找回密码的请求
     *
     * @return
     */
    public static Request<String> changePasswordRequst(String phone, String password) {
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CHANGE_PASSWORD, RequestMethod.GET);
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        request.add("method", InterApi.ACTION_CHANGE_PASSWORD);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("phone", phone);    //加密值
        request.add("password", password);    //加密值
        return request;
    }

    /**
     * 找回密码的请求
     *
     * @return
     */
    public static Request<String> changeNewPasswordRequst(String user_id, String oldPassword,String newPassword) {
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CHANGE_NEW_PASSWORD, RequestMethod.GET);
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        request.add("method", InterApi.ACTION_CHANGE_NEW_PASSWORD);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("old_password", oldPassword);
        request.add("new_password", newPassword);
        return request;
    }

    /**
     * 登录请求
     * @param username     用户名
     * @param password     密码
     * @param receive_id    极光别名
     * @param device      //设备类型  1： android  2:ios
     * @return
     */
    public static Request<String> loginRequest(String username, String password, String receive_id, String device) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_LOGIN, RequestMethod.GET);
        request.add("method", InterApi.ACTION_LOGIN);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("account", username);
        request.add("password", password);
        request.add("receive_id", receive_id);
        request.add("device", device);
        return request;
    }

    /**
     * 充值记录
     *
     * @param type    1： 短信  2：面单
     * @param page    请求页数
     * @param user_id 用户id
     * @return
     */
    public static Request<String> getRechargeRecodeRequst(String type, String page, String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_RECHARGE_RECODE, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_RECHARGE_RECODE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("type", type);
        request.add("page", page);
        return request;
    }

    /**
     * 获取面单商品
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> getRechargePannelRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_RECHARGE_PANNEL, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_RECHARGE_PANNEL);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 短信充值
     *
     * @param user_id    用户id
     * @param message_id 商品id
     * @return
     */
    public static Request<String> getRechargeMoneyRequest(String user_id, String message_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_RECHARGE_MONEY, RequestMethod.GET);
        request.add("method", InterApi.ACTION_RECHARGE_MONEY);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("message_id", message_id);
        return request;
    }

    /**
     * 面单单价
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> getPannelUnitePriceRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_PANNEL_UNITE_PRICE, RequestMethod.GET);
        request.add("method", InterApi.ACTION_PANNEL_UNITE_PRICE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 面单充值
     *
     * @param user_id     用户id
     * @param face_id     面单id
     * @param face_number 面单数量
     * @return
     */
    public static Request<String> getPannelRechargeRequest(String user_id, String face_id, String face_number) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_PANNEL_RECHARGEL, RequestMethod.GET);
        request.add("method", InterApi.ACTION_PANNEL_RECHARGEL);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("face_id", face_id);
        request.add("face_number", face_number);
        return request;
    }

    /**
     * 驿站地址
     *
     * @param user_id 用户id
     * @param type    用户类型  1 ： 寄件人  2： 收件人
     * @return
     */
    public static Request<String> getStageAddressRequest(String user_id, String type) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_STAGE_ADDRESS, RequestMethod.GET);
        request.add("method", InterApi.ACTION_STAGE_ADDRESS);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("type", type);
        return request;
    }

    /**
     * 获取我的地址
     * @param user_id
     * @return
     */
    public static Request<String> getMyAddressRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_MY_ADDRESS, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_MY_ADDRESS);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 获取地区地址
     *
     * @param user_id
     * @return
     */
    public static Request<String> getAreaRequest(String user_id,String express_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_AREA, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_AREA);
        request.add("signature", signature);
        request.add("express_id", express_id);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 更新快递公司状态
     * @param user_id    用户id
     * @param express_id   快递公司id
     * @param type 1：寄件  2：派件
     * @return
     */
    public static Request<String> updateExpressState(String user_id,String express_id,String type) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_UPDATE_EXPRESS, RequestMethod.GET);
        request.add("method", InterApi.ACTION_UPDATE_EXPRESS);
        request.add("signature", signature);
        request.add("express_id", express_id);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("type", type);
        return request;
    }

    /**
     * 修改地址
     *
     * @param user_id   用户id
     * @param realName  姓名
     * @param telephone 电话
     * @param address   详细地址
     * @param book_id   地址id
     * @return
     */
    public static Request<String> changeAddressBook(String user_id, String realName, String telephone,
                                                    String province, String city, String area, String address, String book_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CHNAGE_ADDRESS, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CHNAGE_ADDRESS);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("realname", realName);
        request.add("telephone", telephone);
        request.add("user_id", user_id);
        request.add("province", province);
        request.add("city", city);
        request.add("area", area);
        request.add("address", address);
        request.add("book_id", book_id);
        return request;
    }

    /**
     * 修改收获地址
     *
     * @param user_id   用户id
     * @param realName  姓名
     * @param telephone 电话
     * @param address   详细地址
     * @param book_id   地址id
     * @return
     */
    public static Request<String> changeMyAddressBook(String user_id, String realName, String telephone,
                                                    String province, String city, String area, String address, String book_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CHNAGE_MY_ADDRESS, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CHNAGE_MY_ADDRESS);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("realname", realName);
        request.add("telephone", telephone);
        request.add("user_id", user_id);
        request.add("province", province);
        request.add("city", city);
        request.add("area", area);
        request.add("address", address);
        request.add("book_id", book_id);
        return request;
    }

    /**
     * 添加地址
     *
     * @param user_id   用户id
     * @param realName  姓名
     * @param telephone 电话
     * @param address   详细地址
     * @return
     */
    public static Request<String> addAddressBook(String user_id, String realName, String telephone,
                                                 String province, String city, String county, String address, String type) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_ADD_ADDRESS, RequestMethod.GET);
        request.add("method", InterApi.ACTION_ADD_ADDRESS);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("realname", realName);
        request.add("telephone", telephone);
        request.add("user_id", user_id);
        request.add("area", county);
        request.add("province", province);
        request.add("city", city);
        request.add("type", type);
        request.add("address", address);
        return request;
    }

    /**
     * 添加收获地址
     *
     * @param user_id   用户id
     * @param realName  姓名
     * @param telephone 电话
     * @param address   详细地址
     * @return
     */
    public static Request<String> addMyAddressBook(String user_id, String realName, String telephone,
                                                 String province, String city, String county, String address, String type) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_ADD_MY_ADDRESS, RequestMethod.GET);
        request.add("method", InterApi.ACTION_ADD_MY_ADDRESS);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("realname", realName);
        request.add("telephone", telephone);
        request.add("user_id", user_id);
        request.add("area", county);
        request.add("province", province);
        request.add("city", city);
        request.add("type", type);
        request.add("address", address);
        return request;
    }


    /**
     * 删除我的地址
     *
     * @param user_id 用户id
     * @param book_id 地址id
     * @return
     */
    public static Request<String> deleteMyAddressRequest(String user_id, String book_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_DELETE_MY_ADDRESS, RequestMethod.GET);
        request.add("method", InterApi.ACTION_DELETE_MY_ADDRESS);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("book_id", book_id);
        return request;
    }

    /**
     * 删除地址簙
     *
     * @param user_id 用户id
     * @param book_id 地址id
     * @return
     */
    public static Request<String> deleteAddressRequest(String user_id, String book_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_DELETE_ADDRESS, RequestMethod.GET);
        request.add("method", InterApi.ACTION_DELETE_ADDRESS);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("book_id", book_id);
        return request;
    }

    /**
     * 获取快递公司的请求
     *
     * @param user_id 用户id
     * @param type    类型 1： 快递公司  2：物流公司
     * @return
     */
    public static Request<String> getExpressageRequest(String user_id, String type) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_EXPRESSAGE_LIST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_EXPRESSAGE_LIST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("type", type);
        return request;
    }

    /**
     * 获取物品的类型
     *
     * @param user_id 用户ID
     * @return
     */
    public static Request<String> getGoodsTypeRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GOODS_TYPE, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GOODS_TYPE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 实名认证
     *
     * @param user_id   用户id
     * @param book_id   寄件人id
     * @param realname  姓名
     * @param idcard    身份证号
     * @param just_card 正面照
     * @param back_card 反面照
     * @return
     */
    public static Request<String> commitIdentification(String user_id, String book_id,
                                                       String realname, String idcard, String just_card, String back_card) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_COMMIT_AUTHENTICATION, RequestMethod.GET);
        request.add("method", InterApi.ACTION_COMMIT_AUTHENTICATION);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("book_id", book_id);
        request.add("realname", realname);
        request.add("idcard", idcard);
        request.add("just_card", just_card);
        request.add("back_card", back_card);
        return request;
    }

    /**
     * 实名认证
     *
     * @param user_id   用户id
     * @param realname  姓名
     * @param idcard    身份证号
     * @param just_card 正面照
     * @param back_card 反面照
     * @return
     */
    public static Request<String> commitIdentificationForManager(String user_id, String mail_id,
                                                                 String realname, String idcard, String just_card, String back_card) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_COMMIT_IDENTIFICATION_FOR_MANAGER, RequestMethod.GET);
        request.add("method", InterApi.ACTION_COMMIT_IDENTIFICATION_FOR_MANAGER);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("idcard", idcard);
        request.add("mail_id", mail_id);
        request.add("realname", realname);
        request.add("just_card", just_card);
        request.add("back_card", back_card);
        return request;
    }


    /**
     * 验证是否是实名
     *
     * @param user_id
     * @param book_id
     * @return
     */
    public static Request<String> isIdentifyRequest(String user_id, String book_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_IS_IDENTIFY_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_IS_IDENTIFY_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("book_id", book_id);
        return request;
    }

    /**
     * 下单
     *
     * @param user_id    用户id
     * @param express_id 快递公司id
     * @param send_id    寄件人id
     * @param collect_id 收件人id
     * @param type_id    物品id
     * @param weight     重量
     * @param content    备注
     * @return
     */
    public static Request<String> commitOrderRequest(String user_id, String express_id, String send_id
            , String collect_id, String type_id, String weight, String content) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_COMMIT_ORDER, RequestMethod.GET);
        request.add("method", InterApi.ACTION_COMMIT_ORDER);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("express_id", express_id);
        request.add("send_id", send_id);
        request.add("collect_id", collect_id);
        request.add("type_id", type_id);
        request.add("weight", weight);
        request.add("content", content);
        return request;
    }

    /**
     * @param user_id
     * @param express_id
     * @param start_province
     * @param start_city
     * @param end_province
     * @param end_city
     * @param weight
     * @return
     */
    public static Request<String> getEstimateRequest(String user_id, String express_id, String start_province, String start_city,
                                                     String end_province, String end_city, String weight) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_ESTIMMATE_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_ESTIMMATE_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("express_id", express_id);
        request.add("start_province", start_province);
        request.add("start_city", start_city);
        request.add("end_province", end_province);
        request.add("weight", weight);
        request.add("end_city", end_city);
        return request;
    }

    /**
     * 待收件请求
     *
     * @param user_id    用户id
     * @param express_id 快递公司id
     * @param keywords   关键字
     * @param page       页码
     * @return
     */
    public static Request<String> getWaitCollecRequest(String user_id, String express_id, String keywords, String page) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_WAIT_COLLECT, RequestMethod.GET);
        request.add("method", InterApi.ACTION_WAIT_COLLECT);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("express_id", express_id);
        request.add("keywords", keywords);
        request.add("page", page);
        return request;
    }

    /**
     * 已处理请求
     *
     * @param user_id    用户id
     * @param express_id 快递公司id
     * @param keywords   关键字
     * @param page       页码
     * @param starttime  开始时间
     * @return
     */
    public static Request<String> getFinishEventRequest(String user_id, String express_id, String keywords
            , String page, String starttime) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_HAVE_FINISH, RequestMethod.GET);
        request.add("method", InterApi.ACTION_HAVE_FINISH);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("express_id", express_id);
        request.add("keywords", keywords);
        request.add("page", page);
        request.add("starttime", starttime);
        return request;
    }

    /**
     * 待打印请求
     *
     * @param user_id    用户id
     * @param express_id 快递公司id
     * @param keywords   关键字
     * @param page       页码
     * @param starttime  开始时间
     * @return
     */
    public static Request<String> getWaitPrintRequest(String user_id, String express_id, String keywords
            , String page, String starttime) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_WAIT_PRINT, RequestMethod.GET);
        request.add("method", InterApi.ACTION_WAIT_PRINT);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("express_id", express_id);
        request.add("keywords", keywords);
        request.add("page", page);
        request.add("starttime", starttime);
        return request;
    }

    /**
     * 获取订单详情
     *
     * @param user_id
     * @param mail_id
     * @return
     */
    public static Request<String> getOrderDetailRequest(String user_id, String mail_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_ORDER_DETAIL, RequestMethod.GET);
        request.add("method", InterApi.ACTION_ORDER_DETAIL);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("mail_id", mail_id);
        return request;
    }

    /**
     * 获取订单
     *
     * @param user_id
     * @return
     */
    public static Request<String> getCauseForCannelOrderRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CANNEL_ORDER_CAUSE, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CANNEL_ORDER_CAUSE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 取消订单
     *
     * @param user_id   用户id
     * @param mail_id   订单id
     * @param reason_id 取消原因id
     * @param content   备注
     * @return
     */
    public static Request<String> commitCannelOrderCauseRequest(String user_id, String mail_id, String reason_id, String content) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_COMMIT_CANNEL_ORDER, RequestMethod.GET);
        request.add("method", InterApi.ACTION_COMMIT_CANNEL_ORDER);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("mail_id", mail_id);
        request.add("reason_id", reason_id);
        request.add("content", content);
        return request;
    }

    /**
     * 打印时验证身份是否实名
     *
     * @param user_id 用户id
     * @param mail_id 订单id
     * @return
     */
    public static Request<String> identifySealRequest(String user_id, String mail_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_IDETIFY_AT_SEAL, RequestMethod.GET);
        request.add("method", InterApi.ACTION_IDETIFY_AT_SEAL);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("mail_id", mail_id);
        return request;
    }

    /**
     * 先存后打
     *
     * @param user_id    用户id
     * @param mail_id    订单id
     * @param goods_name 物品名称
     * @param weight     重量
     * @param money      运费
     * @param content    备注
     * @return
     */
    public static Request<String> commitRecordMailRequest(String user_id, String mail_id,
                                                          String goods_name, String weight, String money, String content) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_COMMIT_SAVE_MAIL, RequestMethod.GET);
        request.add("method", InterApi.ACTION_COMMIT_SAVE_MAIL);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("mail_id", mail_id);
        request.add("goods_name", goods_name);
        request.add("weight", weight);
        request.add("money", money);
        request.add("content", content);
        return request;
    }

    /**
     * 待打印 中修改物品信息
     *
     * @param user_id    用户id
     * @param mail_id    订单id
     * @param goods_name 物品名称
     * @param weight     重量
     * @param money      运费
     * @param content    备注
     * @return
     */
    public static Request<String> waitMimeographRequest(String user_id, String mail_id,
                                                        String goods_name, String weight, String money, String content) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_PRINT_ONCE_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_PRINT_ONCE_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("mail_id", mail_id);
        request.add("goods_name", goods_name);
        request.add("weight", weight);
        request.add("money", money);
        request.add("content", content);
        return request;
    }


    /**
     * 再打一单
     *
     * @param user_id    用户id
     * @param mail_id    订单id
     * @param goods_name 物品名称
     * @param weight     重量
     * @param money      运费
     * @param content    备注
     * @return
     */
    public static Request<String> commitRecordMailDetailRequest(String user_id, String mail_id,
                                                                String goods_name, String weight, String money, String content) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_COMMIT_SAVE_MAIL_DETAIL, RequestMethod.GET);
        request.add("method", InterApi.ACTION_COMMIT_SAVE_MAIL_DETAIL);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("mail_id", mail_id);
        request.add("goods_name", goods_name);
        request.add("weight", weight);
        request.add("money", money);
        request.add("content", content);
        return request;
    }

    /**
     * 获取社区版首页的信息
     *
     * @param user_id 用户id
     * @return
     */
    public static Request<String> getPannelmessageRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_PANNEL_MESSAGE_rEQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_PANNEL_MESSAGE_rEQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 获取预估价格
     *
     * @param user_id        用户id
     * @param express_id     快递公司id
     * @param start_province 寄送省份
     * @param start_city     寄送城市
     * @param end_province   收件省份
     * @param end_city       收件城市
     * @param weight         重量
     * @return
     */
    public static Request<String> getPredictMoneyRequest(String user_id, String express_id, String start_province
            , String start_city, String end_province, String end_city, String weight) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_PREDICT_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_PREDICT_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("express_id", express_id);
        request.add("start_province", start_province);
        request.add("start_city", start_city);
        request.add("end_province", end_province);
        request.add("end_city", end_city);
        request.add("weight", weight);
        return request;
    }

    /**
     * 获取快递公司图标信息
     * @param user_id
     * @return
     */
    public static Request<String> getExpressLogoRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_EXPRESS_LOGO_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_EXPRESS_LOGO_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 下载快递公司logo
     * @param user_id   用户id
     * @param express_id   快递公司id
     * @return
     */
    public static Request<Bitmap> uploadLogoRequest(String user_id,String express_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<Bitmap> request = NoHttp.createImageRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_EXPRESS_LOGO_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_EXPRESS_LOGO_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("express_id", express_id);
        return request;
    }

    /**
     * 短信管理
     * @param user_id   用户id
     * @param type  类型 0：失败 1：成功
     * @param page  页数
     * @return
     */
    public static Request<String> getMessageManagerRequest(String user_id,int type,int page) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_MESSAGE_MANAGER_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_MESSAGE_MANAGER_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("type", type);
        request.add("page", page);
        return request;
    }

    /**
     * 重新发送短信
     * @param user_id   用户id
     * @param type   1：单个  2：全部
     * @param message_id  短信id
     * @return
     */
    public static Request<String> sendMessageRequest(String user_id,int type,String message_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_SEND_MESSAGE_AGAIN, RequestMethod.GET);
        request.add("method", InterApi.ACTION_SEND_MESSAGE_AGAIN);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("type", type);
        request.add("message_id", message_id);
        return request;
    }

    /**
     * 获取投诉管理
     * @param user_id  用户id
     * @param type 类型
     * @return
     */
    public static Request<String> getComplainManagerRequest(String user_id,int type) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_COMPLAIN_MANAGER, RequestMethod.GET);
        request.add("method", InterApi.ACTION_COMPLAIN_MANAGER);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("type", type);
        return request;
    }

    /**
     * 搜索物流信息
     * @param user_id  用户id
     * @param numberse   运单号
     * @param express  快递公司id
     * @return
     */
    public static Request<String> getSearchPackRequest(String user_id,String numberse,String express) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_SEARCH_PACKAGE_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_SEARCH_PACKAGE_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("numberse", numberse);
        request.add("express", express);
        return request;
    }

    /**
     * 获取用户的基本信息
     * @param user_id
     * @return
     */
    public static Request<String> getUserBaseMessageRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_USER_BASEMESSAGE, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_USER_BASEMESSAGE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 获取通知公告消息
     * @param user_id
     * @param page
     * @return
     */
    public static Request<String> getNotificationRequest(String user_id,int page) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_NOTIFICATION_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_NOTIFICATION_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("page", page);
        return request;
    }

    /**
     * 获取系统消息
     * @param user_id
     * @param page
     * @return
     */
    public static Request<String> getSystmeMessageRequest(String user_id,int page) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_MESSAGE_CENTER_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_MESSAGE_CENTER_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("page", page);
        return request;
    }

    /**
     * 检测是否绑定账户
     * @param user_id
     * @return
     */
    public static Request<String> checkisBindAccountRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CHECK_BIND_ACCOUNT, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CHECK_BIND_ACCOUNT);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 绑定账号
     * @param user_id   用户id
     * @param type       1 银行卡 2 支付宝
     * @param realname  姓名
     * @param number  卡号
     * @param bank  开户行
     * @param account  支付宝账户
     * @return
     */
    public static Request<String> getBindAccountRequest(String user_id,String type
            ,String realname,String account,String bank,String number) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_BIND_ALI_ACCOUNT, RequestMethod.GET);
        request.add("method", InterApi.ACTION_BIND_ALI_ACCOUNT);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("type", type);
        request.add("realname", realname);
        request.add("number", number);
        request.add("bank", bank);
        request.add("account", account);
        return request;
    }

    /**
     * 获取申请提现
     * @param user_id
     * @param type
     * @param money
     * @return
     */
    public static Request<String> getApplyGetMoneyRequest(String user_id,String type,String money) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_APPLY_MONEY, RequestMethod.GET);
        request.add("method", InterApi.ACTION_APPLY_MONEY);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("type", type);
        request.add("money", money);
        return request;
    }

    /**
     * 获取消费记录
     * @param user_id   用户id
     * @param page   页码
     * @param start  开始时间
     * @param endtime 结束时间
     * @return
     */
    public static Request<String> getMoneyRecordRequest(String user_id,int page,String start,String endtime){
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_MONRY_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_MONRY_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("page", page);
        request.add("starttime", start);
        request.add("endtime", endtime);
        return request;
    }

    /**
     * 获取消费记录
     * @param user_id   用户id
     * @param page   页码
     * @param start  开始时间
     * @param endtime 结束时间
     * @return
     */
    public static Request<String> getConsumeRecordRequest(String user_id,int page,String start,String endtime){
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CONSUME_RECORD_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CONSUME_RECORD_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("page", page);
        request.add("starttime", start);
        request.add("endtime", endtime);
        return request;
    }


    /**
     * 获取客户列表请求
     * @param user_id
     * @return
     */
    public static Request<String> getClientListRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CLIENT_LIST_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CLIENT_LIST_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 获取客户管理列表
     * @param user_id
     * @return
     */
    public static Request<String> getClientManagerListRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CLIENT_MANAGER_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CLIENT_MANAGER_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 添加客户信息
     * @param user_id   用户id
     * @param realname   姓名
     * @param telephone   联系电话
     * @param region   地区
     * @param address 详细地址
     * @param company_name   公司名称
     * @param content  备注
     * @return
     */
    public static Request<String> addClientRequest(String user_id,String realname,
                                                   String telephone,String region,
                                                   String address,String company_name,String content) {

        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_ADD_CLIENT_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_ADD_CLIENT_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("realname", realname);
        request.add("telephone", telephone);
        request.add("region", region);
        request.add("address", address);
        request.add("company_name", company_name);
        request.add("content", content);
        return request;
    }

    /**
     * 编辑客户信息
     * @param user_id   用户id
     * @param realname  姓名
     * @param telephone  电话号码
     * @param region  地区
     * @param address  地址
     * @param company_name   公司名称
     * @param content  备注
     * @param customer_id  地址的id
     * @return
     */
    public static Request<String> editClientMessage(String user_id,String realname,
                                                    String telephone,String region,
                                                    String address,String company_name,String content,String customer_id) {

        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_EDIT_CLIENT_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_EDIT_CLIENT_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("realname", realname);
        request.add("telephone", telephone);
        request.add("region", region);
        request.add("address", address);
        request.add("company_name", company_name);
        request.add("content", content);
        request.add("customer_id", customer_id);
        return request;
    }

    /**
     * 删除客户信息
     * @param user_id   用户id
     * @param customer_id  地址id
     * @return
     */
    public static Request<String> deleteClientRequest(String user_id,String customer_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_DELETE_CLIENT_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_DELETE_CLIENT_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("customer_id", customer_id);
        return request;
    }

    /**
     * 获取客户订单
     * @param user_id
     * @param customer_id  客户id
     * @param page 页码
     * @return
     */
    public static Request<String> getClientOrderDetailRequest(String user_id,String customer_id,int page) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_CLIENT_ORDER_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_CLIENT_ORDER_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("customer_id", customer_id);
        request.add("page", page);
        return request;
    }

    /**
     * 个人中心订单
     * @param user_id
     * @param type
     * @return
     */
    public static Request<String> getMyOrderList(String user_id,int type) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_MY_ORDER_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_MY_ORDER_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("type", type);
        return request;
    }

    /**
     * 获取订单详情
     * @param user_id   用户id
     * @param order_id  订单详情
     * @return
     */
    public static Request<String> getMyOrderDetailRequest(String user_id,String order_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_MY_ORDER_DETAIL_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_MY_ORDER_DETAIL_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("order_id", order_id);
        return request;
    }

    /**
     * 获取物料商城
     * @param user_id  用户id
     * @return
     */
    public static Request<String> getGoodsListRequest(String user_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GOODS_LIST_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GOODS_LIST_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 获取商品详情接口
     * @param user_id
     * @param product_id
     * @return
     */
    public static Request<String> getGoodsListDetailRequest(String user_id,String product_id){
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GOODS_DETAIL_LIST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GOODS_DETAIL_LIST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("product_id", product_id);
        return request;
    }

    /**
     * 加入购物车
     * @param user_id   用户id
     * @param product_id   商品id
     * @param genre_id  型号id
     * @return
     */
    public static Request<String> joinGoodsRequest(String user_id,String product_id,String genre_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_JOIN_GOODS, RequestMethod.GET);
        request.add("method", InterApi.ACTION_JOIN_GOODS);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("product_id", product_id);
        request.add("genre_id", genre_id);
        return request;
    }

    /**
     * 立即购买
     * @param user_id   用户id
     * @param product_id   产品id
     * @param genre_id   型号id
     * @param address_id  地址id
     * @param number  数量
     * @return
     */
    public static Request<String> payForMoneyRightNowRequest(String user_id,String product_id,
                                                             String genre_id,String address_id,int number) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_PAYFOR_ATONCE, RequestMethod.GET);
        request.add("method", InterApi.ACTION_PAYFOR_ATONCE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("product_id", product_id);
        request.add("genre_id", genre_id);
        request.add("address_id", address_id);
        request.add("number", number);
        return request;
    }

    /**
     * 批量购买
     * @param user_id   用户id
     * @param cart_id   购物车记录id
     * @param address_id  地址id
     * @param content  备注
     * @return
     */
    public static  Request<String> payForMoreGoodsRequest(String user_id,String cart_id,
                                                         String address_id,String content) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_PAYFOR_MORE, RequestMethod.GET);
        request.add("method", InterApi.ACTION_PAYFOR_MORE);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("cart_id", cart_id);
        request.add("address_id", address_id);
        request.add("content", content);
        return request;
    }

    /**
     * 获取购物车列表
     * @param user_id
     * @return
     */
    public static Request<String> getShopCarGoodsRequest(String user_id){
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_GET_SHOP_CAR_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_GET_SHOP_CAR_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        return request;
    }

    /**
     * 删除购物车数据
     * @param user_id  用户id
     * @param cart_id  购物车记录id
     * @return
     */
    public static Request<String> deleteGoodsRequest(String user_id,String cart_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_DELETE_GOODS_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_DELETE_GOODS_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("cart_id", cart_id);
        return request;
    }

    /**
     * 修改商品的数量
     * @param user_id   用户id
     * @param cart_id  购物车id
     * @param number  数量
     * @return
     */
    public static Request<String> changeGoodsNumberRequest(String user_id,String cart_id,int number) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CHANGE_GOODS_NUMBER, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CHANGE_GOODS_NUMBER);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("cart_id", cart_id);
        return request;
    }

    /**
     * 获取交接管理
     * @param user_id   用户id
     * @param express_id  快递公司id
     * @param type   1: 表示待交接  2：已交接
     * @return
     */
    public static Request<String> getChangeManagerRequest(String user_id,String express_id,int type) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CHANGE_MANAGER_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CHANGE_MANAGER_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("express_id", express_id);
        request.add("type", type);
        return request;
    }

    /**
     * 确认交接
     * @param user_id   用户id
     * @param handto  签名文件
     * @param mailing_id   寄件id
     * @return
     */
    public static Request<String> sendChangeRequest(String user_id,String handto,String mailing_id) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CHANGE_SNED_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CHANGE_SNED_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("mailing_id", mailing_id);
        request.add("handto", handto);
        return request;
    }

    /**
     * 数据中心
     * @param user_id   用户id
     * @param starttime   开始时间时间戳
     * @param endtime  结束时间时间戳
     * @return
     */
    public static Request<String> getDataCenterRequest(String user_id,String starttime,String endtime) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_DATA_CENTER_rEQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_DATA_CENTER_rEQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("starttime", starttime);
        request.add("endtime", endtime);
        return request;
    }

    /**
     * 财务管理
     * @param user_id   用户id
     * @param starttime  开始时间
     * @param endtime 结束时间
     * @return
     */
    public static Request<String> getMoneyManagerRequest(String user_id,String starttime,String endtime) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_MONEY_MANAGER_REQEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_MONEY_MANAGER_REQEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("starttime", starttime);
        request.add("endtime", endtime);
        return request;
    }

    /**
     * 取消订单
     * @param user_id
     * @param mail_id
     * @return
     */
    public static Request<String> cannelOrderRequest(String user_id,String mail_id,String reason_id,String content) {
        String timeStamp = DateUtil.getCurrentTimeStamp();
        String randomStr = StringUtil.getRandomNumberString(7);
        String encryption = StringUtil.splitStringFromLast(timeStamp, 4);
        String signature = StringUtil.getSignatureString(timeStamp, randomStr, encryption);
        Request<String> request = NoHttp.createStringRequest(InterApi.SERVER_ADDRESS
                + InterApi.ACTION_CANNEL_ORDER_REQUEST, RequestMethod.GET);
        request.add("method", InterApi.ACTION_CANNEL_ORDER_REQUEST);
        request.add("signature", signature);
        request.add("timeStamp", timeStamp);     //时间戳
        request.add("randomStr", randomStr);     //随机值
        request.add("Encryption", encryption);    //加密值
        request.add("user_id", user_id);
        request.add("mail_id", mail_id);
        request.add("reason_id", reason_id);
        request.add("content", content);
        return request;
    }


}
