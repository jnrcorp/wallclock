package com.a3dx2.clock.view;

import com.a3dx2.clock.service.model.WeatherUpdateContext;

public interface WebServiceAwareView {

    WeatherUpdateContext getWeatherUpdateContext();

    void processNoLocation();

    void processNoApiKey();

}
