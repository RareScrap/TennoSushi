package com.webtrust.tennosushi.list_items;

/**
 * Класс, представляющий собой блюд из меню.
 *
 * @author RareScrap
 */
public class FoodItem {
    public final String name; // Название блюда
    public final double price; // Цена блюда
    public final String components; // Состав блюда
    public final String picURL; // Ссылка на картинку блюда

    public FoodItem(String name, double price, String components, String picURL) {
        this.name = name;
        this.price = price;
        this.components = components;
        this.picURL = picURL;
    }
}
