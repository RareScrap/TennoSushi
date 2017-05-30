package com.webtrust.tennosushi.fragments;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.adapters.ShoppingCartItemRecyclerViewAdapter;
import com.webtrust.tennosushi.list_items.FoodItem;

import java.util.ArrayList;
import java.util.List;

/**
 * <p> Фрагмент, реализующий список блюд, которые пользователь добавил в корзину для покупки. </p>
 *
 * <h2 id="BestPractice">Best Practice</h2>
 * <ol>
 *  <li>
 *      Для обработки событий взаимодействия между этим фрагментом и активити (или между другими фрагментами),
 *      активити, которые содержат этот фрагмент, должны реализовывать интерфейс
 *      {@link #OnFragmentInteractionListener}.
 *  </li>
 *
 *  <li>
 *      Используйте фабричный метод {@link #newInstance} и его перегруженые версии
 *      для создания экземпляра этого фрагмента (без и с параметрами соответствено). Не используйте
 *      {@link #ShoppingCartFragment()} в качестве пустого конструктора, т.к. он предназначен только
 *      для использования самой AndroidOS для обеспечения правильного хранения данных приложения в
 *      свернутом состоянии. Кроме того избегайте перегрузки {@link #ShoppingCartFragment()} для
 *      создания конструкторов с параметрами для любых наследников класса {@link Fragment} по той же
 *      причине.
 *      Подробнее о конструкторе фрагментов на странице
 *      <a href="https://developer.android.com/reference/android/app/Fragment.html#Fragment()">Google документации</a>.
 *  </li>
 * </ol>
 *
 * @author RareScrap
 */
public class ShoppingCartFragment extends Fragment {
    /** Список объектов {@link FoodItem}, представляющие добавленные в корзину блюда */
    public static List<FoodItem> addedFoodList = new ArrayList<>();
    /** Итоговая цена всех заказанных блюд */
    public double totalPrice;

    /** Элемент GUI, реализующий функции отображения списка */
    private RecyclerView recyclerView;
    /** LayoutManager для отображения в виде списка */
    private RecyclerView.LayoutManager listLayoutManager;

    /** Адаптер для связывания {@link #recyclerView}
     * c {@link ShoppingCartFragment#listLayoutManager} */
    public ShoppingCartItemRecyclerViewAdapter rvAdapter;

    // TODO: МНЕ КАЖЕТСЯ, ЧТО ЭТО ПИЗДЕЦ КАКАЯ ЕБАЛА
    /** Ссылка на свой последний созданный экземпляр */
    public static ShoppingCartFragment shoppingCartFragmentRef;

    /**
     * Необходимый пустой публичный конструктор. Предназачен для использованя только по нужде
     * AndroidOS. Вместо него используйте {@link #newInstance()} (см. <a href="#BestPractice">Best Practice</a>).
     */
    public ShoppingCartFragment() {
        shoppingCartFragmentRef = this; // Обновление ссылки, когда OS вызывет конструктор в свернутом приложении
    }

    /**
     * Для создания новых экземпляров этого фрагмента без дополнительных параметров используйте этот
     * фабричный метод и его перегруженные версии в случае, когда вам нужно использовать конструктор с
     * параметрами (см. <a href="#BestPractice">Best Practice</a>)
     * @return Новый объект фрагмента {@link ShoppingCartFragment}.
     */
    public static ShoppingCartFragment newInstance() {
        return new ShoppingCartFragment();
    }

