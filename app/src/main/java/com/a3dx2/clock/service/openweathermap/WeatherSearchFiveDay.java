package com.a3dx2.clock.service.openweathermap;

import android.location.Location;

import com.a3dx2.clock.service.WebServiceCaller;
import com.a3dx2.clock.service.WebServiceResultHandler;
import com.a3dx2.clock.service.openweathermap.model.FiveDayResult;
import com.a3dx2.clock.view.WeatherForecastView;

import java.util.Locale;
import java.util.logging.Logger;

public class WeatherSearchFiveDay implements WebServiceWrapper {

    private final Logger LOGGER = Logger.getLogger("com.a3dx2.clock");

    private static final String WEATHER_MAP_SEARCH_FIVE_DAY_URL = "https://api.openweathermap.org/data/2.5/forecast?APPID=%s&lat=%f&lon=%f&units=imperial";

    private final WeatherForecastView view;
    private final WeatherSearchFiveDayResultHandler handler;
    private WebServiceCaller<FiveDayResult> webServiceCaller;

    public WeatherSearchFiveDay(WeatherForecastView view) {
        this.view = view;
        this.handler = new WeatherSearchFiveDayResultHandler();
    }

    @Override
    public void execute(Location location, String openWeatherMapApiKey) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        String url = String.format(Locale.US, WEATHER_MAP_SEARCH_FIVE_DAY_URL, openWeatherMapApiKey, latitude, longitude);
        this.webServiceCaller = new WebServiceCaller<>(url, FiveDayResult.class, handler);
        Void[] theVoid = null;
        webServiceCaller.execute(theVoid);
    }

    private class WeatherSearchFiveDayResultHandler implements WebServiceResultHandler<FiveDayResult> {
        @Override
        public void handleResult(FiveDayResult result) {
            if (result != null) {
                view.createLayoutWithData(result);
            }
        }
    }

}
