package com.webtrust.tennosushi;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.webtrust.tennosushi.fragments.MenuListFragment;
import com.webtrust.tennosushi.fragments.ShoppingCartFragment;

/**
 * Основная активити приложения
 * @author RareScrap
 */
public class MainActivity extends AppCompatActivity {
    /** Фрагмет корзины, который будет доступен на все время работы приложения */
    public ShoppingCartFragment shoppingCartFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Заполнение экрана начальным макетом

        // Создание фрагмента корзины, который будет хранится в памяти на все время работы приложения
        shoppingCartFragment = new ShoppingCartFragment();
        // TODO: Стоит ли тут делать обновление адаптера ShoppingCartFragment? Решил что нет

        // Создание экземпляра MenuListFragment и назначение ему режим отображения элементов в виде карточек
        MenuListFragment startFragment = MenuListFragment.newInstance(MenuListFragment.CARD_MODE);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_menu_container, startFragment); // startFragment заменяет контейнер лдя фрагментов "fragment_menu_container"
        transaction.commit(); // Завершить транзакцию и отобразить фрагмент
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
}
