package com.webtrust.tennosushi;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.webtrust.tennosushi.fragments.MenuListFragment;
import com.webtrust.tennosushi.fragments.ShoppingCartFragment;
import com.webtrust.tennosushi.json_objects.CallMeObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static com.webtrust.tennosushi.utils.PhoneNumberChecker.checkNumber;

/**
 * Основная активити приложения
 * @author RareScrap
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        DataProvider.DataReady {
    /** Фрагмет корзины, который будет доступен на все время работы приложения */
    public ShoppingCartFragment shoppingCartFragment;
    /** Хранилище загруженных из сети данных в виде готовых для работы объектов */
    private static DataProvider dataProvider;
    /** Переменная, указывающая на тип устройства */
    public static boolean isTablet;

    /** Адрес сервера */
    // private final String SERVER_URL = "http://192.168.0.102/index2.php";
    private final String SERVER_URL = "http://romhacking.pw/index2.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Заполнение экрана начальным макетом
        setContentView(R.layout.activity_main);

        // вычисляем диагональ устройства
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        double diagonal = Math.sqrt(Math.pow(metrics.widthPixels, 2) + Math.pow(metrics.heightPixels, 2)) / metrics.xdpi;

        // если диагональ устройства больше или равно 7 дюймам
        // то это устройство - планшет
        isTablet = diagonal >= 7;

        /*if (isTablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return;
        }*/

        if (dataProvider == null) {

            // Подготавливаем DataProvider для загрузки данных
            try {
                dataProvider = new DataProvider(this, new URL(SERVER_URL));
                dataProvider.startDownloadData(); // Начинаем загрузку данных
            } catch (MalformedURLException e) {
                e.printStackTrace();
            /* По хорошему, тут должна показываться надпись "Ошибка в приложении. Сообщить разработчикам?",
            но я думаю, что это будет очень плохо смотреться */
                this.onDownloadError(); // Показать ошибку сети
            }
        }

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.nav_call_me:
                // создаём диалог с вводом номера
                final AlertDialog.Builder adb = new AlertDialog.Builder(this);
                LayoutInflater li = LayoutInflater.from(this);
                View v = li.inflate(R.layout.dialog_call_me, null);
                final EditText phoneNumber = (EditText) v.findViewById(R.id.dialog_call_me_number);
                final AlertDialog ad = adb.setTitle(R.string.call_me).setView(v).setCancelable(false)
                        .setNegativeButton(R.string.cancel, null)
                        .setPositiveButton(R.string.ok, null).create();
                ad.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        ((AlertDialog) dialog).getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // проверочки номера~
                                if (phoneNumber.getText().length() == 0) {
                                    Toast.makeText(MainActivity.this, "Вы не указали номер телефона!", Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if (!checkNumber(phoneNumber.getText().toString())) {
                                    Toast.makeText(MainActivity.this, "Номер телефона имеет неверный формат!", Toast.LENGTH_LONG).show();
                                    return;
                                }

                                // если номер в порядке, то отсылаем его на сервер
                                ad.hide();
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String alertAnswer;
                                        String alertTitle;
                                        int alertIcon = R.drawable.ic_close_black_24dp;
                                        try {
                                            CallMeObject cmo = new CallMeObject(phoneNumber.getText().toString());

                                            // устанавливаем соединение
                                            HttpURLConnection http = (HttpURLConnection) new URL("http://romhacking.pw:1234/").openConnection();

                                            // отсылаем данные
                                            http.setDoOutput(true);
                                            OutputStream os = http.getOutputStream();
                                            os.write(cmo.getJSON().getBytes("UTF-8"));
                                            os.close();

                                            // принимаем ответ
                                            Scanner sc = new Scanner(http.getInputStream());
                                            String answer = "";
                                            if (sc.hasNext()) answer = sc.next();
                                            sc.close();

                                            // проверяем ответ
                                            if (answer.equals("ok!")) {
                                                // если всё ок
                                                alertAnswer = getString(R.string.wait_for_call);
                                                alertTitle = getString(R.string.done);
                                                alertIcon = R.drawable.ic_check_black_24dp;
                                            } else {
                                                // если что-то пошло не так
                                                alertAnswer = getString(R.string.unexcepted_error);
                                                alertTitle = getString(R.string.oops);
                                            }
                                        } catch (Exception ex) {
                                            alertTitle = getString(R.string.unexcepted_error);
                                            alertAnswer = ex.getMessage();
                                        }
                                        final String finalAlertTitle = alertTitle;
                                        final String finalAlertAnswer = alertAnswer;
                                        final int finalAlertIcon = alertIcon;

                                        // выводим второй диалог с результатом операции
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                adb.setTitle(finalAlertTitle).setMessage(finalAlertAnswer)
                                                        .setPositiveButton(R.string.ok, null)
                                                        .setNegativeButton(null, null)
                                                        .setIcon(finalAlertIcon)
                                                        .setView(null)
                                                        .create().show();
                                            }
                                        });
                                    }
                                }).start();
                            }
                        });
                    }
                });
                ad.show();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Отображает самый первый фрагмент приложения, как только данные готовы к использованию
     */
    @Override
    public void onDataReady() {
        // Создание экземпляра MenuListFragment и назначение ему режим отображения элементов в виде карточек
        MenuListFragment startFragment = MenuListFragment.newInstance(MenuListFragment.CARD_MODE);

        // Показываем самый первый фрагмент, с которого пльзователь начиает работу в приложеии
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_menu_container, startFragment); // startFragment заменяет контейнер лдя фрагментов "fragment_menu_container"
        transaction.commit(); // Завершить транзакцию и отобразить фрагмент
    }

    /**
     * Показывает диалоговое окно, если подготовка данных завершилась с ошибкой
     */
    @Override
    public void onDownloadError() {
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
}
