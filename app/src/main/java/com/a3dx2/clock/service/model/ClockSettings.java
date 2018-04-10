package com.a3dx2.clock.service.model;

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
        this.backgroundColor = preferences.getString(context.getString(R.string.pref_key_background_color), context.getString(R.string.default_background_color));
        this.textColor = preferences.getString(context.getString(R.string.pref_key_text_color), context.getString(R.string.default_text_color));
        this.updateFrequencyMinutes = preferences.getInt(context.getString(R.string.pref_key_update_frequency), 30);
        String fontSizeClockTimePref = preferences.getString(context.getString(R.string.pref_key_font_size_time), context.getString(R.string.default_font_size_clock_time));
        this.fontSizeClockTime = Integer.valueOf(fontSizeClockTimePref);
        String fontSizeClockDatePref = preferences.getString(context.getString(R.string.pref_key_font_size_date), context.getString(R.string.default_font_size_clock_date));
        this.fontSizeClockDate = Integer.valueOf(fontSizeClockDatePref);
        String fontSizeTempPref = preferences.getString(context.getString(R.string.pref_key_font_size_weather_temp), context.getString(R.string.default_font_size_weather_temp));
        String fontSizeTimePref = preferences.getString(context.getString(R.string.pref_key_font_size_weather_time), context.getString(R.string.default_font_size_weather_time));
        this.fontSizeWeatherTemp = Integer.valueOf(fontSizeTempPref);
        this.fontSizeWeatherTime = Integer.valueOf(fontSizeTimePref);
        String iconSizePref = preferences.getString(context.getString(R.string.pref_key_icon_size), context.getString(R.string.default_icon_size_weather));
        this.iconSizeMultiplier = Double.valueOf(iconSizePref);
        String timeIntervalPref = preferences.getString(context.getString(R.string.pref_key_weather_time_interval), context.getString(R.string.default_time_interval_weather));
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
