package com.webtrust.tennosushi.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.list_items.FoodItem;

/**
 * Фрагмент, отображающий подробную информацию о блюде, по которумы был
 * сделан клик в {@link FoodListFragment}.
 */

public class DetailFoodFragment extends Fragment {
    /** Блюдо, по которому кликнул пользователь */
    public FoodItem foodItem;

    /**
     * Необходимый пустой публичный конструктор.
     *
     * <p>
     * Используйте фабричный метод {@link DetailFoodFragment#newInstance} для
     * создания экземпляра этого фрагмента. Избегайте создания конструкторов с
     * параметрами для любых наследников класса {@link Fragment}.
     * Подробнее о конструкторе фрагментов на странице
     * <a href="https://developer.android.com/reference/android/app/Fragment.html#Fragment()">Google документации</a>.
     * </p>
     */
    public DetailFoodFragment() {}

    /**
     * Используйте этот фабричный метод для создания новых экземпляров
     * этого фрагмента с использованием предоставленных параментров
     *
     * @param foodItem Объект блюда, по которому кликнул пользователь
     * @return Новый объект фрагмента {@link DetailFoodFragment}.
     */
    public static DetailFoodFragment newInstance(FoodItem foodItem) {
        DetailFoodFragment fragment = new DetailFoodFragment();

        fragment.foodItem = foodItem;

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true); // У фрагмента имеются команды меню
        View returnedView = inflater.inflate(R.layout.detail_food_fragment, container, false);

        // Получение ссылок на элементы GUI
        TextView priceTextView = (TextView) returnedView.findViewById(R.id.food_price_textField);

        // Назначение даных элементам GUI
        priceTextView.setText(foodItem.price + " \u20BD");

        return returnedView;
    }
}
