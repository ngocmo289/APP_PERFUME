package com.nnmo.app_perfume;

public class order {
    private String orderId;
    private String nameuser;
    private Integer totalprice;
    private String address, phonenumber,status;

    public order(String orderId, String nameuser, Integer totalprice, String address, String phonenumber, String status) {
        this.orderId = orderId;
        this.nameuser = nameuser;
        this.totalprice = totalprice;
        this.address = address;
        this.phonenumber = phonenumber;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getNameuser() {
        return nameuser;
    }

    public void setNameuser(String nameuser) {
        this.nameuser = nameuser;
    }

    public Integer getTotalprice() {
        return totalprice;
    }

    public void setTotalprice(Integer totalprice) {
        this.totalprice = totalprice;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public order() {
    }
}
