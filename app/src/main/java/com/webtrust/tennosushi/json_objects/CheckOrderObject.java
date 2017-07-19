package com.webtrust.tennosushi.json_objects;

import com.google.gson.Gson;
import com.webtrust.tennosushi.list_items.OrderItem;

/**
 * Класс, хранящий информацию о ID заказа.
 * Используется при получении информации
 * о статусе заказа с сервера.
 */
public class CheckOrderObject implements JSONable {

    /** Метод сервера */
    public String method = "checkOrder";
    /** ID заказа */
    public int order_id;

    /**
     * Конструктор класса, основанный на {@link OrderItem}.
     * @param oi Экземпляр класса {@link OrderItem}.
     */
    public CheckOrderObject(OrderItem oi) {
        order_id = oi.order_id;
    }

    /**
     * Преобразует {@link CheckOrderObject} в JSON-представление.
     * @return JSON
     */
    @Override
    public String getJSON() {
        return new Gson().toJson(this);
    }
}