    /**
     * Задает начальная кофигурацию фрагменту (напирмер, определяет наличие команд меню).
     * @param savedInstanceState Если фрагмент восстанавливается из предыдущего сохраненного состояния,
     *                           это и есть его предыдущее состояние.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true); // у фрагмента имеются команды меню
    }

    /**
     * Инициализирует UI фрагмента
     * @param inflater Инфлаттер для вызова самых разых View, которые могут пригодиться во фрагменте
     * @param container Если не равно NULL, это родительский ViewGroup, к которому должен
     *                  быть присоединен UI фрагмента. Это может быть использовано для получение
     *                  LayoutParams родительского элемента. ВАЖНО: Фрагмент не должен самостоятельно
     *                  добавлять сюда свой View!
     * @param savedInstanceState Если не равно NULL, то фрагмент восстановился из предыдущего
     *                           сохраненного состояния. Этот объект  и есть его предыдущее состояние.
     * @return UI фрагмента.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_shopping_cart, (ViewGroup) this.getView(), false); // Inflate the layout for this fragment
    }

    /**
     * Задает значения полям, хранящие ссылки на элементы GUI (например, для {@link #recyclerView})
     * @param view UI, которое вернул метод {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * @param savedInstanceState Если не равно NULL, то фрагмент восстановился из предыдущего
     *                           сохраненного состояния. Этот объект  и есть его предыдущее состояние.
     */
    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        // Получение ссылки на recyclerView
        recyclerView = (RecyclerView) getView().findViewById(R.id.cart_list_recyclerView);

        // Создать RecyclerView.Adapter для связывания элементов FoodItem с RecyclerView
        rvAdapter = new ShoppingCartItemRecyclerViewAdapter(addedFoodList);
        recyclerView.setAdapter(rvAdapter);

        /*
        Новый экзмепляр LayoutManager'а создается при возрате к этому фрагменту
        через BackStack во избежания исключения "LayoutManager is already attached
        to a RecyclerView”
         */
        listLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(listLayoutManager);

        // На основании переданного списка определяет что показать: список покупок или картинку пустой корзины
        changeCartUI(addedFoodList);

        // Слушатель кликов, открывающий подробное описание блюда
        /*recyclerView.setOnClickListener( new RecyclerView.Adapter<ShoppingCartItemRecyclerViewAdapter.ViewHolder>);
        recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });*/

        // Запрос на получение данных
        try {
            // TODO: Тут картинка должна браться картинка из кеша
        } catch (Exception e) {
            e.printStackTrace();
        }

