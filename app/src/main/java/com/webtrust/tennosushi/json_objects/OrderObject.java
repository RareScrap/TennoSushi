package com.webtrust.tennosushi.json_objects;

import com.google.gson.Gson;
import com.webtrust.tennosushi.list_items.FoodItem;
import com.webtrust.tennosushi.utils.FoodOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Объект, хранящий информацию о заказе.
 * Используется при передаче информации серверу.
 */

public class OrderObject {

    public String address;
    public Integer apartmentNumber;
    public Integer porchNumber;
    public String phoneNumber;
    public List<OrderFoodItem> items;

    public OrderObject(String address, int apartmentNumber,
                       int porchNumber, String phoneNumber, List<FoodItem> items) {
        this.address = address;
        this.apartmentNumber = apartmentNumber;
        this.porchNumber = porchNumber;
        this.phoneNumber = phoneNumber;

        this.items = new ArrayList<>();
        for (FoodItem fi: items) this.items.add(new OrderFoodItem(fi));
    }

    public OrderObject(String phoneNumber, List<FoodItem> items) {
        this.phoneNumber = phoneNumber;

        this.items = new ArrayList<>();
        for (FoodItem fi: items) this.items.add(new OrderFoodItem(fi));
    }

    public String getJSON() {
        return new Gson().toJson(this);
    }

    private class OrderFoodItem {
        public final int id;
        public final List<FoodOptions> customOptions;
        public final int count;

        public OrderFoodItem(FoodItem fi) {
            id = fi.id;
            customOptions = fi.customOptions;
            count = fi.count;
        }
    }
}
