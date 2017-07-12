package com.webtrust.tennosushi.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment; // Подлючается для использования в javadoc
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar; // Для вывода категорий меню в ActionBar
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.webtrust.tennosushi.MainActivity;
import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.adapters.FoodItemRecyclerViewAdapter;
import com.webtrust.tennosushi.list_items.FoodItem;
import com.webtrust.tennosushi.utils.ShoppingCartIconGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * <p> Фрагмент, реализующий список блюд для какой либо категории. </p>
 *
 * <p>
 * Вид списка определяется private полем {@link MenuListFragment#currentMode}, который может принимать
 * одно нескольких значений: {@link MenuListFragment#CARD_MODE} (список в виде карточек) или
 * {@link MenuListFragment#PLATE_MODE} (в виде постов). Для работы с {@link MenuListFragment#currentMode}
 * используйте методы {@link MenuListFragment#setCurrentMode(int)} и {@link MenuListFragment#getCurrentMode()}.
 * <p>
 *
 * <p>
 * {@link FoodListFragment} являетяся косвенным простым наследником класса {@link Fragment}.
 * Активити, которые содержат этот фрагмент должно реализовывать
 * интерфейс {@link MenuListFragment.OnFragmentInteractionListener}
 * (не реализованно в классе-родителе т.к. пока не изучено) для обработки событий
 * взаимодействия между активностью и фрагментом.
 * </p>
 *
 * <p>
 * Используйте фабричный метод {@link FoodListFragment#newInstance} для
 * создания экземпляра этого фрагмента. Избегайте создания конструкторов с
 * параметрами для любых наследников класса {@link Fragment}.
 * Подробнее о конструкторе фрагментов на странице
 * <a href="https://developer.android.com/reference/android/app/Fragment.html#Fragment()">Google документации</a>.
 * </p>
 *
 * @author RareScrap
 */
public class FoodListFragment extends MenuListFragment {
    /** Список объектов FoodItem, представляющих элементы меню (блюда) */
    public List<FoodItem> foodItemList = new ArrayList<>();
    /** Название категории меню */
    private String foodCategoryName;

    /** Элемент GUI, реализующий функции отображения списка */
    private RecyclerView recyclerView;
    // TODO: Нужно ли вообще сохранять ссылки на LayoutManager'ы? Пока сохраняю их "на всякий случай"
    /** Ссылка на последний исползуемый фрагментом LayoutManager для списка в виде карточек */
    private RecyclerView.LayoutManager listLayoutManager;
    /** Ссылка на последний исползуемый фрагментом LayoutManager для табличного списка */
    private RecyclerView.LayoutManager gridLayoutManager;

    /** Адаптер для связывания {@link FoodListFragment#recyclerView} c {@link FoodListFragment#listLayoutManager}
     * или {@link FoodListFragment#gridLayoutManager} (в зависимости от значения {@link MenuListFragment#currentMode}.*/
    private FoodItemRecyclerViewAdapter rvAdapter; // Адаптер

    /**
     * Необходимый пустой публичный конструктор.
     *
     * <p>
     * Используйте фабричный метод {@link MenuListFragment#newInstance} для
     * создания экземпляра этого фрагмента. Избегайте создания конструкторов с
     * параметрами для любых наследников класса {@link Fragment}.
     * Подробнее о конструкторе фрагментов на странице
     * <a href="https://developer.android.com/reference/android/app/Fragment.html#Fragment()">Google документации</a>.
     * </p>
     */
    public FoodListFragment() {
        super(); // Вызов супера, определяющий режим отображения по умолчанию
    }

    /**
     * Используйте этот фабричный метод для создания новых экземпляров
     * этого фрагмента с использованием предоставленных параментров
     *
     * @param foodCategory Строка, представляющая собой категорию блюд, определенную в JSON Файле.
     *                     На основании этой строки выбираются элементы для отображения в списке.
     * @param foodCategoryName Строка, представляющая собой НАЗВАНИЕ категории блюд, определенную в JSON Файле.
     * @param currentMode Режим отображения списка
     * @return Новый объект фрагмента {@link FoodListFragment}.
     */
    public static FoodListFragment newInstance(int foodCategory, String foodCategoryName, int currentMode) {
        FoodListFragment fragment = new FoodListFragment();

        /*
        Альтеративное сохраение аргументов. Отличительая черта - не использование поле класса, как для
        foodCategoryName. Позже можо будет получить foodCategory, используя getArguments()
         */
        Bundle args = new Bundle();
        args.putInt("foodCategory", foodCategory);
        args.putInt("currentMode", currentMode);
        fragment.setArguments(args);

        // Традиционное сохранение аргумента
        fragment.foodCategoryName = foodCategoryName;

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // у фрагмента имеются команды меню

        // TODO: Разобраться как код ниже используется в паре с непонятным кодом в newInstance()
        /*
        // Обработать аргументы в случае использования метод newInstance()
        if (savedInstanceState != null) {
            this.currentMode = getArguments().getInt("currentMode", CARD_MODE); // TODO: Стоит ли назвачать вторым аргументом CARD_MODE (аргумент по умолчанию)? Или лучше делать это в конструкторе?
        }
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_recyclerview_test, (ViewGroup) this.getView(), false);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        // Получение ссылки на recyclerView
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);

        /*
        Инициализировать LayoutManager для определенного вида списка.
        Новый экзмепляр LayoutManager'ов создается при возрате к этому фрагменту
        через BackStack во избежания исключения "LayoutManager is already attached
        to a RecyclerView”
         */
        if (MenuListFragment.getCurrentMode() == CARD_MODE) {
            listLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(listLayoutManager); // Для карточного списка
        } else { // MenuListFragment.getCurrentMode() == PLATE_MODE
            gridLayoutManager = new GridLayoutManager(getContext(), 2);
            recyclerView.setLayoutManager(gridLayoutManager); // Для плиточного списка
        }

        // Создать RecyclerView.Adapter для связывания элементов списка foodItemList с RecyclerView
        rvAdapter = new FoodItemRecyclerViewAdapter(foodItemList, itemClickListener, buyItemClickListener, getContext());
        recyclerView.setAdapter(rvAdapter);

        // Названичение текста actionBar'у
        ActionBar ab = ((MainActivity) this.getActivity()).getSupportActionBar();
        ab.setTitle(foodCategoryName); // Вывести в титульую строку название блюда
        ab.setSubtitle(""); // Стереть подстроку

        // Получение данных из DataProvider
        try {
            ArrayList<FoodItem> downloadedFoodItemListLink = ((MainActivity) getActivity()).getDataProvider().downloadedFoodItemList;
            for (int i = 0; i < downloadedFoodItemListLink.size(); ++i ) {
                if (downloadedFoodItemListLink.get(i).categoryId == getArguments().getInt("foodCategory"))
                    foodItemList.add(downloadedFoodItemListLink.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Отображение команд меню фрагмента.
     * @param menu Меню
     * @param inflater Инфлатер для меню
     */
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        MenuListFragment.menu = menu;
        menu.clear(); // предотвращает дублирование элементов меню
        inflater.inflate(R.menu.menu_list_menu, menu);
        ShoppingCartIconGenerator.generate(getContext(), 1);
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
            case R.id.sort:
                // Замена одной разметки списка на другую
                if (MenuListFragment.getCurrentMode() == CARD_MODE) {
                    MenuListFragment.setCurrentMode(PLATE_MODE); // Изменяет текущий способ отображеия списка
                    gridLayoutManager = new GridLayoutManager(getContext(), 2);
                    recyclerView.setLayoutManager(gridLayoutManager); // Для плиточного списка
                } else { // currentMode == PLATE_MODE
                    MenuListFragment.setCurrentMode(CARD_MODE); // Изменяет текущий способ отображеия списка
                    listLayoutManager = new LinearLayoutManager(getContext());
                    recyclerView.setLayoutManager(listLayoutManager); // Для карточного списка
                }

                rvAdapter.notifyDataSetChanged(); // Увеомляет rvAdapter о необзодимости перерисоать данные TODO: так ли это?
                return true; // Событие меню обработано
        }

        return super.onOptionsItemSelected(item); //TODO: Разобраться зачем вообще тут нужен супер
    }

    /**
     * Обрабатывает события клика по элементам списка {@link FoodListFragment#foodItemList},
     * кроме кнопки "добавить в корзину".
     * Вызывает подробную информацию о блюде, открывая {@link DetailFoodFragment}.
     */
    private final View.OnClickListener itemClickListener = new View.OnClickListener() {
        /**
         * Вызывается когда по кнопке "добавить в корзину" произошел клик.
         * Открывает {@link DetailFoodFragment}.
         * @param view {@link View}, по которому был сделан клик
         */
        @Override
        public void onClick(View view) {
            FragmentTransaction fTrans = getFragmentManager().beginTransaction();

            int asd = Integer.parseInt( view.getTag().toString(), 10 );
            FoodItem clickedFoodView = foodItemList.get( asd );

            // В теге передаваемого View ПО-ХОРОШЕМУ ДОЛЖНА хранится ID-категории блюда, которое используется для поиска соответствующих блюд
            DetailFoodFragment detailFoodFragment = DetailFoodFragment.newInstance(clickedFoodView);
            fTrans.addToBackStack(null);
            fTrans.replace(R.id.fragment_menu_container, detailFoodFragment);
            fTrans.commit();

            // TODO: При первом запуске приложения без этой строки можно обойтись, но после изменения currentMode, без этой строки не стирается прдыдущий view
            ( (ViewGroup) getActivity().findViewById(R.id.fragment_menu_container) ).removeAllViews();
        }
    };

    /**
     * Обрабатывает события клика по кнопке "добавить корзину" для элементов списка
     * {@link FoodListFragment#foodItemList}, вызывая подробную информацию о блюде,
     * открывая {@link DetailFoodFragment}.
     */
    private final View.OnClickListener buyItemClickListener = new View.OnClickListener() {
        /**
         * Вызывается когда по кноке "добавить в корзину" произошел клик.
         * Показывает уведомление при нажатии и добавляет .
         * @param view {@link View}, по которому был сделан клик
         */
        @Override
        public void onClick(View view) {
            int position = Integer.parseInt( view.getTag().toString(), 10 );
            FoodItem clickedFoodView = foodItemList.get( position );

            // Использется констуктор копирования для создания объекта с такими же полями, но без метаифомации
            // Элементы с одинаковой метаинформацией в списке ShoppingCartFragment при свайпах приводят к непредсказуемому поведеию элеметов списка
            FoodItem newFoodItem = new FoodItem(clickedFoodView);

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
            ShoppingCartIconGenerator.generate(getContext(), 1);

        }
    };

    /**
     * Ищет уже имеющийся FoodItem, добавленный в корзину, чтобы в дальнейшем просто
     * инкрементировать значение порций.
     * @param fi Объект поиска.
     * @return Найденный FoodItem.
     */
    public static FoodItem getExistFoodItem(FoodItem fi) {
        for (FoodItem fi2: ShoppingCartFragment.addedFoodList)
            if (fi2.equals(fi)) return fi2;
        return null;
    }
}