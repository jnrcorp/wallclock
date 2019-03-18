package com.a3dx2.clock.listeners;

import com.a3dx2.clock.service.openweathermap.model.FiveDayResult;

public interface WeatherForecastOnUpdateListener {

    void onUpdate(FiveDayResult result);

}
