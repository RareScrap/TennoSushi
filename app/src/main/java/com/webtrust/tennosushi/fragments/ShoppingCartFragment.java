package com.webtrust.tennosushi.fragments;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;

import com.webtrust.tennosushi.CartListSwipeDetector;
import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.adapters.ShoppingCartItemRecyclerViewAdapter;
import com.webtrust.tennosushi.list_items.FoodItem;

import java.util.ArrayList;
import java.util.List;

/**
 * <p> Фрагмент, реализующий список блюд, которей пользователь добавил в корзину для покупки. </p>
 *
 * <p>
 * {@link ShoppingCartFragment} являетяся простым наследником класса {@link Fragment}.
 * Активити, которые содержат этот фрагмент должно реализовывать
 * интерфейс {@link MenuListFragment.OnFragmentInteractionListener}
 * (не реализованно т.к. пока не изучено) для обработки событий
 * взаимодействия между активностью и фрагментом.
 * </p>
 *
 * <p>
 * Используйте фабричный метод {@link ShoppingCartFragment#newInstance} для
 * создания экземпляра этого фрагмента. Избегайте создания конструкторов с
 * параметрами для любых наследников класса {@link Fragment}.
 * Подробнее о конструкторе фрагментов на странице
 * <a href="https://developer.android.com/reference/android/app/Fragment.html#Fragment()">Google документации</a>.
 * </p>
 *
 * @author RareScrap
 */

public class ShoppingCartFragment extends Fragment {
    /** Список объектов {@link FoodItem}, представляющих добавленные в корзиу блюда */
    public static List<FoodItem> addedFoodList = new ArrayList<>();
    /** Итоговая цена всех заказанных блюд */
    public double totalPrice;

    /** Элемент GUI, реализующий функции отображения списка */
    private RecyclerView recyclerView;
    /** LayoutManager для отображения в виде списка */
    private RecyclerView.LayoutManager listLayoutManager;

    /** Адаптер для связывания {@link ShoppingCartFragment#recyclerView}
     * c {@link ShoppingCartFragment#listLayoutManager}*/
    public ShoppingCartItemRecyclerViewAdapter rvAdapter; // Адаптер

    // Сообщения для Handler'а
    public static final int MSG_UPDATE_ADAPTER 		= 0;
    public static final int MSG_CHANGE_ITEM 		= 1;
    public static final int MSG_ANIMATION_REMOVE 	= 2;

    final CartListSwipeDetector swipeDetector = new CartListSwipeDetector();

