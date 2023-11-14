package com.nnmo.app_perfume;

public class model_product {
    String name, des, img;
    Integer price, sale;
    Boolean item_new, item_popular, item_sale;

    model_product(){

    }

    public model_product(String name, String des, String img, Integer price, Integer sale, Boolean item_new, Boolean item_popular, Boolean item_sale) {
        this.name = name;
        this.des = des;
        this.img = img;
        this.price = price;
        this.sale = sale;
        this.item_new = item_new;
        this.item_popular = item_popular;
        this.item_sale = item_sale;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getSale() {
        return sale;
    }

    public void setSale(Integer sale) {
        this.sale = sale;
    }

    public Boolean getItem_new() {
        return item_new;
    }

    public void setItem_new(Boolean item_new) {
        this.item_new = item_new;
    }

    public Boolean getItem_popular() {
        return item_popular;
    }

    public void setItem_popular(Boolean item_popular) {
        this.item_popular = item_popular;
    }

    public Boolean getItem_sale() {
        return item_sale;
    }

    public void setItem_sale(Boolean item_sale) {
        this.item_sale = item_sale;
    }
}
