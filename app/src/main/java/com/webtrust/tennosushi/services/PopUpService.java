package com.webtrust.tennosushi.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.json_objects.CheckOrderObject;
import com.webtrust.tennosushi.list_items.OrderItem;
import com.webtrust.tennosushi.utils.LocalOrderExchanger;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис, занимающийся рассылкой уведомлений об изменениях
 * статусов заказов.
 */
public class PopUpService extends Service {

    /** Заказы */
    public static List<OrderItem> items;
    /** Загрузчик данных */
    public static LocalOrderExchanger loe;

    /** Поток, в котором выполняется получение актуальной информации о заказах */
    public static Thread thread;
    /** Булева, которая показывает состояние прерывности потока */
    public boolean isInterrupted;

    /**
     * Стандартный пустой конструктор.
     */
    public PopUpService() { }

    /**
     * Вызывается, когда сервис биндят. Хз, когда.
     * @param intent Интент, которым вызвали сервис.
     * @return Биндер
     */
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Вызывается, когда сервис создаётся.
     */
    @Override
    public void onCreate() {
        super.onCreate();

        loe = new LocalOrderExchanger(this);
        items = loe.readData();
    }

    /**
     * Вызывается, когда идёт сигнал работы сервису.
     * @param intent Интент, которым послали сигнал
     * @param flags Флаги
     * @param startId ID старта
     * @return Параметры повторного запуска
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // остановить поток, если он существует
        if (thread != null) {
            isInterrupted = true;
            while (thread.isAlive());
        }

        // запустить новый поток
        thread = new Thread(new Runnable() {
            @Override
            public void run() {
                // получаем менеджер уведомлений
                NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                while (!isInterrupted) {
                    try {
                        // проходим по всем заказам в обратном порядке
                        for (int i = items.size() - 1; i >= 0; i--) {
                            OrderItem oi = items.get(i);

                            // обновляем заказ
                            if (oi.refresh()) {
                                if (oi.getStatus() == OrderItem.OrderStatus.DELETED) {
                                    // если статус заказа удалённый, то удалить заказ с устройства
                                    items.remove(i);
                                    nm.cancel(oi.order_id);
                                    loe.writeData(items);
                                    continue;
                                }

                                // иначе просто вывести уведомление об изменении статуса заказа
                                nm.notify(oi.order_id, new Notification.Builder(PopUpService.this)
                                        .setContentTitle(String.format(getString(R.string.order_status_was_changed), oi.order_id))
                                        .setContentText(String.format(getString(R.string.current_status), oi.getStatus().toString()))
                                        .setSound(RingtoneManager.getActualDefaultRingtoneUri(PopUpService.this, RingtoneManager.TYPE_NOTIFICATION))
                                        .setSmallIcon(R.mipmap.ic_launcher).build());
                            }
                        }
                    } catch (Exception ex) { ex.printStackTrace(); }

                    try { Thread.sleep(5000); } catch (Exception ex) { ex.printStackTrace(); }
                }
            }
        });
        isInterrupted = false;
        thread.start();
        return START_STICKY;
    }
}
