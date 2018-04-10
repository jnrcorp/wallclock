package com.a3dx2.clock.service.openweathermap;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a3dx2.clock.R;
import com.a3dx2.clock.activity.ClockMain;
import com.a3dx2.clock.service.WeatherUpdateService;
import com.a3dx2.clock.service.model.ClockSettings;
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

    private static final SimpleDateFormat HOUR_MINUTE_FORMAT = new SimpleDateFormat("h:mm a");
    private static final SimpleDateFormat DAY_OF_WEEK_HOUR_FORMAT = new SimpleDateFormat("E h a");
    private static final String WEATHER_MAP_SEARCH_FIVE_DAY_URL = "https://api.openweathermap.org/data/2.5/forecast?APPID=%s&lat=%f&lon=%f&units=imperial";

    private final ClockMain activity;
    private final WeatherUpdateService weatherUpdateService;
    private final WeatherSearchFiveDayResultHandler handler;
    private WebServiceCaller webServiceCaller;

    public WeatherSearchFiveDay(ClockMain activity, WeatherUpdateService weatherUpdateService) {
        this.activity = activity;
        this.weatherUpdateService = weatherUpdateService;
        this.handler = new WeatherSearchFiveDayResultHandler(activity);
    }

    public void execute(Location location, String openWeatherMapApiKey) {
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        String url = String.format(Locale.US, WEATHER_MAP_SEARCH_FIVE_DAY_URL, openWeatherMapApiKey, latitude, longitude);
        webServiceCaller = new WebServiceCaller<FiveDayResult>(url, FiveDayResult.class, handler);
        Void[] theVoid = null;
        webServiceCaller.execute(theVoid);
    }

    public void updateLastUpdatedTimeUI(ClockSettings clockSettings) {
        handler.updateLastUpdatedTimeUI(clockSettings);
    }

    private class WeatherSearchFiveDayResultHandler implements WebServiceResultHandler<FiveDayResult> {

        private final TextView lastUpdatedTime;

        public WeatherSearchFiveDayResultHandler(ClockMain activity) {
            this.lastUpdatedTime = new TextView(activity);
            buildLastUpdatedTime();

        }

        private void buildLastUpdatedTime() {
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER;
            lastUpdatedTime.setLayoutParams(layoutParams);
            lastUpdatedTime.setGravity(Gravity.CENTER);
            lastUpdatedTime.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
        }

        public void updateLastUpdatedTimeUI(ClockSettings clockSettings) {
            Integer color = Color.parseColor(clockSettings.getTextColor());
            Integer fontSizeTime = clockSettings.getFontSizeWeatherTime();
            Resources res = activity.getResources();
            int paddingTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, res.getDisplayMetrics());
            lastUpdatedTime.setPadding(0, paddingTop, 0, paddingTop);
            lastUpdatedTime.setTextColor(color);
            lastUpdatedTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSizeTime);
        }

        @Override
        public void handleResult(FiveDayResult result) {
            if (result != null) {
                ClockSettings clockSettings = new ClockSettings(activity);
                Integer timeInterval = Integer.valueOf(clockSettings.getWeatherTimeInterval());
                Integer color = Color.parseColor(clockSettings.getTextColor());
                Integer fontSizeTemp = clockSettings.getFontSizeWeatherTemp();
                Integer fontSizeTime = clockSettings.getFontSizeWeatherTime();
                Resources res = activity.getResources();
                int paddingTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, res.getDisplayMetrics());
                LinearLayout weatherStatuses = activity.findViewById(R.id.weather_status);
                weatherStatuses.removeAllViews();
                Double iconSizeMultiplier = clockSettings.getIconSizeMultiplier();
                weatherUpdateService.setLastWeatherUpdate();
                int counter = 0;
                LOGGER.log(Level.INFO, "weatherData=" + result.toString());
                for (SingleDayResult singleDay : result.getList()) {
                    if (singleDay.getWeather().length == 0) {
                        continue;
                    }
                    Weather weather = singleDay.getWeather()[0];
                    String weatherIconId = "@drawable/weather" + weather.getIcon();
                    Integer drawableId = activity.getResources().getIdentifier(weatherIconId, "drawable", activity.getPackageName());
                    WeatherDayView weatherDayView = new WeatherDayView(activity);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.gravity = Gravity.CENTER;
                    weatherDayView.setLayoutParams(layoutParams);
                    weatherDayView.setGravity(Gravity.CENTER);
                    weatherDayView.setPadding(0, paddingTop, 0, 0);
                    Drawable drawable = activity.getDrawable(drawableId);
                    weatherDayView.setWeatherIcon(drawable, iconSizeMultiplier);
                    weatherDayView.setWeatherTemperature(singleDay.getMain().getTemp());
                    weatherDayView.setFontSize(fontSizeTemp, fontSizeTime);
                    weatherDayView.setTextColor(color);
                    Date forecastDate = new Date(singleDay.getDt()*1000);
                    weatherDayView.setWeatherDayOfWeek(DAY_OF_WEEK_HOUR_FORMAT.format(forecastDate));
                    weatherStatuses.addView(weatherDayView);
                    if (counter % timeInterval != 0) {
                        weatherDayView.setVisibility(WeatherDayView.GONE);
                    }
                    counter += 1;
                }
                updateLastUpdatedTimeUI(clockSettings);
                lastUpdatedTime.setText("Updated\n" + HOUR_MINUTE_FORMAT.format(new Date()));
                weatherStatuses.addView(lastUpdatedTime);
            }
        }
    }

}
