package com.mt.bbdj.baseconfig.internet;

/**
 * Author : ZSK
 * Date : 2018/12/27
 * Description :  网络接口
 */
public class InterApi {

    /**
     * 服务器地址
     */
    // public static final String SERVER_ADDRESS = "http://web2.mingtaikeji.cn/BbdjApi/";
    // public static final String SERVER_ADDRESS = "http://192.168.1.116/BbdjApi/";
      public static final String SERVER_ADDRESS = "http://www.81dja.com/BbdjApi/";
    // public static final String SERVER_ADDRESS = "http://web1.mingtaikeji.cn/BbdjApi/";

    /**
     * 获取验证码
     */
    public static final String ACTION_GET_IDENTIFY_CODE = "getcode100";

    /**
     * 登录
     */
    public static final String ACTION_LOGIN = "login100";

    /**
     * 上传图片
     */
    public static final String ACTION_COMMIT_PICTURE = "upload100";

    /**
     * 提交注册信息
     */
    public static final String ACTION_COMMIT_REGISTER_MESSAGE = "register100";

    /**
     * 找回密码
     */
    public static final String ACTION_CHANGE_PASSWORD = "forget100";

    /**
     * 修改密码
     */
    public static final String ACTION_CHANGE_NEW_PASSWORD = "changePassword900";

    /**
     * 充值记录
     */
    public static final String ACTION_GET_RECHARGE_RECODE = "rechargeRecord200";

    /**
     * 短信充值面板
     */
    public static final String ACTION_GET_RECHARGE_PANNEL = "smsMerchandise200";

    /**
     * 短信充值
     */
    public static final String ACTION_RECHARGE_MONEY = "smsRecharge200";

    /**
     * 面单单价
     */
    public static final String ACTION_PANNEL_UNITE_PRICE = "singleMerchandise200";

    /**
     * 面单充值
     */
    public static final String ACTION_PANNEL_RECHARGEL = "faceRecharge200";

    /**
     * 驿站地址
     */
    public static final String ACTION_STAGE_ADDRESS = "getAddressBook300";

    /**
     * 收货地址
     */
    public static final String ACTION_GET_MY_ADDRESS = "getAddress960";

    /**
     * 获取省市县
     */
    public static final String ACTION_GET_AREA = "getRegion400";

    /**
     * 更新快递公司状态
     */
    public static final String ACTION_UPDATE_EXPRESS = "getExpressInfo600";

    /**
     * 修改地址
     */
    public static final String ACTION_CHNAGE_ADDRESS = "saveAddressBook300";

    /**
     * 修改收货地址
     */
    public static final String ACTION_CHNAGE_MY_ADDRESS = "saveAddress960";

    /**
     * 添加地址
     */
    public static final String ACTION_ADD_ADDRESS = "addAddressBook300";

    /**
     * 添加收获地址
     */
    public static final String ACTION_ADD_MY_ADDRESS = "addAddress960";

    /**
     * 删除地址簿
     */
    public static final String ACTION_DELETE_ADDRESS = "deleteAddressBook300";

    /**
     * 删除收货地址
     */
    public static final String ACTION_DELETE_MY_ADDRESS = "deleteAddress960";

    /**
     * 物流公司
     */
    public static final String ACTION_EXPRESSAGE_LIST = "getExpress300";

    /**
     * 物品类型
     */
    public static final String ACTION_GOODS_TYPE = "getItemType300";

    /**
     * 身份认证
     */
    public static final String ACTION_COMMIT_AUTHENTICATION = "realnameAuthentication300";

    /**
     * 实名验证
     */
    public static final String ACTION_IS_IDENTIFY_REQUEST = "testingAuthentication300";

    /**
     * 手动下单
     */
    public static final String ACTION_COMMIT_ORDER = "placeAnOrder300";

    /**
     * 预估价格
     */
    public static final String ACTION_ESTIMMATE_REQUEST = "freightDateEstimate300";

    /**
     * 待收件
     */
    public static final String ACTION_WAIT_COLLECT = "waitingForCollection300";

    /**
     * 已处理
     */
    public static final String ACTION_HAVE_FINISH = "processed400";

    /**
     * 待打印
     */
    public static final String ACTION_WAIT_PRINT = "pendingPrinting400";

    /**
     * 订单详情
     */
    public static final String ACTION_ORDER_DETAIL = "getMailingdetails500";

    /**
     * 取消订单原因
     */
    public static final String ACTION_CANNEL_ORDER_CAUSE = "getReason400";

    /**
     * 取消订单
     */
    public static final String ACTION_COMMIT_CANNEL_ORDER = "cancellationOrder500";

    /**
     * 打印时验证身份是否实名
     */
    public static final String ACTION_IDETIFY_AT_SEAL = "supplementInformation600";

    /**
     * 先存后打 保存信息
     */
    public static final String ACTION_COMMIT_SAVE_MAIL = "saveInformation500";

    /**
     * 再打一单
     */
    public static final String ACTION_COMMIT_SAVE_MAIL_DETAIL = "fightWaybillNumber600";

