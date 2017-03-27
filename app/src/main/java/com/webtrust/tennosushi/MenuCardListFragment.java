package com.webtrust.tennosushi;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MenuCardListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MenuCardListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuCardListFragment extends Fragment {
    //private OnFragmentInteractionListener mListener;

    // Список объектов Weather, представляющих прогноз погоды
    private List<MenuItem> menuItemList = new ArrayList<>();

    // ArrayAdapter связывает объекты Weather с элементами ListView
    private MenuItemArrayAdapter menuItemArrayAdapter;
    private ListView menuItemListView; // Для вывода информации

    public MenuCardListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MenuCardListFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu_card_list, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

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
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/

    /* Обращение к REST-совместимому веб-сервису за погодными данными
    и сохранение данных в локальном файле HTML */
    private class GetDataTask
            extends AsyncTask<URL, Void, JSONObject> {
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

            /*JSONObject returnedJSON;
            try {
                returnedJSON = new JSONObject( getResources().getString(R.string.test_json) );
                return returnedJSON;
            } catch (JSONException e) {
                e.printStackTrace();
            }*/
            return null;
        }

        // Обработка ответа JSON и обновление ListView
        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject != null) {
                convertJSONtoArrayList(jsonObject); // Заполнение weatherList
                menuItemArrayAdapter.notifyDataSetChanged(); // Связать с ListView
                menuItemListView.smoothScrollToPosition(0); // Прокрутить до верха
            }
        }
    }

    // Создание объектов Weather на базе JSONObject с прогнозом
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
