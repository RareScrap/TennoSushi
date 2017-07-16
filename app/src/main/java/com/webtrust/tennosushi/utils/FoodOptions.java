package com.webtrust.tennosushi.utils;

import java.util.List;

/**
 * Класс, представляющий опции блюда (такие как размер пиццы, толщина теста и наполнитель для вока)
 * @author RareScrap
 */
public class FoodOptions {
    /** ID группы опций блюда */
    public final int id;
    /** ID меню блюд, к которым применины данные опции */
    public final int categoryId;
    /** Название группы опций */
    public final String name;
    /** Список опций (Из-за схожести по структуре с {@link FoodTag}'ами, решено не создавать
     * новый класс */
    public final List<FoodTag> items;

    /**
     * Конструктор, инициализирущий свои поля
     * @param id ID группы опций блюда
     * @param categoryId ID меню блюд, к которым применины данные опции
     * @param name Название группы опций
     * @param items Список опций
     */
    public FoodOptions(int id, int categoryId, String name, List<FoodTag> items) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.items = items;
    }
}
