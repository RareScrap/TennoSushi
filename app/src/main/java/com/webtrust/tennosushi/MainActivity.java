package com.webtrust.tennosushi;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.webtrust.tennosushi.fragments.MenuListFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MenuListFragment startFragment = MenuListFragment.newInstance(MenuListFragment.PLATE_MODE);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.fragment_menu_container, startFragment);
        transaction.commit(); // Отобразить фрагмент
    }
}
