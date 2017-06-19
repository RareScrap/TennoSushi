package com.webtrust.tennosushi;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.webtrust.tennosushi.fragments.MenuListFragment;
import com.webtrust.tennosushi.fragments.ShoppingCartFragment;

/**
 * Основная активити приложения
 * @author RareScrap
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    /** Фрагмет корзины, который будет доступен на все время работы приложения */
    public ShoppingCartFragment shoppingCartFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main); // Заполнение экрана начальным макетом

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

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
}