        setUpItemTouchHelper(); // Инициализация движка свайпов и сопутствующего функционала
        //setUpAnimationDecoratorHelper(); // Установка дополнительных графических эффектов для свайпов
    }

    /**
     * Обрабатывает события выбора команд меню.
     * @param item Выбранный элемент на панели действий (не путать этот параметр с MenuItem,
     *             обозначающий элемент списка)
     * @return True, если событие обработано успешо
     */
    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        // TODO: Добавить кнопку очистки корзины
        // Выбор в зависимости от идентификатора MenuItem
        /*switch (item.getItemId()) {
            case R.id.clean_cart:
                return true; // Событие меню обработано
        }*/

        return super.onOptionsItemSelected(item); //TODO: Разобраться зачем вообще тут нужен супер
    }

    /**
     * Подготавливает объект {@link ItemTouchHelper} и его колбэк
     * {@link android.support.v7.widget.helper.ItemTouchHelper.SimpleCallback} для прослушивания
     * свайпов и реализации основных графических эфектов свайпов (например, таких как красный фон
     * позади элемента, сдвигаемого свайпом)
     */
    private void setUpItemTouchHelper() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            /** Фон, который показывается "за" элеметом при свайпе */
            Drawable background;
            /** Рисунок, информирующей пользователя, что сделаый иим свайп приведет к удалеию элемета */
            Drawable deleteMark;
            /** Отступы {@link #deleteMark}'а */
            int xMarkMargin;
            /** Флаг того, что метод {@link #init()} был вызван */
            boolean initiated;

            /** Инициализирует ресурсы графики, требуемые для свайпа. Например, {@link #background} */
            private void init() {
                background = new ColorDrawable(Color.RED);
                deleteMark = ContextCompat.getDrawable(getActivity(), R.drawable.ic_delete_24dp); // Получение ресурса "мусорной корзины"
                deleteMark.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP); // Фильтр, которй делает корзину белой
                xMarkMargin = (int) getActivity().getResources().getDimension(R.dimen.swipe_delete_mark_margin);
                initiated = true;
            }

            /**
             * Метод, обязательный для реализации, но не использующийся в данном приложении.
             * Нужен для реализации "drag & drop".
             * @param recyclerView RecyclerView, к которому прикрепляется нащ ItemTouchHelper
             * @param viewHolder ViewHolder (т.е. элемент списка), который подвергается перетаскиванию
             *                   по инициативе пользователя
             * @param target ViewHolder, над которым перетаскивается текущий активный элемент
             * @return True, если viewHolder перемещен в позицию адаптера элемета target.
             */
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            /**
             * Возвращает допустимые направления свайпа для данного объекта viewHolder.
             * Эти допустимые направления свайпов настраиваются либо в кострукторе, либо в {@link #setDefaultSwipeDirs(int)}.
             * @param recyclerView {@link RecyclerView}, к которому прикрепляется наш {@link ItemTouchHelper}
             * @param viewHolder {@link RecyclerView.ViewHolder}, для которого запашивается направление свайпа
             * @return Логическое сложение допустимых свайпов (допустимый свайп представляет собой одну из костан группы "Direction Flag")
             */
            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                // Порядковый номер элемента, по которому пользователь хочет сделать свайп
                int position = viewHolder.getAdapterPosition();

                // Запретить свайп, если появился красный фон, ожидающий нажатие кнопки Undo
                if (rvAdapter.undoOn && rvAdapter.isPendingRemoval(position)) {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }

            /**
             * Вызывается когда свайп доведен до конца и палец пользователя убран с экрана.
             * Помещает в очередь на удаление или удаляет элемет списка в зависимости от
             * значения флага {@link ShoppingCartItemRecyclerViewAdapter#undoOn}.
             * @param viewHolder
             * @param swipeDir
             */
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                // Порядковый номер элемента, по которому был сделан свайп
                int swipedPosition = viewHolder.getAdapterPosition();
                if (rvAdapter.undoOn) {
                    rvAdapter.pendingRemoval(swipedPosition); // Добавить элемент в список элементов, ожидающих удаление
                } else {
                    rvAdapter.remove(swipedPosition); // Просто удалить элемент
                }
            }

            /**
             * Вызывается для отрисовки всех объектов позади элемента списка, который сдвинули свайпом.
             * Подробнее:
             * <a href="https://developer.android.com/reference/android/support/v7/widget/helper/ItemTouchHelper.Callback.html#onChildDraw(android.graphics.Canvas,%20android.support.v7.widget.RecyclerView,%20android.support.v7.widget.RecyclerView.ViewHolder,%20float,%20float,%20int,%20boolean)"></a>
             *
             * <p>
             * Этот метод так же вызывается, когда элемент сдвинут, палец убран, но список плавно
             * "задвигает" сдвинутый элемент. При этом viewHolder.getAdapterPosition() дает -1.
             * </p>
             *
             * @param c {@link Canvas}, на котором RecyclerView рисует то, что ему скажут
             * @param recyclerView {@link RecyclerView}, к которому прикрепляется наш {@link ItemTouchHelper}
             * @param viewHolder {@link RecyclerView.ViewHolder}, который анимируется из-за действий пользователя, или сам по себе
             * @param dX Величина горизонтального смещения (отностительно "нормальной" позиции элемента), вызванного действием пользователя
             * @param dY Величина вертикального смещения (отностительно "нормальной" позиции элемента), вызванного действием пользователя
             * @param actionState Тип взаимодействия во View. Это может быть ACTION_STATE_DRAG или ACTION_STATE_SWIPE.
             * @param isCurrentlyActive True, когда анимация вызвана дейсвтиями юзера и False, когда анимация работает "сама по себе"
             */
            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                // View, по которому был сделан свайп
                View itemView = viewHolder.itemView;

                // Этот if сработает, когда элемент сдвинут, палец убран, но список плавно "задвигает" сдвинутый элемент
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                // Проверка на то, были ли инииализированы графические ресурсы для рисования фона и прочих объектов
                if (!initiated) {
                    init();
                }

                // Рисуем красный фон
                background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                background.draw(c);

                // Замер элемета, по которому был сдела свайп
                int itemHeight = itemView.getBottom() - itemView.getTop(); // Растояние в пикселях!
                // Замер размеов иконки удаления
                int intrinsicWidth = deleteMark.getIntrinsicWidth(); // Слово "Intrinsic" можно просто отбросить
                int intrinsicHeight = deleteMark.getIntrinsicWidth();

                // Определение позиции икоки на красном фоне
                int xMarkLeft = itemView.getRight() - xMarkMargin - intrinsicWidth;
                int xMarkRight = itemView.getRight() - xMarkMargin;
                int xMarkTop = itemView.getTop() + (itemHeight - intrinsicHeight)/2;
                int xMarkBottom = xMarkTop + intrinsicHeight;

                // Определяет размеры квадратой области за сдвинутым элеметом, где будет нарисован xMark
                deleteMark.setBounds(xMarkLeft, xMarkTop, xMarkRight, xMarkBottom);
                deleteMark.draw(c); // Рисует икоку удалеия

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * Скрывает список и показывает картинку о том, что корзина пуста
     * @param list Список, по которому определяется пустая ли корзина или нет.
     */
    public void changeCartUI(List list) {
        if (list.isEmpty()) { // Показать картинку пустой корзины
            getView().findViewById(R.id.empty_cart_pic).setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            getView().findViewById(R.id.buy_button_container).setVisibility(View.GONE);
        } else { // Показать элеметы корзиы
            getView().findViewById(R.id.empty_cart_pic).setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            getView().findViewById(R.id.buy_button_container).setVisibility(View.VISIBLE);
        }
    }

    // TODO: Я не могу отследить воздействие этого метода, однако он был в учебном проекте "recycler-view-swipe-to-delete"
    /**
     * Иниализирует {@link android.support.v7.widget.RecyclerView.ItemDecoration} для
     * {@link ShoppingCartFragment#recyclerView}, обеспечивая особый графический эффект, пока
     * активны другие анимации. Этот графический эффект будет рисовать красный фон на пустом пространстве,
     * которое оставил сдвинутый свайпом элемент, пока другие элеметы списка сдвигаются к их новым позициям,
     * заполяя таким образом появившуюся пустоту.
     */
    private void setUpAnimationDecoratorHelper() {
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            /** Фон, который показывается "за" элеметом при свайпе */
            Drawable background;
            /** Флаг того, что метод {@link #init()} был вызван */
            boolean initiated;

            /** Инициализирует ресурсы графики, требуемые для свайпа. Например, {@link #background} */
            private void init() {
                background = new ColorDrawable(Color.RED);
                initiated = true;
            }

            /**
             * Отрисовывает красный фон на пустом пространстве, которое оставил сдвинутый свайпом
             * элемент, пока другие элеметы списка сдвигаются к их новым позициям, заполяя таким
             * образом появившуюся пустоту
             * @param c {@link Canvas}, на котором отрисовывается необходимый графический эффект
             * @param parent {@link RecyclerView}, чей {@link android.support.v7.widget.RecyclerView.ItemDecoration}
             *                                   рисует на холсте "c"
             * @param state Текущее состояние {@link RecyclerView}
             */
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {

                if (!initiated) {
                    init();
                }

                // TODO: Руссифицировать комментарии
                // only if animation is in progress
                if (parent.getItemAnimator().isRunning()) {
                    /*
                    Некоторые элементы могуть быть анимированы вниз, а некоторые анимированы вверх, чтобы закрыть место, оставленно предварительно удаленным элементом.
                    Не исключено, что оба движения могут происходить одновременно.
                    Чтобы воспроизвести это, достаточно одновременно удалить два элемента (очень удобно, если поддерживает мультитач. Еще, это будет лучше заметно, если одновременно удалить два соседних элемента).
                    Так же это можно воспроизвести, удалив два элемета, между которыми есть еще один элемет, а затем удалив и этот самый элемет посередине.
                     */

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
