package com.a3dx2.clock.service.openweathermap;

import android.location.Location;

import com.a3dx2.clock.service.WebServiceCaller;
import com.a3dx2.clock.service.WebServiceResultHandler;
import com.a3dx2.clock.service.openweathermap.constants.OpenWeatherMapConstants;
import com.a3dx2.clock.service.openweathermap.model.CurrentLocationResult;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherSearchCurrent {

    private final Logger LOGGER = Logger.getLogger("com.a3dx2.clock");

    private static final String WEATHER_MAP_SEARCH_CURRENT_URL = "https://api.openweathermap.org/data/2.5/weather?APPID=%s&lat=%f&lon=%f";

    private WebServiceCaller webServiceCaller;
    private WeatherSearchCurrentResultHandler handler = new WeatherSearchCurrentResultHandler();

    public WeatherSearchCurrent(Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        String url = String.format(WEATHER_MAP_SEARCH_CURRENT_URL, OpenWeatherMapConstants.OPEN_WEATHER_MAP_API_KEY, latitude, longitude);
        webServiceCaller = new WebServiceCaller<CurrentLocationResult>(url, CurrentLocationResult.class, handler);
    }

    public void execute() {
        Void[] theVoid = null;
        webServiceCaller.execute(theVoid);
    }

    private class WeatherSearchCurrentResultHandler implements WebServiceResultHandler<CurrentLocationResult> {
        @Override
        public void handleResult(CurrentLocationResult result) {
            if (result != null) {
                LOGGER.log(Level.ALL, result.toString());
            }
        }
    }

}
