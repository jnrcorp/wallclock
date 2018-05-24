package com.a3dx2.clock.service.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;

import com.a3dx2.clock.R;

public class ClockSettings {

    private final String openWeatherApiKey;
    private final Integer backgroundColor;
    private final Integer textColor;
    private final Integer updateForecastFrequencyMinutes;
    private final Integer updateCurrentFrequencyMinutes;
    private final Integer fontSizeClockTime;
    private final Integer fontSizeClockDate;
    private final Integer fontSizeWeatherTemp;
    private final Integer fontSizeWeatherTime;
    private final Double iconSizeMultiplier;
    private final Integer weatherTimeInterval;
    private final Integer weatherForecastDays;
    private final boolean manageBrightness;

    public ClockSettings(Context context) {
        super();
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.openWeatherApiKey = preferences.getString(context.getString(R.string.pref_key_api_key), "");
        this.updateForecastFrequencyMinutes = preferences.getInt(context.getString(R.string.pref_key_update_forecast_frequency), 30);
        this.updateCurrentFrequencyMinutes = preferences.getInt(context.getString(R.string.pref_key_update_current_frequency), 30);
        Integer defaultBackgroundColor = Color.parseColor(context.getString(R.string.default_background_color));
        this.backgroundColor = preferences.getInt(context.getString(R.string.pref_key_background_color), defaultBackgroundColor);
        Integer defaultTextColor = Color.parseColor(context.getString(R.string.default_text_color));
        this.textColor = preferences.getInt(context.getString(R.string.pref_key_text_color), defaultTextColor);
        String fontSizeClockTimePref = preferences.getString(context.getString(R.string.pref_key_font_size_time), context.getString(R.string.default_font_size_clock_time));
        this.fontSizeClockTime = Integer.valueOf(fontSizeClockTimePref);
        String fontSizeClockDatePref = preferences.getString(context.getString(R.string.pref_key_font_size_date), context.getString(R.string.default_font_size_clock_date));
        this.fontSizeClockDate = Integer.valueOf(fontSizeClockDatePref);
        String fontSizeTempPref = preferences.getString(context.getString(R.string.pref_key_font_size_weather_temp), context.getString(R.string.default_font_size_weather_temp));
        this.fontSizeWeatherTemp = Integer.valueOf(fontSizeTempPref);
        String fontSizeTimePref = preferences.getString(context.getString(R.string.pref_key_font_size_weather_time), context.getString(R.string.default_font_size_weather_time));
        this.fontSizeWeatherTime = Integer.valueOf(fontSizeTimePref);
        String iconSizePref = preferences.getString(context.getString(R.string.pref_key_icon_size), context.getString(R.string.default_icon_size_weather));
        this.iconSizeMultiplier = Double.valueOf(iconSizePref);
        String timeIntervalPref = preferences.getString(context.getString(R.string.pref_key_weather_time_interval), context.getString(R.string.default_time_interval_weather));
        this.weatherTimeInterval = Integer.valueOf(timeIntervalPref);
        String forecastDaysPref = preferences.getString(context.getString(R.string.pref_key_forecast_days), "5");
        this.weatherForecastDays = Integer.valueOf(forecastDaysPref);
        String manageBrightnessPref = preferences.getString(context.getString(R.string.pref_key_manage_brightness), "true");
        this.manageBrightness = Boolean.valueOf(manageBrightnessPref);
    }

    public String getOpenWeatherApiKey() {
        return openWeatherApiKey;
    }

    public Integer getBackgroundColor() {
        return backgroundColor;
    }

    public Integer getTextColor() {
        return textColor;
    }

    public Integer getUpdateForecastFrequencyMinutes() {
        return updateForecastFrequencyMinutes;
    }

    public Integer getUpdateCurrentFrequencyMinutes() {
        return updateCurrentFrequencyMinutes;
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

    public Integer getWeatherForecastDays() {
        return weatherForecastDays;
    }

    public Integer getWeatherTimeInterval() {
        return weatherTimeInterval;
    }

    public boolean isManageBrightness() {
        return manageBrightness;
    }

}
