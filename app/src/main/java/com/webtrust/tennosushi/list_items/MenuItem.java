package com.webtrust.tennosushi.list_items;

/**
 * Класс, представляющий собой "категорию" блюда (суши, пицца и т.д.).
 * @author RareScrap
 */
public class MenuItem {
    /** ID-имя категории, необходимое для поиска соответвующих блюд в загруженном JSON */
    public final String category;
    /** Название меню */
    public final String name;
    /** Ссылка на картинку меню */
    public final String picURL;


    /**
     * Конструктор, инициализирующий поля класса {@link MenuItem}.
     * @param name Название меню
     * @param picURL Ссылка на картинку меню
     */
    public MenuItem(String category, String name, String picURL) {
        this.category = category;
        this.name = name;
        this.picURL = picURL;
    }
}
