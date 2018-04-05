package com.a3dx2.clock.service.openweathermap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SingleDayResult {

    private Date dt;
    private Main main;
    private Weather[] weather;
    private Wind wind;
    private Cloud cloud;
    private RecentPrecipitation rain;
    private RecentPrecipitation snow;

    public SingleDayResult() {
        super();
    }

    @Override
    public String toString() {
        return "SingleDayResult{" +
                "dt=" + dt +
                ", main=" + main +
                ", weather=" + Arrays.toString(weather) +
                ", wind=" + wind +
                ", cloud=" + cloud +
                ", rain=" + rain +
                ", snow=" + snow +
                '}';
    }

    public Date getDt() {
        return dt;
    }

    public void setDt(Date dt) {
        this.dt = dt;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public Wind getWind() {
        return wind;
    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Cloud getCloud() {
        return cloud;
    }

    public void setCloud(Cloud cloud) {
        this.cloud = cloud;
    }

    public RecentPrecipitation getRain() {
        return rain;
    }

    public void setRain(RecentPrecipitation rain) {
        this.rain = rain;
    }

    public RecentPrecipitation getSnow() {
        return snow;
    }

    public void setSnow(RecentPrecipitation snow) {
        this.snow = snow;
    }
}
