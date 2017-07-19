package com.webtrust.tennosushi.json_objects;

import com.google.gson.Gson;
import com.webtrust.tennosushi.list_items.OrderItem;

/**
 * Класс, хранящий информацию об отмене заказа.
 */
public class CancelOrderObject implements JSONable {
    /** Метод сервера */
    public String method = "cancelOrder";
    /** ID заказа */
    public int order_id;

    /**
     * Стандартный коструктор, который копирует ID заказа из {@link OrderItem}.
     * @param oi
     */
    public CancelOrderObject(OrderItem oi) {
        order_id = oi.order_id;
    }

    /**
     * Преобразует {@link CancelOrderObject} в JSON-представление.
     * @return JSON
     */
    @Override
    public String getJSON() {
        return new Gson().toJson(this);
    }
}
