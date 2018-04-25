package com.a3dx2.clock.service.openweathermap;

import android.location.Location;

import com.a3dx2.clock.service.WebServiceCaller;
import com.a3dx2.clock.service.WebServiceResultHandler;
import com.a3dx2.clock.service.openweathermap.model.UltraViolet;
import com.a3dx2.clock.view.WeatherServiceAwareView;

import java.util.Locale;

public class WeatherSearchUVIndex implements WebServiceWrapper {

    private static final String WEATHER_MAP_SEARCH_UV_CURRENT_URL = "http://api.openweathermap.org/data/2.5/uvi?appid=%s&lat=%f&lon=%f";

    private final WeatherServiceAwareView<UltraViolet> view;
    private final WeatherSearchUVResultHandler handler;

    public WeatherSearchUVIndex(WeatherServiceAwareView<UltraViolet> view) {
        this.view = view;
        this.handler = new WeatherSearchUVResultHandler();
    }

    @Override
    public void execute(Location location, String openWeatherMapApiKey) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        String url = String.format(Locale.US, WEATHER_MAP_SEARCH_UV_CURRENT_URL, openWeatherMapApiKey, latitude, longitude);
        WebServiceCaller<UltraViolet> webServiceCaller = new WebServiceCaller<>(url, UltraViolet.class, handler);
        Void[] theVoid = null;
        webServiceCaller.execute(theVoid);
    }

    private class WeatherSearchUVResultHandler implements WebServiceResultHandler<UltraViolet> {
        @Override
        public void handleResult(UltraViolet result) {
            if (result != null) {
                view.processWeatherResult(result);
            }
        }
    }

}
