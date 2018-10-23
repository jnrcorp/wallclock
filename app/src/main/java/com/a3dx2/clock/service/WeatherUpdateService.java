package com.a3dx2.clock.service;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;

import com.a3dx2.clock.service.model.WeatherUpdateContext;
import com.a3dx2.clock.service.openweathermap.WebServiceWrapper;
import com.a3dx2.clock.view.WeatherServiceAwareView;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherUpdateService {

    private final Logger LOGGER = Logger.getLogger("com.a3dx2.clock");

    private final Handler weatherUpdateHandler = new Handler();
    private final UpdateWeatherRunnable updateWeatherRunnable = new UpdateWeatherRunnable();

    private final Context context;
    private final WeatherServiceAwareView view;
    private final WebServiceWrapper wrapper;

    public WeatherUpdateService(Context context, WeatherServiceAwareView view, WebServiceWrapper wrapper) {
        this.context = context;
        this.view = view;
        this.wrapper = wrapper;
    }

    public void startWeatherUpdate() {
        weatherUpdateHandler.removeCallbacks(updateWeatherRunnable);
        weatherUpdateHandler.post(updateWeatherRunnable);
    }

    public void stopWeatherUpdate() {
        weatherUpdateHandler.removeCallbacks(updateWeatherRunnable);
    }

    private class UpdateWeatherRunnable implements Runnable {

        @Override
        public void run() {
            WeatherUpdateContext weatherUpdateContext = view.getWeatherUpdateContext();
            try {
                getWeather(weatherUpdateContext);
            } catch (Exception ex) {
                LOGGER.log(Level.ALL, ex.getMessage(), ex);
            } finally {
                int updateFrequencyMinutes = weatherUpdateContext.getUpdateFrequencyMinutes();
                weatherUpdateHandler.postDelayed(this, updateFrequencyMinutes*60*1000);
            }
        }

        private void getWeather(WeatherUpdateContext weatherUpdateContext) {
            String openWeatherApiKey = weatherUpdateContext.getOpenWeatherApiKey();
            LOGGER.log(Level.INFO, "About to load weather: apiKey=" + openWeatherApiKey);
            if (!openWeatherApiKey.trim().isEmpty()) {
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                assert locationManager != null;
                Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    wrapper.execute(location, openWeatherApiKey);
                } else {
                    view.processNoLocation();
                }
            } else if (openWeatherApiKey.trim().isEmpty()) {
                view.processNoApiKey();
            }
        }

    }
}
