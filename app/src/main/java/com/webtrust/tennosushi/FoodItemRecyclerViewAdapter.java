package com.webtrust.tennosushi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by rares on 13.04.2017.
 */

public class FoodItemRecyclerViewAdapter extends RecyclerView.Adapter<FoodItemRecyclerViewAdapter.ViewHolder> {
    // Слушатели MainActivity, регистрируемые для каждого элемента списка
    private final View.OnClickListener clickListener;

    // Кэш для уже загруженных картинок (объектов Bitmap)
    private Map<String, Bitmap> bitmaps = new HashMap<>();

    // List<Foodtem> для хранения данных элементов RecyclerView
    private final List<FoodItem> items;

    // Конструктор
    public FoodItemRecyclerViewAdapter(List<FoodItem> items,
                           View.OnClickListener clickListener) {
        this.items = items;
        this.clickListener = clickListener;
    }

    // Вложенный субкласс RecyclerView.ViewHolder используется для
    // реализации паттерна View-Holder в контексте RecyclerView-логики
    // повторного использования представлений
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView nameTextView;
        public final TextView componentsTextView;
        public final TextView priceTextView;
        public final ImageView foodImageView;

        // Настройка объекта ViewHolder элемента RecyclerView
        public ViewHolder(View itemView, View.OnClickListener clickListener) {
            super(itemView);

            // Получение ссылок на элемент в представлении
            nameTextView = (TextView) itemView.findViewById(R.id.menu_text_card);
            componentsTextView = (TextView) itemView.findViewById(R.id.components_card);
            priceTextView = (TextView) itemView.findViewById(R.id.price_card);
            foodImageView = (ImageView) itemView.findViewById(R.id.menu_image_card);

            // Связывание слушателей с itemView
            itemView.setOnClickListener(clickListener);
        }
    }

    /*
    Создает новый элемент списка и его объект ViewHolder.

    Компонент RecyclerView вызывает метод onCreateViewHolder
    своего объекта RecyclerView.Adapter для
    заполнения макета каждого элемента RecyclerView
    и упаковки его в объект субкласса RecyclerView.ViewHolder с именем ViewHolder.
    Новый объект ViewHolder возвращается RecyclerView для отображения.
    */

    // TODO: Нихера не понял где что "упаковывается". Разобраться

    /*
    Метод получает:
        1)объект субкласса RecyclerView.ViewHolder с
    представлениями View, в которых будут отображаться данные
    (в данном случае один компонент TextView);
        2) значение int, представляющее позицию элемента в RecyclerView.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //ViewHolder.menuTextView = (TextView) convertView.findViewById(R.id.menu_text);
        // Заполнение макета list_item
        View view = LayoutInflater.from( parent.getContext() ).inflate(R.layout.food_card_list_item, parent, false);



        // Создание ViewHolder для текущего элемента
        return (new ViewHolder(view, clickListener));
    }

    // Назначение текста элемента списка для вывода тега запроса
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Получение объекта FoodItem для заданной позиции ListView
        FoodItem foodItem = items.get(position);


        holder.nameTextView.setText(foodItem.name);
        holder.componentsTextView.setText(foodItem.components);
        holder.priceTextView.setText( String.valueOf(foodItem.price) );

        // Если картинка уже загружена, использовать ее; в противном случае загрузить в отдельном потоке
        if (bitmaps.containsKey(foodItem.picURL)) {
            String a1 = foodItem.picURL;
            Bitmap a2 = bitmaps.get(a1);
            holder.foodImageView.setImageBitmap(a2);
        }else { // Загрузить и вывести значок погодных условий
            new LoadImageTask(holder.foodImageView).execute(foodItem.picURL);
        }
    }

    // Возвращение количества элементов, связываемых через адаптер
    @Override
    public int getItemCount() {
        return items.size();
    }



    // Кажись, изменение imageView так же изменяет и аргумент, переданный в конструкторе LoadImageTask(). Таким образом, создается нечно вроде "ссылки"
    // AsyncTask для загрузки изображения в отдельном потоке

    /**
     * Внутренний класс {@link AsyncTask}, предназначенный
     * для загрузки изображения в отдельном потоке
     * @author RareScrap
     */
    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView imageView; // Для вывода миниатюры

        /**
         * Сохранение ImageView для загруженного объекта Bitmap
         * @param imageView ImageView для загруженного объекта Bitmap, который сохраится внутри класса {@link LoadImageTask}
         */
        public LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        /**
         * Загрузить изображение
         * @param params Cодержит URL-адрес изображения
         * @return Загруженное изображение
         */
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(params[0]); // Создать URL для изображения

                // Открыть объект HttpURLConnection, получить InputStream
                // и загрузить изображение
                connection = (HttpURLConnection) url.openConnection(); // Преобразование типа необходимо, потому что метод возвращает URLConnection

                try (InputStream inputStream = connection.getInputStream()) {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmaps.put(params[0], bitmap); // Кэширование
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally { // Этот участок кода будет выполняться независимо от того, какие исключения были возбуждены и перехвачены
                connection.disconnect(); // Закрыть HttpURLConnection
            }

            return bitmap;
        }

        /**
         * Связать изображение с элементом списка
         * @param bitmap Связываемое изображение
         */
        // Выполняется в потоке GUI вроде как для вывода изображения
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}