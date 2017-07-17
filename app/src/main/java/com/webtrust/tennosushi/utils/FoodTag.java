package com.webtrust.tennosushi.utils;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Класс, представляющий собой тег блюда, по которым можно производить поиск
 * @author RareScrap
 */
public class FoodTag implements Parcelable {
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

    protected FoodTag(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<FoodTag> CREATOR = new Creator<FoodTag>() {
        @Override
        public FoodTag createFromParcel(Parcel in) {
            return new FoodTag(in);
        }

        @Override
        public FoodTag[] newArray(int size) {
            return new FoodTag[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }
}
