package com.a3dx2.clock.service.openweathermap;

import android.location.Location;
import android.media.Image;
import android.widget.ImageView;
import android.widget.TextView;

import com.a3dx2.clock.R;
import com.a3dx2.clock.activity.ClockMain;
import com.a3dx2.clock.service.WebServiceCaller;
import com.a3dx2.clock.service.WebServiceResultHandler;
import com.a3dx2.clock.service.openweathermap.model.CurrentLocationResult;
import com.a3dx2.clock.service.openweathermap.model.Weather;
import com.a3dx2.clock.weather.WeatherUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherSearchCurrent {

    private final Logger LOGGER = Logger.getLogger("com.a3dx2.clock");

    private static final String WEATHER_MAP_SEARCH_CURRENT_URL = "https://api.openweathermap.org/data/2.5/weather?APPID=%s&lat=%f&lon=%f&units=imperial";

    private ClockMain activity;
    private WebServiceCaller webServiceCaller;
    private WeatherSearchCurrentResultHandler handler = new WeatherSearchCurrentResultHandler();

    public WeatherSearchCurrent(ClockMain activity, Location location, String openWeatherMapApiKey) {
        this.activity = activity;
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        String url = String.format(WEATHER_MAP_SEARCH_CURRENT_URL, openWeatherMapApiKey, latitude, longitude);
        this.webServiceCaller = new WebServiceCaller<CurrentLocationResult>(url, CurrentLocationResult.class, handler);
    }

    public void execute() {
        Void[] theVoid = null;
        webServiceCaller.execute(theVoid);
    }

    private class WeatherSearchCurrentResultHandler implements WebServiceResultHandler<CurrentLocationResult> {
        @Override
        public void handleResult(CurrentLocationResult result) {
            if (result != null) {
                LOGGER.log(Level.INFO, "currentConditions=" + result.toString());
                Weather weather = result.getWeather()[0];
                String weatherIconId = "@drawable/weather" + weather.getIcon();
                Integer drawableId = activity.getResources().getIdentifier(weatherIconId, "drawable", activity.getPackageName());
                ImageView imageView = activity.findViewById(R.id.current_weather_image);
                imageView.setImageResource(drawableId);
                TextView textView = activity.findViewById(R.id.current_weather_temp);
                String temperature = WeatherUtil.formatTemperature(result.getMain().getTemp());
                textView.setText(temperature);
            }
        }
    }

}
