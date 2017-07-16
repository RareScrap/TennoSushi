package com.webtrust.tennosushi.utils;

/**
 * Класс, представляющий собой тег блюда, по которым можно производить поиск
 * @author RareScrap
 */
public class FoodTag {
    /** ID Тега блюда*/
    public final int id;
    /** Название тега */
    public final String name;

    /**
     * Коструктор, инициализирующий свои поля
     * @param id ID Тега блюда
     * @param name Название тега
     */
    public FoodTag(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
