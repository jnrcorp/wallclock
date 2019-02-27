package com.a3dx2.clock.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.annotation.Dimension;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a3dx2.clock.R;
import com.a3dx2.clock.weather.WeatherUtil;

import org.springframework.util.StringUtils;

public class WeatherDayView extends LinearLayout {

    private ImageView weatherIcon;
    private TextView weatherTemperature;
    private TextView weatherDay;

    public WeatherDayView(Context context) {
        super(context);
        init(context, null, 0);
    }

    public WeatherDayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public WeatherDayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs, defStyle);
    }

    private void init(Context context, AttributeSet attrs, int defStyle) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.weather_day_view, this, true);
        this.weatherIcon = findViewById(R.id.weather_image);
        this.weatherTemperature = findViewById(R.id.weather_temp);
        this.weatherDay = findViewById(R.id.weather_day_of_week);
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeatherDayView, 0, 0);
        try {
            String temperature = attributes.getString(R.styleable.WeatherDayView_temperature);
            setTemperature(temperature);
            String dayOfWeek = attributes.getString(R.styleable.WeatherDayView_dayOfWeek);
            setDayOfWeek(dayOfWeek);
            int textColor = attributes.getColor(R.styleable.WeatherDayView_textColor, Color.WHITE);
            setTextColor(textColor);
            float temperatureTextSize = attributes.getDimension(R.styleable.WeatherDayView_temperatureTextSize, 30);
            setTemperatureTextSize(temperatureTextSize);
            float dayOfWeekTextSize = attributes.getDimension(R.styleable.WeatherDayView_dayOfWeekTextSize, 20);
            setDayOfWeekTextSize(dayOfWeekTextSize);
            int weatherIcon = attributes.getInt(R.styleable.WeatherDayView_weatherIcon, R.drawable.ic_weather01d);
            float iconSizeMultiplier = attributes.getFloat(R.styleable.WeatherDayView_iconSizeMultiplier, 5);
            setWeatherIcon(weatherIcon, iconSizeMultiplier);
        } finally {
            attributes.recycle();
        }
    }

    public void setWeatherIcon(int id, float iconSizeMultiplier) {
        weatherIcon.setImageResource(id);
        setIconSizeMultiplier(iconSizeMultiplier);
    }

    public void setIconSizeMultiplier(float iconSizeMultiplier) {
        WeatherUtil.resizeIcon(weatherIcon, iconSizeMultiplier);
    }

    public void setTemperatureFormatted(Double temp) {
        String temperature = WeatherUtil.formatTemperature(temp);
        setTemperature(temperature);
    }

    public void setTemperature(String temperature) {
        if (temperature == null || temperature.trim().isEmpty()) {
            temperature = "...";
        }
        weatherTemperature.setText(temperature);
    }

    public void setTextColor(int color) {
        weatherTemperature.setTextColor(color);
        weatherDay.setTextColor(color);
        weatherIcon.setColorFilter(color);
    }

    public void setDayOfWeek(String dayOfWeek) {
        weatherDay.setText(dayOfWeek);
    }

    public void setDayOfWeekTextSize(float textSize) {
        weatherDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

    public void setTemperatureTextSize(float textSize) {
        weatherTemperature.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
    }

}
