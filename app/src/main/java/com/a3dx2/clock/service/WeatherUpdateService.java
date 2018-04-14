package com.a3dx2.clock.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;

import com.a3dx2.clock.service.openweathermap.WebServiceWrapper;
import com.a3dx2.clock.view.WebServiceAwareView;

import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherUpdateService {

    private final Logger LOGGER = Logger.getLogger("com.a3dx2.clock");

    private final Handler weatherUpdateHandler = new Handler();
    private final UpdateWeatherRunnable updateWeatherRunnable = new UpdateWeatherRunnable();

    private final Context context;
    private final WebServiceAwareView view;
    private final WebServiceWrapper wrapper;

    private String openWeatherApiKey;
    private Integer updateFrequencyMinutes;
    private Date lastWeatherUpdate;

    public WeatherUpdateService(Context context, WebServiceAwareView view, WebServiceWrapper wrapper) {
        this.context = context;
        this.view = view;
        this.wrapper = wrapper;
    }

    private void updateConfiguration(String openWeatherApiKey, Integer updateFrequencyMinutes) {
        this.openWeatherApiKey = openWeatherApiKey;
        this.updateFrequencyMinutes = updateFrequencyMinutes;
    }

    public void startWeatherUpdate(String openWeatherApiKey, Integer updateFrequencyMinutes) {
        updateConfiguration(openWeatherApiKey, updateFrequencyMinutes);
        weatherUpdateHandler.removeCallbacks(updateWeatherRunnable);
        weatherUpdateHandler.post(updateWeatherRunnable);
    }

    public void stopWeatherUpdate() {
        weatherUpdateHandler.removeCallbacks(updateWeatherRunnable);
    }

    public void setLastWeatherUpdate() {
        this.lastWeatherUpdate = new Date();
    }

    private class UpdateWeatherRunnable implements Runnable {

        @Override
        public void run() {
            try {
                getWeather();
            } catch (Exception ex) {
                LOGGER.log(Level.ALL, ex.getMessage(), ex);
            } finally {
                weatherUpdateHandler.postDelayed(this, updateFrequencyMinutes*60*1000);
            }
        }

        private void getWeather() {
            LOGGER.log(Level.INFO, "About to load weather: apiKey={}", openWeatherApiKey);
            if (!openWeatherApiKey.trim().isEmpty() && isWeatherUpdateDue(updateFrequencyMinutes)) {
                LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                Location location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    wrapper.execute(location, openWeatherApiKey);
                } else {
                    view.processNoLocation();
                }
            } else if (openWeatherApiKey.trim().isEmpty()) {
                view.processNoApiKey();
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
