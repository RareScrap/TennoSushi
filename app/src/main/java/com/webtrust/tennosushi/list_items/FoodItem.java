package com.webtrust.tennosushi.list_items;

import com.webtrust.tennosushi.utils.FoodOptions;
import com.webtrust.tennosushi.utils.FoodTag;

import java.util.ArrayList;

/**
 * Класс, представляющий собой блюдо из какого-либо меню.
 * @author RareScrap
 */
public class FoodItem {
    /** ID блюда, необходимое для поиска соответвующих блюд в загруженном JSON */
    public final int id;
    /** ID категоии, к которой принадлежит блюдо */
    public final int categoryId;
    /** Название блюда */
    public final String name;
    /** Состав блюда */
    public final String components;
    /** Массив тегов блюда */
    public final ArrayList<FoodTag> tags/* = new ArrayList<>()*/;
    /** Сюда помещаются опции, если их требуется применить только к одому блюду из данной категории
     * Если пусто - используются опции, определеные в категории блюда */
    public final ArrayList<FoodOptions> customOptions;
    /** Желаемая заказчиком позиция категории блюда в списке всех категорий блюд */
    public final int position;
    /** Цена блюда */
    public final double price;
    /** Вес порции блюда */
    public final int weight;
    /** Ссылка на картинку блюда */
    public final String picURL;

    /** Опции блюд, доступные для данной категории (в JSON'е берется из родительской категории) */
    public final ArrayList<FoodOptions> options;

    /**
     * Коструктор, копирующий уже существующий FoodItem, но без метаинформации
     * @param foodItem
     */
    public FoodItem(FoodItem foodItem) {
        this.id = foodItem.id;
        this.categoryId = foodItem.categoryId;
        this.name = foodItem.name;
        this.components = foodItem.components;
        this.tags = foodItem.tags;
        this.customOptions = foodItem.customOptions;
        this.position = foodItem.position;
        this.price = foodItem.price;
        this.weight = foodItem.weight;
        this.picURL = foodItem.picURL;

        this.options = foodItem.options;
    }

    /**
     * Конструктор, инициализирующий свои поля.
     * @param id ID блюда, необходимое для поиска соответвующих блюд в загруженном JSON
     * @param categoryId ID категоии, к которой принадлежит блюдо
     * @param name Название блюда
     * @param components Состав блюда
     * @param tags Массив тегов блюда
     * @param customOptions Опции, котоые должны быть у блюда вопреки опциям категории-родителя
     * @param position Желаемая заказчиком позиция категории блюда в списке всех категорий блюд
     * @param price Цена блюда
     * @param weight Вес порции блюда
     * @param picURL Ссылка на картинку блюда
     * @param options Обции к блюду, берущиеся из категории-родителя
     */
    public FoodItem(int id, int categoryId, String name, String components, ArrayList<FoodTag> tags,
                    ArrayList<FoodOptions> customOptions, int position, int price, int weight, String picURL,
                    ArrayList<FoodOptions> options) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.components = components;
        this.tags = tags;
        this.customOptions = customOptions;
        this.position = position;
        this.price = price;
        this.weight = weight;
        this.picURL = picURL;

        this.options = options; // Это не берется из JSON-части блюда. Это берется из его категории-родителя
    }

    /**
     * Преобразует тег к строковому типу
     * @return Возвращает только поле {@link #name}
     */
    @Override
    public String toString() {
        return name;
    }
}
