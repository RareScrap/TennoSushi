package com.webtrust.tennosushi.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
        rvAdapter = new ShoppingCartItemRecyclerViewAdapter(addedFoodList, itemTouchListener);
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
     * Обрабатывает события клика по элементам списка
     * {@link FoodListFragment#foodItemList}, вызывая подробную информацию о блюде.
     */
    private final View.OnTouchListener itemTouchListener = new View.OnTouchListener() {
        public String[] Action = {
            "LR", // Слева направо
            "RL", // Справа налево
            "TB", // Сверху вниз
            "BT", // Снизу вверх
            "None"};

        private static final int HORIZONTAL_MIN_DISTANCE = 50; // Минимальное расстояние для свайпа по горизонтали
        private static final int VERTICAL_MIN_DISTANCE = 80; // Минимальное расстояние для свайпа по вертикали
        private float downX, downY, upX, upY; // Координаты
        private String mSwipeDetected = Action[4]; // Последнее дейтсвие

        public boolean swipeDetected() {
            return mSwipeDetected != Action[4];
        }

        public String getAction() {
            return mSwipeDetected;
        }

        /**
         * Вызывается когда по элементу списка в корзине товаров произошел клик.
         * Открывает {@link DetailFoodFragment}.
         * @param view {@link View}, по которому был сделан клик
         */
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            // TODO: Сделать открытие DetailFragment по нажатию

            // TODO: При первом запуске приложения без этой строки можно обойтись, но после изменения currentMode, без этой строки не стирается прдыдущий view
            //( (ViewGroup) getActivity().findViewById(R.id.fragment_menu_container) ).removeAllViews();


            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN: {
                    downX = event.getX();
                    downY = event.getY();
                    mSwipeDetected = Action[4];
                    break;
                }
                case MotionEvent.ACTION_MOVE: {
                    upX = event.getX();
                    upY = event.getY();

                    float deltaX = downX - upX;
                    float deltaY = downY - upY;

                    // Обнаружение горизонтального свайпа
                    if (Math.abs(deltaX) > HORIZONTAL_MIN_DISTANCE) {
                        // Слева направо
                        if (deltaX < 0) {
                            mSwipeDetected = Action[0];
                        }
                        // Справа налево
                        if (deltaX > 0) {
                            mSwipeDetected = Action[1];
                        }
                    }
                    break;/*else

                    // Обнаружение вертикального свайпа
                    if (Math.abs(deltaY) > VERTICAL_MIN_DISTANCE) {
                        // Сверху вниз
                        if (deltaY < 0) {
                            mSwipeDetected = Action.TB;
                            return false;
                        }
                        // Снизу вверх
                        if (deltaY > 0) {
                            mSwipeDetected = Action.BT;
                            return false;
                        }
                    }*/
                }
            }


            // Для свайпов
            int position = Integer.parseInt( view.getTag().toString(), 10 );
            // Если произошло нажатие по Header или Footer, то ничего не делаем
            //if (position == 0 || position == addedFoodList.size() + 1)
                //return;

            Message msg = new Message();
            msg.arg1 = position;
            // Если был обнаружен свайп, то удаляем айтем
            if (this.swipeDetected()){
                if (this.getAction().equals(Action[0])  ||
                        this.getAction().equals(Action[1]))
                {
                    msg.what = MSG_ANIMATION_REMOVE;
                    msg.arg2 = swipeDetector.getAction().equals(Action[0]) ? 1 : 0;
                    msg.obj = view;
                }
            }
            // Иначе выбираем айтем
            else
                msg.what = MSG_CHANGE_ITEM;

            handler.sendMessage(msg);
            return true;
        }
    };

    /**
     * Запуск анимации удаления
     */
    private Animation getDeleteAnimation(float fromX, float toX, int position)
    {
        Animation animation = new TranslateAnimation(fromX, toX, 0, 0);
        animation.setStartOffset(100);
        animation.setDuration(800);
        animation.setAnimationListener(new DeleteAnimationListenter(position));
        animation.setInterpolator(AnimationUtils.loadInterpolator(getContext(),
                android.R.anim.anticipate_overshoot_interpolator));
        return animation;
    }

    /**
     * Listenter служит для удаления айтема после того, как анимация удаления завершилась
     */
    public class DeleteAnimationListenter implements Animation.AnimationListener
    {
        private int position;
        public DeleteAnimationListenter(int position) {
            this.position = position;
        }

        @Override
        public void onAnimationEnd(Animation arg0) {
            addedFoodList.remove(position);
            rvAdapter.notifyDataSetChanged();
            //removeItem(position);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {}

        @Override
        public void onAnimationStart(Animation animation) {}
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {

            switch (msg.what)
            {
                case MSG_UPDATE_ADAPTER: // Обновление ListView
                    rvAdapter.notifyDataSetChanged();
                    //setCountPurchaseProduct();
                    break;
                case MSG_CHANGE_ITEM: // Сделано / Не сделано дело
                    /*ToDoItem item = list.get(msg.arg1);
                    item.setCheck(!item.isCheck());
                    Utils.sorting(list, 0);
                    saveList();
                    adapter.notifyDataSetChanged();
                    setCountPurchaseProduct();*/
                    break;
                case MSG_ANIMATION_REMOVE: // Старт анимации удаления
                    View view = (View)msg.obj;
                    view.startAnimation(getDeleteAnimation(0, (msg.arg2 == 0) ? -view.getWidth() : 2 * view.getWidth(), msg.arg1));
                    break;
            }
        }
    };
}
