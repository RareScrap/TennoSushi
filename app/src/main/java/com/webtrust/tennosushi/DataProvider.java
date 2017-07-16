package com.webtrust.tennosushi;

import android.os.AsyncTask;

import com.webtrust.tennosushi.list_items.FoodItem;
import com.webtrust.tennosushi.list_items.MenuItem;
import com.webtrust.tennosushi.utils.FoodOptions;
import com.webtrust.tennosushi.utils.FoodTag;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Класс, занимающийся поставкой данных (JSON и картинок)
 * @author RareScrap
 */

public class DataProvider {
    /** Вызывается, когда данные скачаны и распарсены */
    public interface DataReady {
        /*public*/ void onDataReady();
        void onDownloadError();
    }

    /** Адрес, откуда будет скачан JSON с данными */
    public URL jsonURL;
    /** Хранилище для загруженных данных в формате JSON */
    public JSONObject downloadedJSON = null;

    /** Список объектов MenuItem, полученных из JSON'а и представляющих элементы главного меню (категории блюд) */
    public ArrayList<MenuItem> downloadedMenuItemList = new ArrayList<>();
    /** Список объектов FoodItem, полученных из JSON'а и представляющих элементы меню (блюда) */
    public ArrayList<FoodItem> downloadedFoodItemList = new ArrayList<>();
    /** Список тегов блюда, полученных парсингом JSON'а */
    public ArrayList<FoodTag> downloadedFoodTagList = new ArrayList<>();
    /** Список опций блюда, полученных парсингом JSON'а */
    public ArrayList<FoodOptions> downloadedFoodOptionsList = new ArrayList<>();

    /** Объект реализации интерфейса. Приходит из вне */
    public DataReady dataReady;

    /**
     * Конструктор, инициализирующий свои поля
     * @param dataReady Реализация интерфейса, метод которого вызывается, когда данные распарсены
     *                  и готовы к работе
     * @param url Адрес, откуда будет скачан JSON с данными
     */
    public DataProvider(DataReady dataReady, URL url) {
        this.dataReady = dataReady;
        this.jsonURL = url;
    }

