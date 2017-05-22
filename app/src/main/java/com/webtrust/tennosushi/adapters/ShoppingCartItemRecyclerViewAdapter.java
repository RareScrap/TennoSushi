package com.webtrust.tennosushi.adapters;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.webtrust.tennosushi.CartListSwipeDetector;
import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.list_items.FoodItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rares on 03.05.2017.
 */

public class ShoppingCartItemRecyclerViewAdapter
        extends RecyclerView.Adapter<ShoppingCartItemRecyclerViewAdapter.ViewHolder> {
    /** Список для хранения данных элементов RecyclerView */
    private final List<FoodItem> items;



    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec

    List<FoodItem> itemsPendingRemoval;
    int lastInsertedIndex; // so we can add some more items for testing purposes
    public boolean undoOn; // is undo on, you can turn it on from the toolbar menu



    private Handler handler = new Handler(); // hanlder for running delayed runnables
    HashMap<FoodItem, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be


    /** Слушатель нажатия на фотографию блюда */
    //private final View.OnTouchListener touchListener;

    public ShoppingCartItemRecyclerViewAdapter(List<FoodItem> addedFoodList/*, View.OnTouchListener touchListener*/) {
        this.items = addedFoodList;
        //this.touchListener = touchListener;

        itemsPendingRemoval = new ArrayList<>();
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
        public ViewHolder(View itemView) {
            super(itemView);

            // Получение ссылок на элементы GUI в представлении
            nameTextView = (TextView) itemView.findViewById(R.id.name);
            componentsTextView = (TextView) itemView.findViewById(R.id.comonents);
            priceTextView = (TextView) itemView.findViewById(R.id.price);
            weightTextView = (TextView) itemView.findViewById(R.id.weight);

            // // Связывание слушателя со всеми элеметами списка, кроме кнопки "Добавить в корзину"
            //itemView.setOnTouchListener(touchListener);

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
        return (new ViewHolder(view/*, touchListener*/));
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

    public void pendingRemoval(int position) {
        final FoodItem item = items.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(items.indexOf(item));
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    public void remove(int position) {
        FoodItem item = items.get(position);
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }
        if (items.contains(item)) {
            items.remove(position);
            notifyItemRemoved(position);
        }
    }

    public boolean isPendingRemoval(int position) {
        FoodItem item = items.get(position);
        return itemsPendingRemoval.contains(item);
    }
}
