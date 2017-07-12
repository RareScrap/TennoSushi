package com.webtrust.tennosushi.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.list_items.FoodItem;
import com.webtrust.tennosushi.utils.ShoppingCartIconGenerator;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.webtrust.tennosushi.utils.BitmapCacheProvider.getCacheData;
import static com.webtrust.tennosushi.utils.BitmapCacheProvider.getFileNameFromPath;

/**
 * Адаптер для списка меню, основанный на {@link RecyclerView.Adapter<FoodItemRecyclerViewAdapter.ViewHolder>}
 * @author RareScrap
 */
public class FoodItemRecyclerViewAdapter extends RecyclerView.Adapter<FoodItemRecyclerViewAdapter.ViewHolder> {
    /** Слушатель нажатия на элемент списка (кроме кнопки "добавить"), регистрируемые для каждого элемента списка */
    private final View.OnClickListener clickListener;
    /** Слушатель  нажатия на кнопку "добавить", регистрируемые для каждого элемента списка */
    private final View.OnClickListener buyClickListener;

    /** Кэш для уже загруженных картинок (объектов Bitmap) */
    private Map<String, Bitmap> bitmaps = new HashMap<>();

    /** Список для хранения данных элементов RecyclerView */
    private final List<FoodItem> items;

    /** Необходимо для работы кэша. */
    private final Context context;

    /**
     * Конструктор, инициализирующий свои поля.
     * @param items Набор элементов {@link FoodItem}, представляющий
     *              собой входные данные, которые необходимо отобразить
     * @param clickListener Слушатель, который регистрирется для каждого элемента списка
     */
    public FoodItemRecyclerViewAdapter(List<FoodItem> items, View.OnClickListener clickListener, View.OnClickListener buyClickListener, Context context) {
        ShoppingCartIconGenerator.generate(context, 1);
        this.items = items;
        this.clickListener = clickListener;
        this.buyClickListener = buyClickListener;
        this.context = context;
    }

    /**
     * Вложенный субкласс {@link RecyclerView.ViewHolder}. Используется для
     * реализации паттерна View-Holder в контексте RecyclerView-логики
     * повторного использования представлений.
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        /** Ссылка на элемент GUI, представляющий название блюда */
        public final TextView nameTextView;
        /** Ссылка на элемент GUI, представляющий состав блюда */
        public final TextView componentsTextView;
        /** Ссылка на элемент GUI, представляющий цену блюда */
        public final TextView priceTextView;
        /** Ссылка на элемент GUI, представляющий картинку блюда */
        public final ImageView foodImageView;

