package com.webtrust.tennosushi.json_objects;

/**
 * Интерфейс для объектов, умеющих представлять себя в JSON.
 */
public interface JSONable {
    /** Метод, преобразовывающий объект в его JSON-представление. */
    String getJSON();
}
