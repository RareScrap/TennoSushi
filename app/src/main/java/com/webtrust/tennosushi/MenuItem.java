package com.webtrust.tennosushi;

/**
 * Класс, представляющий собой элемент меню (суши, пицца и т.д.)
 * {@link MenuItem} представляет собой "категорию" блюда.
 *
 * @author RareScrap
 */
public class MenuItem {
    public final String name;
    public final String picURL;

    public MenuItem(String name, String picURL) {
        this.name = name;
        this.picURL = picURL;
    }
}
