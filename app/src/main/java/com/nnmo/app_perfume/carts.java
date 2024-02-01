package com.nnmo.app_perfume;
import com.nnmo.app_perfume.cart_item;

import java.util.ArrayList;
import java.util.List;

public class carts {
    private String idCart;
    private List<cart_item> items;

    public carts() {
        // Hàm tạo mặc định cho Firebase
        this.items = new ArrayList<>();
    }

    public carts(String idCart) {
        this.idCart = idCart;
        this.items = new ArrayList<>();
    }

    public String getIdCart() {
        return idCart;
    }

    public void setIdCart(String idCart) {
        this.idCart = idCart;
    }

    public List<cart_item> getItems() {
        return items;
    }

    public void setItems(List<cart_item> items) {
        this.items = items;
    }

    public void addItem(cart_item item) {
        if (item != null && item.getItemId() != null) {
            items.add(item);
        }
    }
}
