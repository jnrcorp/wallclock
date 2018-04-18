package com.a3dx2.clock.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import com.a3dx2.clock.service.WeatherUpdateService;
import com.a3dx2.clock.service.model.WeatherUpdateContext;
import com.a3dx2.clock.service.openweathermap.WebServiceWrapper;

public abstract class WeatherServiceAwareView<T> extends FrameLayout {

    private WeatherUpdateService weatherUpdateService;
    private WeatherUpdateContext weatherUpdateContext;
    private T weatherResult;

    public WeatherServiceAwareView(@NonNull Context context) {
        super(context);
    }

    public WeatherServiceAwareView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherServiceAwareView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public WeatherServiceAwareView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void createWeatherUpdateService(Context context, WebServiceWrapper wrapper) {
        this.weatherUpdateService = new WeatherUpdateService(context, this, wrapper);
    }

    public void initializeWeatherData(String openWeatherMapApiKey, Integer updateFrequencyMinutes) {
        boolean changedAndValid = updateWeatherContext(openWeatherMapApiKey, updateFrequencyMinutes);
        if (changedAndValid) {
            weatherUpdateService.startWeatherUpdate();
        }
    }

    private boolean updateWeatherContext(String openWeatherMapApiKey, int updateFrequencyMinutes) {
        if (openWeatherMapApiKey == null || openWeatherMapApiKey.trim().isEmpty()) {
            weatherUpdateContext = null;
            stopWeatherDataUpdate();
            return false;
        }
        boolean changed = false;
        if (weatherUpdateContext == null) {
            changed = true;
        } else {
            if (!weatherUpdateContext.getOpenWeatherApiKey().equals(openWeatherMapApiKey)) {
                changed = true;
            }
            if (weatherUpdateContext.getUpdateFrequencyMinutes() != updateFrequencyMinutes) {
                changed = true;
            }
        }
        weatherUpdateContext = new WeatherUpdateContext(openWeatherMapApiKey, updateFrequencyMinutes);
        return changed;
    }

    public void stopWeatherDataUpdate() {
        weatherUpdateService.stopWeatherUpdate();
    }

    public WeatherUpdateContext getWeatherUpdateContext() {
        return weatherUpdateContext;
    }

    public T getWeatherResult() {
        return weatherResult;
    }

    public void processWeatherResult(T weatherResult) {
        this.weatherResult = weatherResult;
        displayWeatherResult(weatherResult);
    }

    protected abstract void displayWeatherResult(T weatherResult);

    public abstract void processNoLocation();

    public abstract void processNoApiKey();

}
