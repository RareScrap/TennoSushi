package com.webtrust.tennosushi;

import android.content.Context;
import android.content.DialogInterface;
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
 * Используйте {@link MenuListFragment#newInstance} фабричный метод для
 * создания экземпляра этого фрагмента.
 * @author RareScrap
 */
public class MenuListFragment extends Fragment {
    // Константы, определяющие режим отображения списка
    public static int CARD_MODE = 0;
    public static int PLATE_MODE = 1;

    // Закомментирован, т.к. еще не изучен
    //private OnFragmentInteractionListener mListener;

    // Список объектов MenuItem, представляющих элементы главного меню (категории блюд)
    private List<MenuItem> menuItemList = new ArrayList<>();

    // ArrayAdapter связывает объекты MenuItem с элементами ListView
    private MenuItemArrayAdapter menuItemArrayAdapter;
    private ListView menuItemListListView; // View для вывода информации в виде списка
    private GridView menuItemListGridView; // View для вывода информации в виде плиток

    private int currentMode; // Текущий режим отображения списка

    /**
     * Необходимый пустой публичный конструктор
     */
    public MenuListFragment() {
        setArguments(CARD_MODE);// режим по умолчанию
    }

    /**
     * Метод-замена для конструктора с параметрами т.к.
     * Google ОЧЕНЬ не рекомендует иметь дополнительные конструкторы
     * во фрагментах
     *
     * @param mode Режим отображения списка
     * @return this Возвращает этот же фрагмент (нужночтобы вызывать сразу после конструктора во FragmentTransaction
     * */
    public android.support.v4.app.Fragment setArguments(int mode) {
        this.currentMode = mode; // режим по умолчанию
        return this;
    }

    /**
     * Используйте этот фабричный метод для создания новых экземпляров
     * этого фрагмента с использованием продоставленных параментров
     * (черт знает где эти "параметры", япросто перевел сгенерированный коммент)
     *
     * @return Новый объект фрагмента {@link MenuListFragment}.
     */
    // TODO: Переменуйте и измените типы и количество параметров (перевод)
    // TODO: разобраться зачем нужен этот метод
    public static MenuListFragment newInstance() {
        MenuListFragment fragment = new MenuListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true); // у фрагмента имеются команды меню
        View returnedView; // Возвращаемый View

        // Inflate the layout for this fragment
        if (currentMode == CARD_MODE) {
            returnedView = inflater.inflate(R.layout.fragment_menu_card_list, container, false);
            try {
                URL url = new URL("http://192.168.1.254/index.php");

                GetDataTask getLocalDataTask = new GetDataTask();
                getLocalDataTask.execute(url);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return returnedView;
        }else { // currentMode == PLATE_MODE
            returnedView = inflater.inflate(R.layout.fragment_menu_plates_list, container, false);
            try {
                URL url = new URL("http://192.168.1.254/index.php");

                GetDataTask getLocalDataTask = new GetDataTask();
                getLocalDataTask.execute(url);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return returnedView;
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Лучше ли это место для установки адаптера?
        // ArrayAdapter для связывания weatherList с weatherListView
        menuItemArrayAdapter = new MenuItemArrayAdapter(getActivity(), menuItemList);
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

    // Обработка выбора команд меню
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Выбор в зависимости от идентификатора MenuItem
        switch (item.getItemId()) {
            case R.id.shopping_cart:
                return true; // Событие меню обработано
            case R.id.sort:
                ( (ViewGroup) getActivity().findViewById(R.id.fragment_menu) ).removeAllViews(); // Удаляет View на экране (сам список)

                // Замена одной разметки списка на другую
                if (currentMode == CARD_MODE) {
                    currentMode = PLATE_MODE; // Изменяет тукущий способ отображеия списка
                    LayoutInflater inflater = getActivity().getLayoutInflater();

                    /*
                    Помещает разметку списка как корневой элемент фрагмента
                    Возвращаемое значение не имеет смысла, т.к. всю работу делает сам метод
                    */
                    View view = inflater.inflate(R.layout.fragment_menu_plates_list, (ViewGroup) this.getView(), true);

                    // Получение ссыки на GridView-элемент, помещенный в разметку методом inflate(), приведенным выше
                    menuItemListGridView = (GridView) getView().findViewById(R.id.platesList);
                    menuItemListGridView.setAdapter(menuItemArrayAdapter); // Установка адаптера
                    menuItemArrayAdapter.notifyDataSetChanged(); // Обовлеия данных адаптера
                } else {// currentMode == PLATE_MODE
                    currentMode = CARD_MODE; // Изменяет тукущий способ отображеия списка
                    LayoutInflater inflater = getActivity().getLayoutInflater();

                    /*
                    Помещает разметку списка как корневой элемент фрагмента
                    Возвращаемое значение не имеет смысла, т.к. всю работу делает сам метод
                    */
                    View view = inflater.inflate(R.layout.fragment_menu_card_list, (ViewGroup) this.getView(), true);

                    // Получение ссыки на ListView-элемент, помещенный в разметку методом inflate(), приведенным выше
                    menuItemListListView = (ListView) getView().findViewById(R.id.cardList);
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

                //response = 404; // Это тест

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
}
