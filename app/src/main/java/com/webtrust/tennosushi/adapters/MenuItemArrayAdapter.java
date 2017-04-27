package com.webtrust.tennosushi.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter; // Родительский класс
import android.widget.ImageView;
import android.widget.TextView;

import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.list_items.MenuItem;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Адаптер для списка меню, основанный на {@link ArrayAdapter}
 * @author RareScrap
 */
public class MenuItemArrayAdapter extends ArrayAdapter<MenuItem> {
    /**
     * Внутренний класс для повторного использования представлений списка
     * при прокрутке, реализующий паттерн ViewHolder
     * @author RareScrap
     */
    private static class ViewHolder {
        /** Ссылка на элемент GUI, представляющий картинку категории блюда */
        ImageView menuImageView;
        /** Ссылка на элемент GUI, представляющий название категории блюда */
        TextView menuTextView;
    }

    /** Кэш для уже загруженных картинок (объектов Bitmap) */
    private Map<String, Bitmap> bitmaps = new HashMap<>();

    /** Слушатель кликов по элементам списка */
    View.OnClickListener clickListener;

    /**
     * Конструктор для инициализации унаследованных членов суперкласса
     * @param context Контекст для super()
     * @param MenuItemList Список элементов меню для super()
     * @param clickListener Слушатель, который будет прослушить события элементов списка,
     *                      с которыи работает данный адаптер.
     */
    public MenuItemArrayAdapter(Context context, List<MenuItem> MenuItemList, View.OnClickListener clickListener) {
        /*
        в первом и третьем аргументах передаются объект Context (то есть активность,
        в которой отображается ListView) и List<MenuItem> (список выводимых данных).
        Второй аргумент конструктора суперкласса представляет идентификатор ресурса
        макета, содержащего компонент TextView, в котором отображаются данные ListView.
        Аргумент –1 означает, что в приложении используется пользовательский макет,
        чтобы элемент списка не ограничивался одним компонентом TextView.
         */
        super(context, -1, MenuItemList);
        this.clickListener = clickListener;
    }

    /**
     * Создание пользовательских представлений для элементов ListView
     * Вызывается только когда в ListView, куда выводится списк, есть
     * свободное место. Т.е. если высота списка 0, то этот метод Не вызовется.
     *
     * @param position Позиция элемента в списке
     * @param convertView Один заполненный элемент списка, возвращаемые данным методом
     * @param parent Родительский контейнер для списка
     * @return convertView класса View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Получение объекта MenuItem для заданной позиции ListView
        MenuItem menuItem = getItem(position);

        //Объект, содержащий ссылки на представления элемента списка
        ViewHolder viewHolder;

        // Проверить возможность повторного использования ViewHolder для элемента, вышедшего за границы экрана
        if (convertView == null) { // Объекта ViewHolder нет, создать его
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.card_list_item, parent, false); // В последнем аргументе передается флаг автоматического присоединения представлений
            viewHolder.menuImageView = (ImageView) convertView.findViewById(R.id.menu_image);
            viewHolder.menuTextView = (TextView) convertView.findViewById(R.id.menu_text);
            convertView.setTag(viewHolder);
        }else { // Cуществующий объект ViewHolder используется заново
            viewHolder = (ViewHolder) convertView.getTag();
        }

        // Если картинка уже загружена, использовать ее; в противном случае загрузить в отдельном потоке
        if (bitmaps.containsKey(menuItem.picURL)) {
            viewHolder.menuImageView.setImageBitmap(bitmaps.get(menuItem.picURL));
        }else { // Загрузить и вывести значок погодных условий
            new LoadImageTask(viewHolder.menuImageView).execute(menuItem.picURL);
        }

        // Получить данные из объекта MenuItem и заполнить представления
        // Назначается текст компонентов TextView элемента ListView
        viewHolder.menuTextView.setText(menuItem.name);

        convertView.setOnClickListener(clickListener);

        return convertView; // Вернуть готовое представление элемента
    }

    // Кажись, изменение imageView так же изменяет и аргумент, переданный в конструкторе LoadImageTask(). Таким образом, создается нечно вроде "ссылки"
    /**
     * Внутренний класс {@link AsyncTask}, предназначенный
     * для загрузки изображения в отдельном потоке
     * @author RareScrap
     */
    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        /** Ссылка на элемент GUI, представляющий картинку категории блюда */
        private ImageView imageView;

        /**
         * Сохраняет ImageView для загруженного объекта Bitmap
         * @param imageView ImageView для загруженного объекта Bitmap, который сохраится внутри класса {@link LoadImageTask}
         */
        public LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        /**
         * Загружает изображение из сети.
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
         * Связывает изображение с элементом списка
         * @param bitmap Связываемое изображение
         */
        // Выполняется в потоке GUI вроде как для вывода изображения
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
