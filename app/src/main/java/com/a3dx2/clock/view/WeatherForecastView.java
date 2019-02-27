package com.a3dx2.clock.view;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.a3dx2.clock.R;
import com.a3dx2.clock.service.ScrollingForecastUIService;
import com.a3dx2.clock.service.model.ClockSettings;
import com.a3dx2.clock.service.openweathermap.WeatherSearchFiveDay;
import com.a3dx2.clock.service.openweathermap.model.FiveDayResult;
import com.a3dx2.clock.service.openweathermap.model.SingleDayResult;
import com.a3dx2.clock.service.openweathermap.model.Weather;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherForecastView extends WeatherServiceAwareView<FiveDayResult> {

    private static final Logger LOGGER = Logger.getLogger("com.a3dx2.clock");

    private static final SimpleDateFormat HOUR_MINUTE_FORMAT = new SimpleDateFormat("h:mm a", Locale.US);
    private static final SimpleDateFormat DAY_OF_WEEK_HOUR_FORMAT = new SimpleDateFormat("E h a", Locale.US);

    private ScrollingForecastUIService scrollingForecastUIService;

    private LinearLayout weatherStatuses;

    private TextView forecast;
    private TextView lastUpdatedTime;

    private int orientation;
    private int textColor;
    private float temperatureTextSize;
    private float dayOfWeekTextSize;
    private float iconSizeMultiplier;
    private int timeInterval;
    private int forecastDays;
    private String city;

    public WeatherForecastView(Context context) {
        super(context);
        init(context, null);
    }

    public WeatherForecastView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WeatherForecastView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public WeatherForecastView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ClockSettings clockSettings = new ClockSettings(getContext());
        updateConfiguration(clockSettings);
        initializeWeatherData(clockSettings.getOpenWeatherApiKey(), clockSettings.getUpdateForecastFrequencyMinutes());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopWeatherDataUpdate();
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.weather_forecast_view, this, true);
        FrameLayout scrollingView = findViewById(R.id.scrollingView);
        this.weatherStatuses = findViewById(R.id.weather_statuses);
        this.lastUpdatedTime = new TextView(context);
        this.forecast = new TextView(context);
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeatherForecastView, 0, 0);
        this.textColor = attributes.getColor(R.styleable.WeatherForecastView_textColor, Color.WHITE);
        this.temperatureTextSize = attributes.getDimension(R.styleable.WeatherForecastView_temperatureTextSize, 30);
        this.dayOfWeekTextSize = attributes.getDimension(R.styleable.WeatherForecastView_dayOfWeekTextSize, 20);
        this.iconSizeMultiplier = attributes.getFloat(R.styleable.WeatherForecastView_iconSizeMultiplier, 5);
        this.orientation = attributes.getInt(R.styleable.WeatherForecastView_scrollOrientation, LinearLayout.VERTICAL);
        this.scrollingForecastUIService = new ScrollingForecastUIService(scrollingView, orientation);
        weatherStatuses.setOrientation(orientation);
        this.timeInterval = attributes.getInt(R.styleable.WeatherForecastView_timeInterval, 1);
        this.forecastDays = attributes.getInt(R.styleable.WeatherForecastView_forecastDays, 5);
        createWeatherUpdateService(context, new WeatherSearchFiveDay(this));
        String openWeatherMapApiKey = attributes.getString(R.styleable.WeatherForecastView_openWeatherMapApiKey);
        int updateFrequencyMinutes = attributes.getInt(R.styleable.WeatherForecastView_updateFrequencyMinutes, 30);
        initializeWeatherData(openWeatherMapApiKey, updateFrequencyMinutes);
        buildLastUpdatedTime();
        buildForecast();
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

    private void buildForecast() {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        forecast.setLayoutParams(layoutParams);
        forecast.setGravity(Gravity.CENTER);
        forecast.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);
    }

    public void updateLastUpdatedTimeUI() {
        Resources res = getResources();
        int paddingTop = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, res.getDisplayMetrics());
        lastUpdatedTime.setPadding(0, paddingTop, 0, paddingTop);
        lastUpdatedTime.setTextColor(textColor);
        lastUpdatedTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, dayOfWeekTextSize);
        forecast.setPadding(0, paddingTop, 0, paddingTop);
        forecast.setTextColor(textColor);
        forecast.setTextSize(TypedValue.COMPLEX_UNIT_SP, dayOfWeekTextSize);
    }

    private void updateForecastText() {
        if (forecastDays <= 3) {
            forecast.setText(getContext().getString(R.string.x_hour_forecast, String.valueOf(forecastDays*24), city));
        } else {
            forecast.setText(getContext().getString(R.string.x_day_forecast, String.valueOf(forecastDays), city));
        }
    }

    @Override
    protected void displayWeatherResult(FiveDayResult result) {
        Resources res = getResources();
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 20, res.getDisplayMetrics());
        this.city = result.getCity().getName();
        updateForecastText();
        weatherStatuses.removeAllViews();
        weatherStatuses.addView(forecast);
        int counter = 0;
        int maxCounter = getForecastDaysMaxCounter();
        LOGGER.log(Level.INFO, "weatherData=" + result.toString());
        for (SingleDayResult singleDay : result.getList()) {
            if (singleDay.getWeather().length == 0) {
                continue;
            }
            Weather weather = singleDay.getWeather()[0];
            String weatherIconId = "@drawable/ic_weather" + weather.getIcon();
            Integer drawableId = getResources().getIdentifier(weatherIconId, "drawable", getContext().getPackageName());
            WeatherDayView weatherDayView = new WeatherDayView(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            weatherDayView.setOrientation(LinearLayout.VERTICAL);
            layoutParams.gravity = Gravity.CENTER;
            weatherDayView.setLayoutParams(layoutParams);
            weatherDayView.setGravity(Gravity.CENTER);
            if (orientation == LinearLayout.HORIZONTAL) {
                weatherDayView.setPadding(0, 0, padding, 0);
            } else {
                weatherDayView.setPadding(0, padding, 0, 0);
            }
            weatherDayView.setWeatherIcon(drawableId, iconSizeMultiplier);
            weatherDayView.setTemperatureFormatted(singleDay.getMain().getTemp());
            weatherDayView.setTemperatureTextSize(temperatureTextSize);
            weatherDayView.setDayOfWeekTextSize(dayOfWeekTextSize);
            weatherDayView.setTextColor(textColor);
            Date forecastDate = new Date(singleDay.getDt()*1000);
            weatherDayView.setDayOfWeek(DAY_OF_WEEK_HOUR_FORMAT.format(forecastDate));
            weatherStatuses.addView(weatherDayView);
            if (counter % timeInterval != 0 || counter > maxCounter) {
                weatherDayView.setVisibility(WeatherDayView.GONE);
            }
            counter += 1;
        }
        updateLastUpdatedTimeUI();
        String lastUpdatedDate = HOUR_MINUTE_FORMAT.format(new Date());
        String lastUpdated = getContext().getString(R.string.last_updated, lastUpdatedDate);
        lastUpdatedTime.setText(lastUpdated);
        weatherStatuses.addView(lastUpdatedTime);
        scrollingForecastUIService.activateScroll();
    }

    public int getForecastDaysMaxCounter() {
        return forecastDays * 8;
    }

    public void updateConfiguration(ClockSettings clockSettings) {
        // Integer timeInterval, int textColor, float temperatureTextSize, float dayOfWeekTextSize, float iconSizeMultiplier
        this.timeInterval = clockSettings.getWeatherTimeInterval();
        this.forecastDays = clockSettings.getWeatherForecastDays();
        this.textColor = clockSettings.getTextColor();
        this.temperatureTextSize = clockSettings.getFontSizeWeatherTemp();
        this.dayOfWeekTextSize = clockSettings.getFontSizeWeatherTime();
        this.iconSizeMultiplier = clockSettings.getIconSizeMultiplier().floatValue();
        updateDisplayTimeInterval();
        updateUI();
        updateForecastText();
        initializeWeatherData(clockSettings.getOpenWeatherApiKey(), clockSettings.getUpdateForecastFrequencyMinutes());
    }

    private void updateDisplayTimeInterval() {
        UpdateWeatherDayViews updateWeatherDayViews = new UpdateWeatherDayViews() {
            @Override
            protected void updateEach(int index, WeatherDayView day) {
                int maxCounter = getForecastDaysMaxCounter();
                if (index % timeInterval != 0 || index > maxCounter) {
                    day.setVisibility(View.GONE);
                } else {
                    day.setVisibility(View.VISIBLE);
                }
            }
        };
        updateWeatherDayViews.updateAll();
    }

    private void updateUI() {
        UpdateWeatherDayViews updateWeatherDayViews = new UpdateWeatherDayViews() {
            @Override
            protected void updateEach(int index, WeatherDayView day) {
                day.setTextColor(textColor);
                day.setTemperatureTextSize(temperatureTextSize);
                day.setDayOfWeekTextSize(dayOfWeekTextSize);
                day.setIconSizeMultiplier(iconSizeMultiplier);
            }
        };
        updateWeatherDayViews.updateAll();
    }

    @Override
    public void processNoLocation() {
        UpdateWeatherDayViews updateWeatherDayViews = new UpdateWeatherDayViews() {
            @Override
            protected void updateEach(int index, WeatherDayView day) {
                day.setDayOfWeek(getContext().getString(R.string.no_geo));
            }
        };
        updateWeatherDayViews.updateAll();
    }

    @Override
    public void processNoApiKey() {
        UpdateWeatherDayViews updateWeatherDayViews = new UpdateWeatherDayViews() {
            @Override
            protected void updateEach(int index, WeatherDayView day) {
                day.setDayOfWeek(getContext().getString(R.string.no_key));
            }
        };
        updateWeatherDayViews.updateAll();
    }

    private abstract class UpdateWeatherDayViews {

        void updateAll() {
            int childCount = weatherStatuses.getChildCount();
            for (int i=0; i<childCount; i++) {
                View child = weatherStatuses.getChildAt(i);
                if (child instanceof WeatherDayView) {
                    WeatherDayView day = (WeatherDayView) child;
                    updateEach(i, day);
                }
            }
        }

        protected abstract void updateEach(int index, WeatherDayView day);

    }

}
