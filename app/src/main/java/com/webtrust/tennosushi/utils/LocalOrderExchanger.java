package com.webtrust.tennosushi.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.webtrust.tennosushi.list_items.OrderItem;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Класс, занимающийся загрузкой и выгрузкой данных о заказах
 * в локальное хранилище.
 */
public class LocalOrderExchanger {

    /** Заказы */
    public List<OrderItem> items;
    /** Контекст (нужен для доступа к приватной директории приложения */
    public transient Context context;

    /**
     * Стандартный пустой конструктор.
     */
    public LocalOrderExchanger() {}

    /**
     * Конструктор с контекстом.
     * @param context Контекст
     */
    public LocalOrderExchanger(Context context) {
        this.context = context;
    }

    /**
     * Загружает информацию о заказах.
     * @return Заказы
     */
    public List<OrderItem> readData() {
        try {
            FileInputStream fis = context.openFileInput("orders.json");
            Scanner sc = new Scanner(fis);
            String json = "";
            while (sc.hasNextLine()) json += sc.nextLine();
            sc.close();
            fis.close();
            LocalOrderExchanger loe = new GsonBuilder().setDateFormat("MMM dd, yyyy HH:mm:ss")
                    .create().fromJson(json, LocalOrderExchanger.class);
            return loe.items;
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Выгружает информацию о заказах.
     * @param items Заказы
     */
    public void writeData(List<OrderItem> items) {
        try {
            this.items = items;
            FileOutputStream los = context.openFileOutput("orders.json", Context.MODE_PRIVATE);
            String json = new Gson().toJson(this);
            los.write(json.getBytes("UTF-8"));
            los.close();
        } catch (Exception ex) { ex.printStackTrace(); }
    }
}
