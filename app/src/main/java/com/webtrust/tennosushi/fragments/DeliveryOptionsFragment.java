package com.webtrust.tennosushi.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.webtrust.tennosushi.MainActivity;
import com.webtrust.tennosushi.MapsActivity;
import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.json_objects.*;
import com.webtrust.tennosushi.json_objects.OrderObject.*;
import com.webtrust.tennosushi.list_items.OrderItem;
import com.webtrust.tennosushi.services.PopUpService;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

import static com.webtrust.tennosushi.utils.PhoneNumberChecker.checkNumber;

/**
 * Фрагмент, предоставляющий возможность выбрать способ доставки: курьером или самовывозом.
 * Если выбран курьер - пользователю предлагается ввести адрес (или он определяется автоматически).
 * Если выбран самовывоз - открывается {@link MapView} с отображением адреса пункта самовывоза
 *
 * @author RareScrap
 */
public class DeliveryOptionsFragment extends Fragment
        implements View.OnClickListener,
        OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    /** Элемент GUI, представляющий собой радио-кнопку курьерской доставки */
    private RadioButton courierDelivery;
    /** Элемент GUI, представляющий собой радио-кнопку доставки самовывозом */
    private RadioButton selfDelivery;

    /** Элемент GUI, представляющий собой контейнер текстовых полей, в которых записывается адрес доставки */
    private LinearLayout addressContainer;
    /** Элемент GUI, представляющий собой контейнер карты, которая отображает пункт самовывоза */
    private FrameLayout mapContainer;

    /** Элемент GUI, представляющий собой текстовое поле адреса доставки */
    private EditText address;
    /** Элемент GUI, представляющий собой номер квартиры получателя */
    private EditText apartmentNumber;
    /** Элемент GUI, представляющий собой номер подъезда получателя */
    private EditText porchNumber;
    /** Элемент GUI, представляющий собой номер телефона получателя */
    private EditText telephoneNumber;
    /** Элемент GUI, представляющий собой кнопку выбора места на карте с заполнением поля адреса */
    private ImageButton button;
    /** Элемент GUI, представляющий собой кнопку совершения заказа */
    private Button orderButton;

    /** Элемент GUI, представляющий собой View'ху Google карты  */
    private MapView mapView;
    /** Элемент GUI, представляющий собой объект Google карты */
    private GoogleMap map;
    /** Геокодер, для получения информации о текущем местоположении */
    private Geocoder geocoder;

    /** Элемент GUI, представляющий собой {@link ActionBar} фрагмета */
    private ActionBar ab;

    /** Содержит адрес, возвращаемый MapsActivity после выбора места доставки */
    public static String adr;

    /** Метоположение пункта самовывоза */
    private final LatLng selfDeliveryLocation = new LatLng(52.291128, 104.2490896);
    /** Зум карты по умолчанию */
    private static final int DEFAULT_ZOOM = 15;
    /** Константа предоставленных прав на определение местоположения */
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    /** True, если пользователь предоставил разрешение для геоданных. Иначе, false.*/
    private boolean locationPermissionGranted;

    /** Точка входа для Google Play services. Используется Places API и Fused Location Provider */
    private GoogleApiClient googleApiClient;

    /**
     * Освобождает ресурсы фрагмента. В особенности, {@link #mapView} и {@link #googleApiClient}
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        googleApiClient.stopAutoManage(getActivity());
        googleApiClient.disconnect();
    }

    /**
     * Создает View фрагмента, устанавливает слушатели и текста ActionBar'у, получает карту и
     * инициализирует {@link #googleApiClient}
     *
     * @param inflater Инфлаттер для получения View из XML-разметки
     * @param container Если не равно NULL, это родительский ViewGroup, к которому должен
     *                  быть присоединен View фрагмента. Это может быть использовано для получение
     *                  LayoutParams родительского элемента. ВАЖНО: Фрагмент не должен самостоятельно
     *                  добавлять сюда свой View!
     * @param savedInstanceState Если не равно NULL, то фрагмент восстановился из предыдущего
     *                           сохраненного состояния. Этот объект и есть его предыдущее состояние.
     * @return View фрагмента.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Надуваем View из XML-разметки
        final View returnedView = inflater.inflate(R.layout.fragment_delivery_options, container, false);

        // Получение ссылок на элементы GUI
        courierDelivery = (RadioButton) returnedView.findViewById(R.id.courier_delivery);
        selfDelivery = (RadioButton) returnedView.findViewById(R.id.self_delivery);
        addressContainer = (LinearLayout) returnedView.findViewById(R.id.address_container);
        mapContainer = (FrameLayout) returnedView.findViewById(R.id.map_container);
        address = (EditText) returnedView.findViewById(R.id.address_EditText);
        apartmentNumber = (EditText) returnedView.findViewById(R.id.apartment_number_EditText);
        porchNumber = (EditText) returnedView.findViewById(R.id.porch_number_EditText);
        telephoneNumber = (EditText) returnedView.findViewById(R.id.telephone_number_EditText);
        mapView = (MapView) returnedView.findViewById(R.id.map_view);
        ab = ((MainActivity) this.getActivity()).getSupportActionBar();
        button = (ImageButton) returnedView.findViewById(R.id.set_on_map);
        orderButton = (Button) returnedView.findViewById(R.id.order_button);

        // получаем сохранённые данные
        try {
            Scanner sc = new Scanner(getContext().openFileInput("delivery.json"));
            String json = "";
            while (sc.hasNextLine()) json += sc.nextLine();
            sc.close();
            SavedAddressObject sao = SavedAddressObject.fromJSON(json);
            telephoneNumber.setText(sao.phoneNumber);
            address.setText(sao.address);
            if (sao.apartmentNumber != -1) apartmentNumber.setText(Integer.toString(sao.apartmentNumber));
            if (sao.porchNumber != -1) porchNumber.setText(Integer.toString(sao.porchNumber));
        } catch (Exception ex) { ex.printStackTrace(); }

        // Создаем View карты
        mapView.onCreate(savedInstanceState);

        // Установка слушателей на радиокнопки
        courierDelivery.setOnClickListener(this);
        selfDelivery.setOnClickListener(this);
        button.setOnClickListener(new View.OnClickListener() {
            /**
             * При клике на кнопку, запускает активити с картой для выбора адреса
             *
             * @param v {@link View}, по которой был сдела клик для вызова этого метода (т.е. сама кнопка)
             */
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // Установка слушателя на кнопку заказа
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* new AlertDialog.Builder(getContext()).setTitle("Hello World!")
                        .setMessage("Hello! I'm little alert dialog!")
                        .setCancelable(false)
                        .setPositiveButton("OK, got it!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) { }
                        })
                        .create().show(); */

                AlertDialog.Builder adb = new AlertDialog.Builder(getContext());
                adb.setTitle(R.string.error);
                adb.setIcon(R.drawable.ic_close_black_24dp);
                adb.setPositiveButton(R.string.ok, null);

                // многочисленные проверки введённых данных
                // проверка телефона
                if (telephoneNumber.getText().length() == 0) {  // пустой номер телефона
                    adb.setMessage(R.string.enter_phone_number);
                    adb.create().show();
                    return;
                }
                if (!checkNumber(telephoneNumber.getText().toString())) {   // неверный формат номера телефона
                    adb.setMessage(R.string.incorrect_phone_number);
                    adb.create().show();
                    return;
                }

                if (courierDelivery.isChecked()) {
                    if (address.getText().length() == 0) { // пустой адрес
                        adb.setMessage(R.string.enter_address);
                        adb.create().show();
                        return;
                    }

                    if (apartmentNumber.getText().length() == 0) {  // пустой номер квартиры
                        adb.setMessage(R.string.enter_apartment_number);
                        adb.create().show();
                        return;
                    }
                    try {
                        Integer.parseInt(apartmentNumber.getText().toString());
                    } // неверный формат номера квартиры
                    catch (Exception ex) {
                        adb.setMessage(R.string.incorrect_apartment_number);
                        adb.create().show();
                        return;
                    }

                    if (porchNumber.getText().length() == 0) {  // пустой номер подъезда
                        adb.setMessage(R.string.enter_porch_number);
                        adb.create().show();
                        return;
                    }
                    try {
                        Integer.parseInt(porchNumber.getText().toString());
                    } // неверный формат номера подъезда
                    catch (Exception ex) {
                        adb.setMessage(R.string.incorrect_porch_number);
                        adb.create().show();
                        return;
                    }
                }

                // сохраняем данные
                try {
                    SavedAddressObject sao = new SavedAddressObject(telephoneNumber.getText().toString(),
                            address.getText().toString(), apartmentNumber.getText().toString(),
                            porchNumber.getText().toString());

                    FileOutputStream fos = getContext().openFileOutput("delivery.json", Context.MODE_PRIVATE);
                    fos.write(sao.getJSON().getBytes(("UTF-8")));
                    fos.close();
                } catch (Exception ex) { ex.printStackTrace(); }


                // да начнётся адовый ПИЗДЕЦ! (выгрузка на сервер)

                // создаём диалог с загрузкой
                final ProgressDialog d = new ProgressDialog(getContext());
                d.setTitle(R.string.please_wait);
                d.setMessage(getString(R.string.data_sending));
                d.setCancelable(false);
                d.show();

                // создаём объект заказа
                final OrderObject oo;
                if (selfDelivery.isChecked())
                    oo = new OrderObject(telephoneNumber.getText().toString(), ShoppingCartFragment.addedFoodList);
                else
                    oo = new OrderObject(address.getText().toString(),
                            Integer.parseInt(apartmentNumber.getText().toString()),
                            Integer.parseInt(porchNumber.getText().toString()),
                            telephoneNumber.getText().toString(), ShoppingCartFragment.addedFoodList);

                // выполняем все сетевые действия в отдельном потоке
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            // устанавливаем соединение
                            URL url = new URL("http://romhacking.pw:1234/");
                            HttpURLConnection http = (HttpURLConnection) url.openConnection();

                            http.setRequestMethod("POST");

                            // передаём объект заказа
                            http.setDoOutput(true);
                            http.setReadTimeout(5000);
                            http.setConnectTimeout(5000);
                            OutputStream os = http.getOutputStream();
                            os.write(oo.getJSON().getBytes("UTF-8"));

                            /* Охуеть, но чтобы POST заработал, нужно ещё
                               и считать данные, которые отсылает в ответ
                               сервер. Какой-то пиздец, однако. */
                            // считываем данные
                            Scanner sc = new Scanner(http.getInputStream());
                            String answer = "";
                            while (sc.hasNextLine()) answer += sc.nextLine();
                            sc.close();
                            final OrderObject_Answer orderObjectAnswer = OrderObject_Answer.getFromJSON(answer);
                            if (orderObjectAnswer.status.equals("ok")) {
                                // всё ок
                                if (PopUpService.items == null)
                                    PopUpService.items = new ArrayList<OrderItem>();
                                OrderItem oi = new Gson().fromJson(answer, OrderItem.class);
                                oi.order_date = new Date(System.currentTimeMillis());
                                PopUpService.items.add(oi);
                                PopUpService.loe.writeData(PopUpService.items);
                                returnedView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        d.hide();
                                        new AlertDialog.Builder(getContext())
                                                .setIcon(R.drawable.ic_check_black_24dp)
                                                .setTitle(R.string.done)
                                                .setMessage(getString(R.string.successful_order) + "\nID: " + orderObjectAnswer.order_id)
                                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                })
                                                .create().show();
                                    }
                                });
                            } else {
                                // чёт произошло
                                returnedView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        d.hide();
                                        new AlertDialog.Builder(getContext())
                                                .setTitle(R.string.error_has_occured)
                                                .setMessage("Сервер вернул \"fail!\".")
                                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                    }
                                                })
                                                .create().show();
                                    }
                                });
                            }

                            // отключаемся
                            http.disconnect();

                            // убираем диалог
                            returnedView.post(new Runnable() {
                                @Override
                                public void run() {
                                    d.hide();
                                }
                            });
                        } catch (Exception ex) {
                            // убираем диалог и показываем стэк трейс в другом диалоге
                            returnedView.post(new Runnable() {
                                @Override
                                public void run() {
                                    d.hide();
                                }
                            });
                            final StringWriter sw = new StringWriter();
                            PrintWriter pw = new PrintWriter(sw);
                            ex.printStackTrace(pw);
                            pw.close();
                            returnedView.post(new Runnable() {
                                @Override
                                public void run() {
                                    new AlertDialog.Builder(getContext())
                                            .setTitle(R.string.error_has_occured)
                                            .setMessage(sw.toString())
                                            .create().show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        // Инициализация клиента Play services для использования в Fused Location Provider и Places API.
        // Используйте метод addApi() для запроса Places API и Fused Location Provider.
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .enableAutoManage(getActivity(), this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        googleApiClient.connect();

        // инициализируем получение данных с GPS
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        LocationManager lm = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        try { lm.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, Looper.myLooper()); }
        catch (Exception ex) { ex.printStackTrace(); }

        // Получения Google карты для MapView (map не назначается mapView!)
        mapView.getMapAsync(this);

        // Назначение текста в титл тулбара
        ab.setTitle(R.string.delivery_option_title);

        // Возвращаем View фрагмента
        return returnedView;
    }

    /**
     * Обрабатывает клик по радиокнопкам и отображает соответствующий контент в зависимости от
     * выбранной кнопки
     *
     * @param v View (радио-кнопка), по которой был сделан клик
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.courier_delivery: // Показываем только контент для курьерской доставки
                mapContainer.setVisibility(View.GONE);
                addressContainer.setVisibility(View.VISIBLE);
                break;
            case R.id.self_delivery: // Показываем только контент для доставки самоывозом
                mapContainer.setVisibility(View.VISIBLE);
                addressContainer.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {}

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    /**
     * Сохраняет карту и выставляет зум и начальную точку
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Сохраненяем карту
        map = googleMap;

        // Выставляем локацию и зум для пункта самовывоза
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(selfDeliveryLocation, DEFAULT_ZOOM);
        map.animateCamera(cameraUpdate); // Анимируем камеру, плавно приближаясь к искомой точке

        updateLocationUI(); // Инициализируем GUI карты
    }

    /**
     * Коллбек, обрабатывающий полученное разрешение.
     *
     * @param requestCode  Код входящего предоставленного разрешения
     * @param permissions  Массив разрешений
     * @param grantResults Массив подтвержденных разрешений
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        locationPermissionGranted = false; // Начальая инициализация
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: { // Разрешение предоставлено
                // Если запрос разрешения отменет, массив результатов будет пустым
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI(); // Обвновляем GUI карты
    }

    /**
     * Обновляет GUI карты (например, включает/отключает кнопку MyLocation)
     */
    private void updateLocationUI() {
        if (map == null) { // Проверка существования карты
            return;
        }

        // Запрашиваем разрешение на получение геоданных. Результат обрабатываетя коллбэком (onRequestPermissionsResult)
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        // Если разрешение на использование геоданных
        if (locationPermissionGranted) { // Если получено - включаем кнопку определения местоположения
            map.setMyLocationEnabled(true);
            map.getUiSettings().setMyLocationButtonEnabled(true);
        } else { // Если нет - отключаем эту кнопку
            map.setMyLocationEnabled(false);
            map.getUiSettings().setMyLocationButtonEnabled(false);
        }
    }

    /**
     * Восстанавливае {@link #mapView}
     */
    @Override
    public void onResume() {
        mapView.onResume();

        // Код ниже затирал загруженные сохранённые данные. Поправлено.
        if (adr != null) {
            address.setText(adr); // Устанавливает адрес, который был выбран в MapActivity
            adr = null;
        }
        super.onResume();
    }

    /**
     * Сигнализирует {@link #mapView}, что свободна оперативная память заканчивается
     */
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    /**
     * Вызывается, когда обновились данные с GPS.
     *
     * @param location Данные с GPS
     */
    @Override
    public void onLocationChanged(Location location) {
        try {
            // получаем адрес
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Address a = addresses.get(0);

            // если поле с адресом уже имеет что-либо, то не заменять содержимое актуальными даными
            if (address.getText().length() != 0) return;

            // если адрес был получен успешно, вставить его в поле с адресом
            if (a != null) {
                StringBuilder sb = new StringBuilder();
                sb.append(a.getLocality());
                sb.append(", ");
                sb.append(a.getThoroughfare());
                sb.append(", ");
                sb.append(a.getSubThoroughfare());
                address.setText(sb.toString());
            }
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) { }

    @Override
    public void onProviderEnabled(String provider) { }

    @Override
    public void onProviderDisabled(String provider) { }
}
