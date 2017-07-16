package com.webtrust.tennosushi.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Created by VladislavSavvateev on 12.07.2017.
 */

public class BitmapCacheProvider {
    /**
     * Ищет файл в кэше.
     * @param fileName Имя Файла.
     * @return Найденный файл в кэше. (null, если файл в кэше не найден)
     */
    public static Bitmap getCacheData(final String fileName, Context context) {
        File filesDir = context.getFilesDir();
        File[] files = filesDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) { return name.equals(fileName); }
        });
        if (files.length == 0) return null;
        return BitmapFactory.decodeFile(files[0].getPath());
    }

    /**
     * Отсекает название файла из пути.
     * (я не стал проверять работу класса File для решения этой задачи)
     * @param path Путь до файла.
     * @return Имя файла.
     */
    public static String getFileNameFromPath(String path) {
        try { return path.substring(path.lastIndexOf("/") + 1); }
        catch (Exception ignored) { return null; }
    }
}
