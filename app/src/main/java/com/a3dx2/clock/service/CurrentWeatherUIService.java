package com.a3dx2.clock.service;

import android.graphics.Color;
import android.widget.ImageView;
import android.widget.TextView;

import com.a3dx2.clock.R;
import com.a3dx2.clock.activity.ClockMain;
import com.a3dx2.clock.service.model.ClockSettings;
import com.a3dx2.clock.weather.WeatherUtil;

public class CurrentWeatherUIService {

    private final ClockMain activity;
    private final TextView weatherTempTextView;
    private final ImageView currentTempIcon;

    public CurrentWeatherUIService(ClockMain activity) {
        this.activity = activity;
        this.weatherTempTextView = activity.findViewById(R.id.current_weather_temp);
        this.currentTempIcon = activity.findViewById(R.id.current_weather_image);
    }

    public void updateText(ClockSettings clockSettings) {
        setText(clockSettings);
        setIconSize(clockSettings);
    }

    private void setText(ClockSettings clockSettings) {
        String textColor = clockSettings.getTextColor();
        Integer color = Color.parseColor(textColor);
        weatherTempTextView.setTextColor(color);
        Integer fontSizeTemp = clockSettings.getFontSizeWeatherTemp();
        weatherTempTextView.setTextSize(fontSizeTemp);
    }

    private void setIconSize(ClockSettings clockSettings) {
        Double iconSizeMultiplier = clockSettings.getIconSizeMultiplier();
        WeatherUtil.resizeIcon(currentTempIcon, iconSizeMultiplier);
    }


}
