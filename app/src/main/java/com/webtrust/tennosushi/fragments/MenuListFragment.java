package com.webtrust.tennosushi.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;

import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.adapters.MenuItemArrayAdapter;
import com.webtrust.tennosushi.list_items.MenuItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    /** Хранилище для загруженных данных в формате JSON */
    public static JSONObject downloadedJSON = null;

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
    private static int currentMode = 0; // Текущий режим отображения списка (карточками по умолчанию)

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
     * Используйте фабричный метод {@link MenuListFragment#newInstance} для
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
        try {
            URL url = new URL("http://192.168.1.254/index.php");

            GetDataTask getLocalDataTask = new GetDataTask();
            getLocalDataTask.execute(url);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Развернуть разметку для фрагмента
        if (currentMode == CARD_MODE) { // Для разметки в виде постов
            return inflater.inflate(R.layout.fragment_menu_card_list, container, false);
        } else { // ДЛя разметки в виде плиток
            // currentMode == PLATE_MODE
            return inflater.inflate(R.layout.fragment_menu_plates_list, container, false);
        }

    }

    // Ранее этот код хранился в onActivityCreated
    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        // ArrayAdapter для связывания menuItemList с menuItemListListView или menuItemListGridView
        menuItemArrayAdapter = new MenuItemArrayAdapter(getActivity(), menuItemList, itemClickListener);
        if (currentMode == CARD_MODE) {
            menuItemListListView = (ListView) getView().findViewById(R.id.cardList);
            menuItemListListView.setAdapter(menuItemArrayAdapter);
        }else { // currentMode == PLATE_MODE
            menuItemListGridView = (GridView) getView().findViewById(R.id.platesList);
            menuItemListGridView.setAdapter(menuItemArrayAdapter);
        }
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
        inflater.inflate(R.menu.menu_list_menu, menu);
    }

    //

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
                return true; // Событие меню обработано
            case R.id.sort:
                // Получение ссылки для ViewGroup контейнера фрагментов
                ViewGroup fragmentMenuContainerViewGroup = (ViewGroup) getActivity().findViewById(R.id.fragment_menu_container);
                fragmentMenuContainerViewGroup.removeAllViews(); // Удаляет View на экране (сам список)

                // Замена одной разметки списка на другую
                if (currentMode == CARD_MODE) {
                    currentMode = PLATE_MODE; // Изменяет тукущий способ отображеия списка
                    LayoutInflater inflater = getActivity().getLayoutInflater();

                    /*
                    Помещает разметку списка как корневой элемент фрагмента
                    Возвращаемое значение не имеет смысла, т.к. всю работу делает сам метод
                    */
                    View view = inflater.inflate(R.layout.fragment_menu_plates_list, fragmentMenuContainerViewGroup, true);

                    // Получение ссыки на GridView-элемент, помещенный в разметку методом inflate(), приведенным выше
                    menuItemListGridView = (GridView) fragmentMenuContainerViewGroup.findViewById(R.id.platesList);
                    menuItemListGridView.setAdapter(menuItemArrayAdapter); // Установка адаптера
                    menuItemArrayAdapter.notifyDataSetChanged(); // Обовлеия данных адаптера
                } else {// currentMode == PLATE_MODE
                    currentMode = CARD_MODE; // Изменяет тукущий способ отображеия списка
                    LayoutInflater inflater = getActivity().getLayoutInflater();

                    /*
                    Помещает разметку списка как корневой элемент фрагмента
                    Возвращаемое значение не имеет смысла, т.к. всю работу делает сам метод
                    */
                    View view = inflater.inflate(R.layout.fragment_menu_card_list, fragmentMenuContainerViewGroup, true);

                    // Получение ссыки на ListView-элемент, помещенный в разметку методом inflate(), приведенным выше
                    menuItemListListView = (ListView) fragmentMenuContainerViewGroup.findViewById(R.id.cardList);
                    menuItemListListView.setAdapter(menuItemArrayAdapter); // Установка адаптера
                    menuItemArrayAdapter.notifyDataSetChanged(); // Обовлеия данных адаптера

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

    /* Обращение к REST-совместимому (якобы) веб-сервису за данными блюд и меню
    и сохранение этих данных в локальном файле HTML */
    /**
     * Внутренний класс {@link AsyncTask} для загрузки данных
     * в формате JSON.
     * @author RareScrap
     */
    private class GetDataTask extends AsyncTask<URL, Void, JSONObject> {
        public int CONNECTION_TIMEOUT = 5000; // Максимальное время ожидания данных
        /**
         * Получение данных из сети
         * @param params URL для получения JSON файла
         * @return JSON файл с категориями меню и блюдами в них
         */
        @Override
        protected JSONObject doInBackground(URL... params) {
            HttpURLConnection connection = null;

            try {
                connection = (HttpURLConnection) params[0].openConnection(); // Для выдачи запроса достаточно открыть объект подключения
                connection.setConnectTimeout(this.CONNECTION_TIMEOUT);
                int response = connection.getResponseCode(); // Получить код ответа от веб-сервера

                //response = 404; // Это тест при недоступности сети

                if (response == HttpURLConnection.HTTP_OK) {
                    StringBuilder builder = new StringBuilder();

                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                        String line;
                        while ((line = reader.readLine()) != null) {
                            builder.append(line);
                        }
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }

                    return new JSONObject(builder.toString());
                }else {}
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                connection.disconnect(); // Закрыть HttpURLConnection
            }

            return null;
        }
        /**
         * Обработка ответа JSON и обновление ListView/GridView.
         * @param jsonObject JSON файл полученный после завершения работы doInBackground()
         */
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                downloadedJSON = jsonObject; // Сохранение загруженного файла

                convertJSONtoArrayList(jsonObject); // Заполнение weatherList
                menuItemArrayAdapter.notifyDataSetChanged(); // Связать с ListView

                // Прокрутить до верха
                if (currentMode == CARD_MODE) {
                    menuItemListListView.smoothScrollToPosition(0);
                }else { // currentMode == PLATE_MODE
                    menuItemListGridView.smoothScrollToPosition(0);
                }
            } else { // Вывод алерта в случае, если данные не дошли
                AlertDialog.Builder adBuilder = new AlertDialog.Builder(getActivity());

                // Назначить сообщение AlertDialog
                adBuilder.setMessage(R.string.noConnection_useCashe);

                // Добавить кнопку OK в диалоговое окно
                adBuilder.setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        }
                );

                // Отображение диалогового окна
                adBuilder.create().show();
            }
        }
    }

    /**
     * Создание объектов MenuItem на базе JSONObject
     * с последующим их заесением в menuItemList.
     *
     * @param jsonObject Входящий JSON файл
     */
    private void convertJSONtoArrayList(JSONObject jsonObject) {
        menuItemList.clear(); // Стирание старых погодных данных

        try {
            // Получение массива с категориями блюд
            JSONArray list = jsonObject.getJSONArray("categories");

            // Преобразовать каждый элемент списка в объект Weather
            for (int i = 0; i < list.length(); ++i) {
                JSONObject category = list.getJSONObject(i); // Данные для одной категории меню

                // Получить из JSONObject ID-имя категории блюда
                String categoryID = category.getString("category");

                // Получить из JSONObject название кагеории блюда
                String name = category.getString("name");

                // Получить из JSONObject картинку категории блюда
                String picURL = category.getString("picURL");

                // Добавить новый объект MenuItem в menuItelList
                menuItemList.add( new MenuItem(categoryID, name, picURL));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

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

            String viewCathgory = menuItemList.get( view.getId() ).category;

            // В теге передаваемого View ПО-ХОРОШЕМУ ДОЛЖНА хранится ID-категории блюда, которое используется для поиска соответствующих блюд
            FoodListFragment asd_test = FoodListFragment.newInstance(viewCathgory, currentMode);
            fTrans.addToBackStack(null);
            fTrans.replace(R.id.fragment_menu_container, asd_test);
            fTrans.commit();

            // TODO: При первом запуске приложения без этой строки можно обойтись, но после изменения currentMode, без этой строки не стирается прдыдущий view
            ( (ViewGroup) getActivity().findViewById(R.id.fragment_menu_container) ).removeAllViews();
        }
    };
}
