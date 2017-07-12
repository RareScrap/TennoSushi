package com.webtrust.tennosushi.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.AppCompatDrawableManager;
import android.util.Log;
import android.widget.Toast;

import com.webtrust.tennosushi.MainActivity;
import com.webtrust.tennosushi.R;
import com.webtrust.tennosushi.fragments.MenuListFragment;
import com.webtrust.tennosushi.fragments.ShoppingCartFragment;
import com.webtrust.tennosushi.list_items.FoodItem;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;

// Хуёвина, которая генеритует иконку корзины с
// числом, которое показывает итоговую стоимость
// покупки
public class ShoppingCartIconGenerator {

    /**
     * Генерирует иконку корзины.
     * @param context Контекст приложения.
     * @return Иконка.
     */
    public static void generate(Context context, int id) {
        try {
            Log.d("DEBUG", "LOL");
            Bitmap b = getBitmapFromVectorDrawable(context, R.drawable.ic_shopping_cart_24dp);
            Canvas c = new Canvas(b);
            Paint p = new Paint();
            p.setTextSize(c.getHeight() / 4);
            p.setColor(Color.WHITE);

            if (ShoppingCartFragment.addedFoodList == null) return;
            double sum = ShoppingCartFragment.getTotalPrice();
            if (sum == 0) return;
            String sumStr = new DecimalFormat("#.00").format(sum);
            Log.d("DEBUG", "X:" + (c.getWidth() - p.measureText(sumStr)));
            c.drawText(sumStr, c.getWidth() - p.measureText(sumStr), p.getTextSize(), p);
            MenuListFragment.menu.getItem(id).setIcon(new BitmapDrawable(context.getResources(), b));
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    /**
     * Конвертирует векторый Drawable в Bitmap.
     * @param context Контекст приложения.
     * @param drawableId ID от Drawable.
     * @return Сконвертированный Bitmap.
     */
    private static Bitmap getBitmapFromVectorDrawable(Context context, int drawableId) {
        Drawable drawable = AppCompatDrawableManager.get().getDrawable(context, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }
}
