package com.a3dx2.clock.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import com.a3dx2.clock.R;
import com.a3dx2.clock.listeners.WeatherCurrentOnUpdateListener;
import com.a3dx2.clock.service.model.ClockSettings;
import com.a3dx2.clock.service.openweathermap.WeatherSearchCurrent;
import com.a3dx2.clock.service.openweathermap.model.CurrentLocationResult;
import com.a3dx2.clock.service.openweathermap.model.Weather;
import com.a3dx2.clock.weather.WeatherUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherCurrentView extends WeatherServiceAwareView<CurrentLocationResult> {

    private final Logger LOGGER = Logger.getLogger("com.a3dx2.clock");

    private static final SimpleDateFormat HOUR_MINUTE_FORMAT = new SimpleDateFormat("h:mm a", Locale.US);

    private TextView currentWeatherTemperature;
    private ImageView currentWeatherImage;
    private ImageView sunriseImage;
    private TextView sunriseDetails;
    private ImageView sunsetImage;
    private TextView sunsetDetails;

    private int textColor;
    private float temperatureTextSize;
    private float iconSizeMultiplier;

    private WeatherCurrentOnUpdateListener weatherCurrentOnUpdateListener;

    public WeatherCurrentView(Context context) {
        super(context);
        init(context, null);
    }

    public WeatherCurrentView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public WeatherCurrentView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public WeatherCurrentView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        ClockSettings clockSettings = new ClockSettings(getContext());
        updateConfiguration(clockSettings);
        initializeWeatherData(clockSettings.getOpenWeatherApiKey(), clockSettings.getUpdateCurrentFrequencyMinutes());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopWeatherDataUpdate();
    }

    private void init(Context context, AttributeSet attrs) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        inflater.inflate(R.layout.weather_current_view, this, true);
        TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.WeatherCurrentView, 0, 0);
        this.currentWeatherTemperature = findViewById(R.id.current_weather_temp);
        this.currentWeatherImage = findViewById(R.id.current_weather_image);
        this.sunriseImage = findViewById(R.id.current_weather_sunrise_image);
        this.sunsetImage = findViewById(R.id.current_weather_sunset_image);
        this.sunriseDetails = findViewById(R.id.sunrise_details);
        this.sunsetDetails = findViewById(R.id.sunset_details);
        this.textColor = attributes.getColor(R.styleable.WeatherCurrentView_textColor, Color.WHITE);
        this.iconSizeMultiplier = attributes.getFloat(R.styleable.WeatherCurrentView_iconSizeMultiplier, 3);
        this.temperatureTextSize = attributes.getDimension(R.styleable.WeatherCurrentView_temperatureTextSize, 30);
        createWeatherUpdateService(context, new WeatherSearchCurrent(this));
        String openWeatherMapApiKey = attributes.getString(R.styleable.WeatherCurrentView_openWeatherMapApiKey);
        int updateFrequencyMinutes = attributes.getInt(R.styleable.WeatherForecastView_updateFrequencyMinutes, 30);
        initializeWeatherData(openWeatherMapApiKey, updateFrequencyMinutes);
    }

    @Override
    protected void displayWeatherResult(CurrentLocationResult result) {
        LOGGER.log(Level.INFO, "currentConditions=" + result.toString());
        Weather weather = result.getWeather()[0];
        String weatherIconId = "@drawable/ic_weather" + weather.getIcon();
        int drawableId = getResources().getIdentifier(weatherIconId, "drawable", getContext().getPackageName());
        currentWeatherImage.setImageResource(drawableId);
        sunriseImage.setColorFilter(textColor);
        sunsetImage.setColorFilter(textColor);
        int sunriseDrawableId = getResources().getIdentifier("@drawable/ic_sunrise", "drawable", getContext().getPackageName());
        sunriseImage.setImageResource(sunriseDrawableId);
        int sunsetDrawableId = getResources().getIdentifier("@drawable/ic_sunset", "drawable", getContext().getPackageName());
        sunsetImage.setImageResource(sunsetDrawableId);
        WeatherUtil.resizeIcon(sunriseImage, 3);
        WeatherUtil.resizeIcon(sunsetImage, 3);
        WeatherUtil.resizeIcon(currentWeatherImage, iconSizeMultiplier);
        String temperature = WeatherUtil.formatTemperature(result.getMain().getTemp());
        currentWeatherTemperature.setText(temperature);
        Long sunriseTime = Long.valueOf(result.getSys().getSunrise());
        Long sunsetTime = Long.valueOf(result.getSys().getSunset());
        Date sunrise = new Date(sunriseTime * 1000);
        Date sunset = new Date(sunsetTime * 1000);
        String sunriseDisplayDate = HOUR_MINUTE_FORMAT.format(sunrise);
        String sunsetDisplayDate = HOUR_MINUTE_FORMAT.format(sunset);
        sunriseDetails.setText(sunriseDisplayDate);
        sunsetDetails.setText(sunsetDisplayDate);
        if (weatherCurrentOnUpdateListener != null) {
            weatherCurrentOnUpdateListener.onUpdate(result);
        }
    }

    public void updateConfiguration(ClockSettings clockSettings) {
        // Integer timeInterval, int teUxtColor, float temperatureTextSize, float iconSizeMultiplier
        this.textColor = clockSettings.getTextColor();
        this.temperatureTextSize = clockSettings.getFontSizeWeatherTemp();
        this.iconSizeMultiplier = clockSettings.getIconSizeMultiplier().floatValue();
        currentWeatherTemperature.setTextColor(textColor);
        currentWeatherImage.setColorFilter(textColor);
        sunriseDetails.setTextColor(textColor);
        sunsetDetails.setTextColor(textColor);
        currentWeatherTemperature.setTextSize(TypedValue.COMPLEX_UNIT_SP, temperatureTextSize);
        WeatherUtil.resizeIcon(currentWeatherImage, iconSizeMultiplier);
        sunriseImage.setColorFilter(textColor);
        sunsetImage.setColorFilter(textColor);
        initializeWeatherData(clockSettings.getOpenWeatherApiKey(), clockSettings.getUpdateCurrentFrequencyMinutes());
    }

    @Override
    public void processNoLocation() {
        currentWeatherTemperature.setText(getContext().getString(R.string.no_geo));
    }

    @Override
    public void processNoApiKey() {
        currentWeatherTemperature.setText(getContext().getString(R.string.no_key));
    }

    public void setWeatherCurrentOnUpdateListener(WeatherCurrentOnUpdateListener weatherCurrentOnUpdateListener) {
        this.weatherCurrentOnUpdateListener = weatherCurrentOnUpdateListener;
    }

}
