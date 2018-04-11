package com.a3dx2.clock.service.openweathermap.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Arrays;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentLocationResult {

    private System sys;
    private Coordinates coord;
    private Weather[] weather;
    private Main main;
    private Wind wind;
    private Cloud cloud;
    private RecentPrecipitation rain;
    private RecentPrecipitation snow;
    private Date date;
    private Sun sun;
    private Integer id; // city ID
    private String name; // city Name

    public CurrentLocationResult() {
        super();
    }

    @Override
    public String toString() {
        return "CurrentLocationResult{" +
                "sys=" + sys +
                ", coord=" + coord +
                ", weather=" + Arrays.toString(weather) +
                ", main=" + main +
                ", wind=" + wind +
                ", cloud=" + cloud +
                ", rain=" + rain +
                ", snow=" + snow +
                ", date=" + date +
                ", sun=" + sun +
                ", id=" + id +
                ", name='" + name + '\'' +
                '}';
    }

    public System getSys() {
        return sys;
    }

    public void setSys(System sys) {
        this.sys = sys;
    }

    public Coordinates getCoord() {
        return coord;
    }

    public void setCoord(Coordinates coord) {
        this.coord = coord;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Sun getSun() {
        return sun;
    }

    public void setSun(Sun sun) {
        this.sun = sun;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
