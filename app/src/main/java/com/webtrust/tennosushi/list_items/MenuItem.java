package com.webtrust.tennosushi.list_items;

/**
 * Класс, представляющий собой элемент меню (суши, пицца и т.д.)
 * {@link MenuItem} представляет собой "категорию" блюда.
 *
 * @author RareScrap
 */
public class MenuItem {
    public final String name; // Название меню
    public final String picURL; // Ссылка на картинку меню

    public MenuItem(String name, String picURL) {
        this.name = name;
        this.picURL = picURL;
    }
}
