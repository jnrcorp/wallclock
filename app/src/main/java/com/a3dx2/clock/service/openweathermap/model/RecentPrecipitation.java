package com.a3dx2.clock.service.openweathermap.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RecentPrecipitation {

    @JsonProperty("1h")
    private Integer last1Hour;

    @JsonProperty("3h")
    private Integer last3Hours;

    public RecentPrecipitation() {
        super();
    }

    @Override
    public String toString() {
        return "RecentPrecipitation{" +
                "last1Hour=" + last1Hour +
                ", last3Hours=" + last3Hours +
                '}';
    }

    public Integer getLast1Hour() {
        return last1Hour;
    }

    public void setLast1Hour(Integer last1Hour) {
        this.last1Hour = last1Hour;
    }

    public Integer getLast3Hours() {
        return last3Hours;
    }

    public void setLast3Hours(Integer last3Hours) {
        this.last3Hours = last3Hours;
    }
}
