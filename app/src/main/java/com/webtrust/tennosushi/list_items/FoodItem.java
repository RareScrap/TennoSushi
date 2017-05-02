package com.webtrust.tennosushi.list_items;

/**
 * Класс, представляющий собой блюд из меню.
 * @author RareScrap
 */
public class FoodItem {
    /** Название блюда */
    public final String name;
    /** Цена блюда */
    public final double price;
    /** Состав блюда */
    public final String components;
    /** Вес порции блюда */
    public final int weight;
    /** Ссылка на картинку блюда */
    public final String picURL;
    /** Категория блюда */
    public final String category;

    /**
     * Конструктор, инициализирующий поля класса {@link FoodItem}.
     * @param name Название блюда
     * @param price Цена блюда
     * @param components Состав блюда
     * @param picURL Ссылка на картинку блюда
     */
    public FoodItem(String name, double price, String components, int weight, String picURL, String category) {
        this.name = name;
        this.price = price;
        this.components = components;
        this.weight = weight;
        this.picURL = picURL;
        this.category = category;
    }
}
