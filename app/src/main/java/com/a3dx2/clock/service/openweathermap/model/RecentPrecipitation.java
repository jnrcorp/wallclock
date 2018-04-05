package com.a3dx2.clock.service.openweathermap.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecentPrecipitation {

    @JsonProperty("3h")
    private Integer last3Hours;

    public RecentPrecipitation() {
        super();
    }

    @Override
    public String toString() {
        return "RecentPrecipitation{" +
                "last3Hours=" + last3Hours +
                '}';
    }

    public Integer getLast3Hours() {
        return last3Hours;
    }

    public void setLast3Hours(Integer last3Hours) {
        this.last3Hours = last3Hours;
    }
}
