package com.nnmo.app_perfume;

public class cart_item {
    private String itemId;
    private String name;
    private Integer price;
    private String img;
    private int count;

    public cart_item() {
    }

    public cart_item(String itemId, String name, Integer price, String img, int count) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.img = img;
        this.count = count;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
