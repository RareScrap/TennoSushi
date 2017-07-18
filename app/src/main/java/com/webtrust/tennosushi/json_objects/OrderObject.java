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
public class OrderObject implements JSONable {

    /** Метод сервера */
    public String method = "makeOrder";

    /** Адрес доставки */
    public String address;
    /** Номер квартиры */
    public Integer apartmentNumber;
    /** Номер подъезда */
    public Integer porchNumber;
    /** Номер телефона */
    public String phoneNumber;
    /** Список заказанных блюд */
    public List<OrderFoodItem> items;

    /**
     * Конструктор, используемый при выбранной доставке блюд на дом.
     * @param address Адрес доставки
     * @param apartmentNumber Номер квартиры
     * @param porchNumber Номер подъезда
     * @param phoneNumber Номер телефона
     * @param items Список заказанных блюд
     */
    public OrderObject(String address, int apartmentNumber,
                       int porchNumber, String phoneNumber, List<FoodItem> items) {
        this.address = address;
        this.apartmentNumber = apartmentNumber;
        this.porchNumber = porchNumber;
        this.phoneNumber = phoneNumber;

        this.items = new ArrayList<>();
        for (FoodItem fi: items) this.items.add(new OrderFoodItem(fi));
    }

    /**
     * Конструктор, используемый при выбранном самовывозе.
     * @param phoneNumber Номер телефона
     * @param items Список заказанных блюд
     */
    public OrderObject(String phoneNumber, List<FoodItem> items) {
        this.phoneNumber = phoneNumber;

        this.items = new ArrayList<>();
        for (FoodItem fi: items) this.items.add(new OrderFoodItem(fi));
    }

    /**
     * Преобразует OrderObject в JSON-представление.
     * @return JSON
     */
    public String getJSON() {
        return new Gson().toJson(this);
    }

    /**
     * Специальный класс для оптимизации передачи данных о продуктах.
     * Что-то типа клона FoodItem.
     */
    private class OrderFoodItem {
        /** ID блюда */
        public final int id;
        // TODO: срочно запилить другой массив опций!
        /** Кастомные опции блюда */
        public final List<FoodOptions> customOptions;
        /** Количество порций блюда. */
        public final int count;

        /**
         * Клонирующий конструктор.
         * @param fi FoodItem, который необходимо клонировать.
         */
        public OrderFoodItem(FoodItem fi) {
            id = fi.id;
            customOptions = fi.customOptions;
            count = fi.count;
        }
    }
}
