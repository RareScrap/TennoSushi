package com.webtrust.tennosushi.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.webtrust.tennosushi.DataProvider;
import com.webtrust.tennosushi.MainActivity;
import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.list_items.OfferItem;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.webtrust.tennosushi.utils.BitmapCacheProvider.getCacheData;
import static com.webtrust.tennosushi.utils.BitmapCacheProvider.getFileNameFromPath;

/**
 * Адаптер для карусели в {@link com.webtrust.tennosushi.fragments.MenuListFragment}
 */
public class MenuItemViewPagerAdapter extends PagerAdapter {
    /** Список акций */
    public ArrayList<OfferItem> offers;
    /** TEST: Контекст для вывода toast при клике на картинку */
    private Context context;

    /** Кэш для уже загруженных картинок (объектов Bitmap) */
    private Map<String, Bitmap> bitmaps = new HashMap<>();

    public MenuItemViewPagerAdapter(Context context, ArrayList<OfferItem> offers) {
        this.context = context;
        this.offers = offers;
    }

    @Override
    public int getCount() {
        return offers.size();
        //return offers.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((ImageView) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        //View itemView = layoutInflater.inflate(R.layout.item, container, false);

        OfferItem offer = offers.get(position);
        ImageView itemView = new ImageView(context);
        //LinearLayout.LayoutParams p = new LinearLayout.LayoutParams();
        itemView.setLayoutParams(container.getLayoutParams());
        container.addView(itemView);

        boolean isLandscapeOrientation = context.getResources().getBoolean(R.bool.isLandscapeOrientation);

        if (isLandscapeOrientation) {
            if (offer.bitmapTabletLandscape != null) {
                itemView.setImageBitmap(offer.bitmapTabletLandscape);
            } else { // Загрузить и вывести значок погодных условий
                new LoadImageTask(itemView).execute(offer.tabletPicURL);
            }
        } else {
            // Если картинка уже загружена, использовать ее; в противном случае загрузить в отдельном потоке
            if (offer.bitmap != null) {
                itemView.setImageBitmap(offer.bitmap);
            } else { // Загрузить и вывести значок погодных условий
                new LoadImageTask(itemView).execute(offer.picURL);
            }
        }

        //listening to image click
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "you clicked image " + (position + 1), Toast.LENGTH_LONG).show();
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }

    // Кажись, изменение imageView так же изменяет и аргумент, переданный в конструкторе LoadImageTask(). Таким образом, создается нечно вроде "ссылки"
    /**
     * Внутренний класс {@link AsyncTask}, предназначенный
     * для загрузки изображения в отдельном потоке. Загружает ту картинку, которой соответвтвует
     * устройство и ориентация девайса пользователя.
     * @author RareScrap
     */
    private class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
        /** Ссылка на элемент GUI, представляющий картинку категории блюда */
        private ImageView imageView;

        /**
         * Сохраняет ImageView для загруженного объекта Bitmap
         * @param imageView ImageView для загруженного объекта Bitmap, который сохраится внутри класса {@link MenuItemArrayAdapter.LoadImageTask}
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
