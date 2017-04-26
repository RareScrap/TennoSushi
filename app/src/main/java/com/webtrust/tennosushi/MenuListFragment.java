package com.webtrust.tennosushi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;

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
 * Простой наследник класса {@link Fragment}.
 * Активити, которые содержат этот фрагмент должно реализовывать
 * интерфейс {@link MenuListFragment.OnFragmentInteractionListener}
 * для обработки событий взаимодействия между активностью и фрагментом.
 *
 * Используйте {@link MenuListFragment#newInstance} фабричный метод для
 * создания экземпляра этого фрагмента.
 *
 * @author RareScrap
 */
public class MenuListFragment extends Fragment {
    // Константы, определяющие режим отображения списка
    public static int CARD_MODE = 0; // В виде постов
    public static int PLATE_MODE = 1; // В виде плиток

    // Закомментирован, т.к. еще не изучен
    //private OnFragmentInteractionListener mListener;

    // Список объектов MenuItem, представляющих элементы главного меню (категории блюд)
    private List<MenuItem> menuItemList = new ArrayList<>();

    // ArrayAdapter связывает объекты MenuItem с элементами списка (ListView или GridView)
    private MenuItemArrayAdapter menuItemArrayAdapter;
    private ListView menuItemListListView; // View для вывода информации в виде списка
    private GridView menuItemListGridView; // View для вывода информации в виде плиток

    protected int currentMode; // Текущий режим отображения списка
    public static JSONObject downloadedJSON = null; // Хранилище для загруженного JSON'а

    /**
     * Необходимый пустой публичный конструктор
     */
    public MenuListFragment() {
        this.currentMode = CARD_MODE; // режим по умолчанию
    }

    /**
     * Используйте этот фабричный метод для создания новых экземпляров
     * этого фрагмента с использованием продоставленных параментров
     *
     * @param currentMode Режим отображения списка
     * @return Новый объект фрагмента {@link MenuListFragment}.
     */
    public static MenuListFragment newInstance(int currentMode) {
        MenuListFragment fragment = new MenuListFragment();

        Bundle args = new Bundle(); // Объект для хранения состояний приложения и метаинформации
        args.putInt("currentMode", currentMode);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Обработать аргументы в случае использования метод newInstance()
        if (savedInstanceState != null) {
            this.currentMode = getArguments().getInt("currentMode", CARD_MODE); // TODO: Стоит ли назвачать вторым аргументом CARD_MODE (аргумент по умолчанию)? Или лучше делать это в конструкторе?
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true); // У фрагмента имеются команды меню

        // Запрос на получение данных
        try {
            URL url = new URL("http:// 192.168.1.254/index.php");

            GetDataTask getLocalDataTask = new GetDataTask();
            getLocalDataTask.execute(url);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // Развернуть разметку для фрагмента
        if (currentMode == CARD_MODE) {
            return inflater.inflate(R.layout.fragment_menu_card_list, container, false);
        } else { // currentMode == PLATE_MODE
            return inflater.inflate(R.layout.fragment_menu_plates_list, container, false);
        }

    }

    // Ранее этот код хранился в onActivityCreated
    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        // ArrayAdapter для связывания weatherList с weatherListView
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
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * Отображение команд меню фрагмента
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
     * Обработка выбора команд меню
     *
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

    /**
     * Этот интерфейс должно быть реализован в активити, которая содержит этот
     * фрагмент, чтобы фрагмен смог взаимодействовать с активити
     * и ,возможно, с другими фрагментами, содержащиеся в этой активити.
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
         * Обработка ответа JSON и обновление ListView.
         *
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
            // Получение свойства "list" JSONArray
            JSONArray list = jsonObject.getJSONArray("sushi");

            // Преобразовать каждый элемент списка в объект Weather
            for (int i = 0; i < list.length(); ++i) {
                JSONObject deash = list.getJSONObject(i); // Данные за день
                // Получить JSONObject с температурами дня ("temp")
                String name = deash.getString("name");

                // Получить JSONObject c описанием и значком ("weather")
                String picURL = deash.getString("picURL");

                // Добавить новый объект Weather в weatherList
                menuItemList.add( new MenuItem(name, picURL));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // Слушатель кликов по объектам
    private final View.OnClickListener itemClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            FragmentTransaction fTrans = getFragmentManager().beginTransaction();

            FoodListFragment asd_test = FoodListFragment.newInstance("sushi");
            fTrans.addToBackStack(null);
            fTrans.replace(R.id.fragment_menu_container, asd_test);
            //fTrans.remove( getFragmentManager().findFragmentById(R.id.fragment_menu) );
            //fTrans.add(asd_test, "asd");
            fTrans.commit();

            // TODO: При первом запуске приложения без этой строки можно обойтись, но после изменения currentMode, без этой строки не стирается прдыдущий view
            ( (ViewGroup) getActivity().findViewById(R.id.fragment_menu_container) ).removeAllViews();


        }
    };



    @Override
    public void onResume () {
        super.onResume();



    }
    @Override
    public void onPause () {
        super.onPause();
    }
    @Override
    public void onStop () {
        super.onStop();
    }

}
