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
import android.widget.ImageView;
import android.widget.LinearLayout; // Рут-элемет для XML-разметки контейера опций
import android.widget.RadioButton; // Для работы с опциями
import android.widget.RadioGroup; // Контейнер опций
import android.widget.TextView;

import com.webtrust.tennosushi.MainActivity;
import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.list_items.FoodItem;
import com.webtrust.tennosushi.utils.ShoppingCartIconGenerator;

import static com.webtrust.tennosushi.fragments.FoodListFragment.getExistFoodItem;

import java.util.ArrayList;

/**
 * Фрагмент, отображающий подробную информацию о блюде, по которому был
 * сделан клик в {@link FoodListFragment}.
 */

public class DetailFoodFragment extends Fragment {
    /** Блюдо, по которому кликнул пользователь */
    public static FoodItem foodItem;

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
        ImageView foodPicture = (ImageView) returnedView.findViewById(R.id.foodPicture);
        ActionBar ab = ((MainActivity) this.getActivity()).getSupportActionBar();

        // Назначение данных элементам GUI
        priceTextView.setText(foodItem.price + " \u20BD");
        weightTextView.setText(foodItem.weight + " Г");
        foodPicture.setImageBitmap(foodItem.bitmap);
        componentsTextView.setText(foodItem.components);
        addButton.setOnClickListener(buyItemClickListener);
        ab.setTitle(foodItem.name); // Вывести в титульую строку название блюда
        ab.setSubtitle( ((MainActivity) getActivity()).getDataProvider().downloadedMenuItemList.
                get(foodItem.categoryId).name); // Вывести в подстроку категорию люда

        // Создаем экземпляры контейнеров опций БЕЗ помещеия их в иерархию View'х
        ArrayList<View> optionContainer = new ArrayList<>();
        for (int i = 0; i < foodItem.options.size(); i++) {
            // Надуваем макет опции
            View optionView = inflater.inflate(R.layout.viewgroup_options, (ViewGroup) returnedView, false);
            // Устанавливаем ему тег (служит уникальным идентификатором, т.к. на View существует несколько опций с одним ID)
            optionView.findViewById(R.id.radioGroup).setTag(R.id.radioGroup + "_" + i);
            // Установки имени для вида опции
            ((TextView) optionView.findViewById(R.id.name)).setText(foodItem.options.get(i).name);
            // Добавляем опцию в контейнер
            optionContainer.add(optionView);
        }

        // Добавляем в каждый контейнер радиокнопки
        for (int i = 0; i < optionContainer.size(); i++) {
            for (int j = 0; j < foodItem.options.get(i).items.size(); j++) {
                RadioButton button = new RadioButton(getContext());
                button.setText(foodItem.options.get(i).items.get(j).name);
                button.setTag(j);
                ((RadioGroup) optionContainer.get(i).findViewWithTag(R.id.radioGroup + "_" + i)).addView(button);
            }
        }

        // Добавлям контейнеры опций в иерархию View'х
        for (int i = 0; i < optionContainer.size(); i++) {
            ((LinearLayout) returnedView.findViewById(R.id.detail_root)).addView(optionContainer.get(i));
        }

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

    // TODO: использовать OnClickListener из FoodListFragment

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
            // Сохранеие выбранных опций
            for (int i = 0; i < foodItem.options.size(); i++) {
                RadioGroup radioGroup = (RadioGroup) getView().findViewWithTag(R.id.radioGroup + "_" + i);
                int pos = (int) radioGroup.findViewById(radioGroup.getCheckedRadioButtonId()).getTag();
                //- 1; // т.к. первый элемет начинается с 1, а хочется с 0
                foodItem.choosenOptions.put(foodItem.options.get(i).id, pos);
            }

            // Использется констуктор копирования для создания объекта с такими же полями, но без метаифомации
            // Элементы с одинаковой метаинформацией в списке ShoppingCartFragment при свайпах приводят к непредсказуемому поведеию элеметов списка
            FoodItem newFoodItem = new FoodItem(foodItem);

            /* Очистка выбранных опций у foodItem'а, т.к. тот представляет собой лишь временный объект.
            Его клон был сохранен чуть выше */
            foodItem.choosenOptions.clear();

            // Ищем такое же блюдо-хуюдо в корзине
            FoodItem foodItemInShoppingCart = getExistFoodItem(newFoodItem);
            if (foodItemInShoppingCart != null)
                // если такое уже есть, просто добавляем единицу к кол-ву порций
                foodItemInShoppingCart.count++;
            else
                // иначе, добавляем выбранное блюдо в корзину
                ShoppingCartFragment.addedFoodList.add(newFoodItem);

            // Отобразать уведомление о добавлении
            View v = getView();
            if (v != null)
                Snackbar.make(getView(), "Добавлено в корзину ;)", Snackbar.LENGTH_SHORT).show();

            ShoppingCartIconGenerator.generate(getContext(), 0);
        }
    };
}
