package com.a3dx2.clock.service.openweathermap;

import android.location.Location;

import com.a3dx2.clock.service.WebServiceCaller;
import com.a3dx2.clock.service.WebServiceResultHandler;
import com.a3dx2.clock.service.openweathermap.model.CurrentLocationResult;
import com.a3dx2.clock.view.WeatherCurrentView;

import java.util.Locale;
import java.util.logging.Logger;

public class WeatherSearchCurrent implements WebServiceWrapper {

    private final Logger LOGGER = Logger.getLogger("com.a3dx2.clock");

    private static final String WEATHER_MAP_SEARCH_CURRENT_URL = "https://api.openweathermap.org/data/2.5/weather?APPID=%s&lat=%f&lon=%f&units=imperial";

    private final WeatherCurrentView view;
    private final WeatherSearchCurrentResultHandler handler;

    public WeatherSearchCurrent(WeatherCurrentView view) {
        this.view = view;
        this.handler = new WeatherSearchCurrentResultHandler();
    }

    @Override
    public void execute(Location location, String openWeatherMapApiKey) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        String url = String.format(Locale.US, WEATHER_MAP_SEARCH_CURRENT_URL, openWeatherMapApiKey, latitude, longitude);
        WebServiceCaller<CurrentLocationResult> webServiceCaller = new WebServiceCaller<>(url, CurrentLocationResult.class, handler);
        Void[] theVoid = null;
        webServiceCaller.execute(theVoid);
    }

    private class WeatherSearchCurrentResultHandler implements WebServiceResultHandler<CurrentLocationResult> {
        @Override
        public void handleResult(CurrentLocationResult result) {
            if (result != null) {
                view.createLayoutWithData(result);
            }
        }
    }

}
