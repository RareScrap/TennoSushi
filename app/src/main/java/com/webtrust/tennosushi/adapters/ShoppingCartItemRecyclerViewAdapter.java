package com.webtrust.tennosushi.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.webtrust.tennosushi.CartListSwipeDetector;
import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.list_items.FoodItem;

import java.util.List;

/**
 * Created by rares on 03.05.2017.
 */

public class ShoppingCartItemRecyclerViewAdapter
        extends RecyclerView.Adapter<ShoppingCartItemRecyclerViewAdapter.ViewHolder> {
    /** Список для хранения данных элементов RecyclerView */
    private final List<FoodItem> items;

    /** Слушатель нажатия на фотографию блюда */
    private final View.OnTouchListener touchListener;

    public ShoppingCartItemRecyclerViewAdapter(List<FoodItem> addedFoodList, View.OnTouchListener touchListener) {
        this.items = addedFoodList;
        this.touchListener = touchListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        /** Ссылка на элемент GUI, представляющий название блюда */
        public final TextView nameTextView;
        /** Ссылка на элемент GUI, представляющий состав блюда */
        public final TextView componentsTextView;
        /** Ссылка на элемент GUI, представляющий цену блюда */
        public final TextView priceTextView;
        /** Ссылка на элемент GUI, представляющий вес порции блюда */
        public final TextView weightTextView;

        /**
         * Конструктор, инициализирующий свои поля.
         * @param itemView Представление одного элемента списка
         * @param clickListener Слушатель для этого элемента
         */
        public ViewHolder(View itemView, View.OnTouchListener touchListener) {
            super(itemView);

            // Получение ссылок на элементы GUI в представлении
            nameTextView = (TextView) itemView.findViewById(R.id.name);
            componentsTextView = (TextView) itemView.findViewById(R.id.comonents);
            priceTextView = (TextView) itemView.findViewById(R.id.price);
            weightTextView = (TextView) itemView.findViewById(R.id.weight);

            // // Связывание слушателя со всеми элеметами списка, кроме кнопки "Добавить в корзину"
            itemView.setOnTouchListener(touchListener);

            // Слушание свайпов
            //nameTextView.getParent().setOnTouchListener(swipeDetector);

            // // Связывание слушателя с кнопкой "Добавить в корзину"
            //itemView.findViewById(R.id.addToCart_ImageButton).setOnClickListener(buyClickListener);

        }
    }

    // TODO: Нихера не понял где что "упаковывается". Разобраться
    /**
     * Создает новый элемент списка и его объект ViewHolder.
     *
     * <p>
     * Компонент RecyclerView вызывает метод onCreateViewHolder
     * своего объекта RecyclerView.Adapter для
     * заполнения макета каждого элемента RecyclerView
     * и упаковки его в объект субкласса RecyclerView.ViewHolder с именем ViewHolder.
     * Новый объект ViewHolder возвращается RecyclerView для отображения.
     * </p>
     *
     * @param parent Объект субкласса {@link RecyclerView.ViewHolder} с представлениями View,
     *               в которых будут отображаться данные.
     * @param viewType Значение int, представляющее позицию элемента в списке {@link RecyclerView}.
     * @return Объект, отображающий данные в виде GUI-элемента списка.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //ViewHolder.menuTextView = (TextView) convertView.findViewById(R.id.menu_text);

        // Заполнение макета list_item
        View view = LayoutInflater.from( parent.getContext() ).inflate(R.layout.shopping_cart_item, parent, false);

        // Создание ViewHolder для текущего элемента
        return (new ViewHolder(view, touchListener));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Получение объекта FoodItem для заданной позиции ListView
        FoodItem foodItem = items.get(position);

        // Присвоении ID к View на основании его порядкого номера в списке
        holder.itemView.setTag(position);

        // Назначения текста элементам GUI
        holder.nameTextView.setText(foodItem.name);
        holder.componentsTextView.setText(foodItem.components);
        holder.priceTextView.setText( String.valueOf(foodItem.price) + " \u20BD" );
        holder.weightTextView.setText("Вес: " + foodItem.weight + " Г");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}
