package com.webtrust.tennosushi;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import com.webtrust.tennosushi.adapters.OrderItemRecyclerViewAdapter;
import com.webtrust.tennosushi.services.PopUpService;

/**
 * Activity, на которой располагаются данные о заказах.
 */
public class OrdersActivity extends AppCompatActivity {
    /** RecyclerView, где располагается сама информация о заказах */
    public static RecyclerView orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // стандартные инициализации
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        // настраиваем RecyclerView
        orderList = (RecyclerView) findViewById(R.id.order_list);
        orderList.setAdapter(new OrderItemRecyclerViewAdapter(PopUpService.items));
        LinearLayoutManager llm = new LinearLayoutManager(this);
        orderList.setLayoutManager(llm);
    }
}
