package com.a3dx2.clock.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
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

public class WeatherDayView extends LinearLayout {

    private LinearLayout container;
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
        setOrientation(VERTICAL);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.weather_day_view, this, true);
        weatherIcon = (ImageView) findViewById(R.id.weather_image);
        weatherTemperature = (TextView) findViewById(R.id.weather_temp);
        weatherDay = (TextView) findViewById(R.id.weather_day_of_week);
        container = (LinearLayout) findViewById(R.id.weather_container);
    }

    public void setWeatherIcon(Integer resourceId) {
        weatherIcon.setImageResource(resourceId);
    }

    public void resizeWeatherIcon(Double iconSizeMultiplier) {
        WeatherUtil.resizeIcon(weatherIcon, iconSizeMultiplier);
    }

    public void setWeatherTemperature(Double temperature) {
        String temp = WeatherUtil.formatTemperature(temperature);
        weatherTemperature.setText(temp);
    }

    public void setTextColor(int color) {
        weatherTemperature.setTextColor(color);
        weatherDay.setTextColor(color);
    }

    public void setWeatherDayOfWeek(String date) {
        weatherDay.setText(date);
    }

    public void setFontSize(Integer fontSizeTempSP, Integer fontSizeTimeSP) {
        weatherTemperature.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeTempSP);
        weatherDay.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeTimeSP);
    }

}
