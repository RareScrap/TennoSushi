package com.webtrust.tennosushi.adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.webtrust.tennosushi.OrdersActivity;
import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.json_objects.CancelOrderObject;
import com.webtrust.tennosushi.list_items.OrderItem;
import com.webtrust.tennosushi.services.PopUpService;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static com.webtrust.tennosushi.services.PopUpService.loe;
import static com.webtrust.tennosushi.services.PopUpService.nm;

/**
 * Адаптер для {@link OrdersActivity}.
 */
public class OrderItemRecyclerViewAdapter extends RecyclerView.Adapter<OrderItemRecyclerViewAdapter.ViewHolder> {

    /** Объекты, обрабатываемые адаптером */
    List<OrderItem> items;
    /** Контекст */
    Context context;

    /**
     * Стандартный конструктор.
     * @param items Лист заказов
     */
    public OrderItemRecyclerViewAdapter(List<OrderItem> items) {
        this.items = items;
    }

    /**
     * Этот метод создаёт холдеры для вьюх.
     * @param parent Группы вьюшек
     * @param viewType Тип вьюшки
     * @return Холдер для вьюхи
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(context).inflate(R.layout.order_item, parent, false);
        return new ViewHolder(v);
    }

    /**
     * Этот метод присваивает значения полям на вьюшке.
     * @param holder Холдер
     * @param position Позиция объекта, на основе которого будет происходить наполнение
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // получаем объект заказа
        OrderItem oi = items.get(position);

        // устанавливаем значения простых полей
        holder.id.setText(String.format(Locale.getDefault(), "%s (#%d)", oi.desc, oi.order_id));
        holder.status.setText(String.format(context.getString(R.string.status), oi.getStatus()));

        // генерируем значение для поля времени
        String orderTime = String.format(context.getString(R.string.order_time),
                DateFormat.getDateTimeInstance().format(oi.order_date));
        orderTime += " ";
        long span = System.currentTimeMillis() - oi.order_date.getTime();
        if (TimeUnit.MILLISECONDS.toDays(span) != 0) { // разница во времени больше дня
            orderTime += String.format(context.getString(R.string.its_been_time),
                    context.getString(R.string.days_late), TimeUnit.MILLISECONDS.toDays(span));
        } else if (TimeUnit.MILLISECONDS.toHours(span) != 0) { // разница во времени больше часа
            orderTime += String.format(context.getString(R.string.its_been_time),
                    context.getString(R.string.hours_late), TimeUnit.MILLISECONDS.toHours(span));
        } else if (TimeUnit.MILLISECONDS.toMinutes(span) != 0) { // разница во времени больше минуты
            orderTime += String.format(context.getString(R.string.its_been_time),
                    context.getString(R.string.minutes_late), TimeUnit.MILLISECONDS.toMinutes(span));
        } else {
            orderTime += String.format(context.getString(R.string.its_been_time),
                    context.getString(R.string.seconds_late), TimeUnit.MILLISECONDS.toSeconds(span));
        }

        holder.date.setText(orderTime);
        holder.oi = oi;

        if (oi.order_status == 5) {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    /**
     * Возвращает количество элементов, обрабатываемых адаптером.
     * @return
     */
    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Класс, хранящий ссылки на объекты GUI.
     */
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, DialogInterface.OnClickListener {
        /** Ссылка на элемент GUI, показывающий заказанные блюда и ID заказа */
        TextView id;
        /** Ссылка на элемент GUI, показывающий статус заказа */
        TextView status;
        /** Ссылка на элемент GUI, показывающий время заказа и прошедшее время */
        TextView date;
        /** Ссылка на кнопку удаления заказа */
        ImageButton deleteButton;

        /** Приассигненный OrderItem к ViewHolder */
        OrderItem oi;

        /**
         * Стандартный коструктор
         * @param itemView View, на котором находятся все элементы GUI
         */
        public ViewHolder(View itemView) {
            super(itemView);

            // получаем ссылки
            id = (TextView) itemView.findViewById(R.id.order_item_id);
            status = (TextView) itemView.findViewById(R.id.order_item_status);
            date = (TextView) itemView.findViewById(R.id.order_item_date);
            deleteButton = (ImageButton) itemView.findViewById(R.id.order_item_delete);

            // присваиваем прослушку кликов по кнопке удаления
            deleteButton.setOnClickListener(this);
        }

        /**
         * Происходит при клике НА КНОПКУ УДАЛЕНИЯ.
         * @param v Кнопка
         */
        @Override
        public void onClick(View v) {
            // выдаём warning
            new AlertDialog.Builder(context).setTitle(R.string.confirm_your_choice)
                    .setMessage(R.string.sure_cancel_order)
                    .setNegativeButton(R.string.no, null)
                    .setPositiveButton(R.string.yes, this)
                    .setIcon(R.drawable.ic_help_outline_black_24dp)
                    .create().show();
        }

        /**
         * Происходит при клике НА КНОПКУ "ДА" НА WARNING'Е.
         * @param dialog Диалог
         * @param which Номер кнопки
         */
        @Override
        public void onClick(DialogInterface dialog, int which) {
            // обновляем данные (удаляем объект из массива, перерисовываем RecyclerView)
            items.remove(oi);
            OrdersActivity.orderList.getRecycledViewPool().clear();
            notifyDataSetChanged();

            // удаляем нотификатор, если он есть, записываем обновлённый массив заказов в локальное хранилище
            nm.cancel(oi.order_id);
            loe.writeData(items);

            // посылаем информацию об отмене заказа
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // устанавливаем соединение
                        HttpURLConnection http = (HttpURLConnection) new URL("http://romhacking.pw:1234/").openConnection();

                        // отсылаем данные
                        http.setDoOutput(true);
                        OutputStream os = http.getOutputStream();
                        os.write(new CancelOrderObject(oi).getJSON().getBytes("UTF-8"));
                        os.close();

                        // принимаем данные
                        String answer = "";
                        Scanner sc = new Scanner(http.getInputStream());
                        while (sc.hasNextLine()) answer += sc.nextLine();
                        sc.close();
                    } catch (Exception ex) { ex.printStackTrace(); }
                }
            }).start();
        }
    }
}
