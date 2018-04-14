package com.a3dx2.clock.service.openweathermap;

import android.location.Location;

public interface WebServiceWrapper {

    void execute(Location location, String openWeatherMapApiKey);

}
