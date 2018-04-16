package com.a3dx2.clock.service.model;

public class WeatherUpdateContext {

    private final String openWeatherApiKey;
    private final Integer updateFrequencyMinutes;

    public WeatherUpdateContext(String openWeatherApiKey, Integer updateFrequencyMinutes) {
        this.openWeatherApiKey = openWeatherApiKey;
        this.updateFrequencyMinutes = updateFrequencyMinutes;
    }

    public String getOpenWeatherApiKey() {
        return openWeatherApiKey;
    }

    public Integer getUpdateFrequencyMinutes() {
        return updateFrequencyMinutes;
    }

}
