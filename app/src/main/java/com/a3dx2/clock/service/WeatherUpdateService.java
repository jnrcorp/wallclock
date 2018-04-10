package com.a3dx2.clock.service;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;

import com.a3dx2.clock.activity.ClockMain;
import com.a3dx2.clock.service.model.ClockSettings;
import com.a3dx2.clock.service.openweathermap.WeatherSearchCurrent;
import com.a3dx2.clock.service.openweathermap.WeatherSearchFiveDay;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherUpdateService {

    private final Logger LOGGER = Logger.getLogger("com.a3dx2.clock");

    private final Handler weatherUpdateHandler = new Handler();
    private final UpdateWeatherRunnable updateWeatherRunnable = new UpdateWeatherRunnable();

    private final ClockMain activity;

    private final WeatherSearchFiveDay fiveDay;
    private final WeatherSearchCurrent currentWeather;

    private Date lastWeatherUpdate;

    public WeatherUpdateService(ClockMain activity) {
        this.activity = activity;
        this.fiveDay = new WeatherSearchFiveDay(activity, this);
        this.currentWeather = new WeatherSearchCurrent(activity);
    }

    public void startWeatherUpdate() {
        weatherUpdateHandler.removeCallbacks(updateWeatherRunnable);
        weatherUpdateHandler.post(updateWeatherRunnable);
    }

    public void stopWeatherUpdate() {
        weatherUpdateHandler.removeCallbacks(updateWeatherRunnable);
    }

    public void setLastWeatherUpdate() {
        this.lastWeatherUpdate = new Date();
    }

    public void updateLastTimeUI(ClockSettings clockSettings) {
        fiveDay.updateLastUpdatedTimeUI(clockSettings);
    }

    private class UpdateWeatherRunnable implements Runnable {

        @Override
        public void run() {
            Integer updateFrequency = activity.getClockSettings().getUpdateFrequencyMinutes();
            try {
                String openWeatherApiKey = activity.getClockSettings().getOpenWeatherApiKey();
                getWeather(updateFrequency, openWeatherApiKey);
            } catch (Exception ex) {
                LOGGER.log(Level.ALL, ex.getMessage(), ex);
            } finally {
                weatherUpdateHandler.postDelayed(this, updateFrequency*60*1000);
            }
        }

        private void getWeather(Integer updateFrequency, String openWeatherApiKey) {
            LOGGER.log(Level.INFO, "About to load weather: apiKey={}", openWeatherApiKey);
            if (!openWeatherApiKey.trim().isEmpty() && isWeatherUpdateDue(updateFrequency)) {
                LocationManager lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    fiveDay.execute(location, openWeatherApiKey);
                    currentWeather.execute(location, openWeatherApiKey);
                } else {
                    activity.processNoLocation();
                }
            } else if (openWeatherApiKey.trim().isEmpty()) {
                activity.processNoApiKey();
            }
        }

        private boolean isWeatherUpdateDue(Integer updateFrequency) {
            if (lastWeatherUpdate == null) {
                return true;
            }
            Calendar nextUpdate = Calendar.getInstance();
            nextUpdate.setTime(lastWeatherUpdate);
            nextUpdate.add(Calendar.MINUTE, updateFrequency-5);
            Date now = new Date();
            return now.compareTo(nextUpdate.getTime()) > 0;
        }

    }
}
