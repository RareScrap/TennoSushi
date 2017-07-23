package com.webtrust.tennosushi.fragments;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.webtrust.tennosushi.MainActivity;
import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.adapters.ShoppingCartItemRecyclerViewAdapter;
import com.webtrust.tennosushi.list_items.FoodItem;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
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

    /** Элемент GUI, реализующий функции отображения списка */
    private RecyclerView recyclerView;
    /** Элемент GUI, реализующий функции кнопки "купить" */
    private Button buyButton;
    /** LayoutManager для отображения в виде списка */
    private RecyclerView.LayoutManager listLayoutManager;

    /** Адаптер для связывания {@link #recyclerView}
     * c {@link ShoppingCartFragment#listLayoutManager} */
    public ShoppingCartItemRecyclerViewAdapter rvAdapter;

    // TODO: МНЕ КАЖЕТСЯ, ЧТО ЭТО ПИЗДЕЦ КАКАЯ ЕБАЛА
    /** Ссылка на свой последний созданный экземпляр */
    public static ShoppingCartFragment shoppingCartFragmentRef;

    /** Общая сумма блюд */
    public static double totalPrice;

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
     *                           сохраненного состояния. Этот объект и есть его предыдущее состояние.
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

        // Получение ссылки на кнопку "купить"
        buyButton = (Button) getView().findViewById(R.id.buy_button);
        // Установка слушателя для кнопки "купить"
        buyButton.setOnClickListener(buyClickListener);

        // Создать RecyclerView.Adapter для связывания элементов FoodItem с RecyclerView
        rvAdapter = new ShoppingCartItemRecyclerViewAdapter(addedFoodList, pictureClickListener, this); // Второй аргмент - слушатель кликов по картинке блюда
        recyclerView.setAdapter(rvAdapter);

        /*
        Новый экзмепляр LayoutManager'а создается при возрате к этому фрагменту
        через BackStack во избежания исключения "LayoutManager is already attached
        to a RecyclerView”
         */
        listLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(listLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator() {
            /**
             * Отвечает за анимацию "выдвижения" кнопки Undo (и, возможно, за что-то еще)
             * @param oldHolder Элемент списка, который подвергся изменению
             * @param newHolder Элемент списка, который получился из-за произошедших изменений
             * @param fromX Левый край oldHolder
             * @param fromY Вверхий край oldHolder
             * @param toX Левый край newHolder
             * @param toY Вверхий край newHolder
             * @return True, если позже ожидается (запрашивается) вызов {@link #runPendingAnimations()}, иначе - false
             */
            @Override
            public boolean animateChange(RecyclerView.ViewHolder oldHolder, RecyclerView.ViewHolder newHolder,
                                         int fromX, int fromY, int toX, int toY) {
                // Получение ссылок на элемет CardView в oldHolder и newHolder
                CardView oldHolderCardView = (CardView) oldHolder.itemView.findViewById(R.id.root_card_view);
                CardView newHolderCardView = (CardView) newHolder.itemView.findViewById(R.id.root_card_view);

                // Сохранение старых значение elevation (на свякий случай)
                // СОХРАНЕНИЕ СТАРЫХ ЗНАЧЕНИЙ И ПОСЛЕДУЮЩЕЕ ИХ ИСПОЛЬЗОВАНИЕ ВЕДЕТ ТОМУ, ЧТО ТЕНЬ НЕ БУДЕТ УБИРАТЬСЯ!
                //float oldHolderElevation = oldHolderCardView.getCardElevation();
                //float newHolderElevation = newHolderCardView.getCardElevation();

                // Убираем тень при выдвижении кнопки Undo
                oldHolderCardView.setCardElevation(0);
                newHolderCardView.setCardElevation(0);

                // Запускаем стандартную анимацию
                boolean returnedBool = super.animateChange(oldHolder, newHolder, fromX, fromY, toX, toY);

                // Возвращаем стандартных elevation
                //oldHolderCardView.setCardElevation(oldHolderElevation);
                //newHolderCardView.setCardElevation(newHolderElevation);

                // Возвращем результат стандартной анимации
                return returnedBool;
            }
        });

        // Отвечает за скорость "задвигания" элеметов, закрывающих пустоту после удаления элемета из середины
        // recyclerView.getItemAnimator().setMoveDuration(6000);
        // int d = recyclerView.getItemAnimator().getMoveDuration();

        // На основании переданного списка определяет что показать: список покупок или картинку пустой корзины
        changeCartUI(addedFoodList);

        // Получить общий ценник на блюда
        totalPrice = ShoppingCartFragment.getTotalPrice();

        // Названичение текста actionBar'у
        ActionBar ab = ((MainActivity) this.getActivity()).getSupportActionBar();
        ab.setTitle(getResources().getString(R.string.shopping_cart)
                + " (" + new DecimalFormat("0.00").format(totalPrice) + " \u20BD)");
        ab.setSubtitle(""); // Стереть подстроку

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
     * Регенерирует ActionBar с новым ценником.
     */
    public void reDrawActionBar() {
        ActionBar ab = ((MainActivity) this.getActivity()).getSupportActionBar();
        ab.setTitle(getResources().getString(R.string.shopping_cart)
                + " (" + new DecimalFormat("0.00").format(totalPrice) + " \u20BD)");
        ab.setSubtitle(""); // Стереть подстроку
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        // ПРИНУДИТЕЛЬНО ВЫЗВАТЬ ВСЁ, ЧТО ЕСТЬ В ПЛАНИРОВЩИКЕ АДАПТЕРА
        for (HashMap.Entry<FoodItem, Runnable> pair: rvAdapter.pendingRunnables.entrySet()) {
            try {
                rvAdapter.handler.removeCallbacks(pair.getValue());
                pair.getValue().run();
            } catch (Exception ex) { ex.printStackTrace(); }
        }
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

                /* Удаляем опции. Они будут восстановленны в методе ShoppingCartItemRecclerViewAdapter::onBindViewHolder.
                Если этого не сделать - опции будут дублироваться при каждом нажатии кнопки UNDO */
                ((LinearLayout) viewHolder.itemView.findViewById(R.id.options)).removeAllViews();

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
                int pos = viewHolder.getLayoutPosition(); // TODO: getAdapterPosition()

                // Этот if сработает, когда элемент сдвинут, палец убран, но список плавно "задвигает" сдвинутый элемент
                if (viewHolder.getAdapterPosition() == -1) {
                    // not interested in those
                    return;
                }

                // Проверка на то, были ли инииализированы графические ресурсы для рисования фона и прочих объектов
                if (!initiated) {
                    init();
                }

                // Вьюха предыдущего элемента (null, если ее абсолютно нет на экране)
                View previousView = recyclerView.getLayoutManager().findViewByPosition(pos-1);
                // Вьюха следующего элемента (null, если ее абсолютно нет на экране)
                View nextView = recyclerView.getLayoutManager().findViewByPosition(pos+1);

                // Получение координат вьюх элементов списка
                int[] nextViewLocation = new int[2]; // Координаты следующей (за свайпнутой) вьюхи
                int[] itemViewLocation = new int[2];  // Координаты свайпнутой вьюхи
                int[] previousViewLocation = new int[2];  // Координаты предыдущей вьюхи
                ViewGroup.MarginLayoutParams lp1 = null; // Инициализация отступов для следующей ...
                ViewGroup.MarginLayoutParams lp2 = null; // ... и предыдущей вьюхи

                if (itemView != null) // Если элемент есть на экране
                    itemView.getLocationInWindow(itemViewLocation); // TODO: Как будет вести себя этот кусок кода, если анимации в процессе, а вьюхи нет на экране. Этого можно досич если во время анимации пролистать список вниз
                if (previousView != null) { // Если предыдущей элемент есть на экране (хотя бы частично)
                    previousView.getLocationInWindow(previousViewLocation); // Получить координаты предыдущего элемента
                    lp2 = (ViewGroup.MarginLayoutParams) previousView.getLayoutParams(); // Получить отступы предыдущего элемента
                }
                if (nextView != null) { // Если следущющий элемент есть на экране (хотя бы частично)
                    nextView.getLocationInWindow(nextViewLocation); // Получить координаты следующего элемента
                    lp1 = (ViewGroup.MarginLayoutParams) nextView.getLayoutParams(); // Получить отступы следующего элемента
                }

                // Вычисления координат высот элеметов
                int cordTop;
                int cordBottom;
                ActionBar ab = ((MainActivity) getActivity()).getSupportActionBar();

                // Вычисление высоты статус бара и action бара
                // TODO: Не зннаю как будет работать для полноэкранного режима. Имеет смысл добавить проверку в if ниже
                int resultStatusBar = 0;
                int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
                if (resourceId > 0) {
                    resultStatusBar = getResources().getDimensionPixelSize(resourceId);
                }
                int barsOffset = resultStatusBar+ab.getHeight(); // Смещение для канвы по оси Y

                if (previousView != null) {
                    cordTop = previousViewLocation[1]+previousView.getHeight()- barsOffset+lp2.topMargin+lp2.bottomMargin;
                    //Log.d( String.valueOf( previousView.getTop() ), String.valueOf( cordTop ));
                } else {
                    cordTop = itemView.getTop();
                }

                if (nextView != null) {
                    //Log.d( String.valueOf( nextView.getTop() ), String.valueOf( nextViewLocation[1] ));
                    //Log.d( "a", "1");
                    cordBottom = nextViewLocation[1] - barsOffset-lp1.topMargin-lp1.bottomMargin;
                } else {
                    //Log.d( "a", "2");
                    cordBottom = itemView.getBottom(); // TODO: Учитывает ли getBottom отступы?
                }

                background.setBounds(itemView.getRight() + (int) dX, cordTop, itemView.getRight(), cordBottom);
                background.draw(c);

                // Рисование иконки удаления
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
                deleteMark.draw(c); // Рисует иконку удаления

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
        View emptyCartPic = null;
        try { emptyCartPic = getView().findViewById(R.id.empty_cart_pic); }
        catch (Exception ex) { ex.printStackTrace(); }

        View buyButtonController = null;
        try { buyButtonController = getView().findViewById(R.id.buy_button_container); }
        catch (Exception ex) { ex.printStackTrace(); }

        if (list.isEmpty()) { // Показать картинку пустой корзины
            if (emptyCartPic != null)
                emptyCartPic.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            if (buyButtonController != null)
                buyButtonController.setVisibility(View.GONE);
        } else { // Показать элеметы корзиы
            if (emptyCartPic != null)
                emptyCartPic.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            if (buyButtonController != null)
                buyButtonController.setVisibility(View.VISIBLE);
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

    /**
     * Обрабатывает события клика по картинке блюда для открытия фрагмента
     * {@link DetailFoodFragment} с подробной информацей о нем
     */
    private final View.OnClickListener pictureClickListener = new View.OnClickListener() {
        /**
         * Вызывается когда по картинке блюда произошел клик.
         * Открывает {@link DetailFoodFragment} с подробной информацей о блюде
         * @param view {@link View}, по которому был сделан клик
         */
        @Override
        public void onClick(View view) {
            // Получение порядкового номера элемета в списке
            ShoppingCartItemRecyclerViewAdapter.ViewHolder viewHolder =
                    (ShoppingCartItemRecyclerViewAdapter.ViewHolder) recyclerView.getChildViewHolder(/*(View)*/ getActivity().findViewById(R.id.root_card_view)/*getParent().getParent().getParent()*/);
            int position = viewHolder.getAdapterPosition();
            FoodItem clickedFoodView = addedFoodList.get( position ); // Получение сответствующего FoodItem'а

            // Использется констуктор копирования для создания объекта с такими же полями, но без метаифомации
            // Элементы с одинаковой метаинформацией в списках при различых операциях приводят к непредсказуемому поведеию элеметов списка
            FoodItem newFoodItem = new FoodItem(clickedFoodView);

            // Открытие фрагмета с детальой информацией о блюде
            FragmentTransaction fTrans = getFragmentManager().beginTransaction();
            DetailFoodFragment detailFoodFragment = DetailFoodFragment.newInstance(newFoodItem);
            fTrans.addToBackStack(null);
            fTrans.replace(R.id.fragment_menu_container, detailFoodFragment);
            fTrans.commit();

            // TODO: При первом запуске приложения без этой строки можно обойтись, но после изменения currentMode, без этой строки не стирается прдыдущий view
            ( (ViewGroup) getActivity().findViewById(R.id.fragment_menu_container) ).removeAllViews();
        }
    };

    /**
     * Обрабатывает события клика по кнопке "купить", открывая {@link DeliveryOptionsFragment}
     */
    private final View.OnClickListener buyClickListener = new View.OnClickListener() {
        /**
         * Вызывается когда по кнопке "купить" произошел клик.
         * Открывает {@link DeliveryOptionsFragment} с выбором способа доставки.
         * @param view {@link View}, по которому был сделан клик
         */
        @Override
        public void onClick(View view) {
            // Открытие фрагмета с выбором способа доставки
            FragmentTransaction fTrans = getFragmentManager().beginTransaction();
            DeliveryOptionsFragment deliveryOptionsFragment = new DeliveryOptionsFragment();
            fTrans.addToBackStack(null);
            fTrans.replace(R.id.fragment_menu_container, deliveryOptionsFragment);
            fTrans.commit();

            // TODO: При первом запуске приложения без этой строки можно обойтись, но после изменения currentMode, без этой строки не стирается прдыдущий view
            ( (ViewGroup) getActivity().findViewById(R.id.fragment_menu_container) ).removeAllViews();
        }
    };

    public static double getTotalPrice() {
        double totalPrice = 0;
        for (FoodItem fi: addedFoodList)
            totalPrice += fi.price * fi.count;
        return totalPrice;
    }
}
