package com.webtrust.tennosushi.json_objects;

import com.google.gson.Gson;

/**
 * Класс, хранящий информацию об обратном звонке.
 * Используется при передаче информации серверу.
 */
public class CallMeObject implements JSONable {

    /** Метод сервера */
    public String method = "callMe";

    /** Номер телефона*/
    public String phoneNumber;

    /**
     * Стандартный конструктор.
     * @param phoneNumber Номер телефона.
     */
    public CallMeObject(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Преобразует {@link CallMeObject} в JSON-представление.
     * @return JSON
     */
    @Override
    public String getJSON() {
        return new Gson().toJson(this);
    }
}
