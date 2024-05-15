package com.app.marketpal;

public class ProductClass {

    String market;
    String url;
    String price;
    String name;
    String id;
    String ASSORTEMTNS_DATA[][];
    String desc;
    String original_name;
    String brand_id;
    String coupon_value;
    String value_discount;

    public String getCoupon_value() {
        return coupon_value;
    }

    public void setCoupon_value(String coupon_value) {
        this.coupon_value = coupon_value;
    }

    public String getValue_discount() {
        return value_discount;
    }

    public void setValue_discount(String value_discount) {
        this.value_discount = value_discount;
    }

    ProductClass(){}

    public String getMarket() {
        return market;
    }
    public void setMarket(String market) {
        this.market = market;
    }

    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

    public String getPrice() {
        return price;
    }
    public void setPrice(String price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setID(String id) {this.id = id;}
    public String getID() {return id;}

    public void setASSORTEMTNS_DATA(String[][] ASSORTEMTNS_DATA) {this.ASSORTEMTNS_DATA = ASSORTEMTNS_DATA;}
    public String[][] getASSORTEMTNS_DATA() {return ASSORTEMTNS_DATA;}

    public String getBrand_id() {return brand_id;}

    public void setBrand_id(String brand_id) {this.brand_id = brand_id;}

    public void setDesc(String desc) {this.desc = desc;}
    public String getDesc() {return desc;}

    public void setOrigianlName(String original_name) {this.original_name = original_name;}
    public String getOrigianlName() {return original_name;}

    ProductClass(String coupon_value , String value_discount , String brand_id ,String market , String url , String price , String name , String id , String ASSORTEMTNS_DATA[][] , String desc , String original_name){

        this.market = market;
        this.url = url;
        this.price = price;
        this.name = name;
        this.id = id;
        this.ASSORTEMTNS_DATA =  ASSORTEMTNS_DATA;
        this.desc = desc;
        this.original_name = original_name;
        this.brand_id = brand_id;
        this.coupon_value = coupon_value;
        this.value_discount = value_discount;
    }



}
