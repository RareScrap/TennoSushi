package com.webtrust.tennosushi.list_items;

import com.google.gson.Gson;
import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.json_objects.CheckOrderObject;
import com.webtrust.tennosushi.services.PopUpService;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

// TODO: запилить отдельную Activity для отображения состояния заказов

/**
 * Класс, представляющий собой заказ.
 */
public class OrderItem {
    /**
     * Перечисление всевозможных статусов заказа.
     */
    public enum OrderStatus {
        NOT_STARTED() {
            @Override
            public String toString() {
                return PopUpService.loe.context.getString(R.string.not_started);
            }
        }, COOKING() {
            @Override
            public String toString() {
                return PopUpService.loe.context.getString(R.string.cooking);
            }
        }, DONE() {
            @Override
            public String toString() {
                return PopUpService.loe.context.getString(R.string.done);
            }
        },  // этап готовки
        COURIER_LEFT() {
            @Override
            public String toString() {
                return PopUpService.loe.context.getString(R.string.courier_left);
            }
        }, COURIER_ARRIVE() {
            @Override
            public String toString() {
                return PopUpService.loe.context.getString(R.string.courier_arrives);
            }
        },        // этап доставки
        COMPLETED() {
            @Override
            public String toString() {
                return PopUpService.loe.context.getString(R.string.order_completed);
            }
        }, DELETED,                          // заключительный этап
        UNDEFINED                                            // неопределено
    }

    /** ID заказа */
    public int order_id;
    /** Статус заказа */
    public int order_status = 0;

    /**
     * Возвращает статус заказа из перечисления.
     * @return Статус
     */
    public OrderStatus getStatus() {
        switch (order_status) {
            case 0: return OrderStatus.NOT_STARTED;
            case 1: return OrderStatus.COOKING;
            case 2: return OrderStatus.DONE;
            case 3: return OrderStatus.COURIER_LEFT;
            case 4: return OrderStatus.COURIER_ARRIVE;
            case 5: return OrderStatus.COMPLETED;
            case 6: return OrderStatus.DELETED;
        }
        return OrderStatus.UNDEFINED;
    }

    /**
     * Получает актуальные данные о заказе с сервера.
     * @return true - статус изменился, false - статус не изменился, null - произошла ошибка
     */
    public Boolean refresh() {
        try {
            CheckOrderObject coo = new CheckOrderObject(this);

            HttpURLConnection http = (HttpURLConnection) new URL("http://romhacking.pw:1234/").openConnection();

            http.setDoOutput(true);
            OutputStream os = http.getOutputStream();
            os.write(coo.getJSON().getBytes("UTF-8"));
            os.close();

            Scanner sc = new Scanner(http.getInputStream());
            String answer = "";
            if (sc.hasNext()) answer = sc.next();

            OrderItem oi = new Gson().fromJson(answer, OrderItem.class);
            boolean result = oi.order_status != order_status;
            order_status = oi.order_status;
            return result;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
