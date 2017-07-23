package com.webtrust.tennosushi.json_objects;

import com.google.gson.Gson;

/**
 * Класс, хранящий локально сохранённую информацию об адресе доставки
 */
public class SavedAddressObject implements JSONable {
    /** Номер телефона */
    public String phoneNumber;
    /** Адрес доставки */
    public String address;
    /** Номер квартиры */
    public int apartmentNumber;
    /** Номер подъезда */
    public int porchNumber;

    /**
     * Пустой конструктор.
     */
    public SavedAddressObject() {}

    /**
     * Стандартный конструктор.
     * @param phoneNumber Номер телефона
     * @param address Адрес доставки
     * @param apartmentNumber Номер квартиры
     * @param porchNumber Номер подъезда
     */
    public SavedAddressObject(String phoneNumber, String address,
                              String apartmentNumber, String porchNumber) {
        this.phoneNumber = phoneNumber;
        this.address = address;

        // если номер квартиры не парсится, указать -1
        try { this.apartmentNumber = Integer.parseInt(apartmentNumber); }
        catch (Exception ex) { this.apartmentNumber = -1; }

        // если номер подъезда не парсится, указать -1
        try { this.porchNumber = Integer.parseInt(porchNumber); }
        catch (Exception ex) { this.porchNumber = -1; }
    }

    /**
     * Возвращает новый экземпляр класса {@link SavedAddressObject} на основе JSON.
     * @param json JSON
     * @return Новый экземпляр класса {@link SavedAddressObject}
     */
    public static SavedAddressObject fromJSON(String json) {
        return new Gson().fromJson(json, SavedAddressObject.class);
    }

    /**
     * Преобразует {@link SavedAddressObject} в JSON-представление.
     * @return JSON
     */
    @Override
    public String getJSON() {
        return new Gson().toJson(this);
    }
}
