package com.webtrust.tennosushi;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
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
 * Класс еще ГОВНО!!! Не юзается в продакшене
 * Created by rares on 30.03.2017.
 */

public class FoodListFragment extends MenuListFragment {
    // Список объектов FoodItem, представляющих элементы меню (блюда)
    private List<FoodItem> foodItemList = new ArrayList<>();

    private String foodCategory;

    // Элементы GUI
    RecyclerView recyclerView;
    RecyclerView.LayoutManager listLayoutManager; // LayoutManager для обычного списка
    RecyclerView.LayoutManager gridLayoutManager; // LayoutManager для табличного списка
    FoodItemRecyclerViewAdapter rvAdapter; // Адаптер

    /**
     * Необходимый пустой публичный конструктор
     */
    public FoodListFragment() {
        super(); // Вызов супера, определяющий режим отображения по умолчанию

        // Определение LayoutManager'ов
        listLayoutManager = new LinearLayoutManager(getContext());
        gridLayoutManager = new GridLayoutManager(getContext(), 2); // Количество колонок в таблице;
    }

    public static FoodListFragment newInstance(String foodCategory) {
        FoodListFragment fragment = new FoodListFragment();

        Bundle args = new Bundle();
        args.putString("foodCategory", foodCategory);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // у фрагмента имеются команды меню

        // Получить ссылку на RecyclerView и настроить его
        /*RecyclerView recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);

        // Получить LinearLayoutManager для определенного вида списка
        if (currentMode == CARD_MODE)
            recyclerView.setLayoutManager(listLayoutManager); // Для карточного списка
        else // currentMode == PLATE_MODE
            recyclerView.setLayoutManager(gridLayoutManager); // Для плиточного списка

        // Создать RecyclerView.Adapter для связывания тегов с RecyclerView
        rvAdapter = new FoodItemRecyclerViewAdapter(foodItemList, itemClickListener);
        recyclerView.setAdapter(rvAdapter);*/

        // Назначить ItemDecorator для рисования линий между элементами
        //recyclerView.addItemDecoration(new ItemDivider(this));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: Хз, лучшее ли это место для получения ссылок на элеметы GUI

        View returnedView = inflater.inflate(R.layout.fragment_item_recyclerview_test, (ViewGroup) this.getView(), false);



        // Inflate the layout for this fragment
        return returnedView;
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        // Получение ссылки на recyclerView
        recyclerView = (RecyclerView) getView().findViewById(R.id.recyclerView);

        // Получить LinearLayoutManager для определенного вида списка
        if (currentMode == CARD_MODE)
            recyclerView.setLayoutManager(listLayoutManager); // Для карточного списка
        else // currentMode == PLATE_MODE
            recyclerView.setLayoutManager(gridLayoutManager); // Для плиточного списка

        // Создать RecyclerView.Adapter для связывания тегов с RecyclerView
        rvAdapter = new FoodItemRecyclerViewAdapter(foodItemList, itemClickListener);
        recyclerView.setAdapter(rvAdapter);

        try {
            GetDataTask getLocalDataTask = new GetDataTask();
            getLocalDataTask.execute( getArguments().getString("foodCategory") );
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


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

    private void convertJSONtoArrayList(JSONObject jsonObject, String findName) {
        foodItemList.clear(); // Стирание старых данных

        try {
            // Получение свойства "list" JSONArray
            JSONArray list = jsonObject.getJSONArray(findName);

            // Преобразовать каждый элемент списка в объект Weather
            for (int i = 0; i < list.length(); ++i) {
                JSONObject deash = list.getJSONObject(i); // Данные за день
                // Получить JSONObject с температурами дня ("temp")
                String name = deash.getString("name");
                String components = deash.getString("components");
                String price = deash.getString("price");
                String picURL = deash.getString("picURL"); // Получить URL на картинку с блюдом

                // Добавить новый объект FoodItem в foodItemList
                foodItemList.add( new FoodItem(name, Double.parseDouble(price), components, picURL));
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /** itemClickListener открывает подробное описание блюда
     */
    private final View.OnClickListener itemClickListener = new View.OnClickListener() {
        // Клик по элементу
        @Override
        public void onClick(View view) {
            int a = 5;
        }
    };



}
