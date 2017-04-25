package com.webtrust.tennosushi;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rares on 13.04.2017.
 */

public class FoodItemRecyclerViewAdapter extends RecyclerView.Adapter<FoodItemRecyclerViewAdapter.ViewHolder> {
    // Слушатели MainActivity, регистрируемые для каждого элемента списка
    private final View.OnClickListener clickListener;

    // Кэш для уже загруженных картинок (объектов Bitmap)
    private Map<String, Bitmap> bitmaps = new HashMap<>();

    // List<Foodtem> для хранения данных элементов RecyclerView
    private final List<FoodItem> items;

    // Конструктор
    public FoodItemRecyclerViewAdapter(List<FoodItem> items,
                           View.OnClickListener clickListener) {
        this.items = items;
        this.clickListener = clickListener;
    }

    // Вложенный субкласс RecyclerView.ViewHolder используется для
    // реализации паттерна View-Holder в контексте RecyclerView-логики
    // повторного использования представлений
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView nameTextView;
        public final TextView componentsTextView;
        public final TextView priceTextView;

        // Настройка объекта ViewHolder элемента RecyclerView
        public ViewHolder(View itemView,
                          View.OnClickListener clickListener) {
            super(itemView);
            nameTextView = (TextView) itemView.findViewById(R.id.menu_text_card);
            componentsTextView = (TextView) itemView.findViewById(R.id.components_card);
            priceTextView = (TextView) itemView.findViewById(R.id.price_card);

            // Связывание слушателей с itemView
            itemView.setOnClickListener(clickListener);
        }
    }

    /*
    Создает новый элемент списка и его объект ViewHolder.

    Компонент RecyclerView вызывает метод onCreateViewHolder
    своего объекта RecyclerView.Adapter для
    заполнения макета каждого элемента RecyclerView
    и упаковки его в объект субкласса RecyclerView.ViewHolder с именем ViewHolder.
    Новый объект ViewHolder возвращается RecyclerView для отображения.
    */

    // TODO: Нихера не понял где что "упаковывается". Разобраться

    /*
    Метод получает:
        1)объект субкласса RecyclerView.ViewHolder с
    представлениями View, в которых будут отображаться данные
    (в данном случае один компонент TextView);
        2) значение int, представляющее позицию элемента в RecyclerView.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //ViewHolder.menuTextView = (TextView) convertView.findViewById(R.id.menu_text);
        // Заполнение макета list_item
        View view = LayoutInflater.from( parent.getContext() ).inflate(R.layout.food_card_list_item, parent, false);

        // Создание ViewHolder для текущего элемента
        return (new ViewHolder(view, clickListener));
    }

    // Назначение текста элемента списка для вывода тега запроса
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.nameTextView.setText(items.get(position).name);
        holder.componentsTextView.setText(items.get(position).components);
        holder.priceTextView.setText( String.valueOf(items.get(position).price) );
    }

    // Возвращение количества элементов, связываемых через адаптер
    @Override
    public int getItemCount() {
        return items.size();
    }
}
