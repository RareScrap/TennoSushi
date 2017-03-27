package com.webtrust.tennosushi;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
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
 * интерфейс {@link MenuCardListFragment.OnFragmentInteractionListener}
 * для обработки событий взаимодействия между активностью и фрагментом.
 * Используйте {@link MenuCardListFragment#newInstance} фабричный метод для
 * создания экземпляра этого фрагмента.
 * @author RareScrap
 */
public class MenuCardListFragment extends Fragment {
    // Закомментирован, т.к. еще не изучен
    //private OnFragmentInteractionListener mListener;

    // Список объектов MenuItem, представляющих элементы главного мею (категории блюд)
    private List<MenuItem> menuItemList = new ArrayList<>();

    // ArrayAdapter связывает объекты MenuItem с элементами ListView
    private MenuItemArrayAdapter menuItemArrayAdapter;
    private ListView menuItemListView; // View для вывода информации

    /**
     * Необходимый пустой публичный конструктор
     */
    public MenuCardListFragment() {}

    /**
     * Используйте этот фабричный метод для создания новых экземпляров
     * этого фрагмента с использованием продоставленных параментров
     * (черт знает где эти "параметры", япросто перевел сгенерированный коммент)
     *
     * @return Новый объект фрагмента {@link MenuCardListFragment}.
     */
    // TODO: Переменуйте и измените типы и количество параметров (перевод)
    // TODO: разобраться зачем нужен этот метод
    public static MenuCardListFragment newInstance() {
        MenuCardListFragment fragment = new MenuCardListFragment();
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

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_card_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // TODO: Лучше ли это место для установки адаптера?
        // ArrayAdapter для связывания weatherList с weatherListView
        menuItemListView = (ListView) getView().findViewById(R.id.cardList);
        menuItemArrayAdapter = new MenuItemArrayAdapter(getActivity(), menuItemList);
        menuItemListView.setAdapter(menuItemArrayAdapter);

        try {
            URL url = new URL("http://192.168.1.254/index.php");

            GetDataTask getLocalDataTask = new GetDataTask();
            getLocalDataTask.execute(url);
        }
        catch (Exception e) {
            e.printStackTrace();
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
                int response = connection.getResponseCode(); // Получить код ответа от веб-сервера

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
                }else {
                }
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
                menuItemListView.smoothScrollToPosition(0); // Прокрутить до верха
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
