package com.webtrust.tennosushi.adapters;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.widget.GridLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.fragments.MenuListFragment;
import com.webtrust.tennosushi.fragments.ShoppingCartFragment;
import com.webtrust.tennosushi.list_items.FoodItem;
import com.webtrust.tennosushi.utils.ShoppingCartIconGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class ShoppingCartItemRecyclerViewAdapter extends RecyclerView.Adapter<ShoppingCartItemRecyclerViewAdapter.ViewHolder> {
    /** Список для хранения данных элементов RecyclerView */
    private final List<FoodItem> items;
    /** Время, пока кнопка Undo Все еще доступна*/
    private static final int PENDING_REMOVAL_TIMEOUT = 3000; // 3sec
    /** Список элеметов, ожидающих удаление */
    private List<FoodItem> itemsPendingRemoval;
    /** Флаг, определяющий возможность отменить последствия удаления свайпом */
    public boolean undoOn = true; // is undo on, you can turn it on from the toolbar menu
    /** Обработчик действий объектов {@link Runnable}, которые объявляются как внутренние классы
     * в {@link ShoppingCartItemRecyclerViewAdapter#pendingRemoval(int)} */
    public Handler handler = new Handler(); // hanlder for running delayed runnables
    /** Хранилище, связывающее удаляемый элемент {@link FoodItem} с удаляющим его объектом
     * {@link Runnable}, который определяется в {@link ShoppingCartItemRecyclerViewAdapter#pendingRemoval(int)} */
    public HashMap<FoodItem, Runnable> pendingRunnables = new HashMap<>(); // map of items to pending runnables, so we can cancel a removal if need be
    /** Объект контекста, получаемый в {@link #onCreateViewHolder(ViewGroup, int)}.
     * Используется для доступа к ресурсам из разных частей адаптера
     * (например, в {@link #onBindViewHolder(ViewHolder, int)} */
    private Context context;
    /** Слушатель нажатия на фотографию блюда */
    private final View.OnClickListener clickListener;

    /**
     * Конструктор, инициализирующий поля слушателя клика по фотографии и списка элементов в корзине. Так же
     * инициализует поле {@link ShoppingCartItemRecyclerViewAdapter#itemsPendingRemoval} пустым списком.
     * @param addedFoodList Список товаров {@link FoodItem}, на основе которых инициализируется адаптер
     * @param clickListener Слушатель кликов по картинке блюда
     */
    public ShoppingCartItemRecyclerViewAdapter(List<FoodItem> addedFoodList, View.OnClickListener clickListener) {
        this.items = addedFoodList;
        this.clickListener = clickListener;

        itemsPendingRemoval = new ArrayList<>();
    }

    /**
     * ViewHolder для элементов View'шек элементов {@link FoodItem}
     * @author RareScrap
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        /** Ссылка на элемент GUI, представляющий название блюда */
        public final TextView nameTextView;
        /** Ссылка на элемент GUI, представляющий состав блюда */
        public final TextView componentsTextView;
        /** Ссылка на элемент GUI, представляющий цену блюда */
        public final TextView priceTextView;
        /** Ссылка на элемент GUI, представляющий вес порции блюда */
        public final TextView weightTextView;
        /** Ссылка на элемент GUI, представляющий количество порций блюда */
        public final TextView numberOfDeashesTextView;
        /** Ссылка на элемент GUI, представляющий кнопку undo */
        public final Button undoButton;
        /** Ссылка на элемент GUI, представляющий основной контейнер заказа */
        public final RelativeLayout relativeLayout;
        /** Ссылка на элемент GUI, представляющий контейнер опции пиццы заказа */
        public final GridLayout gridLayout;

        /**
         * Конструктор, инициализирующий свои поля.
         * @param itemView Представление одного элемента списка
         * @param clickListener Слушатель для этого элемента
         */
        public ViewHolder(View itemView, View.OnClickListener clickListener) {
            super(itemView);

            // Получение ссылок на элементы GUI в представлении
            nameTextView = (TextView) itemView.findViewById(R.id.name);
            componentsTextView = (TextView) itemView.findViewById(R.id.comonents);
            priceTextView = (TextView) itemView.findViewById(R.id.price);
            weightTextView = (TextView) itemView.findViewById(R.id.weight);
            numberOfDeashesTextView = (TextView) itemView.findViewById(R.id.number_of_deashes);
            undoButton = (Button) itemView.findViewById(R.id.undo_button);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.main_container);
            gridLayout = (GridLayout) itemView.findViewById(R.id.pizza_options);

            // Связывание слушателя кликов со изображением блюда
            itemView.findViewById(R.id.picture).setOnClickListener(clickListener);
        }
    }

    // TODO: Нихера не понял где что "упаковывается". Разобраться
    /**
     * Создание нового элемента списка и его объекта ViewHolder.
     *
     * <p>
     * Компонент RecyclerView вызывает метод onCreateViewHolder
     * своего объекта RecyclerView.Adapter для
     * заполнения макета каждого элемента RecyclerView
     * и упаковки (сохранения) его в объект субкласса RecyclerView.ViewHolder с именем ViewHolder.
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
        // TODO: Дает ли сохранение контекста в этом методе гарантию того, что объект контекста всегда будет актуален (и может ли он вообще быть неактуален). Или лучше получать его всего один раз в конструкторе адаптера?
        // Сохранение объекта контеста
        context = parent.getContext();

        // Заполнение макета list_item
        View view = LayoutInflater.from(context).inflate(R.layout.shopping_cart_item, parent, false);

        // Создание ViewHolder для текущего элемента
        return (new ViewHolder(view, clickListener));
    }

    /**
     * RecyclerView вызывает этот метод для отображения данных в элемете списка в указанной позиции.
     * Этот метод должен обновлять содержимое itemView (своей View'хи, которая-то и отображается в
     * списке), чтобы отразить элемент в данной позиции.
     * @param holder ViewHolder, который должен быть обновлен для представления контента элемента
     *               в данной позиции в наборе данных.
     * @param position Позиция элемента в списке элементов (наборе данных) адаптера.
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Получение объекта FoodItem для заданной позиции ListView
        final FoodItem item = items.get(position);

        // Присвоении ID к View на основании его порядкого номера в списке
        holder.itemView.setTag(position);

        // Назначения текста элементам GUI
        holder.nameTextView.setText(item.name);
        holder.componentsTextView.setText(item.components);
        holder.priceTextView.setText(String.valueOf(item.price) + " \u20BD");
        holder.weightTextView.setText("Вес: " + item.weight + " Г");
        holder.numberOfDeashesTextView.setText(String.valueOf(item.count));

        // Инициализация объекта, хранящего отступы элемента, по которому был сделан свайп
        ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) holder.itemView.getLayoutParams();

        // Опредляет, какое состояние элемента показать: обычное или состояние с кнопкой Undo
        if (itemsPendingRemoval.contains(item)) {
            // Убираем левый отступ, чтобы красный фон соприкасался с левым краем экрана (так красиво)
            lp.setMargins(0, lp.topMargin, lp.rightMargin, lp.bottomMargin);
            holder.itemView.setLayoutParams(lp);

            // we need to show the "undo" state of the row
            holder.itemView.setBackgroundColor(Color.RED);
            holder.relativeLayout.setVisibility(View.GONE);
            holder.gridLayout.setVisibility(View.GONE);
            holder.undoButton.setVisibility(View.VISIBLE);
            holder.undoButton.setOnClickListener(new View.OnClickListener() {
                    /**
                     * Реализует клик, приводящий к отмене удаления элемета
                     *
                     * @param v View'ха, нажатие на которую привело к отмене удаления элемента из списка
                     */
                    @Override
                    public void onClick(View v) {
                        // user wants to undo the removal, let's cancel the pending task
                        Runnable pendingRemovalRunnable = pendingRunnables.get(item);
                        pendingRunnables.remove(item);
                        if (pendingRemovalRunnable != null)
                            handler.removeCallbacks(pendingRemovalRunnable);
                        itemsPendingRemoval.remove(item);
                        // this will rebind the row in "normal" state
                        notifyItemChanged(items.indexOf(item));
                    }
                });
        } else {
            // we need to show the "normal" state
            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.relativeLayout.setVisibility(View.VISIBLE);
            holder.gridLayout.setVisibility(View.VISIBLE);
            holder.undoButton.setVisibility(View.GONE);
            holder.undoButton.setOnClickListener(null);

            // TODO: Нужен ли тут if для проверки на то, отличен ли текущий левый отступ от shopping_cart_cardview_margin?
            // Возвращаем отступы, которые были изначально
            lp.setMargins((int) context.getResources().getDimension(R.dimen.shopping_cart_cardview_margin),
                    lp.topMargin,
                    lp.rightMargin,
                    lp.bottomMargin);
            holder.itemView.setLayoutParams(lp);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    /**
     * Помещает элемент с указанной позицией в очередь на удаление. Пока этот элемент находится в очереди, пользователю
     * доступнка кнопка Undo
     * @param position Позиция элемента в списке товаров, которые помещается в очередь к удалению.
     */
    public void pendingRemoval(int position) {
        final FoodItem item = items.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            // this will redraw row in "undo" state
            notifyItemChanged(position);
            // let's create, store and post a runnable to remove the item
            final Runnable pendingRemovalRunnable = new Runnable() {
                @Override
                public void run() {
                    remove(items.indexOf(item));

                    // TODO: В идеале, строка ниже должа быть в методе, который вызывается в момент полного окочаия анимации в itemToucherHelper
                    ShoppingCartFragment.shoppingCartFragmentRef.changeCartUI(items);

                    Log.d("Menus", "Total count: " + String.valueOf(MenuListFragment.menu.size()));
                    Log.d("Menus", "Total price: " + String.valueOf(ShoppingCartFragment.getTotalPrice()));
                    if (MenuListFragment.menu.size() != 0)
                        ShoppingCartIconGenerator.generate(context, MenuListFragment.menu.size() - 1);
                }
            };
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }

    /**
     * Удаляет элемент с заданной позицией из списка товаров в корзине и списка товаров,
     * которые стоят в очереди к удалению.
     * @param position Позиция элемента в списке товаров, который следует полностью удалить
     */
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

    /**
     * Проверяет, находится ли элемет с указанной позицией в очереди к удалеию
     * @param position Позиция элемета, который следует проверить на наличие его в очереди к удалению
     * @return true, если элемент находится в очереди к удалению
     */
    public boolean isPendingRemoval(int position) {
        FoodItem item = items.get(position);
        return itemsPendingRemoval.contains(item);
    }
}
