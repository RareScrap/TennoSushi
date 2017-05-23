package com.webtrust.tennosushi.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment; // Подлючается для использования в javadoc
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.webtrust.tennosushi.MainActivity;
import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.adapters.FoodItemRecyclerViewAdapter;
import com.webtrust.tennosushi.list_items.FoodItem;

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
    private List<FoodItem> foodItemList = new ArrayList<>();

    private String foodCategory;

    /** Элемент GUI, реализующий функции отображения списка */
    private RecyclerView recyclerView;
    /** LayoutManager для списка в виде карточек */
    private RecyclerView.LayoutManager listLayoutManager;
    /** LayoutManager для табличного списка */
    private RecyclerView.LayoutManager gridLayoutManager;

    /** Адаптер для связывания {@link FoodListFragment#recyclerView} c {@link FoodListFragment#listLayoutManager}
     * или {@link FoodListFragment#gridLayoutManager} (в зависимости от значения {@link MenuListFragment#currentMode}.*/
    FoodItemRecyclerViewAdapter rvAdapter; // Адаптер

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

        // Определение LayoutManager'ов
        listLayoutManager = new LinearLayoutManager(getContext());
        gridLayoutManager = new GridLayoutManager(getContext(), 2); // Количество колонок в таблице;
    }

    /**
     * Используйте этот фабричный метод для создания новых экземпляров
     * этого фрагмента с использованием предоставленных параментров
     *
     * @param foodCategory Строка, представляющая собой категорию блюд, определенную в JSON Файле.
     *                     На основании этой строки выбираются элементы для отображения в списке.
     * @param currentMode Режим отображения списка
     * @return Новый объект фрагмента {@link FoodListFragment}.
     */
    public static FoodListFragment newInstance(String foodCategory, int currentMode) {
        FoodListFragment fragment = new FoodListFragment();

        // TODO: ХЗ, выполняет ли код ниже что-то полезное
        Bundle args = new Bundle();
        args.putString("foodCategory", foodCategory);
        args.putInt("currentMode", currentMode);
        fragment.setArguments(args);

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
        Получить LayoutManager для определенного вида списка.
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
        rvAdapter = new FoodItemRecyclerViewAdapter(foodItemList, itemClickListener, buyItemClickListener);
        recyclerView.setAdapter(rvAdapter);

        // Запрос на получение данных
        try {
            GetDataTask getLocalDataTask = new GetDataTask();
            getLocalDataTask.execute( getArguments().getString("foodCategory") );
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    recyclerView.setLayoutManager(gridLayoutManager); // Для плиточного списка
                } else { // currentMode == PLATE_MODE
                    MenuListFragment.setCurrentMode(CARD_MODE); // Изменяет текущий способ отображеия списка
                    recyclerView.setLayoutManager(listLayoutManager); // Для карточного списка
                }

                rvAdapter.notifyDataSetChanged(); // Увеомляет rvAdapter о необзодимости перерисоать данные TODO: так ли это?
                return true; // Событие меню обработано
        }

        return super.onOptionsItemSelected(item); //TODO: Разобраться зачем вообще тут нужен супер
    }

    /**
     * Внутренний класс {@link AsyncTask} для получения данных из
     * скачанного в {@link MenuListFragment} файла JSON.
     *
     * @author RareScrap
     */
    private class GetDataTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return params[0];
        }

        @Override
        protected void onPostExecute(String findName) {
            convertJSONtoArrayList(MenuListFragment.downloadedJSON, findName ); // Заполнение weatherList
            rvAdapter.notifyDataSetChanged(); // Связать с ListView

            // Прокрутить до верха
            recyclerView.smoothScrollToPosition(0);
        }
    }

    /**
     * Заполняет {@link FoodListFragment#foodItemList} блюдами с категорией findName.
     * @param jsonObject JSON файл данных, в котором будет происходить поиск
     * @param findName Категория, блюда из которой следует искать
     */
    private void convertJSONtoArrayList(JSONObject jsonObject, String findName) {
        foodItemList.clear(); // Стирание старых данных

        try {
            // Получение свойства "list" JSONArray
            JSONArray list = jsonObject.getJSONArray("categories");

            int categoryIndex = 0;
            for (; categoryIndex < list.length(); ++categoryIndex) {
                if ( list.getJSONObject(categoryIndex).getString("category") == findName )
                    break;
            }

            list = list.getJSONObject(categoryIndex).getJSONArray("food");

            // Преобразовать каждый элемент списка в объект Weather
            for (int i = 0; i < list.length(); ++i) {
                JSONObject deash = list.getJSONObject(i); // Данные за день
                // Получить JSONObject с температурами дня ("temp")
                String name = deash.getString("name");
                String components = deash.getString("components");
                String price = deash.getString("price");
                int weight = deash.getInt("weight");
                String picURL = deash.getString("picURL"); // Получить URL на картинку с блюдом

                // Добавить новый объект FoodItem в foodItemList
                foodItemList.add( new FoodItem(name, Double.parseDouble(price), components, weight, picURL, findName));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * Обрабатывает события клика по элементам списка {@link FoodListFragment#foodItemList},
     * кроме кнопки "добавить  корзину".
     * Вызывает подробную информацию о блюде, открывая {@link DetailFoodFragment}.
     */
    private final View.OnClickListener itemClickListener = new View.OnClickListener() {
        /**
         * Вызывается когда по кноке "добавить в корзину" произошел клик.
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

            // Добавляет выбранное блюдо в корзину
            ShoppingCartFragment.addedFoodList.add(newFoodItem);

            // Отобразать уведомление о добавлении
            Snackbar.make(getView(), "Добавлено в корзину ;)", Snackbar.LENGTH_SHORT).show();
        }
    };
}