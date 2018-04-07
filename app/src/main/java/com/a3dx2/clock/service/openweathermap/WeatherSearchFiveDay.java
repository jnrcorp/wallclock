package com.a3dx2.clock.service.openweathermap;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.widget.ImageView;

import com.a3dx2.clock.R;
import com.a3dx2.clock.service.WebServiceCaller;
import com.a3dx2.clock.service.WebServiceResultHandler;
import com.a3dx2.clock.service.openweathermap.model.FiveDayResult;
import com.a3dx2.clock.service.openweathermap.model.SingleDayResult;
import com.a3dx2.clock.service.openweathermap.model.Weather;
import com.a3dx2.clock.view.WeatherDayView;

import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherSearchFiveDay {

    private final Logger LOGGER = Logger.getLogger("com.a3dx2.clock");

    private static final String WEATHER_MAP_SEARCH_FIVE_DAY_URL = "https://api.openweathermap.org/data/2.5/forecast?APPID=%s&lat=%f&lon=%f&units=imperial";

    private Activity activity;
    private WebServiceCaller webServiceCaller;
    private WeatherSearchFiveDayResultHandler handler = new WeatherSearchFiveDayResultHandler();

    public WeatherSearchFiveDay(Activity activity, Location location, String openWeatherMapApiKey) {
        this.activity = activity;
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        String url = String.format(WEATHER_MAP_SEARCH_FIVE_DAY_URL, openWeatherMapApiKey, latitude, longitude);
        this.webServiceCaller = new WebServiceCaller<FiveDayResult>(url, FiveDayResult.class, handler);
    }

    public void execute() {
        Void[] theVoid = null;
        webServiceCaller.execute(theVoid);
    }

    private class WeatherSearchFiveDayResultHandler implements WebServiceResultHandler<FiveDayResult> {
        @Override
        public void handleResult(FiveDayResult result) {
            if (result != null) {
                int counter = 1;
                LOGGER.log(Level.INFO, "weatherData=" + result.toString());
                for (SingleDayResult singleDay : result.getList()) {
                    for (Weather weather : singleDay.getWeather()) {
                        if (counter <= 5) {
                            String iconName = "@drawable/weather" + weather.getIcon();
                            Integer drawableId = activity.getResources().getIdentifier(iconName, "drawable", activity.getPackageName());
                            Drawable drawable = activity.getDrawable(drawableId);
                            Integer weatherDayImageViewId = activity.getResources().getIdentifier("@id/weather_day_" + counter, "id", activity.getPackageName());
                            WeatherDayView weatherDayView = (WeatherDayView) activity.findViewById(weatherDayImageViewId);
                            weatherDayView.setWeatherIcon(drawableId);
                            weatherDayView.setWeatherTemperature(singleDay.getMain().getTemp());
                            counter += 1;
                            continue;
                        }
                    }
                }
            }
        }
    }

}
