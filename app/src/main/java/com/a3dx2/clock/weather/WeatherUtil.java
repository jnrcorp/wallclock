package com.a3dx2.clock.weather;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.a3dx2.clock.R;

public final class WeatherUtil {

    public static String formatTemperature(Double temperature) {
        return temperature.intValue() + "" + (char) 0x00B0;
    }

    public static void resizeIcon(ImageView imageView, float iconSizeMultiplier) {
        Drawable drawable = imageView.getDrawable();
        resizeIcon(imageView, drawable, iconSizeMultiplier);
    }

    public static void resizeIcon(ImageView imageView, Drawable drawable, float iconSizeMultiplier) {
        int width = (int) (drawable.getIntrinsicWidth() * iconSizeMultiplier);
        int height = (int) (drawable.getIntrinsicHeight() * iconSizeMultiplier);
        android.view.ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        imageView.setLayoutParams(layoutParams);
        imageView.setImageDrawable(drawable);
    }

    private WeatherUtil() {
        super();
    }

}
