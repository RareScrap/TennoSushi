package com.webtrust.tennosushi.list_items;

import android.graphics.Bitmap;

/**
 * Объект, представляющий собой акцию, которой может воспользоваться пользователь
 */
public class OfferItem {
    /** ID акции */
    public final int id;
    /** Описание акции, показывающееся пользователю при клике на View'ху акции в карусели */
    public final String description;

    /** URL с картинкой. Если устройство - планшет, то слюда помещается URL картинки для портретной
     * ориентации экрана. */
    public final String picURL;
    /** URL с картинкой акции для планшетов в альбомной ориентации */
    public final String tabletPicURL;

    /** Картинка-баннер акции. Если устройство - планшет, то слюда помещается картинка для портретной
     * ориентации экрана. */
    public Bitmap bitmap;
    /** Картинка-баннер акции для планшетов в альбомной ориентации */
    public Bitmap bitmapTabletLandscape;

    /**
     * Конструктор, инициализирующий свои поля
     * @param id ID акции
     * @param description Описание акции, показывающееся пользователю при клике на View'ху акции в карусели
     * @param picURL URL с картинкой
     * @param tabletPicURL URL с картинкой акции для планшетов в альбомной ориентации
     */
    public OfferItem(int id, String description, String picURL, String tabletPicURL)  {
        this.id = id;
        this.description = description;
        this.picURL = picURL;
        this.tabletPicURL = tabletPicURL;
    }
}
