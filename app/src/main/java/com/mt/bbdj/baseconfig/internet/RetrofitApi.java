package com.mt.bbdj.baseconfig.internet;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

/**
 * Author : ZSK
 * Date : 2019/4/29
 * Description :  逐步替换成Retrofit
 */
public interface RetrofitApi {
    //订单列表
    @POST("/ServiceOrders/getOrdersList")
    @FormUrlEncoded
    Call<ResponseBody> getOrderList(@FieldMap(encoded = true) Map<String, String> params);

    //获取物品类型
    @GET("/BbdjApi/getItemType300")
    Call<ResponseBody> getGoodsType(@QueryMap Map<String, String> params, @Query("method") String method, @Query("user_id") String user_id);

    //获取寄件信息
    @POST("/ServiceOrders/getOrdersInfo")
    @FormUrlEncoded
    Call<ResponseBody> getExpressMessage(@FieldMap(encoded = true) Map<String, String> params, @Field("mail_id") String mail_id);

    //获取原单号
    @GET("/BbdjApi/getWaybillNumber600")
    Call<ResponseBody> getWaybillNumber(@QueryMap Map<String, String> baseParams, @QueryMap Map<String, String> params);

    //确认接单
    @POST("/ServiceOrders/confirmWaterOrder")
    @FormUrlEncoded
    Call<ResponseBody> confirmReceiveWaterOrder(@FieldMap(encoded = true) Map<String, String> baseParams, @Field("orders_id") String orders_id);

    //确认桶装水送达
    @POST("/ServiceOrders/confirmationService")
    @FormUrlEncoded
    Call<ResponseBody> confirmSendWaterOrder(@FieldMap(encoded = true) Map<String, String> baseParams, @Field("orders_id") String orders_id);

    //确认干洗送达
    @POST("/ServiceOrders/confirmationClearService")
    @FormUrlEncoded
    Call<ResponseBody> confirmSendClearOrder(@FieldMap(encoded = true) Map<String, String> baseParams, @Field("orders_id") String orders_id);

    //确认接单
    @POST("/ServiceOrders/confirmCleaningOrder")
    @FormUrlEncoded
    Call<ResponseBody> confirmReceiveClearOrder(@FieldMap(encoded = true) Map<String, String> baseParams, @Field("orders_id") String orders_id);

    //请求干洗类目
    @POST("/ServiceOrders/getServiceGoods")
    @FormUrlEncoded
    Call<ResponseBody> getClearType(@FieldMap(encoded = true) Map<String, String> baseParams, @Field("orders_id") String orders_id);

    //提交报价
    @POST("/ServiceOrders/confirmGoodsCategory")
    @FormUrlEncoded
    Call<ResponseBody> commitGoodsCategory(@FieldMap(encoded = true) Map<String, String> baseParams, @Field("orders_id") String orders_id,
                                           @Field("commodity_id") String commodity_id,@Field("number") String number);

    //请求干洗类目
    @POST("/ServiceOrders/getReasons")
    @FormUrlEncoded
    Call<ResponseBody> getCannelCause(@FieldMap(encoded = true) Map<String, String> baseParams);


    //服务类项目取消订单
    @POST("/ServiceOrders/cancellationMyOrder")
    @FormUrlEncoded
    Call<ResponseBody> confirmCannelOrder(@FieldMap(encoded = true) Map<String, String> baseParams,@Field("orders_id") String orders_id,@Field("reason_id") String reason_id);

    //服务订单
    @POST("/ServiceOrders/getWholeGoodsOrders")
    @FormUrlEncoded
    Call<ResponseBody> getServiceOrder(@FieldMap(encoded = true) Map<String, String> baseParams,@Field("type") String type,@Field("page") String page);

}
