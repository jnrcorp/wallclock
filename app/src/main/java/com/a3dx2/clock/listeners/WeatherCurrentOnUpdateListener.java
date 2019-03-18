package com.a3dx2.clock.listeners;

import com.a3dx2.clock.service.openweathermap.model.CurrentLocationResult;

public interface WeatherCurrentOnUpdateListener {

    void onUpdate(CurrentLocationResult result);

}
