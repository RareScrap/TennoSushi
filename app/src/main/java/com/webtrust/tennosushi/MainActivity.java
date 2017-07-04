package com.webtrust.tennosushi;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.webtrust.tennosushi.fragments.MenuListFragment;
import com.webtrust.tennosushi.fragments.ShoppingCartFragment;
import com.webtrust.tennosushi.list_items.FoodItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Основная активити приложения
 * @author RareScrap
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    /** Фрагмет корзины, который будет доступен на все время работы приложения */
    public ShoppingCartFragment shoppingCartFragment;
    /** Хранилище загруженных из сети данных в виде готовых для работы объектов */
    private DataProvider dataProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Подготавливаем DataProvider для загрузки данных
        try {
            dataProvider = new DataProvider(this, new URL("http://192.168.0.102/index2.php"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
            /* По хорошему, тут должна показываться надпись "Ошибка в приложении. Сообщить разработчикам?",
            но я думаю, что это будет очень плохо смотреться */
            this.showConnectionErrorDialog(); // Показать ошибку сети
        }
        if (dataProvider != null)
           dataProvider.startDownloadData(); // Начинаем загрузку данных

        // Заполнение экрана начальным макетом
        setContentView(R.layout.activity_main);

        // Подгатавливаем компоенты navigationDrawer'а
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*
        Чтобы меню NavigationDrawer'а рисовалось поверх тулбара - в иерархии View тубар следует распологать
        выше меню NavigationDrawer'а. Чтобы иметь возможность задавать свое местоположение тулбара,
        мы определяем его в разметке и вытаскиваем через findViewById()
         */
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Создание фрагмента корзины, который будет хранится в памяти на все время работы приложения
        shoppingCartFragment = new ShoppingCartFragment();
        // TODO: Стоит ли тут делать обновление адаптера ShoppingCartFragment? Решил что нет



        /*
        // Создание экземпляра MenuListFragment и назначение ему режим отображения элементов в виде карточек
        MenuListFragment startFragment = MenuListFragment.newInstance(MenuListFragment.CARD_MODE);

        // Показываем самый первый фрагмент, с которого пльзователь начиает работу в приложеии
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_menu_container, startFragment); // startFragment заменяет контейнер лдя фрагментов "fragment_menu_container"
        transaction.commit(); // Завершить транзакцию и отобразить фрагмент
        */

    }

    /* Обычно я не испльзую геттер для получения поля без преобразования типа, но я чувствую,
    что сейчас следует поступить именно так */
    /**
     * Геттер для {@link DataProvider}
     * @return
     */
    public DataProvider getDataProvider() {
        return dataProvider;
    }

    /**
     * Отображение фрагмента корзины покупок.

     * @param viewID Ссылка на view, которая заменится нв
     *               {@link ShoppingCartFragment} после работы метода
     */
    public void displayShoppingCartFragment(int viewID) {
        // Использование FragmentTransaction для отображения ShoppingCartFragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, shoppingCartFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // Приводит к отображению ShoppingCartFragment
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showConnectionErrorDialog() {
        AlertDialog.Builder adBuilder = new AlertDialog.Builder(this);

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

    public void dataDownloaded() {
        // Создание экземпляра MenuListFragment и назначение ему режим отображения элементов в виде карточек
        MenuListFragment startFragment = MenuListFragment.newInstance(MenuListFragment.CARD_MODE);

        // Показываем самый первый фрагмент, с которого пльзователь начиает работу в приложеии
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_menu_container, startFragment); // startFragment заменяет контейнер лдя фрагментов "fragment_menu_container"
        transaction.commit(); // Завершить транзакцию и отобразить фрагмент
    }
}