    /**
     * Запускает загрузку данных в виде JSON
     */
    public void startDownloadData() {
        // Запрос на получение данных
        try {
            GetDataTask getLocalDataTask = new GetDataTask();
            getLocalDataTask.execute(jsonURL);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Обращение к REST-совместимому (якобы) веб-сервису за данными блюд и меню
    и сохранение этих данных в локальном файле HTML */
    /**
     * Внутренний класс {@link AsyncTask} для загрузки данных
     * в формате JSON.
     * @author RareScrap
     */
    private class GetDataTask extends AsyncTask<URL, Void, JSONObject> {
        /** Максимальное время ожидания данных */
        public int CONNECTION_TIMEOUT = 5000;

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
                }else {} // TODO: Реализовать поведение при недоступности сети
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

                parseJSON(jsonObject); // Заполнение weatherList
                //menuItemArrayAdapter.notifyDataSetChanged(); // Связать с ListView

                // Прокрутить до верха
                /*if (currentMode == CARD_MODE) {
                    menuItemListListView.smoothScrollToPosition(0);
                }else { // currentMode == PLATE_MODE
                    menuItemListGridView.smoothScrollToPosition(0);
                }*/
            } else { // Информировать в случае, если данные не дошли
                dataReady.onDownloadError();
            }
        }
    }

    /**
     * Соездает объекты MenuItem и FoodItem на базе JSONObject и заполняет
     * {@link #downloadedMenuItemList}, {@link #downloadedFoodItemList},
     * {@link #downloadedFoodTagList} и {@link #downloadedFoodOptionsList}
     *
     * @param jsonObject Входящий JSON файл
     */
    private void parseJSON(JSONObject jsonObject) {
        // Стирание старых данных
        downloadedMenuItemList.clear();
        downloadedFoodItemList.clear();

        try {
            // Получение массива с тегами блюд
            JSONArray tagsJSON = jsonObject.getJSONArray("tags");
            for (int i = 0; i < tagsJSON.length(); ++i) {
                JSONObject tagJSONObject = tagsJSON.getJSONObject(i);
                downloadedFoodTagList.add( new FoodTag(tagJSONObject.getInt("id"), tagJSONObject.getString("name")) );
            }

            // Получение массива опций блюд
            JSONArray allOptionsJSON = jsonObject.getJSONArray("options");
            for (int i = 0; i < allOptionsJSON.length(); ++i) {
                JSONObject optionJSONObject = allOptionsJSON.getJSONObject(i);

                // Получить ID  блюда
                int id = optionJSONObject.getInt("id");
                // Получить ID категории блюда (родительскую категорию)
                int categoryId = optionJSONObject.getInt("category_id");
                // Получить из JSONObject название кагеории блюда
                String name = optionJSONObject.getString("name");

                // Получить массив с элеметами самих опциями
                JSONArray itemsJSON = optionJSONObject.getJSONArray("items");
                List<FoodTag> items = new ArrayList<>();
                for (int j = 0; j <itemsJSON.length() ; ++j) {
                    JSONObject itemJSONObject = itemsJSON.getJSONObject(j);
                    items.add( new FoodTag(itemJSONObject.getInt("id"), itemJSONObject.getString("name")) );
                }

                // Ради этой строки мы и получали данные выше
                downloadedFoodOptionsList.add( new FoodOptions(id, categoryId, name, items) );
            }

            // Получение массива с категориями блюд
            JSONArray categoriesJSON = jsonObject.getJSONArray("categories");
            // Создание объектов MenuItem
            for (int i = 0; i < categoriesJSON.length(); ++i) {
                JSONObject categoryJSONObject = categoriesJSON.getJSONObject(i); // Данные для одной категории меню

                // Получить ID категории блюда
                int id = categoryJSONObject.getInt("id");
                // Получить из JSONObject название кагеории блюда
                String name = categoryJSONObject.getString("name");
                // Получить желаему заказчиком позицию элемета в списке
                int position = categoryJSONObject.getInt("position");

                // Получить список опций для данной категории
                List<FoodOptions> options = new ArrayList<>();
                JSONArray categoryOptions = categoryJSONObject.getJSONArray("options");
                for (int j = 0; j < categoryOptions.length(); ++j) {
                    options.add( downloadedFoodOptionsList.get( categoryOptions.getInt(j) ) );
                }

                // Получить из JSONObject картинку категории блюда
                String picURL = categoryJSONObject.getString("picURL");

                // Добавить новый объект MenuItem в downloadedMenuItemList
                downloadedMenuItemList.add( new MenuItem(id, name, position, options, picURL));
            }

            // Сортируем категории
            downloadedMenuItemList.sort(new Comparator<MenuItem>() {
                @Override
                public int compare(MenuItem o1, MenuItem o2) {
                    return Integer.compare(o1.position, o2.position);
                }
            });

            // Переключаемся на блюда
            JSONArray productsJSON = jsonObject.getJSONArray("products");
            // Создание объектов FoodItem
            for (int i = 0; i < productsJSON.length(); ++i) {
                JSONObject productJSONObject = productsJSON.getJSONObject(i); // Данные для одной категории меню

                // Получить ID  блюда
                int id = productJSONObject.getInt("id");
                // Получить ID категории блюда (родительскую категорию)
                int categoryId = productJSONObject.getInt("category_id");
                // Получить из JSONObject название кагеории блюда
                String name = productJSONObject.getString("name");
                // Получить из JSONObject состав блюда
                String components = productJSONObject.getString("components");

                // Получаем теги (не ссылочные) для конкретого блюда
                JSONArray productTagsJSON = productJSONObject.getJSONArray("tag_id");
                ArrayList<FoodTag> foodTags = new ArrayList<>();
                for (int j = 0; j < productTagsJSON.length(); ++j) {
                    foodTags.add( downloadedFoodTagList.get( productTagsJSON.getInt(j) ) );
                }

                // Получаем custom_options (опции, которые должны быть применены к блюду вопреки опциями в его категории)
                JSONArray categoryOptionsJSON = productJSONObject.getJSONArray("custom_options");
                ArrayList<FoodOptions> customOptions = new ArrayList<>();
                for (int j = 0; j < categoryOptionsJSON.length(); ++j) {
                    customOptions.add(downloadedFoodOptionsList.get( categoryOptionsJSON.getInt(j) ));
                }

                // Получить желаему заказчиком позицию элемета в списке
                int position = productJSONObject.getInt("position");
                // Получить цену блюда
                int price = productJSONObject.getInt("price");
                // Получить вес порции блюда (в граммах)
                int weight = productJSONObject.getInt("weight");
                // Получить из JSONObject картинку категории блюда
                String picURL = productJSONObject.getString("picURL");

                // Получаем опции из категории-родителя
                //JSONArray optionsJSON = productJSONObject.getJSONArray("options");
                ArrayList<FoodOptions> options = new ArrayList<>();
                for (int j = 0; j < downloadedMenuItemList.get(categoryId).options.size(); ++j) {
                    options = (ArrayList<FoodOptions>) downloadedMenuItemList.get(categoryId).options;
                }

                // Добавить новый объект FoodItem в downloadedFoodItemList
                downloadedFoodItemList.add(new FoodItem(id, categoryId, name, components, foodTags, customOptions,
                        position, price, weight, picURL, null, options));
            }

            // Сортируем блюда
            downloadedFoodItemList.sort(new Comparator<FoodItem>() {
                @Override
                public int compare(FoodItem o1, FoodItem o2) {
                    return Integer.compare(o1.position, o2.position);
                }
            });
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        // Инормировать, что данные готовы к использованию
        dataReady.onDataReady();
    }
}
