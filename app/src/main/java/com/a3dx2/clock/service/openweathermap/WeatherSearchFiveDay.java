package com.a3dx2.clock.service.openweathermap;

import android.location.Location;

import com.a3dx2.clock.service.WebServiceCaller;
import com.a3dx2.clock.service.WebServiceResultHandler;
import com.a3dx2.clock.service.openweathermap.constants.OpenWeatherMapConstants;
import com.a3dx2.clock.service.openweathermap.model.CurrentLocationResult;
import com.a3dx2.clock.service.openweathermap.model.FiveDayResult;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherSearchFiveDay {

    private final Logger LOGGER = Logger.getLogger("com.a3dx2.clock");

    private static final String WEATHER_MAP_SEARCH_FIVE_DAY_URL = "https://api.openweathermap.org/data/2.5/forecast?APPID=%s&lat=%f&lon=%f";

    private WebServiceCaller webServiceCaller;
    private WeatherSearchFiveDayResultHandler handler = new WeatherSearchFiveDayResultHandler();

    public WeatherSearchFiveDay(Location location) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        String url = String.format(WEATHER_MAP_SEARCH_FIVE_DAY_URL, OpenWeatherMapConstants.OPEN_WEATHER_MAP_API_KEY, latitude, longitude);
        webServiceCaller = new WebServiceCaller<FiveDayResult>(url, FiveDayResult.class, handler);
    }

    public void execute() {
        Void[] theVoid = null;
        webServiceCaller.execute(theVoid);
    }

    private class WeatherSearchFiveDayResultHandler implements WebServiceResultHandler<FiveDayResult> {
        @Override
        public void handleResult(FiveDayResult result) {
            if (result != null) {
                LOGGER.log(Level.ALL, result.toString());
            }
        }
    }

}
