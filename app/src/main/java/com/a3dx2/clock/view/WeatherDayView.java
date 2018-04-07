package com.a3dx2.clock.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a3dx2.clock.R;

public class WeatherDayView extends LinearLayout {

    private ImageView weatherIcon;
    private TextView weatherTemperature;
    private TextView weatherDay;
    private TextView weatherTime;

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
        setOrientation(VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.weather_day_view, this, true);
        weatherIcon = (ImageView) findViewById(R.id.weather_image);
        weatherTemperature = (TextView) findViewById(R.id.weather_temp);
        weatherDay = (TextView) findViewById(R.id.weather_day_of_week);
        weatherTime = (TextView) findViewById(R.id.weather_time);
    }

    public void setWeatherIcon(Integer resourceId) {
        weatherIcon.setImageResource(resourceId);
    }

    public void setWeatherTemperature(Double temperature) {
        weatherTemperature.setText(temperature.intValue() + "" + (char) 0x00B0);
    }

    public void setTextColor(int color) {
        weatherTemperature.setTextColor(color);
        weatherDay.setTextColor(color);
        weatherTime.setTextColor(color);
    }

    public void setWeatherDayOfWeek(String date) {
        weatherDay.setText(date);
    }

    public void setWeatherTime(String date) {
        weatherTime.setText(date);
    }

}
