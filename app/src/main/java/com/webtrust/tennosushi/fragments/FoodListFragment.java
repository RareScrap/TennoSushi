package com.webtrust.tennosushi.fragments;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button; // Для отслеживания кнопки "скрыть облако тегов"

import com.github.aakira.expandablelayout.ExpandableRelativeLayout; // Раскрываемый контейнер для облака тегов
import com.webtrust.tennosushi.MainActivity;
import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.adapters.FoodItemRecyclerViewAdapter;
import com.webtrust.tennosushi.list_items.FoodItem;
import com.webtrust.tennosushi.utils.ShoppingCartIconGenerator;
import com.webtrust.tennosushi.utils.FoodTag; // Для работы облака тегов

import java.util.ArrayList;
import java.util.List;

import co.lujun.androidtagview.TagContainerLayout; // Облако тегов
import co.lujun.androidtagview.TagView; // Для установки слушателя на тег

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
    /** Теги блюд для данной категории */
    private ArrayList<FoodTag> tags = new ArrayList<>();
    /** Теги, по которым пользователь сделал клик, добавив их тем самым в фильтр */
    private ArrayList<FoodTag> checkedFoodTags = new ArrayList<>();
    /** Название категории меню */
    private String foodCategoryName;

    /** Элемент GUI, реализующий функции отображения списка */
    private RecyclerView recyclerView;
    /** Элемент GUI, вызывающий сжатие/расширение облака тегов {@link #} */
    private Button expandableButton;
    /** Элемет GUI, представляющий собой котейнер для облака тегов */
    private ExpandableRelativeLayout expandableLayout;
    /** Элемент GUI, представляющий собой облако тегов */
    private TagContainerLayout tagContainerLayout;

    // TODO: Нужно ли вообще сохранять ссылки на LayoutManager'ы? Пока сохраняю их "на всякий случай"
    /** Ссылка на последний исползуемый фрагментом LayoutManager для списка в виде карточек */
    private RecyclerView.LayoutManager listLayoutManager;
    /** Ссылка на последний исползуемый фрагментом LayoutManager для табличного списка */
    private RecyclerView.LayoutManager gridLayoutManager;

    /** Адаптер для связывания {@link FoodListFragment#recyclerView} c {@link FoodListFragment#listLayoutManager}
     * или {@link FoodListFragment#gridLayoutManager} (в зависимости от значения {@link MenuListFragment#currentMode}.*/
    private FoodItemRecyclerViewAdapter rvAdapter;

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
    public void onStop() {
        super.onStop();
        rvAdapter.items.clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_recyclerview_test, (ViewGroup) this.getView(), false);
    }

    /**
     * Вызывается после метода {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}. Получает
     * ссылки на элементы GUI, устанавливает слушатели, определяет режим отображения списка,
     * задает начальный вид списка (плитками или постами), устанавливает ему адаптер,
     * назначает текст ActionBar'у, получает данные из {@link com.webtrust.tennosushi.DataProvider}
     * и заполняет облако тегов.
     * @param view {@link View} фрагмента, которое было создано до вызова этого метода
     * @param savedInstanceState Обьект состояния фрагмента
     */
    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        // TODO: свериться с заказчиком по поводу этой хуйни.
        // Эта хуйня может вызвать вылет приложения. Подумать по поводу CustomActivityOnCrash.
      
        // Получение ссылки на элементы GUI
        //try { 
          recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView); 
          
          recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);
          expandableButton = (Button) getView().findViewById(R.id.expandableButton);
          expandableLayout = (ExpandableRelativeLayout) getActivity().findViewById(R.id.expandableLayout);
          tagContainerLayout = (TagContainerLayout) getActivity().findViewById(R.id.tagcontainerLayout);
        //}
        //catch (Exception ex) { ex.printStackTrace(); }
        // TODO Вряд ли работает. Оставленно чтобы знать где ставить цвет тегам.
        tagContainerLayout.setTagBackgroundColor(R.color.tag);
        tagContainerLayout.setTagBorderColor(R.color.tag);

        // Установка слушателей
        expandableButton.setOnClickListener(expandableButtonClickListener);
        tagContainerLayout.setOnTagClickListener(tagClickListener);

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
      
        // TODO: затолкать getContext() в адаптер

        // Создать RecyclerView.Adapter для связывания элементов списка foodItemList с RecyclerView
        rvAdapter = new FoodItemRecyclerViewAdapter(foodItemList, itemClickListener, buyItemClickListener, getContext()/*, new ArrayList<FoodTag>()*/);
        recyclerView.setAdapter(rvAdapter);

        // Названичение текста actionBar'у
        ActionBar ab = ((MainActivity) this.getActivity()).getSupportActionBar();
        ab.setTitle(foodCategoryName); // Вывести в титульую строку название блюда
        ab.setSubtitle(""); // Стереть подстроку

        // Получение данных из DataProvider
        try {
            ArrayList<FoodItem> downloadedFoodItemListLink = ((MainActivity) getActivity()).getDataProvider().downloadedFoodItemList;
            for (int i = 0; i < downloadedFoodItemListLink.size(); i++ ) {
                if (downloadedFoodItemListLink.get(i).categoryId == getArguments().getInt("foodCategory"))
                    foodItemList.add(downloadedFoodItemListLink.get(i));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Получение тегов из блюд для данной категории
        // Ссылка на TagContainerLayout, в котором отображаются теги
        for (int i = 0; i < foodItemList.size(); ++i) {
            FoodItem foodItem = foodItemList.get(i);
            for (int j = 0; j < foodItem.tags.size(); ++j) {
                // Проверка на существование полученого тега в списке
                if (!tags.contains(foodItem.tags.get(j)))
                    tags.add(foodItem.tags.get(j));
            }
        }

        // TODO: Ниже очень много закомментированного кода
        // Добавление сформированного списка тегов в облако тегов
        //List<String> tagsString = new ArrayList<>();
        for (int i = 0; i < tags.size(); ++i) {
            tagContainerLayout.addTag(tags.get(i).name, i);
        }
        //mTagContainerLayout.setTags(tags);
    }
    //ExpandableRelativeLayout expandableLayout1;
    /*public void expandableButton1(View view) {
        expandableLayout1 = (ExpandableRelativeLayout) getActivity().findViewById(R.id.expandableLayout1);
        expandableLayout1.toggle(); // toggle expand and collapse
    }*/

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
  
     /** Обрабатывает события нажатия на кнопку сокрытия/раскрытия облака тегов (вернее, его
     * контейнера {@link #expandableLayout}
     */
    private /*TODO: final?*/ View.OnClickListener expandableButtonClickListener = new View.OnClickListener() {
        // TODO: Javadoc не может сослаться на приватное поле
        /**
         * Вызываеся при клике а кнопку сокрытия/раскрытия контейера {@link #expandableLayout}
         * облака тегов {@link #tagContainerLayout}
         * @param v Кнопка, по которой был сделан клик
         */
        @Override
        public void onClick(View v) {
            expandableLayout.toggle();
        }
    };

    /**
     * Слушатель, обрабатывающий взаимодействия с тегами (например, клик по View тега)
     */
    private TagView.OnTagClickListener tagClickListener = new TagView.OnTagClickListener() {
        /**
         * Обрабатывает события нажатия на View тега, добавляя его тем самым в фильтр. Если было
         * добавлено несколько тегов - будут показаны только те товары, которые содержат все эти теги
         */
        @Override
        public void onTagClick(int position, String text) {
            // Получаем FoodTag, соответвтсвующий вьюхе тега по которой был сделан клик
            FoodTag foodTag = tags.get(position);

            // Если тега еще нет с списке выбранных - добавляем его туда
            if (!checkedFoodTags.contains(foodTag)) {
                checkedFoodTags.add( tags.get(position) );
                tagContainerLayout.getTagView(position).setTagBackgroundColor(getResources().getColor(R.color.checkedTag));

                // TODO: Закоментированный код в каждом из if - способы задаия цвета тегу. Некоторые из их не работают, а некоторые работают специфисно, но интересно. Нужно расписать что выходит на каждой строке и сделать из этого гист
                //tagContainerLayout.getTagView(position).setBackgroundColor(getResources().getColor(R.color.checkedTag));
                //tagContainerLayout.setTagBackgroundColor(getResources().getColor(R.color.checkedTag));
                //tagContainerLayout.setTagBackgroundColor(Color.GREEN);
            } else { // Иначе - удаляем тег из выбранных, т.к. это значит что он уже был выбран пользователем
                checkedFoodTags.remove(foodTag);
                tagContainerLayout.getTagView(position).setTagBackgroundColor(getResources().getColor(R.color.tag));

                //tagContainerLayout.getTagView(position).setBackgroundColor(getResources().getColor(R.color.tag));
                //tagContainerLayout.setTagBackgroundColor(getResources().getColor(R.color.tag));
                //tagContainerLayout.setTagBackgroundColor(Color.BLACK);
                //tagContainerLayout.setTheme(ColorFactory.NONE);
            }

            // Очищаем список отображаемых товаров и заполяем его всеми товарами, подходящими под данную категорию блюд
            foodItemList.clear();
            // Создаем копию списка всех (ВООБЩЕ ВСЕХ) загруженных товаров
            ArrayList<FoodItem> downloadedFoodItemListLink = ((MainActivity) getActivity()).getDataProvider().downloadedFoodItemList;
            // Формируем список блюд, прошедших фильтрацию
            for (int i = 0; i < downloadedFoodItemListLink.size(); i++ ) {
                if (downloadedFoodItemListLink.get(i).categoryId == getArguments().getInt("foodCategory"))
                    foodItemList.add(downloadedFoodItemListLink.get(i));
            }
            rvAdapter.items = foodItemList; // Отображаем товары на экране

            // Формируем список блюд, прошедших фильтрацию
            for (int i = 0; i < rvAdapter.items.size() && !checkedFoodTags.isEmpty() ; i++) {
                if (!rvAdapter.items.get(i).tags.containsAll(checkedFoodTags))
                    rvAdapter.items.remove(i);
            }

            // Измещаем адаптер, об изменеии его данных
            rvAdapter.notifyDataSetChanged();
        }

        @Override
        public void onTagLongClick(int position, String text) {

        }

        @Override
        public void onTagCrossClick(int position) {

        }
    };
}