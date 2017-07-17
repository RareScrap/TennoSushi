package com.webtrust.tennosushi.utils;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Класс, представляющий опции блюда (такие как размер пиццы, толщина теста и наполнитель для вока)
 * @author RareScrap
 */
public class FoodOptions implements Parcelable {
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

    protected FoodOptions(Parcel in) {
        id = in.readInt();
        categoryId = in.readInt();
        name = in.readString();
        items = in.createTypedArrayList(FoodTag.CREATOR);
    }

    public static final Creator<FoodOptions> CREATOR = new Creator<FoodOptions>() {
        @Override
        public FoodOptions createFromParcel(Parcel in) {
            return new FoodOptions(in);
        }

        @Override
        public FoodOptions[] newArray(int size) {
            return new FoodOptions[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(categoryId);
        dest.writeString(name);
        dest.writeTypedList(items);
    }
}
