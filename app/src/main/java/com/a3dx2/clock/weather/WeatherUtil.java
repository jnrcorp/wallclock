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

    public static void resizeIcon(ImageView imageView, Double iconSizeMultiplier) {
        Drawable drawable = imageView.getDrawable();
        int width = (int) (drawable.getIntrinsicWidth() * iconSizeMultiplier);
        int height = (int) (drawable.getIntrinsicHeight() * iconSizeMultiplier);
        android.view.ViewGroup.LayoutParams layoutParams = imageView.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        imageView.setLayoutParams(layoutParams);
    }

    private WeatherUtil() {
        super();
    }

}
