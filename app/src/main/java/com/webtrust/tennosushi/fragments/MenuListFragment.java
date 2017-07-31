package com.webtrust.tennosushi.fragments;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import com.webtrust.tennosushi.MainActivity;
import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.adapters.InfiniteCycleViewPagerAdapter;
import com.webtrust.tennosushi.adapters.MenuItemArrayAdapter;
import com.webtrust.tennosushi.adapters.MenuItemViewPagerAdapter;
import com.webtrust.tennosushi.list_items.MenuItem;
import com.webtrust.tennosushi.utils.ShoppingCartIconGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <p> Фрагмент, реализующий список категорий блюд. </p>
 *
 * <p>
 * Вид списка определяется private полем {@link MenuListFragment#currentMode}, который может принимать
 * одно нескольких значений: {@link MenuListFragment#CARD_MODE} (список в виде карточек) или
 * {@link MenuListFragment#PLATE_MODE} (в виде постов).
 * <p>
 *
 * <p>
 * {@link MenuListFragment} являетяся простым наследником класса {@link Fragment}.
 * Активити, которые содержат этот фрагмент должно реализовывать
 * интерфейс {@link MenuListFragment.OnFragmentInteractionListener}
 * (не реализованно т.к. пока не изучено) для обработки событий
 * взаимодействия между активностью и фрагментом.
 * </p>
 *
 * <p>
 * Используйте фабричный метод {@link MenuListFragment#newInstance} для
 * создания экземпляра этого фрагмента. Избегайте создания конструкторов с
 * параметрами для любых наследников класса {@link Fragment}.
 * Подробнее о конструкторе фрагментов на странице
 * <a href="https://developer.android.com/reference/android/app/Fragment.html#Fragment()">Google документации</a>.
 * </p>
 *
 * @author RareScrap
 */
public class MenuListFragment extends Fragment {
    /** Константа, определяющие режим отображения списка в виде постов (карточек) */
    public static int CARD_MODE = 0;
    /** Константа, определяющие режим отображения списка в виде плиток */
    public static int PLATE_MODE = 1;

    // Закомментирован, т.к. еще не изучен
    //private OnFragmentInteractionListener mListener;

    /** Список объектов MenuItem, представляющих элементы главного меню (категории блюд) */
    private List<MenuItem> menuItemList = new ArrayList<>();

    /** Субкласс {@link android.widget.ArrayAdapter} связывающий объекты MenuItem с элементами списка (ListView или GridView) */
    private MenuItemArrayAdapter menuItemArrayAdapter;

    // View для вывода различных вариантов списка
    /** View для вывода информации в виде списка */
    private ListView menuItemListListView;
    /** View для вывода информации в виде плиток */
    private GridView menuItemListGridView;


    // TODO: Является ли мой подход лучшим решением для этой задачи?
    /**
     * Текущий вид списка. Поле {@link MenuListFragment#currentMode} не должно наследоваться (поэтому стоит private),
     * но его субклассы должны уметь получать к нему доступ (поэтому реализованы set и get методы).
     */
    private static int currentMode; // Текущий режим отображения списка (карточками по умолчанию)

    /** Используется для регенерации иконки корзины. */
    public static Menu menu;

    ViewPager viewPager;
    MenuItemViewPagerAdapter myCustomPagerAdapter;
    int currentPage = 0;
    Timer timer;

    /**
     * Set-метод для currentMode
     * @param inputCurrentMode Новый режим отображения списка
     */
    public static void setCurrentMode(int inputCurrentMode) {
        currentMode = inputCurrentMode;
    }

    /**
     * Get-метод для currentMode
     * @return Текуий режим отображения списка
     */
    public static int getCurrentMode() {
        return currentMode;
    }

    /**
     * Необходимый пустой публичный конструктор.
     *
     * <p>
     * Используйте фабричный метод {@link MenuListFragment#newInstance(int)} для
     * создания экземпляра этого фрагмента. Избегайте создания конструкторов с
     * параметрами для любых наследников класса {@link Fragment}.
     * Подробнее о конструкторе фрагментов на странице
     * <a href="https://developer.android.com/reference/android/app/Fragment.html#Fragment()">Google документации</a>.
     * </p>
     */
    public MenuListFragment() {}

    /**
     * Используйте этот фабричный метод для создания новых экземпляров
     * этого фрагмента с использованием предоставленных параментров
     *
     * @param currentMode Режим отображения списка
     * @return Новый объект фрагмента {@link MenuListFragment}.
     */
    public static MenuListFragment newInstance(int currentMode) {
        MenuListFragment.currentMode = currentMode;
        MenuListFragment fragment = new MenuListFragment();

        // Отображать список в виде currentMode
        MenuListFragment.setCurrentMode(currentMode);

        // TODO: Разобраться когда и как следует использовать подход ниже для передачи аргументов фрагментам
        /*
        Bundle args = new Bundle(); // Объект для хранения состояний приложения и метаинформации
        args.putInt("currentMode", currentMode); // TODO: Эта хуйня не работает.
        fragment.setArguments(args);
        */

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: Разобраться как код ниже используется в паре с закоментированны кодом в newInstance()
        /*
        // Обработать аргументы в случае использования метод newInstance()
        if (savedInstanceState != null) {
            this.currentMode = getArguments().getInt("currentMode", CARD_MODE); // TODO: Стоит ли назвачать вторым аргументом CARD_MODE (аргумент по умолчанию)? Или лучше делать это в конструкторе?
        }
        */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true); // У фрагмента имеются команды меню

        // Запрос на получение данных
        menuItemList = ((MainActivity) getActivity()).getDataProvider().downloadedMenuItemList;

        View returnedView = inflater.inflate(R.layout.fragment_menu, container, false);

        // Развернуть разметку для фрагмента
        if (currentMode == CARD_MODE) { // Для разметки в виде постов
            returnedView.findViewById(R.id.platesList).setVisibility(View.INVISIBLE);
            returnedView.findViewById(R.id.cardList).setVisibility(View.VISIBLE);
        } else { // ДЛя разметки в виде плиток
            // currentMode == PLATE_MODE
            returnedView.findViewById(R.id.platesList).setVisibility(View.VISIBLE);
            returnedView.findViewById(R.id.cardList).setVisibility(View.INVISIBLE);
        }

        return  returnedView;

    }

    // Ранее этот код хранился в onActivityCreated
    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        viewPager = (ViewPager) getActivity().findViewById(R.id.view_pager);

        myCustomPagerAdapter = new MenuItemViewPagerAdapter(getActivity(), ((MainActivity) getActivity()).getDataProvider().downloadedOfferItemList);
        viewPager.setAdapter(myCustomPagerAdapter);

        if (timer != null) {
            timer.cancel();
            timer.purge();
        }

        /*After setting the adapter use the timer */
        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage == myCustomPagerAdapter.offers.size()) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer .schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, 500, 3000);

        // ArrayAdapter для связывания menuItemList с menuItemListListView или menuItemListGridView
        menuItemArrayAdapter = new MenuItemArrayAdapter(getActivity(), menuItemList, itemClickListener);
        menuItemListListView = (ListView) getView().findViewById(R.id.cardList);
        menuItemListListView.setAdapter(menuItemArrayAdapter);
        menuItemListGridView = (GridView) getView().findViewById(R.id.platesList);
        menuItemListGridView.setAdapter(menuItemArrayAdapter);

        // Названичение текста actionBar'у
        ActionBar ab = ((MainActivity) this.getActivity()).getSupportActionBar();
        ab.setTitle( getResources().getString(R.string.app_name) ); // Назачить титульной строке название приложения
        ab.setSubtitle(""); // Стереть подстроку
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*
        // Кусок сгенерированного кода
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // Кусок сгенерированного кода
        //mListener = null;
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
                // Получение ссылки для ViewGroup контейнера фрагментов
                ViewGroup fragmentMenuContainerViewGroup = (ViewGroup) getActivity().findViewById(R.id.fragment_menu_container);
                //fragmentMenuContainerViewGroup.removeAllViews(); // Удаляет View на экране (сам список)

                // Замена одной разметки списка на другую
                if (currentMode == CARD_MODE) {
                    currentMode = PLATE_MODE; // Изменяет тукущий способ отображеия списка

                    getView().findViewById(R.id.platesList).setVisibility(View.VISIBLE);
                    getView().findViewById(R.id.cardList).setVisibility(View.INVISIBLE);
                } else {// currentMode == PLATE_MODE
                    currentMode = CARD_MODE; // Изменяет тукущий способ отображеия списка

                    getView().findViewById(R.id.platesList).setVisibility(View.INVISIBLE);
                    getView().findViewById(R.id.cardList).setVisibility(View.VISIBLE);
                }
            return true; // Событие меню обработано
        }

        return super.onOptionsItemSelected(item); //TODO: Разобраться зачем вообще тут нужен супер
    }

    /*
     * Этот интерфейс должен быть реализован в активити, которая содержит этот
     * фрагмент, чтобы фрагмен смог взаимодействовать с активити
     * и ,возможно, с другими фрагментами, содержащиеся в этой активити.
     *
     * <p>
     * Подробнее смотрите в Android Training lesson: <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a>
     * </p>.
     */
    // Закомментирован, т.к. еще не изучен
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/



    // Слушатель кликов по объектам
    /**
     * Слушатель кликов по объектам MenuItem. При клике отображает {@link FoodListFragment}.
     */
    private final View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentTransaction fTrans = getFragmentManager().beginTransaction();

            /*
            Ниже не самый, но в меру ебаный способ.

            По хорошему, на основании передаваемой view нужно как-то достать связанный
            с ней объект MenuItem, но я не знаю как это сделать
            (setTag и getTag у меня не вышло использовать, т.к. я просто не знаю как из
            getTag извлеч именно одно поле, предварительно помещенного в него объекта
            класса ViewHolder из MenuItemArrayAdapter'а. Так что в качестве замены тегам,
            я в адаптере присваиваю view'шкам их порядковый номер в выводимом списке.

            TODO: Разберись, как решить задачу мульти клик листенера наилучшым образом
             */
            String viewCategoryName = menuItemList.get( view.getId() ).name;
            int viewCategory = menuItemList.get( view.getId() ).id;

            // В теге передаваемого View ПО-ХОРОШЕМУ ДОЛЖНА хранится ID-категории блюда, которое используется для поиска соответствующих блюд
            FoodListFragment foodListFragment = FoodListFragment.newInstance(viewCategory, viewCategoryName, currentMode);
            fTrans.addToBackStack(null);
            fTrans.replace(R.id.fragment_menu_container, foodListFragment);
            fTrans.commit();

            // TODO: При первом запуске приложения без этой строки можно обойтись, но после изменения currentMode, без этой строки не стирается прдыдущий view
            ( (ViewGroup) getActivity().findViewById(R.id.fragment_menu_container) ).removeAllViews();
        }
    };
}