    /**
     * Необходимый пустой публичный конструктор.
     *
     * <p>
     * Используйте фабричный метод {@link ShoppingCartFragment#newInstance} для
     * создания экземпляра этого фрагмента. Избегайте создания конструкторов с
     * параметрами для любых наследников класса {@link Fragment}.
     * Подробнее о конструкторе фрагментов на странице
     * <a href="https://developer.android.com/reference/android/app/Fragment.html#Fragment()">Google документации</a>.
     * </p>
     */
    public ShoppingCartFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // у фрагмента имеются команды меню
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shopping_cart, (ViewGroup) this.getView(), false);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        // Получение ссылки на recyclerView
        recyclerView = (RecyclerView) getView().findViewById(R.id.cart_list_recyclerView);

        /*
        Новый экзмепляр LayoutManager'ов создается при возрате к этому фрагменту
        через BackStack во избежания исключения "LayoutManager is already attached
        to a RecyclerView”
         */
        listLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(listLayoutManager);

        // Создать RecyclerView.Adapter для связывания тегов с RecyclerView
        rvAdapter = new ShoppingCartItemRecyclerViewAdapter(addedFoodList);
        recyclerView.setAdapter(rvAdapter);

        // Слушание свайпов
        //recyclerView.setOnTouchListener(swipeDetector);

        /*recyclerView.setOnClickListener( new RecyclerView.Adapter<ShoppingCartItemRecyclerViewAdapter.ViewHolder>);
        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });*/

        // Запрос на получение данных
        try {
            // тут картинка должна браться картинка из кеша
        } catch (Exception e) {
            e.printStackTrace();
        }

        setUpItemTouchHelper();
        setUpAnimationDecoratorHelper();
    }

    /**
     * Обработка выбора команд меню.
     * @param item Выбранный итем на панели действий (не путать этот параметр с MenuItem, обозначающий элемент списка
     * @return Показатель успешность обработки события
     */
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // Выбор в зависимости от идентификатора MenuItem
        /*switch (item.getItemId()) {
            case R.id.clean_cart:
                return true; // Событие меню обработано
        }*/

        return super.onOptionsItemSelected(item); //TODO: Разобраться зачем вообще тут нужен супер
    }

    /**
     * This is the standard support library way of implementing "swipe to delete" feature. You can do custom drawing in onChildDraw method
     * but whatever you draw will disappear once the swipe is over, and while the items are animating to their new position the recycler view
     * background will be visible. That is rarely an desired effect.
     */
    private void setUpItemTouchHelper() {

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            // we want to cache these and not allocate anything repeatedly in the onChildDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            // not important, we don't want drag & drop
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                int position = viewHolder.getAdapterPosition();
                ShoppingCartItemRecyclerViewAdapter testAdapter =
                        (ShoppingCartItemRecyclerViewAdapter) recyclerView.getAdapter();
                if (testAdapter.undoOn == true && testAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int swipedPosition = viewHolder.getAdapterPosition();
                ShoppingCartItemRecyclerViewAdapter adapter =
                        (ShoppingCartItemRecyclerViewAdapter) recyclerView.getAdapter();
                boolean undoOn = adapter.undoOn;
                if (undoOn) {
                    adapter.pendingRemoval(swipedPosition);
                } else {
                    adapter.remove(swipedPosition);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;

                // not sure why, but this method get's called for viewholder that are already swiped away
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                if (!initiated) {
                    init();
                }

                // draw red background
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * We're gonna setup another ItemDecorator that will draw the red background in the empty space while the items are animating to thier new positions
     * after an item is removed.
     */
    private void setUpAnimationDecoratorHelper() {
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {

            // we want to cache this and not allocate anything repeatedly in the onDraw method
            Drawable background;
            boolean initiated;

            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {

                    // some items might be animating down and some items might be animating up to close the gap left by the removed item
                    // this is not exclusive, both movement can be happening at the same time
                    // to reproduce this leave just enough items so the first one and the last one would be just a little off screen
                    // then remove one from the middle

                    // find first child with translationY > 0
                    // and last one with translationY < 0
                    // we're after a rect that is not covered in recycler-view views at this point in time
                    View lastViewComingDown = null;
                    View firstViewComingUp = null;

                    // this is fixed
                    int left = 0;
                    int right = parent.getWidth();

                    // this we need to find out
                    int top = 0;
                    int bottom = 0;

                    // find relevant translating views
                    int childCount = parent.getLayoutManager().getChildCount();
                    for (int i = 0; i < childCount; i++) {
                        View child = parent.getLayoutManager().getChildAt(i);
                        if (child.getTranslationY() < 0) {
                            // view is coming down
                            lastViewComingDown = child;
                        } else if (child.getTranslationY() > 0) {
                            // view is coming up
                            if (firstViewComingUp == null) {
                                firstViewComingUp = child;
                            }
                        }
                    }

                    if (lastViewComingDown != null && firstViewComingUp != null) {
                        // views are coming down AND going up to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    } else if (lastViewComingDown != null) {
                        // views are going down to fill the void
                        top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                        bottom = lastViewComingDown.getBottom();
                    } else if (firstViewComingUp != null) {
                        // views are coming up to fill the void
                        top = firstViewComingUp.getTop();
                        bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
                    }

                    background.setBounds(left, top, right, bottom);
                    background.draw(c);

                }
                super.onDraw(c, parent, state);
            }

        });
    }
}
