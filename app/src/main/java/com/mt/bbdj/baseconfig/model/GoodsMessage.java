package com.mt.bbdj.baseconfig.model;

import java.io.Serializable;
import java.util.List;

/**
 * Author : ZSK
 * Date : 2019/2/28
 * Description :
 */
public class GoodsMessage implements Serializable {

    private Goods goods;

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    private List<Goods> goodsList;

    public List<Goods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }

    public static class Goods implements Serializable{

        private String goodsName;    //商品名称
        private String goodsPrice;    //商品价格
        private String goodsNumber;    //商品数量
        private String goodsType;     //商品型号
        private String goodsPicture;    //商品图片
        private String goodsID;   //商品id
        private String genre_id;    //型号id

        public String getGenre_id() {
            return genre_id;
        }

        public void setGenre_id(String genre_id) {
            this.genre_id = genre_id;
        }

        public String getGoodsID() {
            return goodsID;
        }

        public void setGoodsID(String goodsID) {
            this.goodsID = goodsID;
        }

        public String getGoodsPicture() {

            return goodsPicture;
        }

        public void setGoodsPicture(String goodsPicture) {
            this.goodsPicture = goodsPicture;
        }

        public String getGoodsTypeName() {
            return goodsTypeName;
        }

        public void setGoodsTypeName(String goodsTypeName) {
            this.goodsTypeName = goodsTypeName;
        }

        private String goodsTypeName;    //商品型号名字

        public String getGoodsName() {
            return goodsName;
        }

        public void setGoodsName(String goodsName) {
            this.goodsName = goodsName;
        }

        public String getGoodsPrice() {
            return goodsPrice;
        }

        public void setGoodsPrice(String goodsPrice) {
            this.goodsPrice = goodsPrice;
        }

        public String getGoodsNumber() {
            return goodsNumber;
        }

        public void setGoodsNumber(String goodsNumber) {
            this.goodsNumber = goodsNumber;
        }

        public String getGoodsType() {
            return goodsType;
        }

        public void setGoodsType(String goodsType) {
            this.goodsType = goodsType;
        }

    }
}
