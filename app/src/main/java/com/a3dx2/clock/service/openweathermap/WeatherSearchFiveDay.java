package com.a3dx2.clock.service.openweathermap;

import android.location.Location;
import android.preference.PreferenceManager;

import com.a3dx2.clock.R;
import com.a3dx2.clock.activity.ClockMain;
import com.a3dx2.clock.service.WebServiceCaller;
import com.a3dx2.clock.service.WebServiceResultHandler;
import com.a3dx2.clock.service.openweathermap.model.FiveDayResult;
import com.a3dx2.clock.service.openweathermap.model.SingleDayResult;
import com.a3dx2.clock.service.openweathermap.model.Weather;
import com.a3dx2.clock.view.WeatherDayView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherSearchFiveDay {

    private final Logger LOGGER = Logger.getLogger("com.a3dx2.clock");

    private static final SimpleDateFormat DAY_OF_WEEK_HOUR_FORMAT = new SimpleDateFormat("EEE h a");
    private static final String WEATHER_MAP_SEARCH_FIVE_DAY_URL = "https://api.openweathermap.org/data/2.5/forecast?APPID=%s&lat=%f&lon=%f&units=imperial";

    private ClockMain activity;
    private WebServiceCaller webServiceCaller;
    private WeatherSearchFiveDayResultHandler handler = new WeatherSearchFiveDayResultHandler();

    public WeatherSearchFiveDay(ClockMain activity, Location location, String openWeatherMapApiKey) {
        this.activity = activity;
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        String url = String.format(Locale.US, WEATHER_MAP_SEARCH_FIVE_DAY_URL, openWeatherMapApiKey, latitude, longitude);
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
                String iconSizePref = PreferenceManager.getDefaultSharedPreferences(activity).getString(activity.getString(R.string.pref_key_icon_size), "1");
                Double iconSizeMultiplier = Double.valueOf(iconSizePref);
                activity.setLastWeatherUpdate();
                int counter = 0;
                int display_counter = 1;
                LOGGER.log(Level.INFO, "weatherData=" + result.toString());
                for (SingleDayResult singleDay : result.getList()) {
                    if (singleDay.getWeather().length == 0) {
                        continue;
                    }
                    Weather weather = singleDay.getWeather()[0];
                    if (display_counter <= 10 && counter % 4 == 0) {
                        String weatherIconId = "@drawable/weather" + weather.getIcon();
                        String weatherDayId = "@id/weather_day_" + display_counter;
                        Integer drawableId = activity.getResources().getIdentifier(weatherIconId, "drawable", activity.getPackageName());
                        Integer weatherDayImageViewId = activity.getResources().getIdentifier(weatherDayId, "id", activity.getPackageName());
                        WeatherDayView weatherDayView = activity.findViewById(weatherDayImageViewId);
                        weatherDayView.setWeatherIcon(drawableId);
                        weatherDayView.resizeWeatherIcon(iconSizeMultiplier);
                        weatherDayView.setWeatherTemperature(singleDay.getMain().getTemp());
                        Date forecastDate = new Date(singleDay.getDt()*1000);
                        weatherDayView.setWeatherDayOfWeek(DAY_OF_WEEK_HOUR_FORMAT.format(forecastDate));
                        display_counter += 1;
                    }
                    counter += 1;
                }
            }
        }
    }

}
