package com.webtrust.tennosushi;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.webtrust.tennosushi.fragments.MenuListFragment;

/**
 * Основная активити приложения
 * @author RareScrap
 */
public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Заполнение экрана начальным макетом

        // Создание экземпляра MenuListFragment и назначение ему режим отображения элементов в виде карточек
        MenuListFragment startFragment = MenuListFragment.newInstance(MenuListFragment.CARD_MODE);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_menu_container, startFragment); // startFragment заменяет контейнер лдя фрагментов "fragment_menu_container"
        transaction.commit(); // Завершить транзакцию и отобразить фрагмент
    }
}
