package com.a3dx2.clock.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.a3dx2.clock.R;
import com.a3dx2.clock.activity.ClockMain;

public class ClockSettings {

    private final String openWeatherApiKey;
    private final String backgroundColor;
    private final String textColor;
    private final Integer updateFrequencyMinutes;
    private final Integer fontSizeClockTime;
    private final Integer fontSizeClockDate;
    private final Integer fontSizeWeatherTemp;
    private final Integer fontSizeWeatherTime;
    private final Double iconSizeMultiplier;
    private final Integer weatherTimeInterval;

    public ClockSettings(Context context) {
        super();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.openWeatherApiKey = preferences.getString(context.getString(R.string.pref_key_api_key), "");
        this.backgroundColor = preferences.getString(context.getString(R.string.pref_key_background_color), "#000000");
        this.textColor = preferences.getString(context.getString(R.string.pref_key_text_color), "#33b5e5");
        this.updateFrequencyMinutes = preferences.getInt(context.getString(R.string.pref_key_update_frequency), 30);
        String fontSizeClockTimePref = preferences.getString(context.getString(R.string.pref_key_font_size_time), "50");
        this.fontSizeClockTime = Integer.valueOf(fontSizeClockTimePref);
        String fontSizeClockDatePref = preferences.getString(context.getString(R.string.pref_key_font_size_date), "30");
        this.fontSizeClockDate = Integer.valueOf(fontSizeClockDatePref);
        String fontSizeTempPref = preferences.getString(context.getString(R.string.pref_key_font_size_temp), "20");
        String fontSizeTimePref = preferences.getString(context.getString(R.string.pref_key_font_size_weather_time), "20");
        this.fontSizeWeatherTemp = Integer.valueOf(fontSizeTempPref);
        this.fontSizeWeatherTime = Integer.valueOf(fontSizeTimePref);
        String iconSizePref = preferences.getString(context.getString(R.string.pref_key_icon_size), "1");
        this.iconSizeMultiplier = Double.valueOf(iconSizePref);
        String timeIntervalPref = preferences.getString(context.getString(R.string.pref_key_weather_time_interval), "2");
        this.weatherTimeInterval = Integer.valueOf(timeIntervalPref);
    }

    public String getOpenWeatherApiKey() {
        return openWeatherApiKey;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public Integer getUpdateFrequencyMinutes() {
        return updateFrequencyMinutes;
    }

    public Integer getFontSizeClockTime() {
        return fontSizeClockTime;
    }

    public Integer getFontSizeClockDate() {
        return fontSizeClockDate;
    }

    public Integer getFontSizeWeatherTemp() {
        return fontSizeWeatherTemp;
    }

    public Integer getFontSizeWeatherTime() {
        return fontSizeWeatherTime;
    }

    public Double getIconSizeMultiplier() {
        return iconSizeMultiplier;
    }

    public Integer getWeatherTimeInterval() {
        return weatherTimeInterval;
    }
}
