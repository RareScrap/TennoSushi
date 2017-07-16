package com.webtrust.tennosushi.list_items;

import android.os.Parcel;
import android.os.Parcelable;

import com.webtrust.tennosushi.utils.FoodOptions;

import java.util.List;

/**
 * Класс, представляющий собой "категорию" блюда (суши, пицца и т.д.).
 * @author RareScrap
 */
public class MenuItem implements Parcelable {
    /** ID категории, необходимое для поиска соответвующих блюд в загруженном JSON */
    public final int id;
    /** Название меню */
    public final String name;
    /** Желаемая заказчиком позиция категории блюда в списке всех категорий блюд */
    public final int position;
    /** Опции блюд, доступные для данной категории */
    public final List<FoodOptions> options;
    /** Ссылка на картинку меню */
    public final String picURL;

    /**
     * Конструктор, инициализирующий поля класса {@link MenuItem}.
     * @param id ID категории
     * @param name Название меню
     * @param position Желаемая заказчиком позиция категории блюда в списке всех категорий блюд
     * @param options Опции блюд, доступные для данной категории
     * @param picURL Ссылка на картинку меню
     */
    public MenuItem(int id, String name, int position, List<FoodOptions> options, String picURL) {
        this.id = id;
        this.name = name;
        this.position = position;
        this.options = options;
        this.picURL = picURL;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(position);
        dest.writeTypedList(options);
        dest.writeString(picURL);
    }
}
