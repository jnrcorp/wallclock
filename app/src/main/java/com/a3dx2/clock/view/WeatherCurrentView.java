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
    private TextView currentWeatherDetails;

    private int textColor;
    private float temperatureTextSize;
    private float iconSizeMultiplier;

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
        initializeWeatherData(clockSettings.getOpenWeatherApiKey(), clockSettings.getUpdateFrequencyMinutes());
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
        this.currentWeatherDetails = findViewById(R.id.current_weather_details);
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
        String weatherIconId = "@drawable/weather" + weather.getIcon();
        Integer drawableId = getResources().getIdentifier(weatherIconId, "drawable", getContext().getPackageName());
        currentWeatherImage.setImageResource(drawableId);
        String temperature = WeatherUtil.formatTemperature(result.getMain().getTemp());
        currentWeatherTemperature.setText(temperature);
        String lastUpdatedDate = HOUR_MINUTE_FORMAT.format(new Date());
        String details = getContext().getString(R.string.current_weather_details, result.getName(), lastUpdatedDate);
        currentWeatherDetails.setText(details);
    }

    public void updateConfiguration(ClockSettings clockSettings) {
        // Integer timeInterval, int teUxtColor, float temperatureTextSize, float iconSizeMultiplier
        this.textColor = clockSettings.getTextColor();
        this.temperatureTextSize = clockSettings.getFontSizeWeatherTemp();
        this.iconSizeMultiplier = clockSettings.getIconSizeMultiplier().floatValue();
        currentWeatherTemperature.setTextColor(textColor);
        currentWeatherDetails.setTextColor(textColor);
        currentWeatherTemperature.setTextSize(TypedValue.COMPLEX_UNIT_SP, temperatureTextSize);
        WeatherUtil.resizeIcon(currentWeatherImage, iconSizeMultiplier);
        initializeWeatherData(clockSettings.getOpenWeatherApiKey(), clockSettings.getUpdateFrequencyMinutes());
    }

    @Override
    public void processNoLocation() {
        currentWeatherTemperature.setText(getContext().getString(R.string.no_geo));
    }

    @Override
    public void processNoApiKey() {
        currentWeatherTemperature.setText(getContext().getString(R.string.no_key));
    }

}
