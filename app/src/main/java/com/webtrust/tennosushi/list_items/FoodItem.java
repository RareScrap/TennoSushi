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
    /** Категория блюда (для навиггации в JSON) */
    public final String category;
    /** Название категория блюда */
    public final String categoryName;

    /**
     * Конструктор, инициализирующий поля класса {@link FoodItem}.
     * @param name Название блюда
     * @param price Цена блюда
     * @param components Состав блюда
     * @param weight Вес порции блюда
     * @param picURL Ссылка на картинку блюда
     * @param category Категория блюда (для навиггации в JSON)
     * @param categoryName Название категории блюда
     */
    public FoodItem(String name, double price, String components, int weight, String picURL, String category, String categoryName) {
        this.name = name;
        this.price = price;
        this.components = components;
        this.weight = weight;
        this.picURL = picURL;
        this.category = category;
        this.categoryName = categoryName;
    }

    /**
     * Коструктор, копирующий уже существующий FoodItem, но без метаинформации
     * @param foodItem
     */
    public FoodItem(FoodItem foodItem) {
        this.name = foodItem.name;
        this.price = foodItem.price;
        this.components = foodItem.components;
        this.weight = foodItem.weight;
        this.picURL = foodItem.picURL;
        this.category = foodItem.category;
        this.categoryName = foodItem.categoryName;
    }
}
