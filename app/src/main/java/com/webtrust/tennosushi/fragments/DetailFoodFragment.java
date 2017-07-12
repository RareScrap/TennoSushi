package com.webtrust.tennosushi.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.webtrust.tennosushi.MainActivity;
import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.list_items.FoodItem;
import com.webtrust.tennosushi.utils.ShoppingCartIconGenerator;

import static com.webtrust.tennosushi.fragments.FoodListFragment.getExistFoodItem;

/**
 * Фрагмент, отображающий подробную информацию о блюде, по которумы был
 * сделан клик в {@link FoodListFragment}.
 */

public class DetailFoodFragment extends Fragment {
    /** Блюдо, по которому кликнул пользователь */
    public FoodItem foodItem;

    /**
     * Необходимый пустой публичный конструктор.
     *
     * <p>
     * Используйте фабричный метод {@link DetailFoodFragment#newInstance} для
     * создания экземпляра этого фрагмента. Избегайте создания конструкторов с
     * параметрами для любых наследников класса {@link Fragment}.
     * Подробнее о конструкторе фрагментов на странице
     * <a href="https://developer.android.com/reference/android/app/Fragment.html#Fragment()">Google документации</a>.
     * </p>
     */
    public DetailFoodFragment() {}

    /**
     * Используйте этот фабричный метод для создания новых экземпляров
     * этого фрагмента с использованием предоставленных параментров
     *
     * @param foodItem Объект блюда, по которому кликнул пользователь. Определяет какими даными
     *                 заполнится фрагмет {@link DetailFoodFragment}.
     * @return Новый объект фрагмента {@link DetailFoodFragment}.
     */
    public static DetailFoodFragment newInstance(FoodItem foodItem) {
        DetailFoodFragment fragment = new DetailFoodFragment();
        fragment.foodItem = foodItem;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // у фрагмента имеются команды меню
    }

    /**
     * Получает ссылки на элеметы GUI и инициализирует ими поля класс {@link DetailFoodFragment}.
     * Так же создает View графического интерфейса макета.
     * @param inflater Инфлаттер для UI фрагмета. Используется для создания View из XML-файла разметки
     * @param container Родительский контейнер, в котором развернется UI фрагмента
     * @param savedInstanceState Если фрагмент восстанавливается из предыдущего сохраненного состояния,
     *                           это и есть его предыдущее состояние.
     * @return UI фрагмента, пригодное для показа на экране
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true); // У фрагмента имеются команды меню
        View returnedView = inflater.inflate(R.layout.detail_food_fragment, container, false);

        // Получение ссылок на элементы GUI
        TextView priceTextView = (TextView) returnedView.findViewById(R.id.food_price_textField);
        TextView weightTextView = (TextView) returnedView.findViewById(R.id.food_weight_textField);
        TextView componentsTextView = (TextView) returnedView.findViewById(R.id.components_textField);
        TextView addButton = (Button) returnedView.findViewById(R.id.addToBusketButton);
        ActionBar ab = ((MainActivity) this.getActivity()).getSupportActionBar();

        // Назначение даных элементам GUI
        priceTextView.setText(foodItem.price + " \u20BD");
        weightTextView.setText(foodItem.weight + " Г");
        componentsTextView.setText(foodItem.components);
        addButton.setOnClickListener(buyItemClickListener);
        ab.setTitle(foodItem.name); // Вывести в титульую строку название блюда
        ab.setSubtitle( ((MainActivity) getActivity()).getDataProvider().downloadedMenuItemList.
                get(foodItem.categoryId).name); // Вывести в подстроку категорию люда


        // Включить дополнительные опции, в зависимости от категории блюда
        /*if (foodItem.category.equals("pizza")) {
            returnedView.findViewById(R.id.pizza_options_container).setVisibility(View.VISIBLE);
        } else if (foodItem.category.equals("wok")) {
            returnedView.findViewById(R.id.wok_options_container).setVisibility(View.VISIBLE);
        }*/

        return returnedView;
    }

    /**
     * Отображение команд меню фрагмента.
     * @param menu Меню
     * @param inflater Инфлатер для меню
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.detail_list_menu, menu);
        ShoppingCartIconGenerator.generate(getContext(), 0);
    }

    /**
     * Обработка выбора команд меню.
     * @param item Выбранный итем на панели действий (не путать этот параметр с MenuItem, обозначающий элемент списка
     * @return Показатель успешность обработки события
     */
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Выбор в зависимости от идентификатора MenuItem
        switch (item.getItemId()) {
            case R.id.shopping_cart:
                ((MainActivity) getActivity()).displayShoppingCartFragment(R.id.fragment_menu_container);
                return true; // Событие меню обработано
        }
        return super.onOptionsItemSelected(item); //TODO: Разобраться зачем вообще тут нужен супер
    }

    /**
     * Обрабатывает события клика по кнопке "добавить в корзину" для элементов списка
     * {@link FoodListFragment#foodItemList}, вызывая подробную информацию о блюде,
     * открывая {@link DetailFoodFragment}.
     */
    private final View.OnClickListener buyItemClickListener = new View.OnClickListener() {
        /**
         * Вызывается когда по кнопке "добавить в корзину" произошел клик.
         * Показывает уведомление при нажатии и добавляет .
         * @param view {@link View}, по которому был сделан клик
         */
        @Override
        public void onClick(View view) {
            // Использется констуктор копирования для создания объекта с такими же полями, но без метаифомации
            // Элементы с одинаковой метаинформацией в списке ShoppingCartFragment при свайпах приводят к непредсказуемому поведеию элеметов списка
            FoodItem newFoodItem = new FoodItem(foodItem);

            // Ищем такое же блюдо-хуюдо в корзине
            FoodItem foodItemInShoppingCart = getExistFoodItem(newFoodItem);
            if (foodItemInShoppingCart != null)
                // если такое уже есть, просто добавляем единицу к кол-ву порций
                foodItemInShoppingCart.count++;
            else
                // иначе, добавляем выбранное блюдо в корзину
                ShoppingCartFragment.addedFoodList.add(newFoodItem);

            // Отобразать уведомление о добавлении
            Snackbar.make(getView(), "Добавлено в корзину ;)", Snackbar.LENGTH_SHORT).show();
            ShoppingCartIconGenerator.generate(getContext(), 0);
        }
    };
}