    /**
     * 寄件管理中身份认证
     */
    public static final String ACTION_COMMIT_IDENTIFICATION_FOR_MANAGER = "testingInformation600";

    /**
     * 立刻打印 之后的信息补充
     */
    public static final String ACTION_PRINT_ONCE_REQUEST = "getWaybillNumber600";

    /**
     * 获取首页面板中的信息
     */
    public static final String ACTION_GET_PANNEL_MESSAGE_rEQUEST = "indexBlending200";

    /**
     * 获取预估价
     */
    public static final String ACTION_GET_PREDICT_REQUEST = "freightDateEstimate300";

    /**
     * 获取快递公司
     */
    public static final String ACTION_GET_EXPRESS_LOGO_REQUEST = "getExpress600";

    /**
     * 短信管理
     */
    public static final String ACTION_GET_MESSAGE_MANAGER_REQUEST = "getSMSManagement700";

    /**
     * 发送短信
     */
    public static final String ACTION_SEND_MESSAGE_AGAIN = "againSendSMS700";

    /**
     * 获取投诉管理
     */
    public static final String ACTION_COMPLAIN_MANAGER = "getComplaintlist700";

    /**
     * 搜索物流信息
     */
    public static final String ACTION_SEARCH_PACKAGE_REQUEST = "getLogisticsSelect700";

    /**
     * 获取用户基本信息
     */
    public static final String ACTION_GET_USER_BASEMESSAGE = "getmessage100";

    /**
     * 获取通知公告
     */
    public static final String ACTION_GET_NOTIFICATION_REQUEST = "getNoticelist700";

    /**
     * 获取系统消息
     */
    public static final String ACTION_GET_MESSAGE_CENTER_REQUEST = "getSystemlist700";

    /**
     * 检测是否绑定账户
     */
    public static final String ACTION_CHECK_BIND_ACCOUNT = "testBindAccount700";

    /**
     * 绑定支付宝账号
     */
    public static final String ACTION_BIND_ALI_ACCOUNT = "getBindAccount700";

    /**
     * 申请提现
     */
    public static final String ACTION_APPLY_MONEY = "getCashApply700";

    /**
     *  获取提现记录
     */
    public static final String ACTION_GET_MONRY_REQUEST = "getWithdrawalslist700";

    /**
     * 消费记录
     */
     public static final String ACTION_CONSUME_RECORD_REQUEST = "getConsumelist700";

    /**
     * 获取客户含订单列表
     */
    public static final String ACTION_CLIENT_LIST_REQUEST = "getCustomerdata800";

    /**
     * 获取客户管理列表
     */
    public static final String ACTION_CLIENT_MANAGER_REQUEST = "getCustomerlist800";

    /**
     *  添加客户信息
     */
    public static final String ACTION_ADD_CLIENT_REQUEST = "addCustomer800";

    /**
     * 删除客户信息
     */
    public static final String ACTION_DELETE_CLIENT_REQUEST = "deleteCustomer800";

    /**
     * 编辑客户信息
     */
    public static final String ACTION_EDIT_CLIENT_REQUEST = "saveCustomer800";

    /**
     * 获取客户订单
     */
    public static final String ACTION_GET_CLIENT_ORDER_REQUEST = "getCustomerMailing800";

    /**
     * 我的订单
     */
    public static final String ACTION_MY_ORDER_REQUEST = "getmyOrders960";

    /**
     * 获取订单详情
     */
    public static final String ACTION_MY_ORDER_DETAIL_REQUEST = "getmyOrdersdetails960";

    /**
     * 获取物料商城列表
     */
    public static final String ACTION_GOODS_LIST_REQUEST = "getProductdata950";

    /**
     * 获取商品详情
     */
    public static final String ACTION_GOODS_DETAIL_LIST = "getProductdetails950";

    /**
     * 加入购物车
     */
    public static final String ACTION_JOIN_GOODS = "addProductcart950";

    /**
     * 立刻清算
     */
    public static final String ACTION_PAYFOR_ATONCE = "buyProductonce950";

    /**
     * 批量购买
     */
    public static final String ACTION_PAYFOR_MORE = "buyProductcart950";

    /**
     * 获取购物车列表
     */
    public static final String ACTION_GET_SHOP_CAR_REQUEST = "getProductcart950";

    /**
     * 删除购物车商品
     */
    public static final String ACTION_DELETE_GOODS_REQUEST = "deleteProductcart950";

    /**
     * 修改商品的数量
     */
    public static final String ACTION_CHANGE_GOODS_NUMBER = "saveCartnumber950";

    /**
     * 获取交接管理
     */
    public static final String ACTION_CHANGE_MANAGER_REQUEST = "getHandoverlist980";

    /**
     * 确认交接
     */
    public static final String ACTION_CHANGE_SNED_REQUEST = "getConfirmHandover980";

    /**
     * 数据中心
     */
    public static final String ACTION_DATA_CENTER_rEQUEST = "getFinancialData980";

    /**
     * 财务管理
     */
    public static final String ACTION_MONEY_MANAGER_REQEST = "getFinanceData990";

    /**
     * 取消订单
     */
    public static final String ACTION_CANNEL_ORDER_REQUEST = "CancellationMail1010";








}