        /**
         * Конструктор, инициализирующий свои поля.
         * @param itemView Представление одного элемента списка
         * @param clickListener Слушатель для этого элемента
         * @param buyClickListener Слушатель для кнопки "добавить"
         */
        public ViewHolder(View itemView, View.OnClickListener clickListener, View.OnClickListener buyClickListener) {
            super(itemView);

            // Получение ссылок на элементы GUI в представлении
            nameTextView = (TextView) itemView.findViewById(R.id.menu_text_card);
            componentsTextView = (TextView) itemView.findViewById(R.id.components_card);
            priceTextView = (TextView) itemView.findViewById(R.id.price_card);
            foodImageView = (ImageView) itemView.findViewById(R.id.menu_image_card);

            // Связывание слушателя со всеми элеметами списка, кроме кнопки "Добавить в корзину"
            itemView.setOnClickListener(clickListener);
            // Связывание слушателя с кнопкой "Добавить в корзину"
            itemView.findViewById(R.id.addToCart_ImageButton).setOnClickListener(buyClickListener);

            // TODO: Вот тут нужно сделать связывание слушателя с кнопкой "Добавить в корзину"
        }
    }

    // TODO: Нихера не понял где что "упаковывается". Разобраться
    /**
     * Создает новый элемент списка и его объект ViewHolder.
     *
     * <p>
     * Компонент RecyclerView вызывает метод onCreateViewHolder
     * своего объекта RecyclerView.Adapter для
     * заполнения макета каждого элемента RecyclerView
     * и упаковки его в объект субкласса RecyclerView.ViewHolder с именем ViewHolder.
     * Новый объект ViewHolder возвращается RecyclerView для отображения.
     * </p>
     *
     * @param parent Объект субкласса {@link RecyclerView.ViewHolder} с представлениями View,
     *               в которых будут отображаться данные.
     * @param viewType Значение int, представляющее позицию элемента в списке {@link RecyclerView}.
     * @return Объект, отображающий данные в виде GUI-элемента списка.
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //ViewHolder.menuTextView = (TextView) convertView.findViewById(R.id.menu_text);

        // Заполнение макета list_item
        View view = LayoutInflater.from( parent.getContext() ).inflate(R.layout.food_card_list_item, parent, false);

        // Создание ViewHolder для текущего элемента
        return (new ViewHolder(view, clickListener, buyClickListener));
    }


    /**
     * Назначает данные элементам GUI.
     * @param holder Объект GUI, содеращий поля, которые следует установить
     * @param position Порядковый номер элемента {@link FoodItem}, который
     *                 хранится в {@link FoodItemRecyclerViewAdapter#items}
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // Получение объекта FoodItem для заданной позиции ListView
        FoodItem foodItem = items.get(position);

        // Присвоении ID к View на основании его порядкого номера в списке
        holder.itemView.setTag(position);
        /* Тот же ID должен быть присвое и кнопке "добавить в корзину", чтобы
        buyClickListener знал, КАКОЕ блюдо добавлено в корзнну */
        holder.itemView.findViewById(R.id.addToCart_ImageButton).setTag(position);

        // Назначения текста элементам GUI
        holder.nameTextView.setText(foodItem.name);
        holder.componentsTextView.setText(foodItem.components);
        holder.priceTextView.setText( String.valueOf(foodItem.price) );

        // Если картинка уже загружена, использовать ее; в противном случае загрузить в отдельном потоке
        if (bitmaps.containsKey(foodItem.picURL)) {
            // Дебажный кусок кода для отладчика
            /*String a1 = foodItem.picURL;
            Bitmap a2 = bitmaps.get(a1);
            holder.foodImageView.setImageBitmap(a2);*/

            holder.foodImageView.setImageBitmap(bitmaps.get( foodItem.picURL ));
        }else { // Загрузить и вывести значок погодных условий
            new LoadImageTask(holder.foodImageView).execute(foodItem.picURL);
        }
    }

    /**
     * Возвращение количества элементов, связываемых через адаптер.
     * @return Количества элементов, связываемых через адаптер
     */
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
        /** Сохраненная ссылка на {@link ImageView} для вывода изображения */
        private ImageView imageView;

        /**
         * Конструктор, инициализирующий свои поля.
         * Сохраняет ссылку на ImageView, куда следует поместить загруженный объект Bitmap.
         * @param imageView Ссылка наImageView, куда следует поместить загруженный объект Bitmap.
         */
        public LoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        /**
         * Загрузить изображение с данного URL адреса.
         * @param params URL-адрес изображения
         * @return Загруженное изображение
         */
        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;

            try {
                URL url = new URL(params[0]); // Создать URL для изображения

                // Ищем картинку в кэше
                bitmap = getCacheData(getFileNameFromPath(url.getFile()), context);
                if (bitmap != null) return bitmap;  // картинка найдена? тогда уходим.


                // Открыть объект HttpURLConnection, получить InputStream
                // и загрузить изображение
                connection = (HttpURLConnection) url.openConnection(); // Преобразование типа необходимо, потому что метод возвращает URLConnection

                try (InputStream inputStream = connection.getInputStream()) {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    bitmaps.put(params[0], bitmap); // Кэширование

                    FileOutputStream fos = context.openFileOutput(getFileNameFromPath(url.getFile()), Context.MODE_PRIVATE);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally { // Этот участок кода будет выполняться независимо от того, какие исключения были возбуждены и перехвачены
                // чтобы не слопать пачку хуйцов, проверим, было ли создано соединение вообще
                // ПОЧЕМУ ЭТО НИКТО НЕ СДЕЛАЛ ДО МЕНЯ?! ВЕДЬ ДАЖЕ СТУДИЯ ОБ ЭТО ГОВОРИЛА!!
                if (connection != null) connection.disconnect(); // Закрыть HttpURLConnection
            }

            return bitmap;
        }

        // Выполняется в потоке GUI вроде как для вывода изображения
        /**
         * Связывает изображение с элементом списка.
         * @param bitmap Связываемое изображение
         */
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
