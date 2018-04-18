package com.a3dx2.clock.service.model;

public class WeatherUpdateContext {

    private final String openWeatherApiKey;
    private final int updateFrequencyMinutes;

    public WeatherUpdateContext(String openWeatherApiKey, int updateFrequencyMinutes) {
        this.openWeatherApiKey = openWeatherApiKey;
        this.updateFrequencyMinutes = updateFrequencyMinutes;
    }

    public String getOpenWeatherApiKey() {
        return openWeatherApiKey;
    }

    public int getUpdateFrequencyMinutes() {
        return updateFrequencyMinutes;
    }

}
